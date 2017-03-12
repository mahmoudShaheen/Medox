package com.slothnull.android.medox;

/**
 * Created by Shaheen on 12-Mar-17
 * Project: Medox
 * Package: com.slothnull.android.medox
 */


import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractData {

    public String heartRate;
    public String pedo;
    public String longitude;
    public String latitude;

    public AbstractData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractData(String heartRate, String pedo, String longitude, String latitude) {
        this.heartRate = heartRate;
        this.pedo = pedo;
        this.longitude = longitude;
        this.latitude = latitude;
    }

}
// [END user_class]