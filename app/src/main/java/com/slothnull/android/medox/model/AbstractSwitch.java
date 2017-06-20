package com.slothnull.android.medox.model;

/**
 * Created by Shaheen on 20-Jun-17
 * Project: Medox
 * Package: com.slothnull.android.medox.model
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractSwitch {

    public String switch1;
    public String switch2;

    public AbstractSwitch() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractSwitch(String switch1, String switch2) {
        this.switch1 = switch1;
        this.switch2 = switch2;
    }

}
// [END user_class]