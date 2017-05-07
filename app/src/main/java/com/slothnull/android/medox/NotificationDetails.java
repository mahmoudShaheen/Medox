package com.slothnull.android.medox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractNotification;

public class NotificationDetails extends AppCompatActivity {

    private static final String TAG = "NotificationDetailsActivity";

    public static final String EXTRA_NOTIFICATION_KEY = "notification_key";

    private DatabaseReference notificationReference;
    private ValueEventListener notificationListener;
    private String notificationKey;

    private TextView timeView;
    private TextView mTitleView;
    private TextView mBodyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);

        // Get post key from intent
        notificationKey = getIntent().getStringExtra(EXTRA_NOTIFICATION_KEY);
        if (notificationKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        notificationReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUID()).child("notification").child(notificationKey);

        // Initialize Views
        timeView = (TextView) findViewById(R.id.timeView);
        mTitleView = (TextView) findViewById(R.id.titleView);
        mBodyView = (TextView) findViewById(R.id.messageView);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractNotification notification = dataSnapshot.getValue(AbstractNotification.class);
                // [START_EXCLUDE]
                timeView.setText(notification.time);
                mTitleView.setText(notification.title);
                mBodyView.setText(notification.message);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(NotificationDetails.this, "Failed to load Notification.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        notificationReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        notificationListener = postListener;

    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (notificationListener != null) {
            notificationReference.removeEventListener(notificationListener);
        }
    }
    private String getUID(){
       return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public void callEmergency(View v){
        Intent intent;
        SharedPreferences sharedPreferences;
        sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String appType = sharedPreferences.getString("appType","");

        if (appType.equals("care")){
            intent = new Intent(this, Home.class);
        }else if( appType.equals("senior") ){
            intent = new Intent(this, SeniorHome.class);
        }else{
            Log.e(TAG, "error Sending Notification: undefined appType");
            return;
        }
        intent.putExtra("position", 5);
        startActivity(intent);
    }
}
