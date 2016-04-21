package com.syncano.gcmsample;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.syncano.library.Syncano;
import com.syncano.library.data.PushDevice;

import java.io.IOException;

public class GetGCMTokenTask extends AsyncTask<Void, Void, String> {
    private Context ctx;
    private final static String TAG = GetGCMTokenTask.class.getSimpleName();

    public GetGCMTokenTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(Void... params) {
        // get gcm token from Google
        String token = getGCMTokenFromGoogle();
        if (token == null || token.isEmpty()) {
            Log.w(TAG, "Error getting GCM token");
            return "";
        }
        Log.i(TAG, "GCM Registration Token: " + token);

        // send gcm token to Syncano
        if (!Syncano.getInstance().registerPushDevice(new PushDevice(token)).send().isSuccess()) {
            return "";
        }

        // save token
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString(PreferencesKeys.TOKEN, token).apply();

        return token;
    }

    private String getGCMTokenFromGoogle() {
        try {
            InstanceID instanceID = InstanceID.getInstance(ctx);
            return instanceID.getToken(ctx.getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
