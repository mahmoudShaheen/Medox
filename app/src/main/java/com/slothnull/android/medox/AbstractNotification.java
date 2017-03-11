package com.slothnull.android.medox;

/**
 * Created by Shaheen on 11-Mar-17
 * Project: Medox
 * Package: com.slothnull.android.medox
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractNotification {

    public String level;
    public String message;
    public String time;
    public String title;

    public AbstractNotification() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractNotification(String level, String title, String message, String time) {
        this.level = level;
        this.message = message;
        this.time = time;
        this.title = title;
    }

}
// [END user_class]