package com.slothnull.android.medox.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.EmergencyNotification;
import com.slothnull.android.medox.R;
import com.slothnull.android.medox.Splash;
import com.slothnull.android.medox.helper.medox;
import com.slothnull.android.medox.model.AbstractConfig;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * Created by Shaheen on 17-Mar-17
 * Project: seniormedox
 * Package: com.slothnull.android.seniormedox
 */

public class IndicatorsService extends Service {
    private static final String TAG = "IndicatorsService";

    private boolean emergencyState = false;

    private GoogleApiClient mClient = null;

    Timer timer;

    public static int oldHeart = 0;
    public static int oldPedo = 0;
    int currentHeart = 0;
    int currentPedo = 0;

    //initially set values to avoid database delay errors
    public int minHeart = -1;
    public int maxHeart = -1;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "started");

        setData();

        //get Google Api client
        mClient = medox.getGoogleApiHelper().getGoogleApiClient();


        //foreground service
        Intent notificationIntent = new Intent(this, Splash.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.logo))
                .setContentTitle("Indicators Service Running")
                .setContentText("Medox Indicators service running")
                .setContentIntent(pendingIntent).build();

        startForeground(1339/* ID of notification */, notification);

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            synchronized public void run() {

                readDataTask();
            }

        },TimeUnit.SECONDS.toMillis(10), TimeUnit.SECONDS.toMillis(10));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void setData(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractConfig config = dataSnapshot.getValue(AbstractConfig.class);
                if (config.maxHeartRate != null) {
                    maxHeart = Integer.parseInt(config.maxHeartRate);
                    Log.i(TAG, "maxHeart: " + maxHeart);
                }
                if (config.minHeartRate != null) {
                    minHeart = Integer.parseInt(config.minHeartRate);
                    Log.i(TAG, "minHeart: " + minHeart);
                }
                // ...

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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
    }

    public void onSensorChanged(int value) {

    }

    public void checkHeart(int heartRate){
        Log.i(TAG, "checking Heart Rate" );

        if (maxHeart != -1 && minHeart != -1){ //initialized "already get that from db"
            if ((heartRate > maxHeart || heartRate < minHeart) &&  !emergencyState){ //!emergencyState to avoid resend emergency
                Log.i(TAG, "HeartRate not safe: sending emerg." );
                emergencyState = true;
                sendEmergency();
            }
            if(heartRate < maxHeart && heartRate > minHeart && emergencyState){ //returned to normal
                emergencyState = false;
            }
        }
    }

    public void sendEmergency(){
        // Launch Emergency Activity
        Intent intent = new Intent(this, EmergencyNotification.class);
        intent.putExtra(EmergencyNotification.INDICATORS_KEY, "true");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void sendIndData(){
        //if user not signed in stop service
        Log.i(TAG, "sending data to fb");
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if(auth == null){
            stopService(new Intent(this, IndicatorsService.class));
            return;
        }
        //send data to db
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("users").child(UID).child("data").child("heartRate")
                .setValue(String.valueOf(currentHeart));
        mDatabase.child("users").child(UID).child("data").child("pedo")
                .setValue(String.valueOf(currentPedo));
    }

    /**
     * Read the current daily step total, computed from midnight of the current day
     * on the device's current timezone.
     */
    private void readDataTask () {
        Log.i(TAG, "readDataTask Called");
        //[START_GET_STEPS_COUNT]
        int total = 0;

        PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
        DailyTotalResult totalResult = result.await(5, TimeUnit.SECONDS);
        if (totalResult.getStatus().isSuccess()) {
            DataSet totalSet = totalResult.getTotal();
            total = totalSet.isEmpty()
                    ? 0
                    : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
        } else {
            Log.w(TAG, "There was a problem getting the step count.");
        }

        Log.i(TAG, "Total steps: " + total);
        currentPedo = total;
        //[END_GET_STEPS_COUNT]

        //[START_CHECK_SEND_PROCESS]
        Log.i(TAG, "new sensor value");
        //to avoid sending Location if not too much change
        boolean heartDiff = Math.abs(currentHeart - oldHeart) > 1;
        boolean pedoDiff = Math.abs(currentPedo - oldPedo) > 1;
        if(heartDiff || pedoDiff)
            sendIndData();

        Log.d(TAG, "Heart Rate: " + Integer.toString(currentHeart));
        Log.d(TAG, "Pedo Rate: " + Integer.toString(currentPedo));
        oldHeart = currentHeart;
        oldPedo = currentPedo;

        checkHeart(currentHeart);
        //[END_CHECK_SEND_PROCESS]

    }
}
