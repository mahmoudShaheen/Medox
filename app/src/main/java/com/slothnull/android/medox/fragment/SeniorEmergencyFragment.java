package com.slothnull.android.medox.fragment;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
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
import com.slothnull.android.medox.EmergencyNotification;
import com.slothnull.android.medox.model.AbstractCommand;
import com.slothnull.android.medox.model.AbstractConfig;
import com.slothnull.android.medox.model.AbstractNotification;
import com.slothnull.android.medox.model.AbstractWarehouse;
import com.slothnull.android.medox.R;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeniorEmergencyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SeniorEmergencyFragment";

    public static SharedPreferences sharedPreferences;

    private AlertDialog.Builder builder ;
    private String cmd = "";
    FloatingActionButton sendButton;
    FloatingActionButton EmergencyButton;
    FloatingActionButton callButton;
    private RadioGroup radioGroup;
    private TableLayout tableLayout;

    private AbstractConfig oldConfig;
    private static String mobileNumber;
    private static String mobileNumber2;
    private String careSkype;

    View view;

    private TextView drug1;
    private TextView drug2;
    private TextView drug3;
    private TextView drug4;

    public SeniorEmergencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view  = inflater.inflate(R.layout.fragment_senior_emergency, container, false);

        sharedPreferences = getActivity().getSharedPreferences(
                getActivity().getPackageName(), Context.MODE_PRIVATE);

        drug1 = (TextView) view.findViewById(R.id.drug1View);
        drug2 = (TextView) view.findViewById(R.id.drug2View);
        drug3 = (TextView) view.findViewById(R.id.drug3View);
        drug4 = (TextView) view.findViewById(R.id.drug4View);
        getConfig();
        getNames();


        sendButton = (FloatingActionButton) view.findViewById(R.id.sendCommand);
        sendButton.setOnClickListener(this);

        EmergencyButton = (FloatingActionButton) view.findViewById(R.id.EmergencyButton);
        EmergencyButton.setOnClickListener(this);

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

        if (v == EmergencyButton){
            sendEmergency();
            return;
        }
        if (v == callButton){
            Log.i(TAG, "call button pressed");
            skype(careSkype, getActivity());
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

    public void sendEmergency(){
        String title = "Emergency Button pressed From Watch!";
        String message = "Action Required IMMEDIATELY !!!!";
        emergencyNotification(title, message);
    }

    private void sendSeniorNotification(String title, String messageBody, int level) {
        Intent intent = new Intent(getActivity(), EmergencyNotification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), level /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.logo))
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(level /* ID of notification */, notificationBuilder.build());
    }

    public static void emergencyNotification(String title, String message){
        //send emergency notification
        String to = "mobile";
        String level = "1";
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String time = DateFormat.getDateTimeInstance().format(new Date());
        AbstractNotification emergency = new AbstractNotification(level, title, message, time, to);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userID).child("notification").push();
        mDatabase.setValue(emergency);
        //send emergency as sms
        String sms = "EmergencySMS";
        sendSMS(mobileNumber, sms);
        sendSMS(mobileNumber2, sms);
    }

    public static void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Log.i(TAG, "SMS Sent to: " + phoneNo);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
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
                if (oldConfig.careSkype != null)
                    careSkype = oldConfig.careSkype;
                if (oldConfig.mobileNumber != null)
                    mobileNumber = oldConfig.mobileNumber;
                if (oldConfig.mobileNumber2 != null)
                    mobileNumber2 = oldConfig.mobileNumber2;
                if(oldConfig.enabled != null){
                    String[] checkArray = new String[3];
                    checkArray= oldConfig.enabled.split(",");
                    if(checkArray[0].equals("0")){ //settings
                        view.findViewById(R.id.restartRPI).setEnabled(false);
                        view.findViewById(R.id.openDoor).setEnabled(false);
                    }
                    if(checkArray[1].equals("0")){ //warehouse
                        view.findViewById(R.id.openWarehouse).setEnabled(false);
                        view.findViewById(R.id.clearBills).setEnabled(false);
                    }
                    if(checkArray[2].equals("0")){ //schedule
                        view.findViewById(R.id.clearTimetable).setEnabled(false);
                        view.findViewById(R.id.forceUpdateTimetable).setEnabled(false);
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
