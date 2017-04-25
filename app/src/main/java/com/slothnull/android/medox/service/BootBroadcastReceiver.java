package com.slothnull.android.medox.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BootBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Broadcast received");
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        if (auth != null){
            final String appType = sharedPreferences.getString("appType", "");
            Log.i(TAG, appType);
            if (appType.equals("senior")) {
                enableServices(context);
                triggerServices(context);
            } else { //user not signed or Care type or undefined app type
                return;
            }
        }
    }
    private void enableServices(Context context){
        PackageManager pm = context.getPackageManager();
        Log.i(TAG, "enable");

        try{
            pm.setComponentEnabledSetting(
                    new ComponentName(context, ShakeService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);


        }catch(Exception e){
            Log.e(TAG, "error enabling Shake Service");
        }
        try{
            pm.setComponentEnabledSetting(
                    new ComponentName(context, LocationService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);


        }catch(Exception e){
            Log.e(TAG, "error enabling Location Service");
        }
        try{
            pm.setComponentEnabledSetting(
                    new ComponentName(context, IndicatorsService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }catch(Exception e){
            Log.e(TAG, "error enabling Indicators Service");
        }
    }
    public void triggerServices(Context context){
        Log.i(TAG, "trigger");

        try{
            Intent shake = new Intent(context, ShakeService.class);
            context.startService(shake);
        }catch(Exception e){
            Log.e(TAG, "error Triggering Shake Service");
        }
        try{
            Intent location = new Intent(context, LocationService.class);
            context.startService(location);
        }catch(Exception e){
            Log.e(TAG, "error Triggering Location Service");
        }
        try{
            Intent indicators = new Intent(context, IndicatorsService.class);
            context.startService(indicators);
        }catch(Exception e){
            Log.e(TAG, "error Triggering Indicators Service");
        }
    }
}
