package com.slothnull.android.medox.model;

/**
 * Created by Mahmoud Shaheen
 * Project: Medox
 * Licence: MIT
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractData {

    public String heartRate;
    public String pedo;
    public String calories;
    public String longitude;
    public String latitude;
    public String billCount;

    public AbstractData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractData(String heartRate, String pedo, String longitude, String latitude, String billCount, String calories) {
        this.heartRate = heartRate;
        this.pedo = pedo;
        this.calories = calories;
        this.longitude = longitude;
        this.latitude = latitude;
        this.billCount = billCount;
    }

}
// [END user_class]