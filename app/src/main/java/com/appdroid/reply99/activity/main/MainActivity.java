package com.appdroid.reply99.activity.main;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.appdroid.reply99.R;
import com.appdroid.reply99.activity.BaseActivity;
import com.appdroid.reply99.viewmodel.SwipeToKillAppDetectViewModel;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ViewModelProvider(this).get(SwipeToKillAppDetectViewModel.class);
    }
}