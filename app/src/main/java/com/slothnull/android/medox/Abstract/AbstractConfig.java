package com.slothnull.android.medox.Abstract;

/**
 * Created by Shaheen on 20-Apr-17
 * Project: Medox
 * Package: com.slothnull.android.medox.Abstract
 */

/**
 * Created by Shaheen on 17-Mar-17
 * Project: seniormedox
 * Package: com.slothnull.android.seniormedox
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

    public AbstractConfig() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

}
