package com.slothnull.android.medox;

/**
 * Created by Mahmoud Shaheen
 * Project: Medox
 * Licence: MIT
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.model.AbstractProfile;

public class MedicalProfile extends AppCompatActivity {


    private static final String TAG = "ProfileActivity";

    private TextView name;
    private TextView sex;
    private TextView birth;
    private TextView height;
    private TextView weight;
    private TextView blood;
    private TextView address;
    private TextView phone;
    private TextView emergency;
    private TextView martial;
    private TextView diseases;
    private TextView allergic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_profile);

        name = (TextView) findViewById(R.id.name);
        sex = (TextView) findViewById(R.id.sex);
        birth = (TextView) findViewById(R.id.birth);
        height = (TextView) findViewById(R.id.height);
        weight = (TextView) findViewById(R.id.weight);
        blood = (TextView) findViewById(R.id.blood);
        address = (TextView) findViewById(R.id.address);
        phone = (TextView) findViewById(R.id.phone);
        emergency = (TextView) findViewById(R.id.emergency);
        martial = (TextView) findViewById(R.id.martial);
        diseases = (TextView) findViewById(R.id.diseases);
        allergic = (TextView) findViewById(R.id.allergic);



        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //data
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractProfile profile = dataSnapshot.getValue(AbstractProfile.class);
                if (profile != null) {
                    if (profile.name != null)
                        name.setText("Name: " + profile.name);
                    if (profile.sex != null)
                        sex.setText("Sex: " + profile.sex);
                    if (profile.birth != null)
                        birth.setText("Birth Date: " + profile.birth);
                    if (profile.height != null)
                        height.setText("Height: " + profile.height);
                    if (profile.weight != null)
                        weight.setText("Weight: " + profile.weight);
                    if (profile.blood != null)
                        blood.setText("Blood Type: " + profile.blood);
                    if (profile.address != null)
                        address.setText("Address: " + profile.address);
                    if (profile.phone != null)
                        phone.setText("Phonr: " + profile.phone);
                    if (profile.emergency != null)
                        emergency.setText("Emergency Contact: " + profile.emergency);
                    if (profile.martial != null)
                        martial.setText("Martial Status: " + profile.martial);
                    if (profile.diseases != null)
                        diseases.setText("Diseases: " + profile.diseases);
                    if (profile.allergic != null)
                        allergic.setText("Allergic To: " + profile.allergic);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("users").child(UID).child("profile")
                .addValueEventListener(dataListener);

    }
}
