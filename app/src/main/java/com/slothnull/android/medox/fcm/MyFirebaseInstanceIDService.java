package com.slothnull.android.medox.fcm;

/**
 * Created by Shaheen on 10-Mar-17
 * Project: Medox
 * Package: com.slothnull.android.medox
 */

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    private DatabaseReference mDatabase;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if(auth == null){
            return;
        }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TO DO: Implement this method to send token to your app server.
        //send value to FDB in users/UID/mobileToken
        SharedPreferences sharedPreferences;
        sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String appType = sharedPreferences.getString("appType","");

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if(auth != null){
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            if (appType.equals("care")){
                mDatabase.child("users").child(UID).child("token").child("mobile").setValue(token);
            }else if( appType.equals("senior") ){
                mDatabase.child("users").child(UID).child("token").child("watch").setValue(token);
            }else{
                Log.e(TAG, "error Sending Token: undefined appType");
            }
        }else{
            Log.i(TAG,"user must be signed to update token");
            Log.i(TAG,"Don't worry Token will be added when user sign in");
        }
    }
}
