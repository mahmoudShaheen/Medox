package com.slothnull.android.medox.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ShakeService extends Service {
    public ShakeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
