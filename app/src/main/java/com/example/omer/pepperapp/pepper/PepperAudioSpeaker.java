package com.example.omer.pepperapp.pepper;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.example.omer.pepperapp.Bus;
import com.example.omer.pepperapp.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Omer on 9/14/2017.
 */

public class PepperAudioSpeaker extends UtteranceProgressListener implements Bus.Listener, TextToSpeech.OnInitListener, PepperComponent {

    private static final int GREETINGS_RESET_CAP = 5;

    private String[] greetings;
    private int greetingsUsed;

    private TextToSpeech textToSpeech;

    public PepperAudioSpeaker(Context context){
        Bus.subscribeToEvent(PepperCommands.GREET,this);
        Bus.subscribeToEvent(PepperCommands.UPDATE_VOCABULARY,this);
        textToSpeech = new TextToSpeech(context, this);
        greetings = context.getResources().getStringArray(R.array.greetings);

    }

    @Override
    public void eventReceived(String code, Object event) {
        if(code.equals(PepperCommands.GREET)){
            startGreeting();
            return;
        }

        if(code.equals(PepperCommands.UPDATE_VOCABULARY)){
            ArrayList<String> newVocab = (ArrayList<String>)event;
            return;
        }
    }

    private void startGreeting(){
        if(greetingsUsed == GREETINGS_RESET_CAP){greetingsUsed=0;}
        int selectGreeting = ThreadLocalRandom.current().nextInt(greetingsUsed,greetings.length-greetingsUsed);
        String temp = greetings[greetingsUsed];
        greetings[greetingsUsed] = greetings[selectGreeting];
        greetings[selectGreeting] = temp;
        String greeting = greetings[greetingsUsed++];
        Bus.post(PepperCallbacks.GREETING_STARTED,greeting);
        textToSpeech.speak(greeting,TextToSpeech.QUEUE_FLUSH,null,generateUtteranceId());
    }


    @Override
    public void onInit(int status) {
        if(status==TextToSpeech.SUCCESS){
            textToSpeech.setOnUtteranceProgressListener(this);
            textToSpeech.setLanguage(Locale.US);
        }
    }

    @Override
    public void onStart(String utteranceId) {

    }

    @Override
    public void onDone(String utteranceId) {
        Bus.post(PepperCallbacks.GREETING_COMPLETE,null);
    }

    @Override
    public void onError(String utteranceId) {

    }



    private String generateUtteranceId(){
        return "PepperAudio"+Long.toString(System.currentTimeMillis());
    }

    @Override
    public void start() {

    }

    @Override
    public void shutDown() {
        if(textToSpeech.isSpeaking()){
            textToSpeech.stop();
            Bus.post(PepperCallbacks.GREETING_COMPLETE,null);
        }
    }
}
