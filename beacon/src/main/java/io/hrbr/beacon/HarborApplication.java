/*
 * Copyright © 2018 HarborIO, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hrbr.beacon;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * This is an abstract class for creating an Application that will send a
 * variety of Harbor Beacon messages.
 *
 */
public abstract class HarborApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "HarborApplication";

    public static HarborApplication thisApplication;

    protected HarborLogger mHarborLogger;

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    /**
     * Method to enable/disable Harbor Heartbeat updates
     *
     * @return true|false
     */
    public abstract boolean wantsHeartbeat();

    /**
     * Method to enable/disable Harbor Geolocation updates
     *
     * @return true|false
     */
    public abstract boolean wantsGeolocation();

    /**
     * Method to return your Harbor Organization API Key
     *
     * @return "YOUR_ORGANIZATION_API_KEY_GOES_HERE"
     */
    protected abstract String getApiKey();

    /**
     * Method to return your Harbor Application ID
     *
     * @return "YOUR_APPLICATION_ID_GOES_HERE"
     */
    public abstract String getAppVersionId();

    /**
     * Method to return your Harbor Beacon Version ID
     *
     * @return "YOUR_BEACON_ID_GOES_HERE"
     */
    public abstract String getBeaconVersionId();

    /**
     * Method to return your Harbor Beacon Instance ID
     *
     * @return "A_UNIQUE_DEVICE_ID_GOES_HERE"
     */
    public abstract String getBeaconInstanceId();

    /**
     * Method to return the {@link io.hrbr.beacon.HarborLogger} instance
     *
     * @return
     */
    public HarborLogger getLogger() {
        return mHarborLogger;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thisApplication = this;

        registerActivityLifecycleCallbacks(this);
        Log.d(TAG, "onCreate");

        String mApiKey = getApiKey();
        String mAppVersionId = getAppVersionId();
        String mBeaconVersionId = getBeaconVersionId();
        String mBeaconInstanceId = getBeaconInstanceId();

        boolean mWantsHeartbeat   = wantsHeartbeat();
        boolean mWantsGeoLocation = wantsGeolocation();

        Context sharedContext = getApplicationContext();
        mHarborLogger = new HarborLogger(sharedContext, mApiKey, mAppVersionId, mBeaconVersionId, mBeaconInstanceId, mWantsHeartbeat, mWantsGeoLocation);
        mHarborLogger.appStart();
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, "onActivityStarted");
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            // App enters foreground
            Log.d(TAG, "appForeground");
            mHarborLogger.appForeground();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "onActivityStopped");
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            // App enters background
            Log.d(TAG, "appBackground");
            mHarborLogger.appBackground();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "onActivityDestroyed");
        mHarborLogger.appKill();
    }
}
