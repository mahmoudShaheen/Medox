package com.slothnull.android.medox.model;

/**
 * Created by Shaheen on 17-Mar-17
 * Project: seniormedox
 * Package: com.slothnull.android.seniormedox
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractMobileToken {

    public String mobile;

    public AbstractMobileToken() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractMobileToken(String token) {
        this.mobile = token;
    }

}
