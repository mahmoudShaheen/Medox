package com.slothnull.android.medox.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Shaheen on 01-Jul-17
 * Project: Medox
 * Package: com.slothnull.android.medox.model
 */

// [START user_class]
@IgnoreExtraProperties
public class AbstractProfile {

    public String name;
    public String sex;
    public String birth;
    public String height;
    public String weight;
    public String blood;
    public String address;
    public String phone;
    public String emergency;
    public String martial;
    public String diseases;
    public String allergic;

    public AbstractProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractProfile(String name, String sex, String birth, String height,
                           String weight, String blood, String address, String phone,
                           String emergency, String martial, String diseases, String allergic) {
        this.name = name;
        this.sex = sex;
        this.birth = birth;
        this.height = height;
        this.weight = weight;
        this.blood = blood;
        this.address = address;
        this.phone = phone;
        this.emergency = emergency;
        this.martial = martial;
        this.diseases = diseases;
        this.allergic = allergic;
    }

}
// [END user_class]