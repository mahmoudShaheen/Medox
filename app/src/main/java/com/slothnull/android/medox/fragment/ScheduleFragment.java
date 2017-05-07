package com.slothnull.android.medox.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractConfig;
import com.slothnull.android.medox.Abstract.AbstractSchedule;
import com.slothnull.android.medox.AddSchedule;
import com.slothnull.android.medox.R;
import com.slothnull.android.medox.viewholder.ScheduleViewHolder;

public class ScheduleFragment extends Fragment implements View.OnClickListener  {

    private static final String TAG = "ScheduleFragment";

    private AlertDialog.Builder builder ;

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<AbstractSchedule, ScheduleViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private boolean deleteEnable = false; //to disable delete for senior if not enabled by care

    FloatingActionButton addButton;


    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        getConfig();

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        addButton = (FloatingActionButton) rootView.findViewById(R.id.addEntry);
        addButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<AbstractSchedule, ScheduleViewHolder>(
                AbstractSchedule.class, R.layout.item_schedule,
                ScheduleViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final ScheduleViewHolder viewHolder,
                                              final AbstractSchedule model, final int position) {
                final DatabaseReference scheduleRef = getRef(position);

                // Set click listener for the whole post view
                final String scheduleKey = scheduleRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buildCheckDialog(scheduleKey);
                        builder.show();
                    }
                });
                viewHolder.bindToSchedule(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(DatabaseReference databaseReference){

        Query scheduleQuery = databaseReference.child("users").child(getUid())
                .child("timetable")
                .limitToFirst(100);
        return scheduleQuery;
    }


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

    public void deleteEntry(String key){
        if(deleteEnable){
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(UID).child("timetable");
            mDatabase.child(key).setValue(null);
        }else{
            Toast.makeText(getActivity(), "Delete is denied by Care Giver!",
                    Toast.LENGTH_LONG).show();
        }
    }


    private void buildCheckDialog(String scheduleKey){
        final String key = scheduleKey;
        builder = new AlertDialog.Builder(getActivity());
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteEntry(key);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        builder.setMessage("Delete Schedule, Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
    }

    public void getConfig() {
        //disable only for senior
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                getActivity().getPackageName(), Context.MODE_PRIVATE);
        final String appType = sharedPreferences.getString("appType", "");
        if(appType.equals("care"))
            return;
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener configListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                AbstractConfig oldConfig = dataSnapshot.getValue(AbstractConfig.class);
                if(oldConfig.enabled != null){
                    String[] checkArray = new String[3];
                    checkArray= oldConfig.enabled.split(",");
                    if(checkArray[2].equals("0")){ //schedule
                        addButton.setEnabled(false);
                        deleteEnable = false;
                    }
                    if(checkArray[2].equals("1")){ //schedule
                        addButton.setEnabled(true);
                        deleteEnable = true;
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
        mDatabase.child("users").child(UID).child("config")
                .addValueEventListener(configListener);
    }

}
