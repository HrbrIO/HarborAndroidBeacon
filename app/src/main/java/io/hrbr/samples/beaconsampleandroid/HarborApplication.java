package io.hrbr.samples.beaconsampleandroid;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import io.hrbr.beacon.HarborLogger;

public class HarborApplication extends Application implements ActivityLifecycleCallbacks {

    private static final String TAG = "HarborApplication";

    public static HarborApplication thisApplication;

    protected HarborLogger mHarborLogger;

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    public HarborLogger getLogger() {
        return mHarborLogger;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thisApplication = this;

        registerActivityLifecycleCallbacks(this);
        Log.d(TAG, "onCreate");

        String mApiKey = "YOUR_ORGANIZATION_API_KEY_GOES_HERE";
        String mAppVersionId = "YOUR_APPLICATION_ID_GOES_HERE";
        String mBeaconVersionId = "YOUR_BEACON_ID_GOES_HERE";
        String mBeaconInstanceId = "A_UNIQUE_DEVICE_ID_GOES_HERE";

        boolean mWantsHeartbeat   = true;
        boolean mWantsGeoLocation = true;

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
