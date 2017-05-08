package com.slothnull.android.medox.offline;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shaheen on 07-May-17
 * Project: Medox
 * Package: com.slothnull.android.medox
 */

/**
 * Description:
 * class used for firebase to work offline, uses the same name as application
 * and extends android.app.Application
 * also android:name="com.slothnull.android.medox.offline.medox" attribute should
 * be added toapplication tag in android manifest file in order to work
 */

public class medox extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
    /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
