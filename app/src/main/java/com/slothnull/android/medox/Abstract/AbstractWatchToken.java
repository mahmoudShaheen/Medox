package com.slothnull.android.medox.Abstract;

/**
 * Created by Shaheen on 17-Mar-17
 * Project: seniormedox
 * Package: com.slothnull.android.medox
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractWatchToken {

    public String watch;

    public AbstractWatchToken() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractWatchToken(String token) {
        this.watch = token;
    }

}
