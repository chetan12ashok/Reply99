package com.appdroid.reply99;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import java.util.List;

public class WhatsappAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (getRootInActiveWindow() == null){
            return;
        }

        AccessibilityNodeInfoCompat nodeInfoCompat = AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());
        List<AccessibilityNodeInfoCompat> massagesNodeList = nodeInfoCompat
                .findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry");
        if (massagesNodeList == null || massagesNodeList.isEmpty()){
            return;
        }

        AccessibilityNodeInfoCompat massageField = massagesNodeList.get(0);
        if (massageField == null || massageField.getText().length() == 0 || massageField.getText().toString().endsWith("   "))
            return;


        List<AccessibilityNodeInfoCompat> sendMassageNodeList = nodeInfoCompat
                .findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
        if (sendMassageNodeList == null || sendMassageNodeList.isEmpty()){
            return;
        }

        AccessibilityNodeInfoCompat sendMassage =  sendMassageNodeList.get(0);
        Log.d("WhatsappAccessibilityService", "onAccessibilityEvent: "+sendMassage.isAccessibilityFocused());
        if (!sendMassage.isVisibleToUser()){
            return;
        }

        // fire send button

        try {

            //Thread.sleep(2000);


            sendMassage.performAction(AccessibilityNodeInfo.ACTION_CLICK);




        }catch (Exception e){

        }


      //  sendMassage.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        try {

            Thread.sleep(1000);
            performGlobalAction(GLOBAL_ACTION_BACK);
            Thread.sleep(1000);

            performGlobalAction(GLOBAL_ACTION_BACK);
            Thread.sleep(1000);
        }catch (Exception e){

        }

        performGlobalAction(GLOBAL_ACTION_BACK);


    }

    @Override
    public void onInterrupt() {

    }
}
