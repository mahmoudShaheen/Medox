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
    public Switch switch3;
    public Switch switch4;
    public Switch switch5;
    public Switch switch6;
    public Switch switch7;
    public Switch switch8;

    private boolean atStart = true; //to avoid updating db onStart "when first receive data"

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
                        light.setText(sensor.light + " lx");
                    if (sensor.temperature != null)
                        temperature.setText(sensor.temperature + " C");
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
        switch3 = (Switch) view.findViewById(R.id.switch3);
        switch4 = (Switch) view.findViewById(R.id.switch4);
        switch5 = (Switch) view.findViewById(R.id.switch5);
        switch6 = (Switch) view.findViewById(R.id.switch6);
        switch7 = (Switch) view.findViewById(R.id.switch7);
        switch8 = (Switch) view.findViewById(R.id.switch8);

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
                    if (abstractSwitch.switch3 != null){
                        if(!abstractSwitch.switch3.isEmpty()){
                            if(abstractSwitch.switch3.equals("on"))
                                switch3.setChecked(true);
                        }
                    }
                    if (abstractSwitch.switch4 != null){
                        if(!abstractSwitch.switch4.isEmpty()){
                            if(abstractSwitch.switch4.equals("on"))
                                switch4.setChecked(true);
                        }
                    }
                    if (abstractSwitch.switch5 != null){
                        if(!abstractSwitch.switch5.isEmpty()){
                            if(abstractSwitch.switch5.equals("on"))
                                switch5.setChecked(true);
                        }
                    }
                    if (abstractSwitch.switch6 != null){
                        if(!abstractSwitch.switch6.isEmpty()){
                            if(abstractSwitch.switch6.equals("on"))
                                switch6.setChecked(true);
                        }
                    }
                    if (abstractSwitch.switch7 != null){
                        if(!abstractSwitch.switch7.isEmpty()){
                            if(abstractSwitch.switch7.equals("on"))
                                switch7.setChecked(true);
                        }
                    }
                    if (abstractSwitch.switch8 != null){
                        if(!abstractSwitch.switch8.isEmpty()){
                            if(abstractSwitch.switch8.equals("on"))
                                switch8.setChecked(true);
                        }
                    }
                    atStart = false;
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
                if(!atStart)
                    updateChecked();
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!atStart)
                    updateChecked();
            }
        });
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!atStart)
                    updateChecked();
            }
        });
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!atStart)
                    updateChecked();
            }
        });
        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!atStart)
                    updateChecked();
            }
        });
        switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!atStart)
                    updateChecked();
            }
        });
        switch7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!atStart)
                    updateChecked();
            }
        });
        switch8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!atStart)
                    updateChecked();
            }
        });

        return view;
    }
    public void updateChecked(){
        String state1 = "off";
        String state2 = "off";
        String state3 = "off";
        String state4 = "off";
        String state5 = "off";
        String state6 = "off";
        String state7 = "off";
        String state8 = "off";
        if(switch1.isChecked())
            state1 = "on";
        if(switch2.isChecked())
            state2 = "on";
        if(switch3.isChecked())
            state3 = "on";
        if(switch4.isChecked())
            state4 = "on";
        if(switch5.isChecked())
            state5 = "on";
        if(switch6.isChecked())
            state6 = "on";
        if(switch7.isChecked())
            state7 = "on";
        if(switch8.isChecked())
            state8 = "on";
        //send to db
        AbstractSwitch abstractSwitch = new AbstractSwitch(state1, state2, state3, state4, state5, state6, state7, state8);
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("iot").child("switch");
        mDatabase.setValue(abstractSwitch);
    }
}
