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
import com.slothnull.android.medox.Abstract.AbstractSchedule;

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
        //TODO: error here if called more than once the list display any item twice
        //TODO: new activity for adding new time, this will also solve the problem
        /*
        //arrayList = new ArrayList<>();
        //keyList = new ArrayList<>();
        arrayList.clear();
        Log.d(TAG, Integer.toString( arrayList.size()));

        keyList.clear();
        Log.d(TAG, Integer.toString( keyList.size()));


        if (arrayAdapter != null) {
            arrayAdapter.clear();
            scheduleList.setAdapter(arrayAdapter);
        }
        */

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener timetableListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    AbstractSchedule timetable = child.getValue(AbstractSchedule.class);
                    if (timetable != null) {
                        arrayList.add( timetable.time  + "\n"  + timetable.billArray );
                        keyList.add(child.getKey());
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
        mDatabase.child("users").child(UID).child("timetable")
                .addValueEventListener(timetableListener);
        //add to list here
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        scheduleList.setAdapter(arrayAdapter);
    }

    public void addEntry(View view){
        TextView time =  (TextView) findViewById(R.id.textTime);
        TextView billArray =  (TextView) findViewById(R.id.textBillArray);
        //add new schedule
        AbstractSchedule timetable = new AbstractSchedule(time.getText().toString(), billArray.getText().toString());
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("timetable").push();
        mDatabase.setValue(timetable);
        //refreshList();
    }

    public void deleteEntry(String key){
        //TODO error after deleting from adapter the error message is mentioned after the function
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID).child("timetable");
        mDatabase.child(key).setValue(null);
        //refreshList();
    }
    /*
    ava.lang.IllegalStateException: The content of the adapter has changed but ListView did not
    receive a notification. Make sure the content of your adapter is not modified from a background
    thread, but only from the UI thread. Make sure your adapter calls notifyDataSetChanged() when
    its content changes. [in ListView(2131427456, class android.widget.ListView)
    with Adapter(class android.widget.ArrayAdapter)]
     */

}
