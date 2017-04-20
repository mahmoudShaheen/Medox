package com.slothnull.android.medox.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractCommand;
import com.slothnull.android.medox.Abstract.AbstractEmergency;
import com.slothnull.android.medox.Abstract.AbstractMobileToken;
import com.slothnull.android.medox.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeniorEmergencyFragment extends Fragment {

    private static final String TAG = "Emergency";
    public static String mobileToken;
    View view;

    public SeniorEmergencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view  = inflater.inflate(R.layout.fragment_senior_emergency, container, false);

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractMobileToken token = dataSnapshot.getValue(AbstractMobileToken.class);
                if (token != null) {
                    mobileToken = token.mobile;
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
                .addValueEventListener(dataListener);
        //add to list here

        return view;
    }

    public void sendEmergency(View view){
        String title = "Emergency Button pressed From Watch!";
        String message = "Action Required IMMEDIATELY !!!!";
        emergencyNotification(title, message);
    }

    public static void emergencyNotification(String title, String message){
        //TODO: get response from mobile to know if message is opened or not
        if (mobileToken != null){
            String token = mobileToken;
            String level = "1";

            AbstractEmergency emergency = new AbstractEmergency(token, level, title, message);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("messages").push();
            mDatabase.setValue(emergency);
        }else{
            //TODO: add another way here for emergency
        }
    }

    public void sendDoor(View view){
        //send command to database for raspberry to fetch
        AbstractCommand command = new AbstractCommand("openDoor");
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("command").push();
        mDatabase.setValue(command);
    }

    public void sendWarehouse(View view){
        //send command to database for raspberry to fetch
        AbstractCommand command = new AbstractCommand("openWarehouse");
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("command").push();
        mDatabase.setValue(command);
    }


}
