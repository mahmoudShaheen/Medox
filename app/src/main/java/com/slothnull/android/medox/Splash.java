package com.slothnull.android.medox;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.slothnull.android.medox.service.IndicatorsService;
import com.slothnull.android.medox.service.LocationService;

public class Splash extends Activity {
    String TAG = "SplashActivity";
    //TODO: ask for permissions
    //https://developer.android.com/training/permissions/requesting.html
    //TODO: go to settings if not configured "after Auth"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        //check if user is already signed-in or not
        //if not call Authentication Activity

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if(auth == null){
            callAuth();
            Log.i(TAG, "1");
        }else{
            //if user signed in
            //go to Home Activity according to user type
            String appType = sharedPreferences.getString("appType","");
            Log.i(TAG, appType );
            if (appType.equals("care")){
                callCare();
            }else if( appType.equals("senior") ){
                callSenior();
            }else{ //user signed but undefined app type
                callAuth();
                Log.i(TAG, "2");
            }
        }
    }
    private void callAuth(){
        signOut();
        Intent intent = new Intent(this, Authentication.class);
        startActivity(intent);
        finish();
    }
    private void callCare(){
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        stopServices();
        disableServices();
        finish();
    }
    private void callSenior(){
        Intent intent = new Intent(this, SeniorHome.class);
        startActivity(intent);
        enableServices();
        triggerServices();
        finish();
    }
    public void triggerServices(){
        try{
            Intent location = new Intent(this, LocationService.class);
            startService(location);
        }catch(Exception e){
            Log.e(TAG, "error Triggering Location Service");
        }
        try{
            Intent indicators = new Intent(this, IndicatorsService.class);
            startService(indicators);
        }catch(Exception e){
            Log.e(TAG, "error Triggering Indicators Service");
        }
    }

    private void stopServices(){
        try{
            stopService(new Intent(this, LocationService.class));
        }catch(Exception e){
            Log.e(TAG, "error Stopping Location Service");
        }
        try{
            stopService(new Intent(this, IndicatorsService.class));
        }catch (Exception e){
            Log.e(TAG, "error Stopping Indicators Service");
        }
    }

    private void enableServices(){
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();

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

    private void disableServices() {
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
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

    public void signOut() {
        try{
            stopService(new Intent(this, IndicatorsService.class));
            stopService(new Intent(this, LocationService.class));
        }catch(Exception e){
            Log.e(TAG, "error Stopping Services during sign-out");
        }
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }
}
