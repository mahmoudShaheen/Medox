package com.slothnull.android.medox.model;

/**
 * Created by Mahmoud Shaheen
 * Project: Medox
 * Licence: MIT
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractSwitch {

    public String switch1;
    public String switch2;
    public String switch3;
    public String switch4;
    public String switch5;
    public String switch6;
    public String switch7;
    public String switch8;

    public AbstractSwitch() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractSwitch(String switch1, String switch2,
                          String switch3, String switch4,
                          String switch5, String switch6,
                          String switch7, String switch8) {
        this.switch1 = switch1;
        this.switch2 = switch2;
        this.switch3 = switch3;
        this.switch4 = switch4;
        this.switch5 = switch5;
        this.switch6 = switch6;
        this.switch7 = switch7;
        this.switch8 = switch8;
    }

}
// [END user_class]