package com.slothnull.android.medox;

/**
 * Created by Mahmoud Shaheen
 * Project: Medox
 * Licence: MIT
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.service.IndicatorsService;
import com.slothnull.android.medox.service.LocationService;
import com.slothnull.android.medox.service.ShakeService;

public class Splash extends Activity {

    private static final String TAG = "SplashActivity";

    private static final int MY_PERMISSIONS_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static final int MY_PERMISSIONS_BODY_SENSORS = 2;

    DatabaseReference mDatabase;
    ValueEventListener configListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //add image to image view as no image showing using xml ref.
        ImageView logoView = (ImageView) findViewById(R.id.logoView);
        logoView.setImageResource(R.drawable.logo);

        SharedPreferences sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        //check if user is already signed-in or not
        //if not call Authentication Activity
        permissions();
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth == null) {
            callAuth();
            Log.i(TAG, "1");
        } else {
            //if user signed in
            //get app type
            final String appType = sharedPreferences.getString("appType", "");
            Log.i(TAG, appType);

            //check if configured
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(UID).child("user").child("configured");

            configListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    String configured = dataSnapshot.getValue(String.class);
                    if (configured != null) {
                        if (configured.equals("true")) {
                            //go to Home Activity according to user type
                            if (appType.equals("care")) {
                                callCare();
                            } else if (appType.equals("senior")) {
                                callSenior();
                            } else { //user signed but undefined app type
                                callAuth();
                                Log.i(TAG, "2");
                            }
                        } else {
                            //go to Settings or toast a message according to user type
                            if (appType.equals("care")) {
                                callSettings();
                            } else if (appType.equals("senior")) {
                                Toast.makeText(getApplicationContext(),
                                        "Sign in as Care Giver to edit settings First!",
                                        Toast.LENGTH_LONG).show();
                            } else { //user signed but undefined app type
                                callAuth();
                                Log.i(TAG, "2");
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            mDatabase.addValueEventListener(configListener);
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
        stopServices(this);
        disableServices(this);
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
            Intent shake = new Intent(this, ShakeService.class);
            startService(shake);
        }catch(Exception e){
            Log.e(TAG, "error Triggering Shake Service");
        }
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

    public static void stopServices(Context context){
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

    private void enableServices(){
        Context context = getApplicationContext();
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

    public static void disableServices(Context context) {
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

    public void signOut() {
        stopServices(this);
        disableServices(this);
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }

    public void callSettings(){
        Intent settings = new Intent(this, Settings.class);
        startActivity(settings);
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            mDatabase.removeEventListener(configListener);
        }catch(Exception e){
            Log.i(TAG, "unable to remove event Listener");
        }

    }

    public void permissions(){
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.SEND_SMS")
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{"android.permission.SEND_SMS"},
                        MY_PERMISSIONS_SEND_SMS);
        }
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    MY_PERMISSIONS_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.BODY_SENSORS")
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.BODY_SENSORS"},
                    MY_PERMISSIONS_BODY_SENSORS);
        }
    }
}
