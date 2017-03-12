package com.slothnull.android.medox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Indicators extends Activity {

    private static final String TAG = "Indicators";
    public TextView pedo;
    public TextView heartRate;
    public TextView longitude;
    public TextView latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicators);

        pedo = (TextView) findViewById(R.id.textPedo);
        heartRate = (TextView) findViewById(R.id.textHeartRate);
        longitude = (TextView) findViewById(R.id.textLongitude);
        latitude = (TextView) findViewById(R.id.textLatitude);

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractData data = dataSnapshot.getValue(AbstractData.class);
                if (data != null) {
                    pedo.setText(data.pedo);
                    heartRate.setText(data.heartRate);
                    longitude.setText(data.longitude);
                    latitude.setText(data.latitude);
                    // ...
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("users").child(UID).child("data")
                .addValueEventListener(dataListener);
        //add to list here

    }

    public void refreshData(View view){ //sendRefreshRequest
        String cmd = "data";
        //send command to database for raspberry to fetch
        AbstractCommand command = new AbstractCommand(cmd);
        //TODO: add if (UID != null) to all classes
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("watchCommand").push();
        mDatabase.setValue(command);
    }

}
