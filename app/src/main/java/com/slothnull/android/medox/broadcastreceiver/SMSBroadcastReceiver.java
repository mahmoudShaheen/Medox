package com.slothnull.android.medox.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                smsReader(bundle);
            } else { //user not signed or Senior type or undefined app type
                return;
            }
        }
    }
    public void smsReader(Bundle bundle){
        if (bundle != null) {
            final Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdusObj.length; i++) {
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                String message = currentMessage.getDisplayMessageBody();
                Log.i(TAG, "senderNum: "+ phoneNumber + "; message: " + message);
            }
        }
    }
}
