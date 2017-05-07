package com.slothnull.android.medox.fragment;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractCommand;
import com.slothnull.android.medox.Abstract.AbstractConfig;
import com.slothnull.android.medox.Abstract.AbstractEmergency;
import com.slothnull.android.medox.Abstract.AbstractMobileToken;
import com.slothnull.android.medox.Abstract.AbstractWarehouse;
import com.slothnull.android.medox.R;
import com.slothnull.android.medox.SeniorHome;
import com.slothnull.android.medox.fcm.MyFirebaseMessagingService;

import java.net.InetAddress;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeniorEmergencyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SeniorEmergencyFragment";

    public static final String SHAKE_KEY = "shake";
    public static String mobileToken;

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

        // Get post key from intent
        String shakeKey = getActivity().getIntent().getStringExtra(SHAKE_KEY);
        if (shakeKey != null) {
            sendShakeEmergency();
        }

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
        Context c = getActivity().getApplicationContext();
        emergencyNotification(c, title, message);
    }
    public void sendShakeEmergency(){
        String title = "Emergency Shake From Watch!";
        String message = "Action Required IMMEDIATELY !!!!";
        sendSeniorNotification("Emergency Sent!", "Click here for more Actions!", 10);
        Context c = getActivity().getApplicationContext();
        emergencyNotification(c, title, message);
    }
    private void sendSeniorNotification(String title, String messageBody, int level) {
        Intent intent = new Intent(getActivity(), SeniorHome.class);
        intent.putExtra("position", 5);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), level /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(level /* ID of notification */, notificationBuilder.build());
    }

    public static void emergencyNotification(Context c, String title, String message){
        if (mobileToken != null && isNetworkConnected(c)){
            String token = mobileToken;
            String level = "1";

            AbstractEmergency emergency = new AbstractEmergency(token, level, title, message);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("messages").push();
            mDatabase.setValue(emergency);
        }else{
            String sms = title + "\n" + message;
            sendSMS(c, mobileNumber, sms);
        }
    }

    public static void sendSMS(Context c, String phoneNo, String msg) {
        try {
            if(phoneNo == null){
                phoneNo = sharedPreferences.getString("mobileNumber", "");
            }
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Log.i(TAG, "SMS Sent to: " + phoneNo);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
    private static boolean isNetworkConnected(Context c) {
        //check connection state
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
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
                if (oldConfig.mobileNumber != null) {
                    mobileNumber = oldConfig.mobileNumber;
                    //save mobile number to shared prefs
                    sharedPreferences.edit().putString("mobileNumber", mobileNumber).apply();
                }
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
