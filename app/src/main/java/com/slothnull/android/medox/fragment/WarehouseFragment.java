package com.slothnull.android.medox.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractCommand;
import com.slothnull.android.medox.Abstract.AbstractData;
import com.slothnull.android.medox.Abstract.AbstractWarehouse;
import com.slothnull.android.medox.R;

import java.util.ArrayList;
import java.util.List;

public class WarehouseFragment extends Fragment implements View.OnClickListener {


    View view;
    private static final String TAG = "Warehouse";
    public TextView billCount;
    public TextView billArray;

    public EditText drug1;
    public EditText drug2;
    public EditText drug3;
    public EditText drug4;

    public WarehouseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_warehouse, container, false);

        billCount = (TextView) view.findViewById(R.id.textBillCount);
        billArray = (TextView) view.findViewById(R.id.textBillArray);

        drug1 = (EditText) view.findViewById(R.id.drug1);
        drug2 = (EditText) view.findViewById(R.id.drug2);
        drug3 = (EditText) view.findViewById(R.id.drug3);
        drug4 = (EditText) view.findViewById(R.id.drug4);
        return view;
    }

    //TODO: add warehouse class has drug names and a way to change it
    public void addData() { //sendRefreshRequest
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

    public void getBillCount(){

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

    public void getNames(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener warehouseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    AbstractWarehouse warehouse = child.getValue(AbstractWarehouse.class);
                    if (warehouse.id != null) {
                        switch (warehouse.id) {
                            case "1":
                                drug1.setText(warehouse.name);
                                break;
                            case "2":
                                drug2.setText(warehouse.name);
                                break;
                            case "3":
                                drug3.setText(warehouse.name);
                                break;
                            case "4":
                                drug4.setText(warehouse.name);
                                break;
                            default:
                                break;
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
        mDatabase.child("users").child(UID).child("warehouse")
                .addValueEventListener(warehouseListener);
        //add to list here

    }

    public void updateNames(){
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
    @Override
    public void onClick(View view) {
        //do what you want to do when button is clicked
        switch (view.getId()) {
            case R.id.updateNames:
                updateNames();
                break;
            case R.id.getNames:
                getNames();
                break;
            case R.id.getBillCount:
                getBillCount();
                break;
            case R.id.addData:
                addData();
                break;
        }
    }
}
