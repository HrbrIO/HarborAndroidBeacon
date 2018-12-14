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

/**
 * A singleton for sending requests to the Harbor Beacon service.
 *
 * <p>Calling {@link #log(String, JSONObject)} will enqueue the given Request for dispatch,
 * resolving from either cache or network on a worker thread, and then delivering a parsed
 * response on the main thread.
 */
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

    /**
     * Create or return the static instance of the BeaconSingleton.
     *
     * @param context
     * @param apiKey
     * @param appVersionId
     * @param beaconVersionId
     * @param beaconInstanceId
     * @return The Beacon Singleton
     */
    public static synchronized BeaconSingleton getInstance(Context context, String apiKey, String appVersionId, String beaconVersionId, String beaconInstanceId) {
        if (mInstance == null) {
            mInstance = new BeaconSingleton(context, apiKey, appVersionId, beaconVersionId, beaconInstanceId);
        }
        return mInstance;
    }

    /**
     * Create or return the static instance of the RequestQueue.
     *
     * @return The request dispatch queue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     *
     * @param req The Request
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Queue a request for sending.
     *
     * @param msgType
     * @param json
     * @param timestamp
     * @param listener
     * @param errorListener
     */
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

    /**
     * Queue a request for sending.
     *
     * @param msgType
     * @param json
     * @param timestamp
     */
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

    /**
     * Queue a request for sending.
     *
     * @param msgType
     * @param json
     */
    public void log(final String msgType, JSONObject json) {
        log(msgType, json, AUTO_TIMESTAMP);
    }

    /**
     * Queue a request for sending.
     *
     * @param msgType
     */
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

