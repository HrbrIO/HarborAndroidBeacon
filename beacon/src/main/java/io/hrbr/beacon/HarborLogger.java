package io.hrbr.beacon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HarborLogger {

    private static final String TAG = "HarborLogger";

    // Activity lifecycle
    protected static final String APP_START_MSG = "APP_START_MSG";
    protected static final String APP_BG_MSG    = "APP_BG_MSG";
    protected static final String APP_FG_MSG    = "APP_FG_MSG";
    protected static final String APP_KILL_MSG  = "APP_KILL_MSG";
    protected static final String SCREEN_VIEW   = "SCREEN_VIEW";
    protected static final String SCREEN_DWELL  = "SCREEN_DWELL";

    protected static final String HEARTBEAT     = "HEARTBEAT";
    protected static final String GEOLOCATION   = "GEOLOCATION";

    protected static final int HEARTBEAT_INTERVAL_SECS = 60;

    private static Context mCtx;
    private boolean mWithHeartbeat;
    private boolean mWithGeolocation;
    private final Map<String, Long> mDwellTimers;

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private Handler mHeartBeatHandler;

    public BeaconSingleton mBeaconSingleton;

    public HarborLogger(Context context, String apiKey, String appVersionId, String beaconVersionId, String beaconInstanceId, boolean withHeartbeat, boolean withGeolocation) {
        mCtx = context;
        mWithHeartbeat = withHeartbeat;
        mWithGeolocation = withGeolocation;
        mDwellTimers = new HashMap<String, Long>();

        mBeaconSingleton = BeaconSingleton.getInstance(context, apiKey, appVersionId, beaconVersionId, beaconInstanceId);

        if (mWithHeartbeat)   setupHeartbeat();
        if (mWithGeolocation) setupGeolocation();
    }

    public void log(final String msgType, JSONObject json) {
        Log.d(TAG, "Sending Harbor Beacon of type: " + msgType);
        if (mBeaconSingleton != null) mBeaconSingleton.log(msgType, json);
    }

    public void log(final String msgType) {
        log(msgType, null);
    }

    public void appStart() {
        log(APP_START_MSG);
    }

    public void appBackground() {
        if (mWithGeolocation) stopLocationUpdates();
        if (mWithHeartbeat)   stopHeartbeat();
        log(APP_BG_MSG);
    }

    public void appForeground() {
        log(APP_FG_MSG);
        if (mWithHeartbeat)   startHeartbeat();
        if (mWithGeolocation) startLocationUpdates();
    }

    public void appKill() {
        log(APP_KILL_MSG);
    }


    /**
     * When an activity becomes visible Activity.onResume you should call this w/
     * the activity name.
     * - It immediately sends a SCREEN_VIEW Beacon
     * - It starts a timer to calculate a SCREEN_DWELL
     *
     * @param screenName
     */
    public void startScreenDwell(String screenName){

        JSONObject json = new JSONObject();
        try {
            json.put("screen", screenName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mDwellTimers.put(screenName, new Long(System.currentTimeMillis()));
        log(SCREEN_VIEW, json);
    }

    /**
     * When an activity becomes invisible Activity.onPause you should call this w/
     * the activity name
     * - It stops the timer and sends the SCREEN_DWELL Beacon
     *
     * @param screenName
     */
    public void stopScreenDwell(String screenName) {

        if (mDwellTimers.containsKey(screenName)) {
            Long startTime = mDwellTimers.remove(screenName);
            Long dwellTime = System.currentTimeMillis() - startTime;

            JSONObject json = new JSONObject();
            try {
                json.put("time", dwellTime.longValue());
                json.put("screen", screenName);

                log(SCREEN_DWELL, json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Attempt to stop a screen dwell timer called " + screenName + " that does not exist");
        }
    }

    public void logLocation(Location loc) {
        JSONObject json = new JSONObject();
        try {
            json.put("latitude",    loc.getLatitude());
            json.put("longitude",    loc.getLongitude());
            json.put("altitude",     loc.getAltitude());
            json.put("hz_accuracy",  loc.getAccuracy());
            // Need API-26 for this
            //json.put("vrt_accuracy", loc.getVerticalAccuracyMeters());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(GEOLOCATION, json);
    }

    private void setupGeolocation() {
        if (!mWithGeolocation) return;
        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mCtx);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "Location callback");
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    if (location != null) {
                        // Logic to handle location object
                        Log.d(TAG, "Loc: " + location);
                        logLocation(location);
                    } else {
                        Log.d(TAG, "Loc is null");
                    }
                }
            };
        };

    }

    private void stopLocationUpdates() {
        if (!mWithGeolocation) return;
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void startLocationUpdates() {
        if (!mWithGeolocation) return;
        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (mFusedLocationClient != null) {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(30000);
            mLocationRequest.setFastestInterval(15000);
            //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }

    }


    private void setupHeartbeat() {
        if (!mWithHeartbeat) return;
        HandlerThread handlerThread = new HandlerThread("HarborLoggerHandlerThread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        mHeartBeatHandler = new Handler(looper);
    }

    private void stopHeartbeat() {
        if (!mWithHeartbeat) return;

        if (mHeartBeatHandler != null) {
            mHeartBeatHandler.removeCallbacksAndMessages(null);
        }
    }

    private void startHeartbeat() {
        if (!mWithHeartbeat) return;
        stopHeartbeat();
        Runnable heartbeat = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Heartbeat running...");
                log(HEARTBEAT);
                if (mHeartBeatHandler != null) {
                    // Schedule another runnable on loop
                    mHeartBeatHandler.postDelayed(this, 1000 * HEARTBEAT_INTERVAL_SECS);
                }
            }
        };

        // Put a runnable immediately on loop
        if (mHeartBeatHandler != null) {
            mHeartBeatHandler.post(heartbeat);
        }
    }
}