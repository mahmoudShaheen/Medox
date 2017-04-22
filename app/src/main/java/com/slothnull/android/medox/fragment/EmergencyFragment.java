package com.slothnull.android.medox.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.slothnull.android.medox.Abstract.AbstractCommand;
import com.slothnull.android.medox.R;

//TODO: add other commands
public class EmergencyFragment extends Fragment implements View.OnClickListener {

    private AlertDialog.Builder builder ;
    private String cmd = "";
    FloatingActionButton sendButton;
    private RadioGroup radioGroup;
    private TableLayout tableLayout;
    View view;

    public EmergencyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_emergency, container, false);

        sendButton = (FloatingActionButton) view.findViewById(R.id.sendCommand);
        sendButton.setOnClickListener(this);

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
    //TODO: Convert to Radio buttons or list and one button
    @Override
    public void onClick(View v) {
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
}
