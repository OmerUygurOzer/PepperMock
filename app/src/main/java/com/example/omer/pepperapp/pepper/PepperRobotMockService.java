package com.example.omer.pepperapp.pepper;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.omer.pepperapp.Bus;
import com.example.omer.pepperapp.views.PepperMainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Omer on 9/14/2017.
 */

public class PepperRobotMockService extends Service implements Bus.Listener{


    private static PepperRobotMockService instance;

    public static void start(Context context){
        context.startService(new Intent(context,PepperRobotMockService.class));
    }

    private static final long UI_RESTART_DELAY = 7000;




    private PepperHumanSensor pepperHumanSensor;
    private PepperAudioSpeaker pepperAudioSpeaker;

    private boolean uiVisible;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(instance == null){
            instance = this;
        }else {
            return Service.START_NOT_STICKY;
        }

        Log.d("PEPPER LOG","Pepper services started");
        Bus.subscribeToEvent(PepperCallbacks.HUMAN_DETECTED,this);
        Bus.subscribeToEvent(PepperCommands.NO_UI,this);
        pepperHumanSensor = new PepperHumanSensor();
        pepperAudioSpeaker = new PepperAudioSpeaker(this);
        pepperHumanSensor.start();
        uiVisible = true;
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pepperHumanSensor.shutDown();
        pepperAudioSpeaker.shutDown();
        instance = null;
    }

    @Override
    public void eventReceived(String code, Object event) {
        if(code.equals(PepperCallbacks.HUMAN_DETECTED)){
            if(!uiVisible){
                uiVisible = true;
                PepperMainActivity.start(this);
            }
            return;
        }

        if(code.equals(PepperCommands.NO_UI)){
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    uiVisible = false;
                };
            },UI_RESTART_DELAY);
        }
    }
}
