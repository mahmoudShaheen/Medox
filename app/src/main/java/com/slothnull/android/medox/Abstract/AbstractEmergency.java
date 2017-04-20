package com.slothnull.android.medox.Abstract;

/**
 * Created by Shaheen on 20-Apr-17
 * Project: Medox
 * Package: com.slothnull.android.medox.Abstract
 */

/**
 * Created by Shaheen on 17-Mar-17
 * Project: seniormedox
 * Package: com.slothnull.android.seniormedox
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractEmergency {

    public String to;
    public String level;
    public String title;
    public String message;

    public AbstractEmergency() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractEmergency(String to, String level, String title, String message) {
        this.to = to;
        this.level = level;
        this.title = title;
        this.message = message;
    }

}
// [END user_class]
