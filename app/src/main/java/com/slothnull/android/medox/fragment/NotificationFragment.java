package com.slothnull.android.medox.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.Abstract.AbstractNotification;
import com.slothnull.android.medox.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment implements View.OnClickListener {

    //TODO: error some times list view doesn't view any item "in first activity load only"
    //if you press back then go to activity again it works well
    private static final String TAG = "Notifications";
    public ListView notificationList;
    public ArrayAdapter arrayAdapter;
    public List<String> arrayList = new ArrayList<String>();

    View view;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification, container, false);

        notificationList = (ListView)view.findViewById(R.id.notificationList);
        refreshList();
        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getActivity(), arrayList.get(position), Toast.LENGTH_LONG).show();

            }
        });
        return view;
    }

    //TODO: refreshList adds notifications again
    public void refreshList(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener notificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    AbstractNotification notification = child.getValue(AbstractNotification.class);
                    if (notification != null) {
                        arrayList.add(notification.time + ":  " + notification.title + "\n" + notification.message);
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
        mDatabase.child("users").child(UID).child("notification")
                .addValueEventListener(notificationListener);
        //add to list here

        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        notificationList.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.refreshButton:
                refreshList();
                break;
        }
    }
}
