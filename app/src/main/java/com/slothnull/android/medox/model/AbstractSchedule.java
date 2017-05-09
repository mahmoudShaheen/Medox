package com.slothnull.android.medox.model;

/**
 * Created by Shaheen on 13-Mar-17
 * Project: Medox
 * Package: com.slothnull.android.medox
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractSchedule {

    public String time;
    public String billArray;

    public AbstractSchedule() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractSchedule(String time, String billArray) {
        this.time = time;
        this.billArray = billArray;
    }

}
// [END user_class]