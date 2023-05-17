package com.appdroid.reply99.model;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {




    public void onCallStateChanged(int state, String incomingNumber) {


        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                Log.d("DEBUGAAAA", "IDLE"+incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:

                Log.d("DEBUGAAAA", "OFFHOOK"+incomingNumber);


                break;
            case TelephonyManager.CALL_STATE_RINGING:
               Log.d("DEBUGAAAA", "RINGING"+incomingNumber);
                break;
        }
    }

}
