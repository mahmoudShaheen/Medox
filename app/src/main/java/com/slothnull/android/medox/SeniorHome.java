package com.slothnull.android.medox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.slothnull.android.medox.fragment.EmergencyFragment;
import com.slothnull.android.medox.fragment.IndicatorsFragment;
import com.slothnull.android.medox.fragment.LocationFragment;
import com.slothnull.android.medox.fragment.NotificationFragment;
import com.slothnull.android.medox.fragment.ScheduleFragment;
import com.slothnull.android.medox.fragment.SeniorEmergencyFragment;
import com.slothnull.android.medox.fragment.StatusFragment;
import com.slothnull.android.medox.fragment.WarehouseFragment;

public class SeniorHome extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //TODO: update fragments for Senior
        //TODO: ALSO add checks and config class
        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new StatusFragment(),
                    new IndicatorsFragment(),
                    new NotificationFragment(),
                    new ScheduleFragment(),
                    new WarehouseFragment(),
                    new SeniorEmergencyFragment(),
                    new LocationFragment()
            };
            /*
            private final String[] mFragmentNames = new String[] {
                    "Status",
                    "Ind",
                    "Not",
                    "Sched",
                    "War",
                    "Emer",
                    "Loc"
            };*/
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            /*
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }*/
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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

        mViewPager.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                //TODO: call Emergency fragment
                //add code here
                return true;
            }
        });
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
        Authentication.signOut();
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
