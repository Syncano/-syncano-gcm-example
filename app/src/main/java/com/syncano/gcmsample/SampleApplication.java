package com.syncano.gcmsample;

import android.app.Application;

import com.syncano.library.SyncanoBuilder;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new SyncanoBuilder().androidContext(getApplicationContext()).apiKey(BuildConfig.SYNCANO_API_KEY)
                .instanceName(BuildConfig.SYNCANO_INSTANCE_NAME).setAsGlobalInstance(true).useLoggedUserStorage(true)
                .build();
    }
}
