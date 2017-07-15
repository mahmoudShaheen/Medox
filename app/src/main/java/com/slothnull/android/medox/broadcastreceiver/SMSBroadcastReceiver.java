package com.slothnull.android.medox.broadcastreceiver;

/**
 * Created by Mahmoud Shaheen
 * Project: Medox
 * Licence: MIT
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.slothnull.android.medox.EmergencyNotification;
import com.slothnull.android.medox.R;

public class SMSBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Broadcast received");
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        //get current user
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        if (auth != null){ //user is signed in
            final String appType = sharedPreferences.getString("appType", "");
            Log.i(TAG, appType);
            if (appType.equals("care")) {
                smsReader(bundle, context);
            } else { //user not signed or Senior type or undefined app type
                return;
            }
        }
    }
    public void smsReader(Bundle bundle, Context context){
        if (bundle != null) {
            final Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdusObj.length; i++) {
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                String message = currentMessage.getDisplayMessageBody();
                Log.i(TAG, "senderNum: "+ phoneNumber + "; message: " + message);
                if(message.equals("EmergencySMS")){
                    sendNotification(context);
                }
            }
        }
    }
    public void sendNotification(Context context){
        Intent intent = new Intent(context, EmergencyNotification.class);
        updateStatus("fail");

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1444 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri =
                Uri.parse("android.resource://"+context.getApplicationContext().getPackageName()
                        +"/"+R.raw.emergency);//Here is FILE_NAME is the name of file that you want to play

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.logo))
                .setContentTitle("Emergency From Senior!")
                .setContentText("Action Required IMMEDIATELY !!!!")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1444 /* ID of notification */, notificationBuilder.build());
    }
    public void updateStatus(String state){
        //if user not signed in stop service
        Log.i(TAG, "updating status: " + state);
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if(auth == null){
            return;
        }
        //send data to db
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("users").child(UID).child("status").child("emergency")
                .setValue(state);
    }
}
