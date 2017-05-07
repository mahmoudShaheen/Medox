package com.slothnull.android.medox;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.slothnull.android.medox.fragment.EmergencyFragment;
import com.slothnull.android.medox.fragment.IndicatorsFragment;
import com.slothnull.android.medox.fragment.LocationFragment;
import com.slothnull.android.medox.fragment.NotificationFragment;
import com.slothnull.android.medox.fragment.ScheduleFragment;
import com.slothnull.android.medox.fragment.StatusFragment;
import com.slothnull.android.medox.fragment.WarehouseFragment;
import com.slothnull.android.medox.service.IndicatorsService;
import com.slothnull.android.medox.service.LocationService;

public class Home extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
                    new EmergencyFragment(),
                    new LocationFragment()
            };

            private final String[] mFragmentNames = new String[] {
                    "Home",
                    "Indicators",
                    "Notifications",
                    "Schedule",
                    "Warehouse",
                    "Emergency",
                    "Location"
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

        /*
        View mainTab;

        mainTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
        mainTab.setBackgroundResource(R.drawable.user);
        mainTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);
        mainTab.setBackgroundResource(R.drawable.indicators);
        mainTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(2);
        mainTab.setBackgroundResource(R.drawable.notification);
        mainTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(3);
        mainTab.setBackgroundResource(R.drawable.schedule);
        mainTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(4);
        mainTab.setBackgroundResource(R.drawable.warehouse);
        mainTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(5);
        mainTab.setBackgroundResource(R.drawable.emergency);
        mainTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(6);
        mainTab.setBackgroundResource(R.drawable.location);
        */
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
        stopService(new Intent(this, IndicatorsService.class));
        stopService(new Intent(this, LocationService.class));
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Intent intent = new Intent(this, Authentication.class);
        startActivity(intent);
    }
    public void settings(){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    //ProgressDialog
/*
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
    }*/
}
