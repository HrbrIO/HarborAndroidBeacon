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
