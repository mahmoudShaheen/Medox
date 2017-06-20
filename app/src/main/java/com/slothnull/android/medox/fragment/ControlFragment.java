package com.slothnull.android.medox.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.model.AbstractSensor;
import com.slothnull.android.medox.R;
import com.slothnull.android.medox.model.AbstractSwitch;

public class ControlFragment extends Fragment {

    private static final String TAG = "IndicatorsFragment";

    public TextView temperature;
    public TextView light;

    public Switch switch1;
    public Switch switch2;

    View view;
    public ControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_control, container, false);


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
        mDatabase.child("users").child(UID).child("iot").child("sensor")
                .addValueEventListener(dataListener);

        //Switches
        switch1 = (Switch) view.findViewById(R.id.switch1);
        switch2 = (Switch) view.findViewById(R.id.switch2);

        //data
        ValueEventListener switchListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractSwitch abstractSwitch = dataSnapshot.getValue(AbstractSwitch.class);
                if (abstractSwitch != null) {
                    if (abstractSwitch.switch1 != null){
                        if(!abstractSwitch.switch1.isEmpty()){
                            if(abstractSwitch.switch1.equals("on"))
                                switch1.setChecked(true);
                        }
                    }
                    if (abstractSwitch.switch2 != null){
                        if(!abstractSwitch.switch2.isEmpty()){
                            if(abstractSwitch.switch2.equals("on"))
                                switch2.setChecked(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("users").child(UID).child("iot").child("switch")
                .addValueEventListener(switchListener);

        //switches on checked change listener
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateChecked();
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateChecked();
            }
        });

        return view;
    }
    public void updateChecked(){
        String state1 = "off";
        String state2 = "off";
        if(switch1.isChecked())
            state1 = "on";
        if(switch2.isChecked())
            state2 = "on";
        //send to db
        AbstractSwitch abstractSwitch = new AbstractSwitch(state1, state2);
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("iot").child("switch").push();
        mDatabase.setValue(abstractSwitch);
    }
}
