package com.slothnull.android.medox.model;

/**
 * Created by Shaheen on 30-Jun-17
 * Project: Medox
 * Package: com.slothnull.android.medox.model
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractStatus {

    public String heart;
    public String location;
    public String emergency;
    public String bills;

    public AbstractStatus() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractStatus(String heart, String location,
                          String emergency, String bills) {
        this.heart = heart;
        this.location = location;
        this.emergency = emergency;
        this.bills = bills;
    }

}
// [END user_class]