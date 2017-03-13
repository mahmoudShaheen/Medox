package com.slothnull.android.medox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Warehouse extends Activity {

    private static final String TAG = "Warehouse";
    public TextView billCount;
    public TextView billArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);
        billCount = (TextView) findViewById(R.id.textBillCount);
        billArray = (TextView) findViewById(R.id.textBillArray);

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractData data = dataSnapshot.getValue(AbstractData.class);
                if (data != null) {
                    billCount.setText(data.billCount);
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

    //TODO: add warehouse class has drug names and a way to change it
    public void addData(View view) { //sendRefreshRequest
        //send command to database for raspberry to fetch
        String cmd;
        if (billArray.getText() != null) {
            cmd = "addBills," + billArray.getText();
            AbstractCommand command = new AbstractCommand(cmd);
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(UID).child("command").push();
            mDatabase.setValue(command);
        }else{
            //no data added
        }
    }
}