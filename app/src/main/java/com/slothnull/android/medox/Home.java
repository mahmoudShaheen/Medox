package com.slothnull.android.medox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void auth(View view){
        Intent intent = new Intent(this, Authentication.class);
        startActivity(intent);
    }
    public void notification(View view){
        Intent intent = new Intent(this, Notifications.class);
        startActivity(intent);
    }
    public void welcome(View view){
        Intent intent = new Intent(this, Welcome.class);
        startActivity(intent);
    }
    public void location(View view){
        Intent intent = new Intent(this, Location.class);
        startActivity(intent);
    }
    public void indicators(View view){
        Intent intent = new Intent(this, Indicators.class);
        startActivity(intent);
    }
    public void schedule(View view){
        Intent intent = new Intent(this, Schedule.class);
        startActivity(intent);
    }
    public void warehouse(View view){
        Intent intent = new Intent(this, Warehouse.class);
        startActivity(intent);
    }
    public void settings(View view){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
    public void emergency(View view){
        Intent intent = new Intent(this, Emergency.class);
        startActivity(intent);
    }
}
