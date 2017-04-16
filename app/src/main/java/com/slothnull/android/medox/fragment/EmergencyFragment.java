package com.slothnull.android.medox.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.slothnull.android.medox.Abstract.AbstractCommand;
import com.slothnull.android.medox.R;

//TODO: add other commands
public class EmergencyFragment extends Fragment implements View.OnClickListener {

    private AlertDialog.Builder builder ;
    private String cmd;
    View view;

    public EmergencyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_emergency, container, false);


        builder = new AlertDialog.Builder(getActivity());
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        sendCommand(cmd);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
    return view;
    }

    public void sendCommand(String cmd){
        //send command to database for raspberry to fetch
        AbstractCommand command = new AbstractCommand(cmd);
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("command").push();
        mDatabase.setValue(command);
    }
    //TODO: Convert to Radio buttons or list and one button
    @Override
    public void onClick(View view) {
        //do what you want to do when button is clicked
        Button button = (Button)view;
        cmd = (String) button.getText();
        builder.show();
    }
}
