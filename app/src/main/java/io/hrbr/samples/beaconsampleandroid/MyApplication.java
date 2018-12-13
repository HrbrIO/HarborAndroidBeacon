package io.hrbr.samples.beaconsampleandroid;

import io.hrbr.beacon.HarborApplication;

public class MyApplication extends HarborApplication {

    /**
     * Method to enable/disable Harbor Heartbeat updates
     * @return true|false
     */
    @Override
    public boolean wantsHeartbeat() {
        return true;
    }

    /**
     * Method to enable/disable Harbor Geolocation updates
     * @return true|false
     */
    @Override
    public boolean wantsGeolocation() {
        return true;
    }

    /**
     * Method to return your Harbor Organization API Key
     *
     * @return "YOUR_ORGANIZATION_API_KEY_GOES_HERE"
     */
    @Override
    protected String getApiKey() {
        return "YOUR_ORGANIZATION_API_KEY_GOES_HERE";
    }

    /**
     * Method to return your Harbor Application ID
     *
     * @return "YOUR_APPLICATION_ID_GOES_HERE"
     */
    @Override
    public String getAppVersionId() {
        return "YOUR_APPLICATION_ID_GOES_HERE";
    }

    /**
     * Method to return your Harbor Beacon Version ID
     *
     * @return "YOUR_BEACON_ID_GOES_HERE"
     */
    @Override
    public String getBeaconVersionId() {
        return "YOUR_BEACON_ID_GOES_HERE";
    }

    /**
     * Method to return your Harbor Beacon Instance ID
     *
     * @return "A_UNIQUE_DEVICE_ID_GOES_HERE"
     */
    @Override
    public String getBeaconInstanceId() {
        return "A_UNIQUE_DEVICE_ID_GOES_HERE";
    }
}
