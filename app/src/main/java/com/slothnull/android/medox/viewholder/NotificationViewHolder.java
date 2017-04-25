package com.slothnull.android.medox.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.slothnull.android.medox.Abstract.AbstractNotification;
import com.slothnull.android.medox.R;

/**
 * Created by Shaheen on 21-Apr-17
 * Project: Medox
 * Package: com.slothnull.android.medox.viewholder
 */

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    public TextView titleView;
    public TextView timeView;
    public TextView messageView;
    public TextView levelView;

    public NotificationViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.titleView);
        timeView = (TextView) itemView.findViewById(R.id.timeView);
        messageView = (TextView) itemView.findViewById(R.id.messageView);
        levelView = (TextView) itemView.findViewById(R.id.levelView);
    }

    public void bindToNotification(AbstractNotification notification) {
        titleView.setText(notification.title);
        timeView.setText(notification.time);
        if(notification.level.equals("1"))
            levelView.setText("Emergency");
        messageView.setText(notification.message);
    }
}
