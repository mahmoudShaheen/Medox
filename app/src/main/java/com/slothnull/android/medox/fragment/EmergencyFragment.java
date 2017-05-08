package com.slothnull.android.medox.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractCommand;
import com.slothnull.android.medox.Abstract.AbstractConfig;
import com.slothnull.android.medox.Abstract.AbstractWarehouse;
import com.slothnull.android.medox.R;

public class EmergencyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "EmergencyFragment";

    private AlertDialog.Builder builder ;
    private String cmd = "";
    FloatingActionButton sendButton;
    FloatingActionButton callButton;
    private RadioGroup radioGroup;
    private TableLayout tableLayout;
    private AbstractConfig oldConfig;
    private String seniorSkype;
    View view;

    private TextView drug1;
    private TextView drug2;
    private TextView drug3;
    private TextView drug4;

    public EmergencyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_emergency, container, false);

        drug1 = (TextView) view.findViewById(R.id.drug1View);
        drug2 = (TextView) view.findViewById(R.id.drug2View);
        drug3 = (TextView) view.findViewById(R.id.drug3View);
        drug4 = (TextView) view.findViewById(R.id.drug4View);
        getConfig();
        getNames();

        sendButton = (FloatingActionButton) view.findViewById(R.id.sendCommand);
        sendButton.setOnClickListener(this);
        callButton = (FloatingActionButton) view.findViewById(R.id.callButton);
        callButton.setOnClickListener(this);

        buildCheckDialog();
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        tableLayout = (TableLayout) view.findViewById(R.id.tableLayout);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.dispense ){
                    tableLayout.setVisibility(View.VISIBLE);
                }else{
                    tableLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
    return view;
    }

    private void buildCheckDialog(){
        builder = new AlertDialog.Builder(getActivity());
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        sendCommand();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
                cmd = ""; //reset cmd to avoid error
            }
        };
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
    }

    public void sendCommand(){
        //send command to database for raspberry to fetch
        if(cmd.isEmpty())
            return;
        AbstractCommand command = new AbstractCommand(cmd);
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("command").push();
        mDatabase.setValue(command);
    }

    public static void skype(String number, Context ctx) {
        try {
            //Intent sky = new Intent("android.intent.action.CALL_PRIVILEGED");
            //the above line tries to create an intent for which the skype app doesn't supply public api

            Intent sky = new Intent("android.intent.action.VIEW");
            sky.setData(Uri.parse("skype:" + number));
            Log.d("UTILS", "tel:" + number);
            ctx.startActivity(sky);
        } catch (ActivityNotFoundException e) {
            Log.e("SKYPE CALL", "Skype failed", e);
        }

    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked

        if (v == callButton){
            Log.i(TAG, "call button pressed");
            skype(seniorSkype, getActivity());
            return;
        }

        int selectedId = radioGroup.getCheckedRadioButtonId();

        switch(selectedId){
            case (R.id.openDoor):
                cmd = "openDoor";
                break;
            case (R.id.openWarehouse):
                cmd = "openWarehouse";
                break;
            case (R.id.dispenseNext):
                cmd = "dispenseNext";
                break;
            case (R.id.forceUpdateTimetable):
                cmd = "forceUpdateTimetable";
                break;
            case (R.id.clearBills):
                cmd = "clearBills";
                break;
            case (R.id.clearTimetable):
                cmd = "clearTimetable";
                break;
            case (R.id.restartRPI):
                cmd = "restartRPI";
                break;
            case (R.id.dispense):
                String bills;
                cmd = "dispense,";
                bills = "";
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
                break;
            default:
                break;
        }
        builder.show();
    }

    private String getBills(String bills){
        if (bills.isEmpty()){
            bills = "0";
        }
        return bills;
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


    public void getConfig() {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                oldConfig = dataSnapshot.getValue(AbstractConfig.class);
                if (oldConfig.seniorSkype != null)
                    seniorSkype = oldConfig.seniorSkype;
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
