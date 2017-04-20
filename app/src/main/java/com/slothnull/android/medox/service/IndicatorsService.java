package com.slothnull.android.medox.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractConfig;
import com.slothnull.android.medox.fragment.SeniorEmergencyFragment;

/**
 * Created by Shaheen on 17-Mar-17
 * Project: seniormedox
 * Package: com.slothnull.android.seniormedox
 */

public class IndicatorsService extends Service {

    private static final String TAG = "IndicatorsService";
    public static boolean check;
    public static final String BROADCAST_ACTION = "Hello World";
    public HeartRateListener listener;
    //Sensor and SensorManager
    public Sensor mHeartRateSensor;
    public SensorManager mSensorManager;
    public static float heartRate;

    //initially set values to avoid database delay errors
    public float maxHeartRate = 999999999;
    public float minHeartRate = 0;

    Intent intent;

    @Override
    public void onStart(Intent intent, int startId) {
        check = true;
        setData();
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        listener = new HeartRateListener();
        if (mSensorManager != null) {
            mSensorManager.registerListener(listener, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void setData(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractConfig config = dataSnapshot.getValue(AbstractConfig.class);
                if (config != null) {
                    maxHeartRate = Float.parseFloat(config.maxHeartRate);
                    minHeartRate = Float.parseFloat(config.minHeartRate);
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
        mDatabase.child("users").child(UID).child("config")
                .addValueEventListener(configListener);
    }

    // TODO: make service never stop "Indicators, Location, Data"
    //http://stackoverflow.com/questions/15758980/android-service-needs-to-run-always-never-pause-or-stop
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //smsHandler.sendEmptyMessageDelayed(DISPLAY_DATA, 1000);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        check = false;
        Log.v("STOP_SERVICE", "DONE");
        if (mSensorManager!=null)
            mSensorManager.unregisterListener(listener);
    }

    public class HeartRateListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            //Update your data.
            if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                heartRate = event.values[0];
                checkHeartRate();
            }
        }

        public void checkHeartRate(){
            if (heartRate > maxHeartRate || heartRate < minHeartRate){
                SeniorEmergencyFragment.emergencyNotification("HeartRate Emergency from watch",
                        "heartRate is: " + heartRate);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }
}
