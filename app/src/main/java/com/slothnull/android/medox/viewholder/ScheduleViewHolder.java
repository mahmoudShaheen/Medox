package com.slothnull.android.medox.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.slothnull.android.medox.Abstract.AbstractSchedule;
import com.slothnull.android.medox.R;

/**
 * Created by Shaheen on 22-Apr-17
 * Project: Medox
 * Package: com.slothnull.android.medox.viewholder
 */

public class ScheduleViewHolder extends RecyclerView.ViewHolder {
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
        String message =
                "\nDrug[1]: " +
                billArray[0] +
                "\nDrug[2]: " +
                billArray[1] +
                "\nDrug[3]: " +
                billArray[2] +
                "\nDrug[4]: " +
                billArray[3] ;
        levelView.setText("");
        messageView.setText(message);
    }
}
