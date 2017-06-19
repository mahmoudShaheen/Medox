package com.slothnull.android.medox.model;

/**
 * Created by Shaheen on 19-Jun-17
 * Project: Medox
 * Package: com.slothnull.android.medox.model
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractSensor {

    public String temperature;
    public String light;

    public AbstractSensor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractSensor(String temperature, String light) {
        this.temperature = temperature;
        this.light = light;
    }

}
// [END user_class]