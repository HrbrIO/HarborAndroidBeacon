package io.hrbr.beacon;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BeaconSingleton {

    protected static final String TAG = "BeaconSingleton";

    protected static final long AUTO_TIMESTAMP = -1;

    private static BeaconSingleton mInstance;
    private static Context mCtx;

    private RequestQueue mRequestQueue;

    private String mApiKey;
    private String mAppVersionId;
    private String mBeaconVersionId;
    private String mBeaconInstanceId;

    private BeaconSingleton(Context context, String apiKey, String appVersionId, String beaconVersionId, String beaconInstanceId) {
        mCtx = context;

        mApiKey           = apiKey;
        mAppVersionId     = appVersionId;
        mBeaconVersionId  = beaconVersionId;
        mBeaconInstanceId = beaconInstanceId;

        mRequestQueue = getRequestQueue();
    }

    public static synchronized BeaconSingleton getInstance(Context context, String apiKey, String appVersionId, String beaconVersionId, String beaconInstanceId) {
        if (mInstance == null) {
            mInstance = new BeaconSingleton(context, apiKey, appVersionId, beaconVersionId, beaconInstanceId);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void log(final String msgType, JSONObject json, long timestamp, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        BeaconRequest request = new BeaconRequest(
                mApiKey,
                mAppVersionId,
                mBeaconVersionId,
                mBeaconInstanceId,
                timestamp,
                msgType,
                json,
                listener, errorListener);

        addToRequestQueue(request);
    }

    public void log(final String msgType, JSONObject json, long timestamp) {
        log(msgType, json, timestamp, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.v(TAG, msgType + " message OK");
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    Log.e(TAG, msgType + " message ERR: " + error.toString());
                }
            });
    }

    public void log(final String msgType, JSONObject json) {
        log(msgType, json, AUTO_TIMESTAMP);
    }

    public void log(final String msgType) {
        log(msgType, null);
    }

    private class BeaconRequest extends JsonObjectRequest {

        protected static final String BASE_URL = "https://harbor-stream.hrbr.io/beacon";

        private String mApiKey;
        private String mAppVersionId;
        private String mBeaconVersionId;
        private String mBeaconInstanceId;
        private long   mTimestamp;

        private final String mBeaconMessageType;

        public BeaconRequest(
                String apiKey, String appVersionId, String beaconVersionId, String beaconInstanceId,
                long timestamp,
                String beaconMessageType, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(
                    BASE_URL,
                    jsonRequest == null ? new JSONObject() : jsonRequest,
                    listener,
                    errorListener);
            mApiKey            = apiKey;
            mAppVersionId      = appVersionId;
            mBeaconVersionId   = beaconVersionId;
            mBeaconInstanceId  = beaconInstanceId;
            mTimestamp         = (timestamp == AUTO_TIMESTAMP) ? System.currentTimeMillis() : timestamp;
            mBeaconMessageType = beaconMessageType;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String>  params = new HashMap<String, String>();

            params.put("Content-Type", "application/json");
            params.put("Accept", "application/json");

            params.put("apikey", mApiKey);
            params.put("appVersionId", mAppVersionId);
            params.put("beaconVersionId", mBeaconVersionId);
            params.put("dataTimestamp", "" + mTimestamp);

            params.put("beaconinstanceid", mBeaconInstanceId);

            params.put("beaconMessageType", mBeaconMessageType);

            return params;
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = "{}";
                return Response.success(
                        new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                //} catch (UnsupportedEncodingException e) {
                //    return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }

    }

}

