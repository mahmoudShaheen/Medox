package com.slothnull.android.medox.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.slothnull.android.medox.SeniorHome;
import com.slothnull.android.medox.fragment.SeniorEmergencyFragment;

public class ShakeService extends Service implements SensorEventListener {

    private static final String TAG = "ShakeService";

    // variables for shake detection
    private static final float SHAKE_THRESHOLD = 18f; // m/S^2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 1000;
    private long mLastShakeTime;
    private SensorManager mSensorMgr;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "started");
        // Get a sensor manager to listen for shakes
        mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Listen for shakes
        Sensor accelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.i(TAG,String.valueOf(accelerometer != null) );
        if (accelerometer != null) {
            mSensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        Log.v(TAG, "started Create");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
                //Log.d(TAG, "Acceleration is " + acceleration + "m/s^2");

                if (acceleration > SHAKE_THRESHOLD) {
                    mLastShakeTime = curTime;
                    Log.i(TAG, "Calling Emergency");
                    sendEmergency();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ignore
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // Stop listening for shakes
        mSensorMgr.unregisterListener(this);
    }

    public void sendEmergency(){
        // Launch NotificationDetails Activity
        Intent intent = new Intent(this, SeniorHome.class);
        intent.putExtra("position", 5);
        intent.putExtra(SeniorEmergencyFragment.SHAKE_KEY, "true");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
