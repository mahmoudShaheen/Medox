package com.slothnull.android.medox;

/**
 * Created by Shaheen on 12-Mar-17
 * Project: Medox
 * Package: com.slothnull.android.medox
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
