package com.example.omer.pepperapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.omer.pepperapp.pepper.PepperRobotMockService;

/**
 * Created by Omer on 9/16/2017.
 */

public class PhoneRebootBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PepperRobotMockService.start(context);
    }
}
