package com.slothnull.android.medox;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.slothnull.android.medox.service.IndicatorsService;
import com.slothnull.android.medox.service.LocationService;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        //check if user is already signed-in or not
        //if not call Authentication Activity
        try{
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }catch(Exception e){
            callAuth();
        }

        //if user signed in
        //go to Home Activity according to user type
        String appType = sharedPreferences.getString("appType","");
        if (appType.equals("care")){
            callCare();
        }else if( appType.equals("senior") ){
            callSenior();
        }else{
            callAuth();
        }

    }
    private void callAuth(){
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
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        enableServices();
        triggerServices();
        finish();
    }
    public void triggerServices(){
        if(!LocationService.check){
            Intent location = new Intent(this, LocationService.class);
            startService(location);
        }
        if(!IndicatorsService.check){
            Intent indicators = new Intent(this, IndicatorsService.class);
            startService(indicators);
        }
    }

    private void stopServices(){
        if(LocationService.check){
            stopService(new Intent(this, LocationService.class));
        }
        if(IndicatorsService.check){
            stopService(new Intent(this, IndicatorsService.class));
        }
    }

    private void enableServices(){
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
        int disbledCheck = pm.COMPONENT_ENABLED_STATE_DISABLED;

        int locationCheck = pm.getComponentEnabledSetting(
                new ComponentName(context, LocationService.class));

        int indicatorsCheck = pm.getComponentEnabledSetting(
                new ComponentName(context, IndicatorsService.class));

        if (locationCheck == disbledCheck) {
            pm.setComponentEnabledSetting(
                    new ComponentName(context, LocationService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }

        if (indicatorsCheck == disbledCheck) {
            pm.setComponentEnabledSetting(
                    new ComponentName(context, IndicatorsService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    private void disableServices() {
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
        int enabledCheck = pm.COMPONENT_ENABLED_STATE_ENABLED;

        int locationCheck = pm.getComponentEnabledSetting(
                new ComponentName(context, LocationService.class));

        int indicatorsCheck = pm.getComponentEnabledSetting(
                new ComponentName(context, IndicatorsService.class));

        if (locationCheck == enabledCheck) {
            pm.setComponentEnabledSetting(
                    new ComponentName(context, LocationService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }

        if (indicatorsCheck == enabledCheck) {
            pm.setComponentEnabledSetting(
                    new ComponentName(context, IndicatorsService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
