/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.syncano.gcmsample;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;
    private State state = State.GETTING_TOKEN;
    private boolean activityResumed = false;
    private String token;

    private enum State {
        GETTING_TOKEN,
        FAILED,
        SUCCESS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        textView = (TextView) findViewById(R.id.informationTextView);
        imageView = (ImageView) findViewById(R.id.image);

        if (!checkPlayServices()) {
            // Device doesn't have play services. GCM will not work.
            state = State.FAILED;
            return;
        }

        // check if got token before
        token = PreferenceManager.getDefaultSharedPreferences(this).getString(PreferencesKeys.TOKEN, "");
        if (!token.isEmpty()) {
            Log.i(TAG, "Token is: " + token);
            // got it before
            state = State.SUCCESS;
            return;
        }

        // get GCM token and register it on Syncano
        (new GetGCMTokenTask(getApplicationContext()) {
            @Override
            protected void onPostExecute(String token) {
                MainActivity.this.token = token;
                state = token.isEmpty() ? State.FAILED : State.SUCCESS;
                refreshView();
            }
        }).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityResumed = true;
        refreshView();
    }

    private void refreshView() {
        if (!activityResumed) return;
        switch (state) {
            case GETTING_TOKEN:
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(ProgressBar.VISIBLE);
                textView.setText(R.string.token_getting);
                break;
            case FAILED:
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_error_outline_black_48dp);
                progressBar.setVisibility(ProgressBar.GONE);
                textView.setText(R.string.token_error);
                break;
            case SUCCESS:
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_done_black_48dp);
                progressBar.setVisibility(ProgressBar.GONE);
                textView.setText(String.format(getString(R.string.token_success), token));
                break;
        }
    }

    @Override
    protected void onPause() {
        activityResumed = false;
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
