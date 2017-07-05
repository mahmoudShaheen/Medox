package com.slothnull.android.medox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.model.AbstractConfig;



public class Settings extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private static final int PLACE_PICKER_REQUEST = 1;

    private ProgressDialog mProgressDialog;

    private CheckedTextView settingsEnable;
    private CheckedTextView warehouseEnable;
    private CheckedTextView scheduleEnable;
    private String[] checkArray;
    public static String latitude;
    public static String longitude;

    private EditText maxHeart;
    private EditText minHeart;
    private EditText mobileNumber;
    private EditText mobileNumber2;
    private EditText careSkype;
    private EditText seniorSkype;
    private EditText maxDistance;

    AbstractConfig oldConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        disableSenior();
        //wait for getting old config
        showProgressDialog();

        checkListeners();
        checkArray = new String[3];
        checkArray[0] = "0";
        checkArray[1] = "0";
        checkArray[2] = "0";
        maxHeart = (EditText) findViewById(R.id.maxHeart);
        minHeart = (EditText) findViewById(R.id.minHeart);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        mobileNumber2 = (EditText) findViewById(R.id.mobileNumber2);
        careSkype = (EditText) findViewById(R.id.careSkype);
        seniorSkype = (EditText) findViewById(R.id.seniorSkype);
        maxDistance = (EditText) findViewById(R.id.maxDistance);

        getConfig();
    }

    private void disableSenior(){
        //remove enabled group in senior app
        SharedPreferences sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        final String appType = sharedPreferences.getString("appType", "");
        if(appType.equals("senior")){
            findViewById(R.id.enabledView).setVisibility(View.GONE);
            findViewById(R.id.enabledLayout).setVisibility(View.GONE);
        }
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
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            latitude = String.valueOf(place.getLatLng().latitude);
            longitude = String.valueOf(place.getLatLng().longitude);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void save(View v){
        //strings to send to config class
        String mMaxDistance = maxDistance.getText().toString();
        String mMaxHeart = maxHeart.getText().toString();
        String mMinHeart = minHeart.getText().toString();
        String mMobileNumber = mobileNumber.getText().toString();
        String mMobileNumber2 = mobileNumber2.getText().toString();
        String mCareSkype = careSkype.getText().toString();
        String mSeniorSkype = seniorSkype.getText().toString();

        //if fields are empty save old config
        if (mMaxDistance.isEmpty())
            mMaxDistance = oldConfig.maxDistance;
        if (mMaxHeart.isEmpty())
            mMaxHeart = oldConfig.maxHeartRate;
        if (mMinHeart.isEmpty())
            mMinHeart = oldConfig.minHeartRate;
        if (mMobileNumber.isEmpty())
            mMobileNumber = oldConfig.mobileNumber;
        if (mMobileNumber2.isEmpty())
            mMobileNumber2 = oldConfig.mobileNumber2;
        if (mCareSkype.isEmpty())
            mCareSkype = oldConfig.careSkype;
        if (mSeniorSkype.isEmpty())
            mSeniorSkype = oldConfig.seniorSkype;

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if( //if all config class fields aren't null -> set user as configured
                   mMaxDistance != null
                && mMaxHeart != null
                && mMinHeart != null
                && latitude != null
                && longitude != null
                && mMobileNumber != null
                && mMobileNumber2 != null
                && mCareSkype != null
                && mSeniorSkype != null
           ){
            if( //if all config class fields aren't empty -> set user as configured
                        !mMaxDistance.isEmpty()
                    && !mMaxHeart.isEmpty()
                    && !mMinHeart.isEmpty()
                    && !latitude.isEmpty()
                    && !longitude.isEmpty()
                    && !mMobileNumber.isEmpty()
                    && !mMobileNumber2.isEmpty()
                    && !mCareSkype.isEmpty()
                    && !mSeniorSkype.isEmpty()
                ){
                DatabaseReference configured = FirebaseDatabase.getInstance().getReference();
                configured.child("users").child(UID).child("user").child("configured").setValue("true");
            }
        }

        //send notification to database to access it later in Notification Activity
        AbstractConfig config = new AbstractConfig(
                mMaxDistance,
                latitude,
                longitude,
                mMaxHeart,
                mMinHeart,
                mMobileNumber,
                mMobileNumber2,
                mCareSkype,
                mSeniorSkype,
                (checkArray[0] + "," + checkArray[1] +"," + checkArray[2]));


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("config");
        mDatabase.setValue(config);
        finish();
    }

    public void getConfig(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                oldConfig = dataSnapshot.getValue(AbstractConfig.class);
                if(oldConfig != null){
                    if(oldConfig.maxHeartRate != null)
                        maxHeart.setHint(maxHeart.getHint() + oldConfig.maxHeartRate);
                    if(oldConfig.minHeartRate != null)
                        minHeart.setHint(minHeart.getHint() + oldConfig.minHeartRate);
                    if(oldConfig.maxDistance != null)
                        maxDistance.setHint(maxDistance.getHint() + oldConfig.maxDistance);
                    if(oldConfig.homeLatitude != null)
                        latitude = oldConfig.homeLatitude;
                    if(oldConfig.homeLongitude != null)
                        longitude = oldConfig.homeLongitude;
                    if(oldConfig.mobileNumber != null)
                        mobileNumber.setHint(mobileNumber.getHint() + oldConfig.mobileNumber);
                    if(oldConfig.mobileNumber2 != null)
                        mobileNumber2.setHint(mobileNumber2.getHint() + oldConfig.mobileNumber2);
                    if(oldConfig.careSkype != null)
                        careSkype.setHint(careSkype.getHint() + oldConfig.careSkype);
                    if(oldConfig.seniorSkype != null)
                        seniorSkype.setHint(seniorSkype.getHint() + oldConfig.seniorSkype);
                    if(oldConfig.enabled != null){
                        checkArray = oldConfig.enabled.split(",");
                        settingsEnable.setChecked(checkArray[0].equals("1"));
                        warehouseEnable.setChecked(checkArray[1].equals("1"));
                        scheduleEnable.setChecked(checkArray[2].equals("1"));
                    }
                }
                hideProgressDialog();
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
    //progress dialog to wait for saved data
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
