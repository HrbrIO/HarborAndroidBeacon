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
import android.widget.TextView;

public class GlassFullActivity extends HarborActivity {

    public static final String EXTRA_MESSAGE = "io.hrbr.BUTTON";

    protected Integer mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SCREEN = "Half Empty";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glass_full);

        mCount = getIntent().getExtras().getInt(EXTRA_MESSAGE,0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final TextView mTextView = (TextView) findViewById(R.id.pollResultsTextView);

        if (mCount == 1) {
            mTextView.setText("You are the first to make that choice");
        } else {
            mTextView.setText(mCount + " people have made that choice");
        }
    }

}
