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
