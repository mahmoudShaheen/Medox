package com.slothnull.android.medox.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.slothnull.android.medox.fragment.SeniorEmergencyFragment;

public class ShakeService extends Service  {

    private static final String TAG = "ShakeService";
    public static final String BROADCAST_ACTION = "Hello World";

    Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "started");
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendEmergency(){
        String title = "Emergency Shake From Watch!";
        String message = "Action Required IMMEDIATELY !!!!";
        Context c = getApplicationContext();
        SeniorEmergencyFragment.emergencyNotification(c, title, message);
    }
}
