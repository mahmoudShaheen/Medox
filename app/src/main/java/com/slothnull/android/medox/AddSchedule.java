package com.slothnull.android.medox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.model.AbstractSchedule;
import com.slothnull.android.medox.model.AbstractWarehouse;

public class AddSchedule extends AppCompatActivity {

    private static final String TAG = "AddScheduleActivity";

    private TextView drug1;
    private TextView drug2;
    private TextView drug3;
    private TextView drug4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);


        drug1 = (TextView) findViewById(R.id.drug1View);
        drug2 = (TextView) findViewById(R.id.drug2View);
        drug3 = (TextView) findViewById(R.id.drug3View);
        drug4 = (TextView) findViewById(R.id.drug4View);
        getNames();
    }

    public void addEntry(View view){
        TimePicker timePicker =  (TimePicker) findViewById(R.id.timePicker);
        String billArray ="";
        String bills;
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

    public void getNames(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener warehouseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    AbstractWarehouse warehouse = child.getValue(AbstractWarehouse.class);
                    if (warehouse.id != null) {
                        switch (warehouse.id) {
                            case "1":
                                drug1.setText(warehouse.name);
                                break;
                            case "2":
                                drug2.setText(warehouse.name);
                                break;
                            case "3":
                                drug3.setText(warehouse.name);
                                break;
                            case "4":
                                drug4.setText(warehouse.name);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("users").child(UID).child("warehouse")
                .addValueEventListener(warehouseListener);
        //add to list here

    }
}
