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
import com.slothnull.android.medox.model.AbstractSensor;
import com.slothnull.android.medox.R;

public class ControlFragment extends Fragment {

    private static final String TAG = "IndicatorsFragment";

    public TextView temperature;
    public TextView light;

    View view;
    public ControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_indicators, container, false);


        temperature = (TextView) view.findViewById(R.id.textTemperature);
        light = (TextView) view.findViewById(R.id.textLight);

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //data
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractSensor sensor = dataSnapshot.getValue(AbstractSensor.class);
                if (sensor != null) {
                    if (sensor.light != null)
                        light.setText(sensor.light);
                    if (sensor.temperature != null)
                        temperature.setText(sensor.temperature);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("users").child(UID).child("iot").child("data").child("sensor")
                .addValueEventListener(dataListener);
        return view;
    }
}
