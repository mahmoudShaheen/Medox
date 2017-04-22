package com.slothnull.android.medox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.slothnull.android.medox.Abstract.AbstractSchedule;

public class AddSchedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
    }

    public void addEntry(View view){
        TimePicker timePicker =  (TimePicker) findViewById(R.id.timePicker);
        String billArray ="";
        String bills = "";
        String hour = timePicker.getCurrentHour().toString();
        String minute = timePicker.getCurrentMinute().toString();
        if(minute.equals("0")){
            minute += "0";
        }
        String time = hour + ":" + minute + ":00";
        bills = "";
        bills += ((TextView)findViewById(R.id.drug1Picker)).getText();
        billArray += getBills(bills);
        billArray += ",";
        bills = "";
        bills +=((TextView)findViewById(R.id.drug2Picker)).getText();
        billArray += getBills(bills);
        billArray += ",";
        bills = "";
        bills += ((TextView)findViewById(R.id.drug3Picker)).getText();
        billArray += getBills(bills);
        billArray += ",";
        bills = "";
        bills += ((TextView)findViewById(R.id.drug4Picker)).getText();
        billArray += getBills(bills);
        //add new schedule
        AbstractSchedule timetable = new AbstractSchedule(time, billArray);
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("timetable").push();
        mDatabase.setValue(timetable);
        finish();
    }
    private String getBills(String bills){
        if (bills.isEmpty()){
            bills = "0";
        }
        return bills;
    }
}
