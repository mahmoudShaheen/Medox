package com.slothnull.android.medox.fragment;

/**
 * Created by Mahmoud Shaheen
 * Project: Medox
 * Licence: MIT
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.slothnull.android.medox.Home;
import com.slothnull.android.medox.R;
import com.slothnull.android.medox.SeniorHome;
import com.slothnull.android.medox.model.AbstractStatus;

public class StatusFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "StatusFragment";

    View view;
    String appType;
    FloatingActionButton notificationButton;
    FloatingActionButton emergencyButton;

    private TextView stateHeart;
    private TextView stateLocation;
    private TextView stateEmergency;
    private TextView stateBills;


    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_status, container, false);

        notificationButton = (FloatingActionButton) view.findViewById(R.id.buttonNotification);
        notificationButton.setOnClickListener(this);
        emergencyButton = (FloatingActionButton) view.findViewById(R.id.buttonEmergency);
        emergencyButton.setOnClickListener(this);

        stateHeart = (TextView) view.findViewById(R.id.stateHeart);
        stateLocation = (TextView) view.findViewById(R.id.stateLocation);
        stateEmergency = (TextView) view.findViewById(R.id.stateEmergency);
        stateBills = (TextView) view.findViewById(R.id.stateBills);

        getStatus();
        getAppType();

        return view;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        if (v == notificationButton){
            Log.i(TAG, "Notification button pressed");
            if(appType.equals("care")){
                Intent intent = new Intent(getActivity(), Home.class);
                intent.putExtra("position", 2);
                startActivity(intent);
            }else if(appType.equals("senior")){
                Intent intent = new Intent(getActivity(), SeniorHome.class);
                intent.putExtra("position", 2);
                startActivity(intent);
            }
        }else if(v == emergencyButton){
            if(appType.equals("care")){
                Intent intent = new Intent(getActivity(), Home.class);
                intent.putExtra("position", 5);
                startActivity(intent);
            }else if(appType.equals("senior")){
                Intent intent = new Intent(getActivity(), SeniorHome.class);
                intent.putExtra("position", 5);
                startActivity(intent);
            }
        }else{
            return;
        }
        getActivity().finish();
    }

    public void getAppType(){
        SharedPreferences sharedPreferences =
                getActivity().getSharedPreferences(getActivity().getPackageName()
                        , Context.MODE_PRIVATE);
        appType = sharedPreferences.getString("appType", "");
        Log.i(TAG, appType);
    }

    public void getStatus(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener statusListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UID
                AbstractStatus status = dataSnapshot.getValue(AbstractStatus.class);
                if (status != null) {
                    if(status.heart != null){
                        if(status.heart.equals("ok")){
                            stateHeart.setText("Ok");
                            stateHeart.setTextColor(ContextCompat.getColor(getActivity()
                                    , R.color.colorPrimary));
                        }else if(status.heart.equals("fail")){
                            stateHeart.setText("Fail");
                            stateHeart.setTextColor(ContextCompat.getColor(getActivity()
                                    , R.color.colorRed));
                        }
                    }
                    if(status.location != null){
                        if(status.location.equals("ok")){
                            stateLocation.setText("Ok");
                            stateLocation.setTextColor(ContextCompat.getColor(getActivity()
                                    , R.color.colorPrimary));
                        }else if(status.location.equals("fail")){
                            stateLocation.setText("Fail");
                            stateLocation.setTextColor(ContextCompat.getColor(getActivity()
                                    , R.color.colorRed));
                        }
                    }
                    if(status.emergency != null){
                        if(status.emergency.equals("ok")){
                            stateEmergency.setText("Ok");
                            stateEmergency.setTextColor(ContextCompat.getColor(getActivity()
                                    , R.color.colorPrimary));
                        }else if(status.emergency.equals("fail")){
                            stateEmergency.setText("Fail");
                            stateEmergency.setTextColor(ContextCompat.getColor(getActivity()
                                    , R.color.colorRed));
                        }
                    }
                    if(status.bills != null){
                        if(status.bills.equals("ok")){
                            stateBills.setText("Ok");
                            stateBills.setTextColor(ContextCompat.getColor(getActivity()
                                    , R.color.colorPrimary));
                        }else if(status.bills.equals("fail")){
                            stateBills.setText("Fail");
                            stateBills.setTextColor(ContextCompat.getColor(getActivity()
                                    , R.color.colorRed));
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("users").child(UID).child("status")
                .addValueEventListener(statusListener);
        //add to list here

    }
}
