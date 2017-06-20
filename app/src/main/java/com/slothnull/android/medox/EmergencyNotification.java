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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.slothnull.android.medox.fragment.EmergencyFragment;
import com.slothnull.android.medox.fragment.SeniorEmergencyFragment;

public class EmergencyNotification extends AppCompatActivity {

    private static final String TAG = "EmergencyNotification";

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
}
