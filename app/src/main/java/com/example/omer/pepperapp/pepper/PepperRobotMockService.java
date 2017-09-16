package com.example.omer.pepperapp.pepper;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.omer.pepperapp.Bus;

/**
 * Created by Omer on 9/14/2017.
 */

public class PepperRobotMockService extends Service{

    public static void start(Context context){
        context.startService(new Intent(context,PepperRobotMockService.class));
    }

    private PepperHumanSensor pepperHumanSensor;
    private PepperAudioSpeaker pepperAudioSpeaker;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("PEPPER LOG","Pepper services started");
        pepperHumanSensor = new PepperHumanSensor();
        pepperAudioSpeaker = new PepperAudioSpeaker(this);
        pepperHumanSensor.start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pepperHumanSensor.shutDown();
        pepperAudioSpeaker.shutDown();
    }
}
