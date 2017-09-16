package com.example.omer.pepperapp.pepper;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.omer.pepperapp.Bus;

import java.io.IOException;

/**
 * Created by Omer on 9/14/2017.
 */

public class PepperHumanSensor implements PepperComponent {


    private static long AMP_CHECK_RATE = 2000;
    private static int ENV_CHECK_REFRESH = 60;

    private MediaRecorder mediaRecorder;
    private int totalAmpSoFar;
    private double averageLevel;
    private int envCheckCount;

    private Thread soundCheckThread;
    private final Object ampCheckLock;
    private boolean ampCheckActive;



    private Handler mainThreadHandler;

    public PepperHumanSensor(){
        mainThreadHandler = new Handler(Looper.getMainLooper());
        ampCheckLock = new Object();
        totalAmpSoFar = 0;
        averageLevel = 0;
        envCheckCount = 1;
    }

    private MediaRecorder createMediaRecorder(){
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile("/dev/null");
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recorder;
    }

    @Override
    public void start(){
        synchronized (ampCheckLock) {
            Log.d("PEPPER LOG","Started Sensor");
            ampCheckActive = true;
            if(mediaRecorder==null){
                mediaRecorder = createMediaRecorder();
            }else {
                mediaRecorder.reset();
            }
            mediaRecorder.start();
            getSoundCheckThread().start();
        }
    }
    @Override
    public void shutDown(){
        synchronized (ampCheckLock) {
            ampCheckActive = false;
            try {
                getSoundCheckThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mediaRecorder.stop();
            soundCheckThread = null;
            mediaRecorder = null;
        }
    }

    private boolean isAmpCheckActive(){
        synchronized (ampCheckLock){
            return ampCheckActive;
        }
    }

    private Thread getSoundCheckThread(){
        if(soundCheckThread==null){
            soundCheckThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isAmpCheckActive()){
                        sleep();
                        checkAmplitude();
                    }

                }
            });
        }
        return soundCheckThread;
    }

    private void checkAmplitude() {
        int curLevel = mediaRecorder.getMaxAmplitude();
        envCheckCount++;
        if(envCheckCount == ENV_CHECK_REFRESH){
           refreshCurData();
        }
        totalAmpSoFar +=curLevel;
        averageLevel = totalAmpSoFar/envCheckCount;
        Log.d("PEPPER LOG","curAmp:" + curLevel);

        if(curLevel> averageLevel){
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    Bus.post(PepperCallbacks.HUMAN_DETECTED,null);
                }
            });
        }
        averageLevel = curLevel;
    }

    private void refreshCurData(){
        envCheckCount = 1;
        totalAmpSoFar = 0;
        averageLevel  = 0;
    }

    private void sleep(){
        try {
            Thread.sleep(AMP_CHECK_RATE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
