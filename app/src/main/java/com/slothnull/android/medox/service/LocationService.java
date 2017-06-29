package com.slothnull.android.medox.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.EmergencyNotification;
import com.slothnull.android.medox.R;
import com.slothnull.android.medox.SeniorHome;
import com.slothnull.android.medox.Splash;
import com.slothnull.android.medox.model.AbstractConfig;
import com.slothnull.android.medox.fragment.SeniorEmergencyFragment;

/**
 * Created by Shaheen on 17-Mar-17
 * Project: seniormedox
 * Package: com.slothnull.android.seniormedox
 */

public class LocationService extends Service implements LocationListener {

    private static final String TAG = "LocationService";

    private boolean emergencyState = false;
    public static final String BROADCAST_ACTION = "Hello World";
    public LocationManager locationManager;
    public static double latitude;
    public static double longitude;
    public static double oldLatitude = 0;
    public static double oldLongitude = 0;
    public String provider;
    public Location location;
    //initially set values to avoid database delay errors
    public double maxDistance = -1;
    public double homeLatitude = -1;
    public double homeLongitude = -1;

    Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "started");
        intent = new Intent(BROADCAST_ACTION);

        setData();
        try{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            provider = locationManager.getBestProvider(new Criteria(), true);

            location = locationManager.getLastKnownLocation(provider);
            Log.i(TAG, "Location can be achieved!");
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }catch(SecurityException e){
            Log.i(TAG, "No location :(");
        }

        //foreground service
        Intent notificationIntent = new Intent(this, Splash.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.logo))
                .setContentTitle("Location Service Running")
                .setContentText("Medox Location service running")
                .setContentIntent(pendingIntent).build();

        startForeground(1338/* ID of notification */, notification);
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
                if (config.maxDistance != null) {
                    maxDistance = Double.parseDouble(config.maxDistance);
                    Log.i(TAG, "maxDistance" + maxDistance);
                }
                if (config.homeLatitude != null) {
                    homeLatitude = Double.parseDouble(config.homeLatitude);
                    Log.i(TAG, "homeLatitude" + homeLatitude);
                }
                if (config.homeLongitude != null) {
                    homeLongitude = Double.parseDouble(config.homeLongitude);
                    Log.i(TAG, "homeLongitude" + homeLongitude);
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
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
    }

    public void onLocationChanged(final Location location) {
        Log.i(TAG, "Location changed");

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        provider = location.getProvider();

        //to avoid sending Location if not too much change
        boolean LatDiff = Math.abs(latitude - oldLatitude) > 0.001;
        boolean LongDiff = Math.abs(longitude - oldLongitude) > 0.001;
        if(LatDiff || LongDiff)
            sendLocData();

        Log.d(TAG, "Latitude" + Double.toString(location.getLatitude()));
        Log.d(TAG, "Longitude" + Double.toString(location.getLongitude()));
        Log.d(TAG, "provider" + location.getProvider());
        oldLatitude = latitude;
        oldLongitude = longitude;

        checkDistance(latitude, longitude, provider);
    }

    public void checkDistance(double lat, double lon, String prov){
        Log.i(TAG, "checking Distance" );
        Location myLocation = new Location(prov);
        myLocation.setLatitude(lat);
        myLocation.setLongitude(lon);

        Location homeLocation = new Location(prov);
        homeLocation.setLatitude(homeLatitude);
        homeLocation.setLongitude(homeLongitude);

        float distance = myLocation.distanceTo(homeLocation);
        Log.i(TAG, "distance: " + distance );
        if (maxDistance != -1){ //initialized "already get that from db"
            if (distance > maxDistance && !emergencyState){ //!emergencyState to avoid resend emergency
                Log.i(TAG, "distance > maxDistance: sending emerg." );
                emergencyState = true;
                sendEmergency();
            }
            if(distance < maxDistance && emergencyState){ //returned to home
                emergencyState = false;
            }
        }
    }

    public void sendEmergency(){
        // Launch Emergency Activity
        Intent intent = new Intent(this, EmergencyNotification.class);
        intent.putExtra(EmergencyNotification.LOCATION_KEY, "true");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onProviderDisabled(String provider) {
        Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
    }

    public void onProviderEnabled(String provider) {
        Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText( getApplicationContext(), "Gps status changed", Toast.LENGTH_SHORT).show();
    }
    public void sendLocData(){
        //if user not signed in stop service
        Log.i(TAG, "sending data to fb");
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if(auth == null){
            stopService(new Intent(this, LocationService.class));
            return;
        }
        //send data to db
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("users").child(UID).child("data").child("latitude")
                .setValue(String.valueOf(latitude));
        mDatabase.child("users").child(UID).child("data").child("longitude")
                .setValue(String.valueOf(longitude));
    }
}