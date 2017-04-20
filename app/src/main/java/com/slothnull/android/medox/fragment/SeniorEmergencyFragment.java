package com.slothnull.android.medox.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.slothnull.android.medox.Abstract.AbstractEmergency;
import com.slothnull.android.medox.Abstract.AbstractMobileToken;
import com.slothnull.android.medox.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeniorEmergencyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Emergency";
    public static String mobileToken;

    private AlertDialog.Builder builder ;
    private String cmd = "";
    FloatingActionButton sendButton;
    FloatingActionButton EmergencyButton;
    private RadioGroup radioGroup;
    private TableLayout tableLayout;
    View view;

    public SeniorEmergencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view  = inflater.inflate(R.layout.fragment_senior_emergency, container, false);


        sendButton = (FloatingActionButton) view.findViewById(R.id.sendCommand);
        sendButton.setOnClickListener(this);

        EmergencyButton = (FloatingActionButton) view.findViewById(R.id.EmergencyButton);
        EmergencyButton.setOnClickListener(this);

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
    //TODO: Convert to Radio buttons or list and one button
    @Override
    public void onClick(View v) {

        if (v == EmergencyButton){
            sendEmergency();
            return;
        }

        //do what you want to do when button is clicked
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
                cmd = "dispense";
                cmd += ",";
                cmd += ((TextView)view.findViewById(R.id.drug1Picker)).getText();
                cmd += ",";
                cmd += ((TextView)view.findViewById(R.id.drug2Picker)).getText();
                cmd += ",";
                cmd += ((TextView)view.findViewById(R.id.drug3Picker)).getText();
                cmd += ",";
                cmd += ((TextView)view.findViewById(R.id.drug4Picker)).getText();
                break;
            default:
                break;
        }
        builder.show();
    }

    public void sendEmergency(){
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

}