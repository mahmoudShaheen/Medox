package com.slothnull.android.medox.fragment;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractData;
import com.slothnull.android.medox.R;

public class LocationFragment extends Fragment implements OnMapReadyCallback,LocationListener {

    private static final String TAG = "LocationFragment";

    private GoogleMap mMap;
    MapView mMapView;
    public double longitude = 0;
    public double latitude = 0;

    public Location mLocation;
    public double mLongitude = 0;
    public double mLatitude = 0;
    public String mProvider = "";
    public static String watchToken;
    View view;

    public LocationManager locationManager;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_location, container, false);
        //map initialization
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        try{
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            mProvider = locationManager.getBestProvider(new Criteria(), true);

            mLocation = locationManager.getLastKnownLocation(mProvider);
            Log.i(TAG, "Location achieved!");
            locationManager.requestLocationUpdates(mProvider, 400, 1, this);
        }catch(SecurityException e){
            Log.i(TAG, "No location :(");
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //get remote location
        mMap = googleMap;
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractData data = dataSnapshot.getValue(AbstractData.class);
                if (data != null) {
                    longitude = Double.parseDouble(data.longitude);
                    latitude = Double.parseDouble(data.latitude);
                    updateMarker();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("users").child(UID).child("data")
                .addValueEventListener(dataListener);
    }

    public void updateMarker(){
        // clear map Add a marker for watch and move the camera
        mMap.clear();
        LatLng watch = new LatLng(latitude, longitude);
        LatLng mobile = new LatLng(mLatitude, mLongitude);
        LatLngBounds bounds = getBuilder(watch, mobile);

        checkDistance();

        //view other location
        Marker watchMarker = mMap.addMarker(new MarkerOptions()
                .position(watch)
                .title("Watch")
                .icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        watchMarker.showInfoWindow();

        //view my location
        mMap.addMarker(new MarkerOptions().position(mobile).title("Mobile"));

        //animate camera to view the two locations
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mMap.animateCamera(cu);
    }

    public LatLngBounds getBuilder(LatLng loc1, LatLng loc2){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(loc1);
        builder.include(loc2);
        LatLngBounds bounds = builder.build();
        return bounds;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stop location update
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(this);
    }

    public void onLocationChanged(final Location location) {
        updateMarker();
        Log.i(TAG, "Location changed");

        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        mProvider = location.getProvider();
        Log.d(TAG, "Latitude" + Double.toString(location.getLatitude()));
        Log.d(TAG, "Longitude" + Double.toString(location.getLongitude()));
        Log.d(TAG, "provider" + location.getProvider());
    }

    public void checkDistance(){
        if(mLatitude == 0 || mLongitude == 0)
            return;
        Location myLocation = new Location(mProvider);
        myLocation.setLatitude(mLatitude);
        myLocation.setLongitude(mLongitude);

        Location otherLocation = new Location(mProvider);
        otherLocation.setLatitude(latitude);
        otherLocation.setLongitude(longitude);

        float distance = myLocation.distanceTo(otherLocation);
        Integer dist = Math.round(distance);
        ((TextView)view.findViewById(R.id.distanceView)).setText(String.valueOf(dist) + " meter");
    }

    public void onProviderDisabled(String provider) {
        Log.i(TAG, "Gps Disabled");
    }

    public void onProviderEnabled(String provider) {
        Log.i(TAG, "Gps Enabled");
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "Gps status changed");
    }
}
