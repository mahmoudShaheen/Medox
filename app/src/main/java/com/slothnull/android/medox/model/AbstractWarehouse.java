package com.slothnull.android.medox.model;

/**
 * Created by Shaheen on 13-Mar-17
 * Project: Medox
 * Package: com.slothnull.android.medox
 */

import com.google.firebase.database.IgnoreExtraProperties;

    // [START user_class]
    @IgnoreExtraProperties
    public class AbstractWarehouse {

        public String id;
        public String name;

        public AbstractWarehouse() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public AbstractWarehouse(String id, String name) {
            this.id = id;
            this.name = name;
        }

    }
// [END user_class]
