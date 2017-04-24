package com.slothnull.android.medox;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractConfig;

public class Settings extends AppCompatActivity implements LocationListener {

    private final String TAG = "SettingsActivity";

    private ProgressDialog mProgressDialog;

    private CheckedTextView settingsEnable;
    private CheckedTextView warehouseEnable;
    private CheckedTextView scheduleEnable;
    private String[] checkArray;
    private Location mLocation;
    public String mProvider;
    public String latitude;
    public String longitude;

    private EditText maxHeart;
    private EditText minHeart;
    private EditText mobileNumber;
    private EditText careSkype;
    private EditText seniorSkype;
    private EditText maxDistance;

    AbstractConfig oldConfig;

    public LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        careSkype = (EditText) findViewById(R.id.careSkype);
        seniorSkype = (EditText) findViewById(R.id.seniorSkype);
        maxDistance = (EditText) findViewById(R.id.maxDistance);

        setData();

        try{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            mProvider = locationManager.getBestProvider(new Criteria(), true);

            mLocation = locationManager.getLastKnownLocation(mProvider);
            Log.i(TAG, "Location achieved!");
            locationManager.requestLocationUpdates(mProvider, 400, 1, this);
        }catch(SecurityException e){
            Log.i(TAG, "No location :(");
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
        try{
            latitude = Double.toString(mLocation.getLatitude());
            longitude = Double.toString(mLocation.getLongitude());
            Toast.makeText(this, "Location Achieved!", Toast.LENGTH_LONG).show();
            Log.i(TAG, latitude);
            Log.i(TAG, longitude);
        }catch (Exception e){
            Toast.makeText(this, "Can't get Location, Try Again!", Toast.LENGTH_LONG).show();
        }
    }

    public void save(View v){
        //strings to send to config class
        String mMaxDistance = maxDistance.getText().toString();
        String mMaxHeart = maxHeart.getText().toString();
        String mMinHeart = minHeart.getText().toString();
        String mMobileNumber = mobileNumber.getText().toString();
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
        if (mCareSkype.isEmpty())
            mCareSkype = oldConfig.careSkype;
        if (mSeniorSkype.isEmpty())
            mSeniorSkype = oldConfig.seniorSkype;


        //send notification to database to access it later in Notification Activity
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AbstractConfig config = new AbstractConfig(
                mMaxDistance,
                latitude,
                longitude,
                mMaxHeart,
                mMinHeart,
                mMobileNumber,
                mCareSkype,
                mSeniorSkype,
                (checkArray[0] + "," + checkArray[1] +"," + checkArray[2]));


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("config");
        mDatabase.setValue(config);
        finish();
    }

    //Location Listener Functions
    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        Log.i(TAG,Double.toString(mLocation.getLatitude()) );
        Log.i(TAG,Double.toString(mLocation.getLongitude()) );
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    public void setData(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                oldConfig = dataSnapshot.getValue(AbstractConfig.class);
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
