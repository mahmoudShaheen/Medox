package com.slothnull.android.medox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class Splash extends Activity {

    private static final String TAG = "SplashActivity";

    DatabaseReference mDatabase;
    ValueEventListener configListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //add image to image view as no image showing using xml ref.
        ImageView logoView = (ImageView) findViewById(R.id.logoView);
        logoView.setImageResource(R.drawable.logo);

        //get permissions for marshmallow users
        BasicHelper.permissions(this, this);

        //check if user is already signed-in or not
        if (!BasicHelper.isAuth()) { //if not call Authentication Activity
            callAuth();
        } else {//if user signed in
            //get app type and go to corresponding Activity
            //Also checks if account is configured or not
            final String appType = BasicHelper.getAppType(this);
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
        BasicHelper.signOut(this);
        //hideProgressDialog();
        Intent intent = new Intent(this, Authentication.class);
        startActivity(intent);
        finish();
    }

    //call Care giver Home activity and disable/stop services
    private void callCare(){
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        BasicHelper.stopServices(this);
        BasicHelper.disableServices(this);
        finish();
    }

    //calls Senior citizen Home activity and trigger/enable services
    private void callSenior(){
        Intent intent = new Intent(this, SeniorHome.class);
        startActivity(intent);
        BasicHelper.enableServices(this);
        BasicHelper.triggerServices(this);
        finish();
    }

    //call settings Activity to complete account configuration
    public void callSettings(){
        Intent settings = new Intent(this, Settings.class);
        startActivity(settings);
        finish();
    }

    @Override //remove event listener
    public void onDestroy(){
        super.onDestroy();
        try{
            mDatabase.removeEventListener(configListener);
        }catch(Exception e){
            Log.i(TAG, "unable to remove event Listener");
        }
    }
}
