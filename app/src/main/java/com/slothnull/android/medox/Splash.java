package com.slothnull.android.medox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

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
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }else if( appType.equals("senior") ){
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }else{
            callAuth();
        }

    }
    private void callAuth(){
        Intent intent = new Intent(this, Authentication.class);
        startActivity(intent);
        finish();
    }
}
