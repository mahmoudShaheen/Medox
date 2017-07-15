package com.slothnull.android.medox.model;

/**
 * Created by Mahmoud Shaheen
 * Project: Medox
 * Licence: MIT
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractConfig {

    public String maxDistance;
    public String homeLatitude;
    public String homeLongitude;
    public String maxHeartRate;
    public String minHeartRate;
    public String mobileNumber;
    public String mobileNumber2;
    public String careSkype;
    public String seniorSkype;
    public String enabled;

    public AbstractConfig() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public AbstractConfig(String maxDistance,
            String homeLatitude,
            String homeLongitude,
            String maxHeartRate,
            String minHeartRate,
            String mobileNumber,
            String mobileNumber2,
            String careSkype,
            String seniorSkype,
            String enabled){
        this.maxDistance = maxDistance;
        this.homeLatitude = homeLatitude;
        this.homeLongitude = homeLongitude;
        this.maxHeartRate = maxHeartRate;
        this.minHeartRate = minHeartRate;
        this.mobileNumber = mobileNumber;
        this.mobileNumber2 = mobileNumber2;
        this.careSkype = careSkype;
        this.seniorSkype = seniorSkype;
        this.enabled = enabled;
    }

}
