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
import com.slothnull.android.medox.model.AbstractData;
import com.slothnull.android.medox.R;

public class IndicatorsFragment extends Fragment {

    private static final String TAG = "IndicatorsFragment";

    public TextView pedo;
    public TextView heartRate;

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

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //data
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractData data = dataSnapshot.getValue(AbstractData.class);
                if (data != null) {
                    if (data.pedo != null)
                        pedo.setText(data.pedo);
                    if (data.heartRate != null)
                        heartRate.setText(data.heartRate);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("users").child(UID).child("data")
                .addValueEventListener(dataListener);
        return view;
    }
}
