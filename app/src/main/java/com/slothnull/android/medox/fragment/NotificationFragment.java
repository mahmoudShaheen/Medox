package com.slothnull.android.medox.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.slothnull.android.medox.model.AbstractNotification;
import com.slothnull.android.medox.R;
import com.slothnull.android.medox.viewholder.NotificationViewHolder;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<AbstractNotification, NotificationViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

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
        mAdapter = new FirebaseRecyclerAdapter<AbstractNotification, NotificationViewHolder>(
                AbstractNotification.class, R.layout.item_notification,
                NotificationViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final NotificationViewHolder viewHolder,
                                              final AbstractNotification model, final int position) {

                viewHolder.bindToNotification(model);
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
        Query recentNotificationsQuery = databaseReference;
        SharedPreferences sharedPreferences = getContext()
                .getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
        String appType = sharedPreferences.getString("appType","");
        Log.i(TAG, appType );
        if (appType.equals("care")){
            recentNotificationsQuery = databaseReference.child("users").child(getUid())
                    .child("notification")
                    .limitToFirst(100);
        }else if( appType.equals("senior") ){
            recentNotificationsQuery = databaseReference.child("users").child(getUid())
                    .child("watchNotification")
                    .limitToFirst(100);
        }else{ //user signed but undefined app type
            Log.i(TAG, "undefined app type");
            getActivity().finish();
        }
        return recentNotificationsQuery;
    }
}
