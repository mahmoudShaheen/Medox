package com.slothnull.android.medox.fragment;

/**
 * Created by Mahmoud Shaheen
 * Project: Medox
 * Licence: MIT
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.slothnull.android.medox.model.AbstractCommand;
import com.slothnull.android.medox.model.AbstractConfig;
import com.slothnull.android.medox.model.AbstractData;
import com.slothnull.android.medox.model.AbstractWarehouse;
import com.slothnull.android.medox.R;

import java.util.ArrayList;
import java.util.List;

public class WarehouseFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "WarehouseFragment";

    View view;
    AbstractWarehouse warehouse;

    public TextView drug1;
    public TextView drug2;
    public TextView drug3;
    public TextView drug4;

    public EditText drug1Picker;
    public EditText drug2Picker;
    public EditText drug3Picker;
    public EditText drug4Picker;

    public EditText drug1Edit;
    public EditText drug2Edit;
    public EditText drug3Edit;
    public EditText drug4Edit;

    private FloatingActionButton addData;
    private FloatingActionButton updateNames;

    private AlertDialog.Builder addDataBuilder;
    private AlertDialog.Builder updateNamesBuilder;

    public WarehouseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_warehouse, container, false);
        getConfig(); //enable or disable items

        drug1 = (TextView) view.findViewById(R.id.drug1View);
        drug2 = (TextView) view.findViewById(R.id.drug2View);
        drug3 = (TextView) view.findViewById(R.id.drug3View);
        drug4 = (TextView) view.findViewById(R.id.drug4View);

        drug1Picker = (EditText) view.findViewById(R.id.drug1Picker);
        drug2Picker = (EditText) view.findViewById(R.id.drug2Picker);
        drug3Picker = (EditText) view.findViewById(R.id.drug3Picker);
        drug4Picker = (EditText) view.findViewById(R.id.drug4Picker);

        drug1Edit = (EditText) view.findViewById(R.id.drug1Edit);
        drug2Edit = (EditText) view.findViewById(R.id.drug2Edit);
        drug3Edit = (EditText) view.findViewById(R.id.drug3Edit);
        drug4Edit = (EditText) view.findViewById(R.id.drug4Edit);

        addData = (FloatingActionButton) view.findViewById(R.id.addData);
        addData.setOnClickListener(this);
        updateNames = (FloatingActionButton) view.findViewById(R.id.updateNames);
        updateNames.setOnClickListener(this);

        buildCheckDialog();
        getNames();
        getBillCount();
        return view;
    }

    public void getBillCount(){

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractData data = dataSnapshot.getValue(AbstractData.class);
                if(data != null) {
                    if (data.billCount != null) {
                        String[] billArray = data.billCount.split(",");
                        drug1Picker.setHint(billArray[0]);
                        drug2Picker.setHint(billArray[1]);
                        drug3Picker.setHint(billArray[2]);
                        drug4Picker.setHint(billArray[3]);
                        // ...
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

        String drug1Name = drug1Edit.getText().toString();
        String drug2Name = drug2Edit.getText().toString();
        String drug3Name = drug3Edit.getText().toString();
        String drug4Name = drug4Edit.getText().toString();

        if (drug1Name.isEmpty())
            drug1Name = drug1.getText().toString();
        if (drug2Name.isEmpty())
            drug2Name = drug2.getText().toString();
        if (drug3Name.isEmpty())
            drug3Name = drug3.getText().toString();
        if (drug4Name.isEmpty())
            drug4Name = drug4.getText().toString();

        List<AbstractWarehouse> warehouse = new ArrayList<>();
        warehouse.add(new AbstractWarehouse("1",drug1Name));
        warehouse.add(new AbstractWarehouse("2",drug2Name));
        warehouse.add(new AbstractWarehouse("3",drug3Name));
        warehouse.add(new AbstractWarehouse("4",drug4Name));

        for (int i=0;i<4;i++) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(UID).child("warehouse").push();
            mDatabase.setValue(warehouse.get(i));
        }
    }

    public void addData() { //sendRefreshRequest
        //send command to database for raspberry to fetch
        String cmd;
        String bills = "";
        cmd = "addBills,";
        bills += ((TextView)view.findViewById(R.id.drug1Picker)).getText();
        cmd += getBills(bills);
        cmd += ",";
        bills = "";
        bills +=((TextView)view.findViewById(R.id.drug2Picker)).getText();
        cmd += getBills(bills);
        cmd += ",";
        bills = "";
        bills += ((TextView)view.findViewById(R.id.drug3Picker)).getText();
        cmd += getBills(bills);
        cmd += ",";
        bills = "";
        bills += ((TextView)view.findViewById(R.id.drug4Picker)).getText();
        cmd += getBills(bills);

        AbstractCommand command = new AbstractCommand(cmd);
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("command").push();
        mDatabase.setValue(command);
    }

    @Override
    public void onClick(View view) {
        //do what you want to do when button is clicked
        switch (view.getId()) {
            case R.id.updateNames:
                updateNamesBuilder.show();
                break;
            case R.id.addData:
                addDataBuilder.show();
                break;
        }
    }
    private String getBills(String bills){
        if (bills.isEmpty()){
            bills = "0";
        }
        return bills;
    }


    private void buildCheckDialog(){
       addDataBuilder = new AlertDialog.Builder(getActivity());
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        addData();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        addDataBuilder.setMessage("New Bills count will be sent to Box, Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);

        updateNamesBuilder = new AlertDialog.Builder(getActivity());
        DialogInterface.OnClickListener dialog2ClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        updateNames();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        updateNamesBuilder.setMessage("Names will be updated, Are you sure?")
                .setPositiveButton("Yes", dialog2ClickListener)
                .setNegativeButton("No", dialog2ClickListener);
    }
    public void getConfig() {
        //disable only for senior
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                getActivity().getPackageName(), Context.MODE_PRIVATE);
        final String appType = sharedPreferences.getString("appType", "");
        if(appType.equals("care"))
            return;
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractConfig oldConfig = dataSnapshot.getValue(AbstractConfig.class);
                if(oldConfig != null) {
                    if (oldConfig.enabled != null) {
                        String[] checkArray = new String[3];
                        checkArray = oldConfig.enabled.split(",");
                        if (checkArray[1].equals("0")) { //warehouse
                            drug1Picker.setEnabled(false);
                            drug2Picker.setEnabled(false);
                            drug3Picker.setEnabled(false);
                            drug4Picker.setEnabled(false);
                            drug1Edit.setEnabled(false);
                            drug2Edit.setEnabled(false);
                            drug3Edit.setEnabled(false);
                            drug4Edit.setEnabled(false);
                            addData.setEnabled(false);
                            updateNames.setEnabled(false);
                        }
                        if (checkArray[1].equals("1")) { //warehouse
                            drug1Picker.setEnabled(true);
                            drug2Picker.setEnabled(true);
                            drug3Picker.setEnabled(true);
                            drug4Picker.setEnabled(true);
                            drug1Edit.setEnabled(true);
                            drug2Edit.setEnabled(true);
                            drug3Edit.setEnabled(true);
                            drug4Edit.setEnabled(true);
                            addData.setEnabled(true);
                            updateNames.setEnabled(true);
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
        mDatabase.child("users").child(UID).child("config")
                .addValueEventListener(configListener);
    }
}
