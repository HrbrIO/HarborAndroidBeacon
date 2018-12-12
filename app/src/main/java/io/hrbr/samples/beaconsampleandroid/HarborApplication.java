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

    private static String mApiKey = "YOUR_ORGANIZATION_API_KEY_GOES_HERE";
    private static String mAppVersionId = "YOUR_APPLICATION_ID_GOES_HERE";
    private static String mBeaconVersionId = "YOUR_BEACON_ID_GOES_HERE";
    private static String mBeaconInstanceId = "A_UNIQUE_DEVICE_ID_GOES_HERE";

    public static HarborApplication thisApplication;

    public static Context sharedContext;

    protected static HarborLogger mHarborLogger;

    private boolean wantsHeartbeat   = true;
    private boolean wantsGeoLocation = true;

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    public static HarborLogger getLogger() {
        return mHarborLogger;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thisApplication = this;
        sharedContext = getApplicationContext();
        registerActivityLifecycleCallbacks(this);
        Log.d(TAG, "onCreate");

        mHarborLogger = new HarborLogger(sharedContext, mApiKey, mAppVersionId, mBeaconVersionId, mBeaconInstanceId, wantsHeartbeat, wantsGeoLocation);
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
