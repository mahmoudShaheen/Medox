package com.slothnull.android.medox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Schedule extends Activity {

    private static final String TAG = "Schedule";
    public ListView scheduleList;
    public ArrayAdapter arrayAdapter;
    public List<String> arrayList = new ArrayList<>();
    public List<String> keyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        scheduleList = (ListView)findViewById(R.id.listSchedule);
        refreshList();
        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteEntry(keyList.get(position));
            }
        });

    }

    private void refreshList(){

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener notificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractSchedule timetable = dataSnapshot.getValue(AbstractSchedule.class);
                if (timetable != null) {
                    arrayList.add( timetable.time  + "\n"  + timetable.billArray );
                    keyList.add(dataSnapshot.getKey());
                    // ...
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("users").child(UID).child("notification")
                .addValueEventListener(notificationListener);
        //add to list here


        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        scheduleList.setAdapter(arrayAdapter);
    }

    public void addEntry(View view){
        TextView time =  (TextView) findViewById(R.id.textTime);
        TextView billArray =  (TextView) findViewById(R.id.textBillArray);
        //add new schedule
        AbstractSchedule timetable = new AbstractSchedule((String) time.getText().toString(), (String) billArray.getText().toString());
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("timetable").push();
        mDatabase.setValue(timetable);
    }

    public void deleteEntry(String key){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("timetable");
        mDatabase.child(key).setValue(null);
    }

}
