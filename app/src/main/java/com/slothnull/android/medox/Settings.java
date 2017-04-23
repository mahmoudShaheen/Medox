package com.slothnull.android.medox;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.slothnull.android.medox.Abstract.AbstractConfig;

public class Settings extends AppCompatActivity {

    private final String TAG = "SettingsActivity";

    private CheckedTextView settingsEnable;
    private CheckedTextView warehouseEnable;
    private CheckedTextView scheduleEnable;
    private String[] checkArray;
    private Location mLocation;

    private EditText maxHeart;
    private EditText minHeart;
    private EditText mobileNumber;
    private EditText careSkype;
    private EditText seniorSkype;
    private EditText maxDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        checkListeners();
        checkArray = new String[3];
        maxHeart = (EditText) findViewById(R.id.maxHeart);
        minHeart = (EditText) findViewById(R.id.minHeart);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        careSkype = (EditText) findViewById(R.id.careSkype);
        seniorSkype = (EditText) findViewById(R.id.seniorSkype);
        maxDistance = (EditText) findViewById(R.id.maxDistance);
    }

    private void checkListeners(){
        settingsEnable = (CheckedTextView) findViewById(R.id.settingsEnable);
        settingsEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingsEnable.isChecked()){
                    settingsEnable.setChecked(false);
                    checkArray[0]= "0";
                }else{
                    settingsEnable.setChecked(true);
                    checkArray[0]="1";
                }
            }
        });
        warehouseEnable = (CheckedTextView) findViewById(R.id.warehouseEnable);
        warehouseEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (warehouseEnable.isChecked()){
                    warehouseEnable.setChecked(false);
                    checkArray[1]= "0";
                }else{
                    warehouseEnable.setChecked(true);
                    checkArray[1]="1";
                }
            }
        });
        scheduleEnable = (CheckedTextView) findViewById(R.id.scheduleEnable);
        scheduleEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scheduleEnable.isChecked()){
                    scheduleEnable.setChecked(false);
                    checkArray[2]= "0";
                }else{
                    scheduleEnable.setChecked(true);
                    checkArray[2]="1";
                }
            }
        });
    }
    public void locate(View v){
        LocationManager locationManager;
        String mProvider;
        try{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            mProvider = locationManager.getBestProvider(new Criteria(), true);

            mLocation = locationManager.getLastKnownLocation(mProvider);
            Log.i(TAG, "Location achieved!");
            Log.i(TAG, mLocation.toString());
        }catch(SecurityException e){
            Log.i(TAG, "No location :(");
            Toast.makeText(this,"can't get Location", Toast.LENGTH_LONG).show();
        }
    }
    public void save(View v){
        //send notification to database to access it later in Notification Activity
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        AbstractConfig config = new AbstractConfig(
                maxDistance.getText().toString(),
                String.valueOf(mLocation.getLatitude()),
                String.valueOf(mLocation.getLongitude()),
                maxHeart.getText().toString(),
                minHeart.getText().toString(),
                mobileNumber.getText().toString(),
                careSkype.getText().toString(),
                seniorSkype.getText().toString(),
                (checkArray[0] + "," + checkArray[1] +"," + checkArray[2] +","));


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("config");
        mDatabase.setValue(config);
        finish();
    }
}
