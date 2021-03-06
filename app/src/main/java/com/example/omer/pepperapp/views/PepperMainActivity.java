package com.example.omer.pepperapp.views;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.omer.pepperapp.Bus;
import com.example.omer.pepperapp.R;
import com.example.omer.pepperapp.pepper.PepperCallbacks;
import com.example.omer.pepperapp.pepper.PepperCommands;
import com.example.omer.pepperapp.pepper.PepperRobotMockService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Omer on 9/14/2017.
 */

public class PepperMainActivity extends Activity implements Bus.Listener {

    public static void start(Service service){
        Intent intent = new Intent(service,PepperMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        service.startActivity(intent);
    }

    private static final int RECORD_REQUEST = 0;

    private TextView greetingTextView;
    private Button engAccessButton;

    private boolean currentlyGreeting;
    private boolean isActive;

    private ArrayList<String> chosenVocab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        greetingTextView = (TextView)findViewById(R.id.tv_greeting);
        engAccessButton  = (Button)findViewById(R.id.btn_engineer_access);

        Bus.subscribeToEvent(PepperCallbacks.HUMAN_DETECTED,this);
        Bus.subscribeToEvent(PepperCallbacks.GREETING_STARTED,this);
        Bus.subscribeToEvent(PepperCallbacks.GREETING_COMPLETE,this);

        final String[] defaultVocab = getResources().getStringArray(R.array.greetings);
        chosenVocab = new ArrayList<String>(Arrays.asList(defaultVocab));

        engAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EngineerAccessActivity.start(PepperMainActivity.this, chosenVocab);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_REQUEST);

        } else {
            PepperRobotMockService.start(this);
        }



        isActive = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chosenVocab = EngineerAccessActivity.extractVocabs(data);
        Bus.post(PepperCommands.UPDATE_VOCABULARY,chosenVocab);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PepperRobotMockService.start(this);
            }
        }
    }

    @Override
    public void eventReceived(String code, Object event) {
        if(!isActive)return;

        if(code.equals(PepperCallbacks.HUMAN_DETECTED)){
            handleHumanDetection();
            return;
        }

        if(code.equals(PepperCallbacks.GREETING_STARTED)){
            handleGreetingStart((String)event);
            return;
        }

        if(code.equals(PepperCallbacks.GREETING_COMPLETE)){
            handleGreetingDone();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bus.post(PepperCommands.NO_UI,null);
    }

    private void handleHumanDetection(){
        if(!currentlyGreeting) {
            Bus.post(PepperCommands.GREET, null);
        }
    }

    private void handleGreetingStart(String greeting){
        greetingTextView.setText(greeting);
        currentlyGreeting = true;
    }

    private void handleGreetingDone(){
        currentlyGreeting = false;
    }




}
