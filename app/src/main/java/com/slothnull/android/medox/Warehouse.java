package com.slothnull.android.medox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Warehouse extends Activity {

    private static final String TAG = "Warehouse";
    public TextView billCount;
    public TextView billArray;

    public EditText drug1;
    public EditText drug2;
    public EditText drug3;
    public EditText drug4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);
        billCount = (TextView) findViewById(R.id.textBillCount);
        billArray = (TextView) findViewById(R.id.textBillArray);

        drug1 = (EditText) findViewById(R.id.editText1);
        drug2 = (EditText) findViewById(R.id.editText2);
        drug3 = (EditText) findViewById(R.id.editText3);
        drug4 = (EditText) findViewById(R.id.editText4);

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
            cmd = "addBills," + billArray.getText().toString();
            AbstractCommand command = new AbstractCommand(cmd);
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(UID).child("command").push();
            mDatabase.setValue(command);
        }else{
            //no data added
        }
    }

    public void getNames(){

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractWarehouse warehouse = dataSnapshot.getValue(AbstractWarehouse.class);
                if (warehouse != null) {
                    switch (warehouse.id) {
                        case"1":
                            drug1.setText(warehouse.name);
                            break;
                        case"2":
                            drug2.setText(warehouse.name);
                            break;
                        case"3":
                            drug3.setText(warehouse.name);
                            break;
                        case"4":
                            drug4.setText(warehouse.name);
                            break;
                        default:
                            break;
                    }

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

    public void updateNames(View view){
        //clear class first
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("warehouse");
        cDatabase.setValue(null);

        List<AbstractWarehouse> warehouse = new ArrayList<>();
        warehouse.add(new AbstractWarehouse("1",drug1.getText().toString()));
        warehouse.add(new AbstractWarehouse("2",drug2.getText().toString()));
        warehouse.add(new AbstractWarehouse("3",drug3.getText().toString()));
        warehouse.add(new AbstractWarehouse("4",drug4.getText().toString()));

        for (int i=0;i<4;i++) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(UID).child("warehouse").push();
            mDatabase.setValue(warehouse.get(i));
        }
    }
}