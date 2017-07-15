package com.slothnull.android.medox.model;

/**
 * Created by Mahmoud Shaheen
 * Project: Medox
 * Licence: MIT
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractUser {

    public String username;
    public String email;

    public AbstractUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
// [END user_class]