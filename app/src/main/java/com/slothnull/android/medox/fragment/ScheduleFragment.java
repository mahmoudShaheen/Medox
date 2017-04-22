package com.slothnull.android.medox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.slothnull.android.medox.AddSchedule;
import com.slothnull.android.medox.R;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = "Schedule";
    public ListView scheduleList;
    public ArrayAdapter arrayAdapter;
    public List<String> arrayList = new ArrayList<>();
    public List<String> keyList = new ArrayList<>();

    View view;
    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_schedule, container, false);

        scheduleList = (ListView)view.findViewById(R.id.listSchedule);
        view.findViewById(R.id.addEntry).setOnClickListener(this);
        refreshList();
        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteEntry(keyList.get(position));
            }
        });
        return view;
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
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        scheduleList.setAdapter(arrayAdapter);
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

    @Override
    public void onClick(View view) {
        //do what you want to do when button is clicked
        switch (view.getId()) {
            case R.id.addEntry:
                Intent intent = new Intent(getActivity(), AddSchedule.class);
                startActivity(intent);
                break;
        }
    }

}
