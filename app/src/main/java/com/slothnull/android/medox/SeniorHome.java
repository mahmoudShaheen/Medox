package com.slothnull.android.medox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.fragment.ControlFragment;
import com.slothnull.android.medox.model.AbstractConfig;
import com.slothnull.android.medox.fragment.IndicatorsFragment;
import com.slothnull.android.medox.fragment.NotificationFragment;
import com.slothnull.android.medox.fragment.ScheduleFragment;
import com.slothnull.android.medox.fragment.SeniorEmergencyFragment;
import com.slothnull.android.medox.fragment.StatusFragment;
import com.slothnull.android.medox.fragment.WarehouseFragment;

public class SeniorHome extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SeniorHomeActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private int position;
    private AbstractConfig oldConfig;
    private boolean settingsEnable;

    //Google Fit
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        checkConnection();
        showProgressDialog();

        getConfig(); //for settings enabled state

        callFitAuth(savedInstanceState); //Authenticate App for google fit

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        Log.i(TAG, Integer.toString(position));

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new StatusFragment(),
                    new IndicatorsFragment(),
                    new NotificationFragment(),
                    new ScheduleFragment(),
                    new WarehouseFragment(),
                    new SeniorEmergencyFragment(),
                    new ControlFragment()
            };

            private final String[] mFragmentNames = new String[] {
                    "Home",
                    "Indicators",
                    "Notifications",
                    "Schedule",
                    "Warehouse",
                    "Emergency",
                    "Control"
            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if (position != -1){
            mViewPager.setCurrentItem(position);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.signout) {
            signOut();
            finish();
            return true;
        }if (i == R.id.settings) {
            settings();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    public void signOut(){
        Splash.stopServices(this);
        Splash.disableServices(this);
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Intent intent = new Intent(this, Authentication.class);
        startActivity(intent);
    }
    public void settings(){
        if(settingsEnable) {//check if Settings enabled for Senior
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Settings Access is denied by Care Giver!",
                    Toast.LENGTH_LONG).show();
        }
    }


    //progress dialog to wait for saved data
    private ProgressDialog mProgressDialog;
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



    public void getConfig(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                oldConfig = dataSnapshot.getValue(AbstractConfig.class);
                if(oldConfig.enabled != null){
                    String enabled[]= oldConfig.enabled.split(",");
                    settingsEnable = (enabled[0].equals("1"));
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
    private void checkConnection(){
        //check connection state
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null){//Not Connected
            String message = "You are offline, changes will not take effect until connection is restored!";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message).setPositiveButton("OK", null);
            builder.show();
        }
    }


    //[START_FITNESS_FUNCTIONS]
    public void callFitAuth(Bundle savedInstanceState){

        Log.i(TAG, "callFitAuth");
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Log.i(TAG, "Connecting...");
        mApiClient.connect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
        if( !authInProgress ) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult( SeniorHome.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {

            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        if( requestCode == REQUEST_OAUTH ) {
            authInProgress = false;
            if( resultCode == RESULT_OK ) {
                if( !mApiClient.isConnecting() && !mApiClient.isConnected() ) {
                    mApiClient.connect();
                }
            } else if( resultCode == RESULT_CANCELED ) {
                Log.e( "GoogleFit", "RESULT_CANCELED" );
            }
        } else {
            Log.e(TAG, "requestCode NOT request_oauth");
        }
    }
    //[END_FITNESS_FUNCTIONS]
}
