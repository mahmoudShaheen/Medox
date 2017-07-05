package com.slothnull.android.medox.model;

/**
 * Created by Mahmoud Shaheen
 * Project: Medox
 * Licence: MIT
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractNotification {

    public String level;
    public String message;
    public String time;
    public String title;
    public String to;

    public AbstractNotification() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractNotification(String level, String title, String message, String time, String to) {
        this.level = level;
        this.message = message;
        this.time = time;
        this.title = title;
        this.to = to;
    }

}
// [END user_class]