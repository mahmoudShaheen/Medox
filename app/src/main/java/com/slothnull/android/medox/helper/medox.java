package com.slothnull.android.medox.helper;

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
 * also android:name="com.slothnull.android.medox.helper.medox" attribute should
 * be added to application tag in android manifest file in order to work
 */


/**
 * Also used for getting google api client from anywhere in the app
 * used for indicators service
 */

public class medox extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
    /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mInstance = this;
        googleApiHelper = new GoogleApiHelper(mInstance);
    }
    private GoogleApiHelper googleApiHelper;
    private static medox mInstance;



    public static synchronized medox getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }
    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }
}
