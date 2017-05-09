package com.slothnull.android.medox.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.slothnull.android.medox.model.AbstractSchedule;
import com.slothnull.android.medox.model.AbstractWarehouse;
import com.slothnull.android.medox.R;

/**
 * Created by Shaheen on 22-Apr-17
 * Project: Medox
 * Package: com.slothnull.android.medox.viewholder
 */

public class ScheduleViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "ScheduleViewHolder";

    public TextView titleView;
    public TextView timeView;
    public TextView messageView;
    public TextView levelView;
    private String drug1;
    private String drug2;
    private String drug3;
    private String drug4;

    public ScheduleViewHolder(View itemView) {
        super(itemView);

        drug1 = "Drug[1]:  ";
        drug2 = "Drug[2]:  ";
        drug3 = "Drug[3]:  ";
        drug4 = "Drug[4]:  ";

        titleView = (TextView) itemView.findViewById(R.id.titleView);
        timeView = (TextView) itemView.findViewById(R.id.timeView);
        messageView = (TextView) itemView.findViewById(R.id.messageView);
        levelView = (TextView) itemView.findViewById(R.id.levelView);
    }

    public void bindToSchedule(AbstractSchedule schedule) {
        getNames();
        titleView.setText("Bills: ");
        timeView.setText(schedule.time);
        String[] billArray = schedule.billArray.split(",");
        String message =
                "\n" + drug1 + ":  " +
                billArray[0] +
                "\n" + drug2 + ":  " +
                billArray[1] +
                "\n" + drug3 + ":  " +
                billArray[2] +
                "\n" + drug4 + ":  " +
                billArray[3] ;
        levelView.setText("");
        messageView.setText(message);
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
                                drug1 = (warehouse.name);
                                break;
                            case "2":
                                drug2 = (warehouse.name);
                                break;
                            case "3":
                                drug3 = (warehouse.name);
                                break;
                            case "4":
                                drug4 = (warehouse.name);
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
