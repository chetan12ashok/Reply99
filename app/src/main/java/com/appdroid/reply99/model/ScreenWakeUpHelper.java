package com.appdroid.reply99.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ScreenWakeUpHelper extends DeviceAdminReceiver {
    public static void turnScreenOn(Activity context) {
        try {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        } catch (Exception ex) {
            Log.e("PercolateAndroidUtils", "Unable to turn on screen for activity " + context);
        }
    }
}