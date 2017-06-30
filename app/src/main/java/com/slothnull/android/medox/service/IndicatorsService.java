package com.slothnull.android.medox.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
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


public class IndicatorsService extends Service {
    private static final String TAG = "IndicatorsService";

    private static final int HEART_REQUEST_INTERVAL = 5;
    private static final int PEDO_REQUEST_INTERVAL = 10;
    private static final int CAL_REQUEST_INTERVAL = 10;

    private boolean emergencyState = false;

    private GoogleApiClient mClient = null;
    private OnDataPointListener mListener;

    Timer pedoTimer;
    Timer calTimer;

    public static int oldHeart = 0;
    public static int oldPedo = 0;
    public static int oldCal = 0;
    int currentHeart = 0;
    int currentPedo = 0;
    int currentCal = 0;

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

        //steps timer every 10 seconds updates firebase
        pedoTimer = new Timer();
        pedoTimer.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                readPedo();
            }
        },TimeUnit.SECONDS.toMillis(PEDO_REQUEST_INTERVAL)
                , TimeUnit.SECONDS.toMillis(PEDO_REQUEST_INTERVAL));

        //calories timer every 10 seconds updates firebase
        calTimer = new Timer();
        calTimer.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                readCal();
            }
        },TimeUnit.SECONDS.toMillis(CAL_REQUEST_INTERVAL)
                , TimeUnit.SECONDS.toMillis(CAL_REQUEST_INTERVAL));

        registerHeart();
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

    /**
     * Read the current daily step total, computed from midnight of the current day
     * on the device's current timezone.
     */
    private void readPedo () {
        Log.i(TAG, "readPedo Called");
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
        //to avoid sending data if not too much change
        boolean pedoDiff = Math.abs(currentPedo - oldPedo) > 5;
        if(pedoDiff){
            //if user not signed in stop service
            Log.i(TAG, "sending pedo rate to fb");
            FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            if(auth == null){
                stopService(new Intent(this, IndicatorsService.class));
                return;
            }
            //send data to db
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDatabase.child("users").child(UID).child("data").child("pedo")
                    .setValue(String.valueOf(currentPedo));
        }

        Log.d(TAG, "Pedo Rate: " + Integer.toString(currentPedo));
        oldPedo = currentPedo;
        //[END_CHECK_SEND_PROCESS]

    }

    /**
     * Read the current daily step total, computed from midnight of the current day
     * on the device's current timezone.
     */
    private void readCal () {
        Log.i(TAG, "readCal Called");
        //[START_GET_STEPS_COUNT]
        int total = 0;

        PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.AGGREGATE_CALORIES_EXPENDED);
        DailyTotalResult totalResult = result.await(5, TimeUnit.SECONDS);
        if (totalResult.getStatus().isSuccess()) {
            DataSet totalSet = totalResult.getTotal();
            total = Math.round(totalSet.isEmpty()
                    ? 0
                    : totalSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat());
        } else {
            Log.w(TAG, "There was a problem getting the Calories");
        }

        Log.i(TAG, "Total Calories: " + total);
        currentCal = total;
        //[END_GET_STEPS_COUNT]

        //[START_CHECK_SEND_PROCESS]
        //to avoid sending data if not too much change
        boolean calDiff = Math.abs(currentCal - oldCal) > 5;
        if(calDiff){
            //if user not signed in stop service
            Log.i(TAG, "sending Calories to fb");
            FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            if(auth == null){
                stopService(new Intent(this, IndicatorsService.class));
                return;
            }
            //send data to db
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDatabase.child("users").child(UID).child("data").child("calories")
                    .setValue(String.valueOf(currentCal));
        }

        Log.d(TAG, "Calories: " + Integer.toString(currentCal));
        oldCal = currentCal;
        //[END_CHECK_SEND_PROCESS]

    }

    /**
     * Read the current heart rate and sends it to fb,
     *  also checks if heart rate is in the safe region
     */
    public void registerHeart(){
        Log.i(TAG, "readHeart Called");


        //[START_GET_HEART_RATE]
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);
                    currentHeart = val.asInt();
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                .setDataSourceTypes(DataSource.TYPE_RAW, DataSource.TYPE_DERIVED)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {

                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            // There isn't heart rate source here
                            final DataType dataType = dataSource.getDataType();
                            Fitness.SensorsApi.add(mClient,
                                    new SensorRequest.Builder()
                                            .setDataSource(dataSource)
                                            .setDataType(dataType)
                                            .setSamplingRate(HEART_REQUEST_INTERVAL, TimeUnit.SECONDS)
                                            .build(),
                                    mListener)
                                    .setResultCallback(new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            if (status.isSuccess()) {
                                                Log.i(TAG, "Listener registered!");
                                            } else {
                                                Log.i(TAG, "Listener not registered.");
                                            }
                                        }
                                    });
                        }
                    }
                });
        //[END_GET_HEART_RATE]

        //[START_CHECK_SEND_PROCESS]
        //to avoid sending Location if not too much change
        boolean heartDiff = Math.abs(currentHeart - oldHeart) > 1;
        if(heartDiff){
            //if user not signed in stop service
            Log.i(TAG, "sending heart rate to fb");
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
        }
        Log.d(TAG, "Heart Rate: " + Integer.toString(currentHeart));
        oldHeart = currentHeart;
        checkHeart(currentHeart);
        //[END_CHECK_SEND_PROCESS]
    }
}
