package com.slothnull.android.medox;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.slothnull.android.medox.service.IndicatorsService;
import com.slothnull.android.medox.service.LocationService;
import com.slothnull.android.medox.service.ShakeService;

/**
 * Created by Shaheen on 09-May-17
 * Project: Medox
 * Package: com.slothnull.android.medox
 */

public class BasicHelper {

    private static final int MY_PERMISSIONS_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static final int MY_PERMISSIONS_BODY_SENSORS = 2;
    private static final String TAG = "helperClass";

    public static void triggerServices(Context context){
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

    private static void stopServices(Context context){
        try{
            context.stopService(new Intent(context, ShakeService.class));
        }catch(Exception e){
            Log.e(TAG, "error Stopping Shake Service");
        }
        try{
            context.stopService(new Intent(context, LocationService.class));
        }catch(Exception e){
            Log.e(TAG, "error Stopping Location Service");
        }
        try{
            context.stopService(new Intent(context, IndicatorsService.class));
        }catch (Exception e){
            Log.e(TAG, "error Stopping Indicators Service");
        }
    }

    private static void enableServices(Context context){
        PackageManager pm = context.getPackageManager();

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

    private static void disableServices(Context context) {
        PackageManager pm = context.getPackageManager();

        try{
            pm.setComponentEnabledSetting(
                    new ComponentName(context, ShakeService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);


        }catch(Exception e){
            Log.e(TAG, "error disabling Shake Service");
        }
        try{
            pm.setComponentEnabledSetting(
                    new ComponentName(context, LocationService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);


        }catch(Exception e){
            Log.e(TAG, "error disabling Location Service");
        }
        try{
            pm.setComponentEnabledSetting(
                    new ComponentName(context, IndicatorsService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);

        }catch(Exception e){
            Log.e(TAG, "error disabling Indicators Service");
        }
    }

    public static void signOut(Context context) {
        stopServices(context);
        disableServices(context);
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }

    public static void permissions(Context context, Activity activity){
        if (ContextCompat.checkSelfPermission(context,
                "android.permission.SEND_SMS")
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{"android.permission.SEND_SMS"},
                    MY_PERMISSIONS_SEND_SMS);
        }
        if (ContextCompat.checkSelfPermission(context,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    MY_PERMISSIONS_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(context,
                "android.permission.BODY_SENSORS")
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{"android.permission.BODY_SENSORS"},
                    MY_PERMISSIONS_BODY_SENSORS);
        }
    }
}
