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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends HarborActivity {

    public static final String EXTRA_MESSAGE = "io.hrbr.BUTTON";

    private Integer mEmptyCount, mFullCount;

    public String emptyPercentage() {
        Double result = (mEmptyCount + mFullCount) < 1 ? 0 : (100.0 * mEmptyCount / new Double(mEmptyCount + mFullCount));
        return result.intValue() + "";// toString();
    }

    public String fullPercentage() {
        Double result = (mEmptyCount + mFullCount) < 1 ? 0 : (100.0 * mFullCount / new Double(mEmptyCount + mFullCount));
        return result.intValue() + "";// toString();
    }

    public void updatePollResults() {
        final TextView mTextView = (TextView) findViewById(R.id.pollResultsTextView);
        String s = "Empty: " + emptyPercentage() + "%, Full: " + fullPercentage() + "%";
        mTextView.setText(s);
    }

    public Integer getSavedPref(String key) {
        SharedPreferences prefs = this.getSharedPreferences("io.hrbr.sample", Context.MODE_PRIVATE);
        return prefs.getInt(key, 0);
    }

    public void setSavedPref(String key, Integer val) {
        SharedPreferences prefs = this.getSharedPreferences("io.hrbr.sample", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SCREEN = "MainActivity";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmptyCount = getSavedPref("emptyCount");
        mFullCount = getSavedPref("fullCount");

        updatePollResults();
    }

    public void onClickEmpty(View view) {
        mEmptyCount++;

        setSavedPref("emptyCount", mEmptyCount);
        updatePollResults();
        logButtonPress("empty");
        logPollResults();

        Intent intent = new Intent(this, GlassEmptyActivity.class);
        intent.putExtra(EXTRA_MESSAGE, mEmptyCount);
        startActivity(intent);
    }

    public void onClickFull(View view) {
        mFullCount++;

        setSavedPref("fullCount", mFullCount);
        updatePollResults();
        logButtonPress("full");
        logPollResults();

        Intent intent = new Intent(this, GlassFullActivity.class);
        intent.putExtra(EXTRA_MESSAGE, mFullCount);
        startActivity(intent);
    }

    public void logButtonPress(final String button) {
        JSONObject json = new JSONObject();
        try {
            json.put("page", SCREEN);
            json.put("button", button);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mHarborLogger.log("BUTTON_SELECT", json);
    }

    public void logPollResults() {
        JSONObject json = new JSONObject();
        JSONObject jsonCt = new JSONObject();
        JSONObject jsonPt = new JSONObject();
        try {
            jsonCt.put("empty", mEmptyCount);
            jsonCt.put("full", mFullCount);
            jsonPt.put("empty", emptyPercentage());
            jsonPt.put("full", fullPercentage());
            json.put("count", jsonCt);
            json.put("percent", jsonPt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mHarborLogger.log("POLL_RESULTS", json);
    }
}
