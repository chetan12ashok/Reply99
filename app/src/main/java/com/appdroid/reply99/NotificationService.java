package com.appdroid.reply99;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import static com.appdroid.reply99.activity.Dashboard.context;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.RemoteInput;

import com.appdroid.reply99.model.CustomRepliesData;
import com.appdroid.reply99.model.preferences.PreferencesManager;
import com.appdroid.reply99.model.utils.ContactsHelper;
import com.appdroid.reply99.model.utils.DbUtils;
import com.appdroid.reply99.model.utils.NotificationHelper;
import com.appdroid.reply99.model.utils.NotificationUtils;
import com.appdroid.reply99.receivers.SMSUtils;
import com.appdroid.reply99.room.ConversionHolder;
import com.appdroid.reply99.room.UtilityRoomDatabase;
import com.appdroid.reply99.service.KeepAliveService;

import static java.lang.Math.max;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

public class NotificationService extends NotificationListenerService {
    private final String TAG = "ChetanAshok";
    CustomRepliesData customRepliesData;
    private DbUtils dbUtils;
    String flag = null;

    public static String getContactNameByPhoneNumber(Context context, String phoneNumber) {/*from   w  ww.  j  a  v  a2 s  . c om*/
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);


            Log.d("onNotificationRemoved", " || Posted by: " + sbn.getPackageName()+" ||  extras :  "+sbn.getNotification().extras.get("android.title"));


