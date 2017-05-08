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


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.slothnull.android.medox.Abstract.AbstractNotification;
import com.slothnull.android.medox.Home;
import com.slothnull.android.medox.R;
import com.slothnull.android.medox.SeniorHome;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TO DO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if(auth == null){
            return;
        }

        SharedPreferences sharedPreferences;
        sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String appType = sharedPreferences.getString("appType","");

        if (appType.equals("care")){
            onCareMessageReceived(remoteMessage);
        }else if( appType.equals("senior") ){
            onSeniorMessageReceived(remoteMessage);
        }else{
            Log.e(TAG, "error Sending Notification: undefined appType");
        }



        /*
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification("Notification Message", remoteMessage.getNotification().getBody(), '0');
        }*/

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    private void onCareMessageReceived(RemoteMessage remoteMessage) {
        Map<String,String> payload;
        int level;
        String message;
        String title;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            payload = new HashMap<>(remoteMessage.getData());
            level = Integer.parseInt(payload.get("level"));
            message = payload.get("message");
            title = payload.get("title");
            String time = DateFormat.getDateTimeInstance().format(new Date());

            //send notification to user
            sendCareNotification(title, message, level);

            //send notification to database to access it later in Notification Activity
            AbstractNotification notification = new AbstractNotification(String.valueOf(level), title, message, time);
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(UID).child("notification").push();
            mDatabase.setValue(notification);
        }
    }
    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendCareNotification(String title, String messageBody, int level) {
        Intent intent = new Intent(this, Home.class);
        if (level == 1){
            intent = new Intent(this, Home.class);
            intent.putExtra("position", 5);
        }else{
            intent.putExtra("position", 3);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, level /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.logo))
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(level /* ID of notification */, notificationBuilder.build());
    }

    private void onSeniorMessageReceived(RemoteMessage remoteMessage) {
        Map<String,String> payload;
        int level;
        String message;
        String title;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            payload = new HashMap<>(remoteMessage.getData());
            level = Integer.parseInt(payload.get("level"));
            message = payload.get("message");
            title = payload.get("title");

            //send notification to user
            sendSeniorNotification(title, message, level);

            String time = DateFormat.getDateTimeInstance().format(new Date());
            //send notification to database to access it later in Notification Activity
            AbstractNotification notification = new AbstractNotification(String.valueOf(level), title, message, time);
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(UID).child("watchNotification").push();
            mDatabase.setValue(notification);
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendSeniorNotification(String title, String messageBody, int level) {
        Intent intent = new Intent(this, SeniorHome.class);
        intent.putExtra("position", 5);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, level /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.logo))
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(level /* ID of notification */, notificationBuilder.build());
    }
}
