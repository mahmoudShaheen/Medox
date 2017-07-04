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
import com.slothnull.android.medox.fragment.ScheduleFragment;
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

    public ScheduleViewHolder(View itemView) {
        super(itemView);


        titleView = (TextView) itemView.findViewById(R.id.titleView);
        timeView = (TextView) itemView.findViewById(R.id.timeView);
        messageView = (TextView) itemView.findViewById(R.id.messageView);
        levelView = (TextView) itemView.findViewById(R.id.levelView);
    }

    public void bindToSchedule(AbstractSchedule schedule) {
        titleView.setText("Bills: ");
        timeView.setText(schedule.time);
        String[] billArray = schedule.billArray.split(",");
        String message ="";
        if (!billArray[0].equals("0"))
            message += "\n" + ScheduleFragment.drug1 + ":  " + billArray[0] ;
        if (!billArray[1].equals("0"))
            message += "\n" + ScheduleFragment.drug2 + ":  " + billArray[1] ;
        if (!billArray[2].equals("0"))
            message += "\n" + ScheduleFragment.drug3 + ":  " + billArray[2] ;
        if (!billArray[3].equals("0"))
            message += "\n" + ScheduleFragment.drug4 + ":  " + billArray[3] ;
        levelView.setText("");
        messageView.setText(message);
    }

}
