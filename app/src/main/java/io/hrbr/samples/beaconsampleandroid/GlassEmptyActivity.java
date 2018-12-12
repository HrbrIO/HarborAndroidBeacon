package io.hrbr.samples.beaconsampleandroid;

import android.os.Bundle;
import android.widget.TextView;

public class GlassEmptyActivity extends HarborActivity {

    public static final String EXTRA_MESSAGE = "io.hrbr.BUTTON";

    protected Integer mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SCREEN = "Half Empty";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glass_empty);

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
