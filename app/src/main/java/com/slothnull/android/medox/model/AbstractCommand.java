package com.slothnull.android.medox.model;

/**
 * Created by Mahmoud Shaheen
 * Project: Medox
 * Licence: MIT
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractCommand {

    public String cmd;

    public AbstractCommand() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractCommand(String cmd) {
        this.cmd = cmd;
    }

}
// [END user_class]