                if (NotificationUtils.isNewNotification(sbn)){


                    if (sbn.getNotification().extras.get("android.title") != null){
                        Log.d(TAG, "On Massage Sending Time: "+sbn.getNotification().category);
                        Log.d(TAG, "Flag For outGoing call "+flag);
                        if (sbn.getNotification().category != null){
                                if (sbn.getNotification().category.contains("call"))
                            if (!getContactNameByPhoneNumber(getApplicationContext(),sbn.getNotification().extras.get("android.title").toString()).equals("")){
                             //   sendMassage(getContactNameByPhoneNumber(getApplicationContext(),sbn.getNotification().extras.get("android.title").toString()));
                            }
                        }

                    }

                }

    }

    public void sendToWhatsappA() throws UnsupportedEncodingException, InterruptedException {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        String url = "https://api.whatsapp.com/send?phone=918788343984&text="+ URLEncoder.encode("HI.....","UTF-8");
        Intent intent  = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.whatsapp");
        intent.setData(Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.resolveActivity(packageManager) != null){
            startActivity(intent);
            Thread.sleep(1000);
            Log.d("CHETANAAAAAAAAAAAA", "sendToWhatsapp: calll");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendMassage(String phoneNumber,String flag) {



        SharedPreferences massageSendingPrefrance = getApplicationContext().getSharedPreferences("SendingFlag",MODE_PRIVATE);

        String incomingCallFlag = massageSendingPrefrance.getString("incomingCall","true");
        String outgoingCallFlag = massageSendingPrefrance.getString("outgoingCall","true");
        String missedCallFlag = massageSendingPrefrance.getString("missedCall","true");

        String rules = massageSendingPrefrance.getString("rule","noRule");
        String ignoreContactsFlag = massageSendingPrefrance.getString("ignoreContacts","false");


        SharedPreferences sendMassageRefrance = getApplicationContext().getSharedPreferences("MassagesWithFlags",MODE_PRIVATE);
        String incomingMassage = sendMassageRefrance.getString("incomingMassage","");
        String outgoingMassage = sendMassageRefrance.getString("outgoingMassage","");
        String missedCallMassage = sendMassageRefrance.getString("missedCallMassage","");
        String sendToAllMassage =  sendMassageRefrance.getString("sendToAllMassage","");


        String incomingImage = sendMassageRefrance.getString("incomingImageForWhatsapp","");
        String outgoingImage = sendMassageRefrance.getString("outgoingImageForWhatsapp","");
        String missedCallImage = sendMassageRefrance.getString("missedCallImageForWhatsapp","");
        String sendToAllImage = sendMassageRefrance.getString("sendToAllImageForWhatsapp",getApplicationContext().getResources().getString(R.string.sendToAllImageForWhatsApp));







        String incomingWhatsAppVideoOrImageFlag = sendMassageRefrance.getString("incomingWhatsappImageOrVideo","image");
        String outgoingWhatsAppVideoOrImageFlag = sendMassageRefrance.getString("outgoingWhatsappImageOrVideo","image");
        String missedCallWhatsAppVideoOrImageFlag = sendMassageRefrance.getString("missedCallWhatsappImageOrVideo","image");
        String sendToAllWhatsAppVideoOrImageFlag = sendMassageRefrance.getString("sendToAllWhatsappImageOrVideo","image");


        String incomingImageForSMS = sendMassageRefrance.getString("incomingImageForSMS","");
        String outgoingImageForSMS = sendMassageRefrance.getString("outgoingImageForSMS","");
        String missedCallImageForSMS = sendMassageRefrance.getString("missedCallImageForSMS","");
        String sendToAllImageForSMS = sendMassageRefrance.getString("sendToAllImageForSMS",getApplicationContext().getResources().getString(R.string.sendToAllImageForSMS));



        boolean serviceFlag = sendMassageRefrance.getBoolean("serviceFlag",true);
        String sendWithFilterContactFlag = massageSendingPrefrance.getString("sendWithFilterContact","false");

        String filterContact = massageSendingPrefrance.getString("filterContact","default");


        Log.d(TAG, "sendWithFilterContactFlag: "+sendWithFilterContactFlag+" filterContact :"+filterContact);


        if (serviceFlag){
            if (incomingCallFlag.equals("true") && flag.equals("Incoming")){


                if (sendWithFilterContactFlag.equals("true") && !filterContact.equals("default") ){


                        if (!incomingMassage.equals("")){
                            checkIsAIgnoreContact(phoneNumber,flag,rules,incomingMassage,filterContact,incomingImage,incomingImageForSMS,incomingWhatsAppVideoOrImageFlag);
                        }else{
                            checkIsAIgnoreContact(phoneNumber,flag,rules,sendToAllMassage,filterContact,sendToAllImage,sendToAllImageForSMS,sendToAllWhatsAppVideoOrImageFlag);
                        }


                }else {


                    if (rules.equals("noRule")){

                        if (!incomingMassage.equals("")){
                            SMSUtils.sendSMS(getApplicationContext(), phoneNumber,incomingMassage,flag,incomingImage,incomingImageForSMS,incomingWhatsAppVideoOrImageFlag);
                        }else{
                            SMSUtils.sendSMS(getApplicationContext(), phoneNumber,sendToAllMassage,flag,sendToAllImage,sendToAllImageForSMS,sendToAllWhatsAppVideoOrImageFlag);
                        }



                    }else {

                        if (!incomingMassage.equals("")){
                            checkAcordingToRules(phoneNumber,flag,rules,incomingMassage,incomingImage,incomingImageForSMS,incomingWhatsAppVideoOrImageFlag);
                        }else{
                            checkAcordingToRules(phoneNumber,flag,rules,sendToAllMassage,sendToAllImage,sendToAllImageForSMS,sendToAllWhatsAppVideoOrImageFlag);
                        }

                    }
                }


            }else if (outgoingCallFlag.equals("true") && flag.equals("Outgoing")){



                if (sendWithFilterContactFlag.equals("true") && !filterContact.equals("default") ){


                    if (!outgoingMassage.equals("")){
                        checkIsAIgnoreContact(phoneNumber,flag,rules,outgoingMassage,filterContact,outgoingImage,outgoingImageForSMS,outgoingWhatsAppVideoOrImageFlag);
                    }else{
                        checkIsAIgnoreContact(phoneNumber,flag,rules,sendToAllMassage,filterContact,sendToAllImage,sendToAllImageForSMS,sendToAllWhatsAppVideoOrImageFlag);
                    }


                }else {

                    if (rules.equals("noRule")){

                        if (!outgoingMassage.equals("")){
                            SMSUtils.sendSMS(getApplicationContext(), phoneNumber,outgoingMassage,flag,outgoingImage,outgoingImageForSMS,outgoingWhatsAppVideoOrImageFlag);
                        }else{
                            SMSUtils.sendSMS(getApplicationContext(), phoneNumber,sendToAllMassage,flag,sendToAllImage,sendToAllImageForSMS,sendToAllWhatsAppVideoOrImageFlag);
                        }



                    }else {

                        if (!outgoingMassage.equals("")){
                            checkAcordingToRules(phoneNumber,flag,rules,outgoingMassage,outgoingImage,outgoingImageForSMS,outgoingWhatsAppVideoOrImageFlag);
                        }else{
                            checkAcordingToRules(phoneNumber,flag,rules,sendToAllMassage,sendToAllImage,sendToAllImageForSMS,sendToAllWhatsAppVideoOrImageFlag);
                        }

                    }
                }


            }else if (missedCallFlag.equals("true") && flag.equals("Missed")){



                if (sendWithFilterContactFlag.equals("true") && !filterContact.equals("default") ){


                    if (!missedCallMassage.equals("")){
                        checkIsAIgnoreContact(phoneNumber,flag,rules,missedCallMassage,filterContact,missedCallImage,missedCallImageForSMS,missedCallWhatsAppVideoOrImageFlag);
                    }else{
                        checkIsAIgnoreContact(phoneNumber,flag,rules,sendToAllMassage,filterContact,sendToAllImage,sendToAllImageForSMS,sendToAllWhatsAppVideoOrImageFlag);
                    }


                }else {

                    if (rules.equals("noRule")){

                        if (!missedCallMassage.equals("")){
                            SMSUtils.sendSMS(getApplicationContext(), phoneNumber,missedCallMassage,flag,missedCallImage,missedCallImageForSMS,missedCallWhatsAppVideoOrImageFlag);
                        }else{
                            SMSUtils.sendSMS(getApplicationContext(), phoneNumber,sendToAllMassage,flag,sendToAllImage,sendToAllImageForSMS,sendToAllWhatsAppVideoOrImageFlag);
                        }

                    }else {

                        if (!missedCallMassage.equals("")){
                            checkAcordingToRules(phoneNumber,flag,rules,missedCallMassage,missedCallImage,missedCallImageForSMS,missedCallWhatsAppVideoOrImageFlag);
                        }else{
                            checkAcordingToRules(phoneNumber,flag,rules,sendToAllMassage,sendToAllImage,sendToAllImageForSMS,sendToAllWhatsAppVideoOrImageFlag);
                        }

                    }
                }

            }
        }


    }

    private void checkAcordingToRules(String phoneNumber, String flag, String rules, String massage,String imageForWhatsapp,String imageToSendOnSMS,String imageOrVideoFlag) {
        getLastConvetionForThisNumber(phoneNumber,massage,flag,imageForWhatsapp,imageToSendOnSMS,imageOrVideoFlag);
        Log.d(TAG, "checkAcordingToRules: "+rules);
    }

    private void checkIsAIgnoreContact(String phoneNumber, String flag, String rules,String massage,String filterContactFlag,String imageToSendOnWhatsApp,String imageToSendOnSMS,String imageOrVideoFlag) {


        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {

                boolean isContactPresent =  UtilityRoomDatabase.getInstance(getApplicationContext()).massagesDao()
                        .isContactExistInIgnoreTable(phoneNumber);

                Log.d(TAG, "runSSSSSSSSSSS: "+filterContactFlag);

                if (filterContactFlag.equals("notBlockContacts")){
                    if (!isContactPresent){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {

                                Log.d(TAG, "runSSSSSSSSSSS: "+phoneNumber+" flag : "+flag+" rules : "+rules+" massage: "+massage);

                                if (rules.equals("noRule")){
                                    SMSUtils.sendSMS(getApplicationContext(), phoneNumber,massage,flag,imageToSendOnWhatsApp,imageToSendOnSMS,imageOrVideoFlag);
                                }else {
                                    checkAcordingToRules(phoneNumber,flag,rules,massage,imageToSendOnWhatsApp,imageToSendOnSMS,imageOrVideoFlag);
                                }


                            }
                        });
                    }
                }else if (filterContactFlag.equals("onlyBlockContacts")){
                    if (isContactPresent){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {

                                Log.d(TAG, "runSSSSSSSSSSS: "+phoneNumber+" flag : "+flag+" rules : "+rules+" massage: "+massage);

                                if (rules.equals("noRule")){
                                    SMSUtils.sendSMS(getApplicationContext(), phoneNumber,massage,flag,imageToSendOnWhatsApp,imageToSendOnSMS,imageOrVideoFlag);
                                }else {
                                    checkAcordingToRules(phoneNumber,flag,rules,massage,imageToSendOnWhatsApp,imageToSendOnSMS,imageOrVideoFlag);
                                }


                            }
                        });
                    }
                }




          /*      ((Activity)getApplicationContext()).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                    }
                });*/


            }
        });
        thread.start();
    }

    private void getLastConvetionForThisNumber(String phoneNumber, String massage, String callingFlag,String  imageForWhatsapp,String imageToSendOnSMS,String imageOrVideoFlag) {

        Log.d(TAG, "getLastConvetionForThisNumber: "+phoneNumber);

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
               ConversionHolder conversionHolder = UtilityRoomDatabase.getInstance(getApplicationContext()).massagesDao()
                        .getLastConversionForThisNumber(phoneNumber);

                Log.d(TAG, "getLastConvetionForThisNumber: "+conversionHolder);

                if (conversionHolder != null){
                    getDiffranceBetweenDates(conversionHolder,massage,phoneNumber,callingFlag,imageForWhatsapp,imageToSendOnSMS,imageOrVideoFlag);
                }else {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            SMSUtils.sendSMS(getApplicationContext(), phoneNumber,massage,callingFlag,imageForWhatsapp,imageToSendOnSMS,imageOrVideoFlag);
                        }
                    });


                }

            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDiffranceBetweenDates(ConversionHolder conversionHolder, String massage, String phoneNumber,
                                          String callingFlag,String imageForWhatsapp,String imageToSendOnSMS,String imageOrVideoFlag) {
            long todayDate = System.currentTimeMillis();
            long lastConversionDate = conversionHolder.getDate();

            long diff =  todayDate - lastConversionDate;
            int hours = (int) (diff / (1000 * 60 * 60));

              Date d = new Date(lastConversionDate);


              Date currentDate = new Date();


     /*   if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = df.format(lastConversionDate);

        }*/

        Calendar currentDateCal = Calendar.getInstance();
        currentDateCal.setTime(currentDate);
        int currentDay = currentDateCal.get(Calendar.DAY_OF_MONTH);

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int saveDay = cal.get(Calendar.DAY_OF_MONTH);


        //int diffrance = (24-newHours);

        Log.d(TAG, "getDiffranceBetweenDates: current day "+ currentDay+" save day : "+saveDay);

        //   current data = 1 save date = 30

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (currentDay>saveDay){
                    SMSUtils.sendSMS(getApplicationContext(), phoneNumber,massage,callingFlag,imageForWhatsapp,imageToSendOnSMS,imageOrVideoFlag);
                }else if ((saveDay == 30 || saveDay == 31) && (currentDay >= 1) ){
                    SMSUtils.sendSMS(getApplicationContext(), phoneNumber,massage,callingFlag,imageForWhatsapp,imageToSendOnSMS,imageOrVideoFlag);
                }
            }
        });



    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {


        Log.d(TAG, "onTaskRemoved:Calll ");

        SharedPreferences sendMassageRefrance = getApplicationContext().getSharedPreferences("MassagesWithFlags",MODE_PRIVATE);
        boolean serviceFlag = sendMassageRefrance.getBoolean("serviceFlag",true);


        if (serviceFlag){
            Intent myIntent = new Intent(getApplicationContext(), KeepAliveService.class);
            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, myIntent, FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 5);
            alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        super.onTaskRemoved(rootIntent);



    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("android.intent.action.PHONE_STATE")){
                //action for sms received
                String TAG = "BroadcastFromMyApp";

                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);





                SharedPreferences sharedPreferences = context.getSharedPreferences("CallingFlags",MODE_PRIVATE);
                boolean ringingFlag =    sharedPreferences.getBoolean("ringingFlag",false);
                boolean offhookFlag = sharedPreferences.getBoolean("offhookFlag",false);
                boolean idealFlag =  sharedPreferences.getBoolean("idealFlag",false);



                if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    Log.d(TAG, "EXTRA_STATE_RINGING : "+incomingNumber);

                    if (incomingNumber != null){
                        SharedPreferences.Editor editor = context.getSharedPreferences("CallingFlags",MODE_PRIVATE).edit();
                        editor.putBoolean("ringingFlag",true);
                        editor.apply();
                    }


                }
                if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){

                    Log.d(TAG, "EXTRA_STATE_OFFHOOK : "+incomingNumber);
                   // getLastCallDetails(context);

                    if (incomingNumber != null){
                        SharedPreferences.Editor editor = context.getSharedPreferences("CallingFlags",MODE_PRIVATE).edit();
                        editor.putBoolean("offhookFlag",true);
                        editor.apply();
                    }


                }
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){

                    if (incomingNumber != null){
                        SharedPreferences.Editor editor = context.getSharedPreferences("CallingFlags",MODE_PRIVATE).edit();
                        editor.putBoolean("idealFlag",true);
                        editor.apply();
                        idealFlag =true;
                    }

                    Log.d(TAG, "EXTRA_STATE_IDLE "+incomingNumber);
                    if (incomingNumber != null){

                        Log.d(TAG, "getContactNameByPhoneNumber: "+getContactNameByPhoneNumber(getApplicationContext(),incomingNumber));



                        if ((ringingFlag) && (offhookFlag) && (idealFlag)){
                            Log.d("AllFlags", "Incoming Call.....!");
                            sendMassage(incomingNumber,"Incoming");

                        }else if ((ringingFlag) && (idealFlag)){
                            Log.d("AllFlags", "Missed Call....!");
                            sendMassage(incomingNumber,"Missed");
                        }else if (offhookFlag && idealFlag){
                            Log.d("AllFlags", "Outgoing Call....!");

                            sendMassage(incomingNumber,"Outgoing");
                        }

                        resetFlags();

                    }

                }


            }
            else if(action.equals(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)){
                //action for phone state changed
                Log.d("BroadcastFromMyApp", "onReceive: in else");

            }
        }
    };

    private void resetFlags() {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("CallingFlags",MODE_PRIVATE).edit();
        editor.putBoolean("ringingFlag",false);
        editor.putBoolean("offhookFlag",false);
        editor.putBoolean("idealFlag",false);
        editor.apply();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction("your_action_strings"); //further more
        filter.addAction("your_action_strings"); //further more

        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {


        SharedPreferences sendMassageRefrance = getApplicationContext().getSharedPreferences("MassagesWithFlags",MODE_PRIVATE);
        boolean serviceFlag = sendMassageRefrance.getBoolean("serviceFlag",true);

        if (serviceFlag){
            Intent myIntent = new Intent(getApplicationContext(), KeepAliveService.class);

            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, myIntent, FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


            AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 5);
            alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            unregisterReceiver(receiver);
        }
        super.onDestroy();
   //     startService(new Intent(this, NotificationService.class));  /// add by chetan
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

       Log.d("ChetanAshok", "onNotificationPosted: " + sbn.getNotification().category + " || Posted by: " + sbn.getPackageName()+" ||  Text :  "+sbn.getNotification().extras.getString("android.text")+"\n\n");

       if (sbn.getNotification().extras.getString("android.text") != null){
           if (sbn.getNotification().extras.getString("android.text").contains("ongoing") || sbn.getNotification().extras.getString("android.text").contains("call")) {
               flag = sbn.getNotification().extras.getString("android.text");
           }
       }


        // for incoming  call

        /*if (sbn.getNotification().category != null){
            if (Calendar.getInstance().getTimeInMillis() - lastMessageTime < 1000 && lastMessageContent.equalsIgnoreCase( sbn.getNotification().category)) {
                // Ignore

                Log.d("ChetanAshok", "Ignore: " + sbn.getNotification().category + " || Posted by: " + sbn.getPackageName()+" ||  tag "+sbn.getTag());

                return;
            } else {
                lastMessageContent = sbn.getNotification().category;
                lastMessageTime = Calendar.getInstance().getTimeInMillis();
                Log.d("ChetanAshok", "UseFull: " + sbn.getNotification().category + " || Posted by: " + sbn.getPackageName()+" ||  tag "+sbn.getTag());

                phoneListener = new MyPhoneStateListener();
                telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }*/



        if (canReply(sbn) && shouldReply(sbn)) {
         //   sendReply(sbn);
        }
    }


    private boolean canReply(StatusBarNotification sbn) {
        return isServiceEnabled() &&
                isSupportedPackage(sbn) &&
                NotificationUtils.isNewNotification(sbn) &&
                isGroupMessageAndReplyAllowed(sbn) &&
                canSendReplyNow(sbn);
    }

    private boolean shouldReply(StatusBarNotification sbn) {
        PreferencesManager prefs = PreferencesManager.getPreferencesInstance(this);
        boolean isGroup = sbn.getNotification().extras.getBoolean("android.isGroupConversation");

        //Check contact based replies
        if (prefs.isContactReplyEnabled() && !isGroup) {
            //Title contains sender name (at least on WhatsApp)
            String senderName = sbn.getNotification().extras.getString("android.title");
            //Check if should reply to contact
            boolean isNameSelected =
                    (ContactsHelper.getInstance(this).hasContactPermission()
                            && prefs.getReplyToNames().contains(senderName)) ||
                            prefs.getCustomReplyNames().contains(senderName);
            if ((isNameSelected && prefs.isContactReplyBlacklistMode()) ||
                    !isNameSelected && !prefs.isContactReplyBlacklistMode()) {
                //If contact is on the list and contact reply is on blacklist mode, 
                // or contact is not in the list and reply is on whitelist mode,
                // we don't want to reply
                return false;
            }
        }

        //Check more conditions on future feature implementations

        //If we got here, all conditions to reply are met
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //START_STICKY  to order the system to restart your service as soon as possible when it was killed.
        return START_STICKY;
    }

    private void sendReply(StatusBarNotification sbn) {
        NotificationWear notificationWear = NotificationUtils.extractWearNotification(sbn);
        // Possibly transient or non-user notification from WhatsApp like
        // "Checking for new messages" or "WhatsApp web is Active"
        if (notificationWear.getRemoteInputs().isEmpty()) {
            return;
        }

        customRepliesData = CustomRepliesData.getInstance(this);

        RemoteInput[] remoteInputs = new RemoteInput[notificationWear.getRemoteInputs().size()];

        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle localBundle = new Bundle();//notificationWear.bundle;
        int i = 0;
        for (RemoteInput remoteIn : notificationWear.getRemoteInputs()) {
            remoteInputs[i] = remoteIn;
            // This works. Might need additional parameter to make it for Hangouts? (notification_tag?)
            localBundle.putCharSequence(remoteInputs[i].getResultKey(), customRepliesData.getTextToSendOrElse());
            i++;
        }

        RemoteInput.addResultsToIntent(remoteInputs, localIntent, localBundle);
        try {
            if (notificationWear.getPendingIntent() != null) {
                if (dbUtils == null) {
                    dbUtils = new DbUtils(getApplicationContext());
                }
                dbUtils.logReply(sbn, NotificationUtils.getTitle(sbn));
                notificationWear.getPendingIntent().send(this, 0, localIntent);
                if (PreferencesManager.getPreferencesInstance(this).isShowNotificationEnabled()) {
                    NotificationHelper.getInstance(getApplicationContext()).sendNotification(sbn.getNotification().extras.getString("android.title"), sbn.getNotification().extras.getString("android.text"), sbn.getPackageName());
                }
                cancelNotification(sbn.getKey());
                if (canPurgeMessages()) {
                    dbUtils.purgeMessageLogs();
                    PreferencesManager.getPreferencesInstance(this).setPurgeMessageTime(System.currentTimeMillis());
                }
            }
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, "replyToLastNotification error: " + e.getLocalizedMessage());
        }
    }

    private boolean canPurgeMessages() {
        //Added L to avoid numeric overflow expression
        //https://stackoverflow.com/questions/43801874/numeric-overflow-in-expression-manipulating-timestamps
        long daysBeforePurgeInMS = 30 * 24 * 60 * 60 * 1000L;
        return (System.currentTimeMillis() - PreferencesManager.getPreferencesInstance(this).getLastPurgedTime()) > daysBeforePurgeInMS;
    }

    private boolean isSupportedPackage(StatusBarNotification sbn) {
        return PreferencesManager.getPreferencesInstance(this)
                .getEnabledApps()
                .contains(sbn.getPackageName());
    }

    private boolean canSendReplyNow(StatusBarNotification sbn) {
        // Do not reply to consecutive notifications from same person/group that arrive in below time
        // This helps to prevent infinite loops when users on both end uses Watomatic or similar app
        int DELAY_BETWEEN_REPLY_IN_MILLISEC = 10 * 1000;

        String title = NotificationUtils.getTitle(sbn);
        String selfDisplayName = sbn.getNotification().extras.getString("android.selfDisplayName");
        if (title != null && title.equalsIgnoreCase(selfDisplayName)) { //to protect double reply in case where if notification is not dismissed and existing notification is updated with our reply
            return false;
        }
        if (dbUtils == null) {
            dbUtils = new DbUtils(getApplicationContext());
        }
        long timeDelay = PreferencesManager.getPreferencesInstance(this).getAutoReplyDelay();
        return (System.currentTimeMillis() - dbUtils.getLastRepliedTime(sbn.getPackageName(), title) >= max(timeDelay, DELAY_BETWEEN_REPLY_IN_MILLISEC));
    }

    private boolean isGroupMessageAndReplyAllowed(StatusBarNotification sbn) {
        String rawTitle = NotificationUtils.getTitleRaw(sbn);
        //android.text returning SpannableString
        SpannableString rawText = SpannableString.valueOf("" + sbn.getNotification().extras.get("android.text"));
        // Detect possible group image message by checking for colon and text starts with camera icon #181
        boolean isPossiblyAnImageGrpMsg = ((rawTitle != null) && (": ".contains(rawTitle) || "@ ".contains(rawTitle)))
                && ((rawText != null) && rawText.toString().contains("\uD83D\uDCF7"));
        if (!sbn.getNotification().extras.getBoolean("android.isGroupConversation")) {
            return !isPossiblyAnImageGrpMsg;
        } else {
            return PreferencesManager.getPreferencesInstance(this).isGroupReplyEnabled();
        }
    }

    private boolean isServiceEnabled() {
        return PreferencesManager.getPreferencesInstance(this).isServiceEnabled();
    }
}
