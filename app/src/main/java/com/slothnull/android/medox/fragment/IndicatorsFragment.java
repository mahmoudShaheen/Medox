package com.slothnull.android.medox.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractData;
import com.slothnull.android.medox.Abstract.AbstractMessages;
import com.slothnull.android.medox.Abstract.AbstractWatchToken;
import com.slothnull.android.medox.R;

public class IndicatorsFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = "Indicators";
    public TextView pedo;
    public TextView heartRate;
    public TextView longitude;
    public TextView latitude;
    public static String watchToken;

    View view;
    public IndicatorsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_indicators, container, false);


        pedo = (TextView) view.findViewById(R.id.textPedo);
        heartRate = (TextView) view.findViewById(R.id.textHeartRate);
        longitude = (TextView) view.findViewById(R.id.textLongitude);
        latitude = (TextView) view.findViewById(R.id.textLatitude);

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //data
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

        ValueEventListener tokenListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractWatchToken token = dataSnapshot.getValue(AbstractWatchToken.class);
                if (token != null) {
                    watchToken = token.watch;
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
        mDatabase.child("users").child(UID).child("token")
                .addValueEventListener(tokenListener);
        //add to list here
        return view;
    }

    //TODO: auto Refresh
    public void refreshData(){ //sendRefreshRequest
        /*String cmd = "data";
        //send command to database for raspberry to fetch
        AbstractCommand command = new AbstractCommand(cmd);
        //TODO: add if (UID != null) to all classes
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("watchCommand").push();
        mDatabase.setValue(command);*/
        String level = "5";
        AbstractMessages data = new AbstractMessages(watchToken, level);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("messages").push();
        mDatabase.setValue(data);
    }


    @Override
    public void onClick(View view) {
        //do what you want to do when button is clicked
        switch (view.getId()) {
            case R.id.refreshData:
                refreshData();
                break;
        }
    }

}
