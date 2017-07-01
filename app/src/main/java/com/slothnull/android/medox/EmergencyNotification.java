package com.slothnull.android.medox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.slothnull.android.medox.fragment.EmergencyFragment;
import com.slothnull.android.medox.fragment.SeniorEmergencyFragment;

import static com.slothnull.android.medox.fragment.SeniorEmergencyFragment.emergencyNotification;

public class EmergencyNotification extends AppCompatActivity {

    private static final String TAG = "EmergencyNotification";

    public static final String SHAKE_KEY = "shake";
    public static final String LOCATION_KEY = "location";
    public static final String INDICATORS_KEY = "indicators";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_notification);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth == null) {
            finish();
        }


        // Get post key from intent
        String shakeKey = getIntent().getStringExtra(SHAKE_KEY);
        if (shakeKey != null) {
            sendShakeEmergency();
        }
        String locationKey = getIntent().getStringExtra(LOCATION_KEY);
        if (locationKey != null) {
            sendLocationEmergency();
        }
        String indicatorsKey = getIntent().getStringExtra(INDICATORS_KEY);
        if (indicatorsKey != null) {
            sendIndicatorsEmergency();
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        final String appType = sharedPreferences.getString("appType", "");
        Log.i(TAG, appType);
        //go to Home Activity according to user type
        if (appType.equals("care")) {
            // Create the adapter that will return a fragment for each section
            mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
                private final Fragment[] mFragments = new Fragment[] { new EmergencyFragment() };
                private final String[] mFragmentNames = new String[] { "Emergency" };
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
        } else if (appType.equals("senior")) {
            Toast.makeText(this,"Emergency Sent!", Toast.LENGTH_LONG).show();
            // Create the adapter that will return a fragment for each section
            mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
                private final Fragment[] mFragments = new Fragment[] { new SeniorEmergencyFragment() };
                private final String[] mFragmentNames = new String[] { "Emergency" };
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
        } else { //user signed but undefined app type
            finish();
            Log.i(TAG, "2");
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void openApp(View view){
        Intent intent = new Intent(this, Splash.class);
        startActivity(intent);
        finish();
    }

    public void sendShakeEmergency(){
        String title = "Emergency Shake From Watch!";
        String message = "Action Required IMMEDIATELY !!!!";
        emergencyNotification(title, message);
    }

    public void sendLocationEmergency(){
        String title = "Location Emergency from watch";
        String message = "Mobile location is out of safe distance ";
        emergencyNotification(title, message);
    }

    public void sendIndicatorsEmergency(){
        String title = "Indicators Emergency from watch";
        String message = "Indicators are out of safe value ";
        emergencyNotification(title, message);
    }
}
