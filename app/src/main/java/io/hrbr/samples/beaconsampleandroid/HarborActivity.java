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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.hrbr.beacon.HarborApplication;
import io.hrbr.beacon.HarborLogger;

public class HarborActivity extends AppCompatActivity {

    private static final String TAG = "HarborActivity";

    protected String SCREEN;// = "HarborBeaconActivity";

    protected HarborLogger mHarborLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (SCREEN == null) SCREEN = "HarborActivity";

        HarborApplication app = (HarborApplication)getApplicationContext();
        mHarborLogger = app.getLogger();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHarborLogger.startScreenDwell(SCREEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHarborLogger.stopScreenDwell(SCREEN);
    }


}
