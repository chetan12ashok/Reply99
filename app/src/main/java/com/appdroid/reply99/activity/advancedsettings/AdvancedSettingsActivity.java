package com.appdroid.reply99.activity.advancedsettings;

import android.os.Bundle;

import com.appdroid.reply99.R;
import com.appdroid.reply99.activity.BaseActivity;

public class AdvancedSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_settings);

        setTitle(R.string.advanced_settings);
    }
}
