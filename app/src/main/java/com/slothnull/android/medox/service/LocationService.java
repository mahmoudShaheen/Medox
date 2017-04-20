package com.slothnull.android.medox.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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

public class LocationService extends Service implements LocationListener {

    private static final String TAG = "LocationService";
    public static final String BROADCAST_ACTION = "Hello World";
    public LocationManager locationManager;
    public static double latitude;
    public static double longitude;
    public String provider;
    public Location location;
    //initially set values to avoid database delay errors
    public double maxDistance =  999999999;
    public double homeLatitude = 0;
    public double homeLongitude = 0;

    Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "started");
        intent = new Intent(BROADCAST_ACTION);
    }


    //TODO: ask for permission for android 6
    //https://developer.android.com/training/permissions/requesting.html
    @Override
    public void onStart(Intent intent, int startId) {
        setData();
        try{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            provider = locationManager.getBestProvider(new Criteria(), true);

            location = locationManager.getLastKnownLocation(provider);
            Log.i(TAG, "Location achieved!");
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }catch(SecurityException e){
            Log.i(TAG, "No location :(");
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
                if (config.maxDistance != null)
                    maxDistance = Double.parseDouble(config.maxDistance);
                if (config.homeLatitude != null)
                    homeLatitude = Double.parseDouble(config.homeLatitude);
                if (config.homeLongitude != null)
                    homeLongitude = Double.parseDouble(config.homeLongitude);
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
        locationManager.removeUpdates(this);
    }

    public void onLocationChanged(final Location location) {
        Log.i(TAG, "Location changed");

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        provider = location.getProvider();
        checkDistance(latitude, longitude, provider);
        Log.d(TAG, "Latitude" + Double.toString(location.getLatitude()));
        Log.d(TAG, "Longitude" + Double.toString(location.getLongitude()));
        Log.d(TAG, "provider" + location.getProvider());
    }

    public void checkDistance(double lat, double lon, String prov){
        Location myLocation = new Location(prov);
        myLocation.setLatitude(lat);
        myLocation.setLongitude(lon);

        Location homeLocation = new Location(prov);
        myLocation.setLatitude(homeLatitude);
        myLocation.setLongitude(homeLongitude);

        float distance = myLocation.distanceTo(homeLocation);
        if (distance > maxDistance){
            SeniorEmergencyFragment.emergencyNotification("Location Emergency from watch",
                    "Distance is: " + distance);
        }
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
}