package com.slothnull.android.medox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractUser;
import com.slothnull.android.medox.service.IndicatorsService;
import com.slothnull.android.medox.service.LocationService;

public class Splash extends Activity {
    String TAG = "SplashActivity";
    DatabaseReference mDatabase;
    ValueEventListener configListener;
    //TODO: ask for permissions
    //https://developer.android.com/training/permissions/requesting.html
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        //check if user is already signed-in or not
        //if not call Authentication Activity

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth == null) {
            callAuth();
            Log.i(TAG, "1");
        } else {
            //if user signed in
            //get app type
            showProgressDialog();
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
                    if(configured != null && configured.equals("true")){
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
        hideProgressDialog();
        Intent intent = new Intent(this, Authentication.class);
        startActivity(intent);
        finish();
    }
    private void callCare(){
        hideProgressDialog();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        stopServices();
        disableServices();
        finish();
    }
    private void callSenior(){
        hideProgressDialog();
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
    public void callSettings(){
        hideProgressDialog();
        Intent settings = new Intent(this, Settings.class);
        startActivity(settings);
        finish();
    }

    //progress dialog to wait for saved data
    private ProgressDialog mProgressDialog;
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mDatabase.removeEventListener(configListener);
    }
}
