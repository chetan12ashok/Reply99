package com.appdroid.reply99.receivers;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.appdroid.reply99.NotificationService;
import com.appdroid.reply99.R;
import com.appdroid.reply99.activity.ConversionActivity;
import com.appdroid.reply99.activity.WackUP;
import com.appdroid.reply99.activity.main.MainActivity;
import com.appdroid.reply99.loginActivitys.FlashActivity;
import com.appdroid.reply99.model.ScreenWakeUpHelper;
import com.appdroid.reply99.room.ConversionHolder;
import com.appdroid.reply99.room.UtilityRoomDatabase;
import com.appdroid.reply99.whatsappAutoReply.IApiInterface;
import com.appdroid.reply99.whatsappAutoReply.RetrofitInstance;
import com.appdroid.reply99.whatsappAutoReply.imageAndButtonsTempletForWA.ComponentsItem;
import com.appdroid.reply99.whatsappAutoReply.imageAndButtonsTempletForWA.CustomModel;
import com.appdroid.reply99.whatsappAutoReply.imageAndButtonsTempletForWA.Image;
import com.appdroid.reply99.whatsappAutoReply.imageAndButtonsTempletForWA.Language;
import com.appdroid.reply99.whatsappAutoReply.imageAndButtonsTempletForWA.ParametersItem;
import com.appdroid.reply99.whatsappAutoReply.imageAndButtonsTempletForWA.Template;
import com.appdroid.reply99.whatsappAutoReply.imageAndButtonsTempletForWA.Video;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SMSUtils extends BroadcastReceiver {

    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";
    public static final String DELIVERED_SMS_ACTION_NAME = "SMS_DELIVERED";
    private static final String TAG = "SMSUtils";



    @Override
    public void onReceive(Context context, Intent intent) {
        //Detect l'envoie de sms
        if (intent.getAction().equals(SENT_SMS_ACTION_NAME)) {


            switch (getResultCode()) {
                case Activity.RESULT_OK: // Sms sent
                    Log.d(TAG, "Sms Send: ");

                    getLastConversion(context,true);
                    //Toast.makeText(context,"Sms Send ", Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: // generic failure
                   // Toast.makeText(context, "RESULT_ERROR_GENERIC_FAILURE", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "RESULT_ERROR_GENERIC_FAILURE");

                    getLastConversion(context,false);

                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE: // No service
                  //  Toast.makeText(context, "RESULT_ERROR_NO_SERVICE", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "RESULT_ERROR_NO_SERVICE");
                    getLastConversion(context,false);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU: // null pdu
                    getLastConversion(context,false);
                    Log.d(TAG, "RESULT_ERROR_NULL_PDU");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: //Radio off
                    Log.d(TAG, "RESULT_ERROR_RADIO_OFF");
                    getLastConversion(context,false);
                    //   Toast.makeText(context, "RESULT_ERROR_RADIO_OFF", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        //detect la reception d'un sms
        else if (intent.getAction().equals(DELIVERED_SMS_ACTION_NAME)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "Sms Send", Toast.LENGTH_LONG).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "not  received.", Toast.LENGTH_LONG).show();
                    break;
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotification(ConversionHolder conversionHolder, boolean deliveryFlag, Context context, boolean whatsappDeliveryFlag) {


        SharedPreferences massageSendingSharedPreferences = context.getSharedPreferences("SendingFlag",MODE_PRIVATE);
        String notificationFlag = massageSendingSharedPreferences.getString("notificationFlag","true");



        if (notificationFlag.equals("true")){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel("myCh","channel",NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(context, ConversionActivity.class);

            if (deliveryFlag){
                intent.setAction("positive");
            }else {
                intent.setAction("negative");
            }

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(FlashActivity.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);


            NotificationCompat.Builder builder = null;
            if (!deliveryFlag && !whatsappDeliveryFlag){
                builder = new NotificationCompat.Builder(context,"myCh")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText("Fail to send both massages")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setContentTitle("Fail to send massage to : "+conversionHolder.getCallerName());
            }else if (deliveryFlag && whatsappDeliveryFlag){
                builder = new NotificationCompat.Builder(context,"myCh")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText("Both massages send successfully")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Massage send to : "+conversionHolder.getCallerName());
            }else if (deliveryFlag && !whatsappDeliveryFlag){
                builder = new NotificationCompat.Builder(context,"myCh")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText("Text massages send successfully & Whatsapp massage failed to send")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Massage send to : "+conversionHolder.getCallerName());
            }else{
                builder = new NotificationCompat.Builder(context,"myCh")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText("Whatsapp massages send successfully & Text massage failed to send")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Massage send to : "+conversionHolder.getCallerName());
            }







     /*       if (!deliveryFlag){
                builder = new NotificationCompat.Builder(context,"myCh")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Fail to send")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setContentText("Fail to send massage to : "+conversionHolder.getCallerName());
            }else {
                builder = new NotificationCompat.Builder(context,"myCh")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Massage Send")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentText("Massage send to : "+conversionHolder.getCallerName());
            }*/


            builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
            Notification notification = builder.build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(1,notification);
        }
    }

    private void getLastConversion(Context context,boolean deliveryFlag) {
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                ConversionHolder conversionHolder = UtilityRoomDatabase.getInstance(context)
                        .massagesDao()
                        .getLastConversion();
                Log.d(TAG, "getLastConversion: Phone : " + conversionHolder.getPhoneNumber() + " | Massage : " + conversionHolder.getMassage() + " Flag : " + conversionHolder.isDeliveryFlag());

               createNotificationForSMS(conversionHolder,deliveryFlag,context);


                saveToOnlineDB(conversionHolder,deliveryFlag,context);

                UtilityRoomDatabase.getInstance(context).massagesDao().updateConversion(deliveryFlag, conversionHolder.getConversionId());

                try {



                   sendToWhatsapp(conversionHolder,context);
                } catch (Exception e) {
                    Log.d(TAG, "GIRANARE: "+e.getLocalizedMessage());
                }


           /*     if (isNetworkAvailable(context)){

                    SharedPreferences massageSendingSharedPreferences = context.getSharedPreferences("SendingFlag",MODE_PRIVATE);
                    String notificationFlag = massageSendingSharedPreferences.getString("whatsappFlag","true");


                    if (!notificationFlag.equals("false")){
                       // wakeupScreen(context,conversionHolder,deliveryFlag);
                    }

                    //  sendToWhatsapp(conversionHolder,context);
                }else {
                    createNotification(conversionHolder,deliveryFlag,context,false);
                }*/



        /*        if (isNetworkAvailable(context)){
                    try {

                        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                        wakeupScreen(context,conversionHolder);

                        if( myKM.inKeyguardRestrictedInputMode()) {
                            //it is locked

                            Log.d(TAG, "KeyguardManager: "+myKM.inKeyguardRestrictedInputMode());
                        } else {
                            wakeupScreen(context,conversionHolder);
                            //it is not locked
                            PhoneUnlockedReceiver receiver = new PhoneUnlockedReceiver();
                            IntentFilter filter = new IntentFilter();

                            filter.addAction(Intent.ACTION_USER_PRESENT);
                            filter.addAction(Intent.ACTION_SCREEN_OFF);
                            context.registerReceiver(receiver, filter);

                            Log.d(TAG, "KeyguardManager: "+myKM.inKeyguardRestrictedInputMode());
                        }
                    }catch (Exception e){

                    }

                    //  sendToWhatsapp(conversionHolder,context);
                }*/

            }
        });
        thread.start();
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean canSendSMS(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void sendSMS(final Context context, String phoneNumber, String message,String callingFlag,String imageForWhatsapp,String imageToSendOnSMS,String imageOrVideoFlag) {




        ConversionHolder conversionHolder = new ConversionHolder();
        String nameFromNumber = getContactNameByPhoneNumber(context,phoneNumber);
        conversionHolder.setPhoneNumber(phoneNumber);
        conversionHolder.setMassage(message);
        conversionHolder.setDate(new Date().getTime());



        Log.d(TAG, "sendSMSAAAAAA: "+callingFlag);


        SharedPreferences sendMassageRefrance = context.getSharedPreferences("MassagesWithFlags",MODE_PRIVATE);

        String incomingMassageForWhatsApp = sendMassageRefrance.getString("incomingMassageForWhatsapp","");
        String outgoingMassageForWhatsApp = sendMassageRefrance.getString("outgoingMassageForWhatsapp","");
        String missedCallMassageForWhatsApp = sendMassageRefrance.getString("missedCallMassageForWhatsapp","");
        String sendToAllMassageForWhatsApp =  sendMassageRefrance.getString("sendToAllMassageForWhatsapp","");


        String incomingImage = sendMassageRefrance.getString("incomingImageForWhatsapp","");
        String outgoingImage = sendMassageRefrance.getString("outgoingImageForWhatsapp","");
        String missedCallImage = sendMassageRefrance.getString("missedCallImageForWhatsapp","");
        String sendToAllImage = sendMassageRefrance.getString("sendToAllImageForWhatsapp",context.getResources().getString(R.string.sendToAllImageForWhatsApp));



        String incomingWhatsAppVideoOrImageFlag = sendMassageRefrance.getString("incomingWhatsappImageOrVideo","image");
        String outgoingWhatsAppVideoOrImageFlag = sendMassageRefrance.getString("outgoingWhatsappImageOrVideo","image");
        String missedCallWhatsAppVideoOrImageFlag = sendMassageRefrance.getString("missedCallWhatsappImageOrVideo","image");
        String sendToAllWhatsAppVideoOrImageFlag = sendMassageRefrance.getString("sendToAllWhatsappImageOrVideo","image");

        ScreenWakeUpHelper.turnScreenOn((Activity) context);


        //       conversionHolder.setWhatsAppMassage(whatsAppMassage);

        if (callingFlag.equals("Incoming")){

            if (!incomingMassageForWhatsApp.equals("")){
                conversionHolder.setWhatsAppMassage(incomingMassageForWhatsApp);
                conversionHolder.setImageForWhatsApp(incomingImage);

                imageOrVideoFlag = incomingWhatsAppVideoOrImageFlag;
                conversionHolder.setImageOrVideoFlag(imageOrVideoFlag);
            }else {
                conversionHolder.setWhatsAppMassage(sendToAllMassageForWhatsApp);
                conversionHolder.setImageForWhatsApp(sendToAllImage);
                imageOrVideoFlag = sendToAllWhatsAppVideoOrImageFlag;
                conversionHolder.setImageOrVideoFlag(imageOrVideoFlag);
            }

        }else if (callingFlag.equals("Outgoing")){

            Log.d(TAG, "sendSMS: "+outgoingMassageForWhatsApp);
           // Log.d(TAG, "sendSMS: "+sendToAllMassageForWhatsApp);

            if (!outgoingMassageForWhatsApp.equals("")){
                conversionHolder.setWhatsAppMassage(outgoingMassageForWhatsApp);
                conversionHolder.setImageForWhatsApp(outgoingImage);
                imageOrVideoFlag = outgoingWhatsAppVideoOrImageFlag;
                conversionHolder.setImageOrVideoFlag(imageOrVideoFlag);
            }else {
                conversionHolder.setWhatsAppMassage(sendToAllMassageForWhatsApp);
                conversionHolder.setImageForWhatsApp(sendToAllImage);
                imageOrVideoFlag = sendToAllWhatsAppVideoOrImageFlag;
                conversionHolder.setImageOrVideoFlag(imageOrVideoFlag);
            }

        }else if (callingFlag.equals("Missed")){

            if (!missedCallMassageForWhatsApp.equals("")){
                conversionHolder.setWhatsAppMassage(missedCallMassageForWhatsApp);
                conversionHolder.setImageForWhatsApp(missedCallImage);
                imageOrVideoFlag = missedCallWhatsAppVideoOrImageFlag;
                conversionHolder.setImageOrVideoFlag(imageOrVideoFlag);
            }else {
                conversionHolder.setWhatsAppMassage(sendToAllMassageForWhatsApp);
                conversionHolder.setImageForWhatsApp(sendToAllImage);
                imageOrVideoFlag = sendToAllWhatsAppVideoOrImageFlag;
                conversionHolder.setImageOrVideoFlag(imageOrVideoFlag);
            }

        }






        if (nameFromNumber.equals("")){
            conversionHolder.setCallerName(phoneNumber);
        }else {
            conversionHolder.setCallerName(nameFromNumber);
        }

        conversionHolder.setCallingFlag(callingFlag);

        if (!canSendSMS(context)) {
            Toast.makeText(context, "cannot_send_sms", Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences sendingFlags = context.getSharedPreferences("SendingFlag",MODE_PRIVATE);
        String whatsappFlag = sendingFlags.getString("whatsappFlag","true");
        String smsFlag = sendingFlags.getString("smsFlag","true");


        if (whatsappFlag.equals("true")){

            if (isNetworkAvailable(context)){
                wakeupScreen(context,conversionHolder,imageToSendOnSMS,imageOrVideoFlag);
            }else {
                conversionHolder.setWhatsAppDeliveryFlag(false);
                createNotificationForWhatsApp(conversionHolder,context,"WhatsApp Massage Fail to send Due to No Internet Connection..");


                if (smsFlag.equals("true")){
                    PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT_SMS_ACTION_NAME), PendingIntent.FLAG_IMMUTABLE);
                    PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED_SMS_ACTION_NAME), PendingIntent.FLAG_IMMUTABLE);

                    final SMSUtils smsUtils = new SMSUtils();
                    //register for sending and delivery
                    context.getApplicationContext().registerReceiver(smsUtils, new IntentFilter(SMSUtils.SENT_SMS_ACTION_NAME));
                    context.getApplicationContext().registerReceiver(smsUtils, new IntentFilter(DELIVERED_SMS_ACTION_NAME));

                    SmsManager sms = SmsManager.getDefault();
                    ArrayList<String> parts = sms.divideMessage(message+" \n\nMedia : "+imageToSendOnSMS);

                    ArrayList<PendingIntent> sendList = new ArrayList<>();
                    sendList.add(sentPI);

                    ArrayList<PendingIntent> deliverList = new ArrayList<>();
                    deliverList.add(deliveredPI);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                            List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                            SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(0);
                            SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);

                            SharedPreferences sharedPreferences = context.getSharedPreferences("defaultMassages", Context.MODE_PRIVATE);
                            String simFlag = sharedPreferences.getString("simFlg","sim1");


                            if (!simFlag.equals("default")){

                                if (simFlag.equals("sim1")) {
                                    SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId())
                                            .sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList); //send with sim1
                                }else {
                                    SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId())
                                            .sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);
                                }

                            }else {
                                sms.sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);
                            }

                        }else {
                            sms.sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);
                        }
                    } else {
                        sms.sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.unregisterReceiver(smsUtils);
                        }
                    }, 10000);

                    conversionHolder.setDeliveryFlag(true);
                }else {
                    conversionHolder.setDeliveryFlag(false);
                }
                saveDataToDB(conversionHolder,context);


            }

        }else{

            if (smsFlag.equals("true")){
                PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT_SMS_ACTION_NAME), PendingIntent.FLAG_IMMUTABLE);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED_SMS_ACTION_NAME), PendingIntent.FLAG_IMMUTABLE);

                final SMSUtils smsUtils = new SMSUtils();
                //register for sending and delivery
                context.getApplicationContext().registerReceiver(smsUtils, new IntentFilter(SMSUtils.SENT_SMS_ACTION_NAME));
                context.getApplicationContext().registerReceiver(smsUtils, new IntentFilter(DELIVERED_SMS_ACTION_NAME));

                SmsManager sms = SmsManager.getDefault();
                //ArrayList<String> parts = sms.divideMessage(message+" \n\nMedia : "+imageToSendOnSMS);
                ArrayList<String> parts = sms.divideMessage(message);

                ArrayList<PendingIntent> sendList = new ArrayList<>();
                sendList.add(sentPI);

                ArrayList<PendingIntent> deliverList = new ArrayList<>();
                deliverList.add(deliveredPI);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                        List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                        SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(0);
                        SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);

                        SharedPreferences sharedPreferences = context.getSharedPreferences("defaultMassages", Context.MODE_PRIVATE);
                        String simFlag = sharedPreferences.getString("simFlg","sim1");


                        if (!simFlag.equals("default")){

                            if (simFlag.equals("sim1")) {
                                SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId())
                                        .sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList); //send with sim1
                            }else {
                                SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId())
                                        .sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);
                            }

                        }else {
                            sms.sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);
                        }

                    }else {
                        sms.sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);
                    }
                } else {
                    sms.sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.unregisterReceiver(smsUtils);
                    }
                }, 10000);

                conversionHolder.setDeliveryFlag(true);
            }else {
                conversionHolder.setDeliveryFlag(false);
            }

            conversionHolder.setWhatsAppDeliveryFlag(false);
            saveDataToDB(conversionHolder,context);
        }
    }

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

    private static void saveDataToDB(ConversionHolder conversionHolder,Context context) {

/*        String nameFromNumber = getContactNameByPhoneNumber(context,phoneNumber);

        ConversionHolder conversionHolder = new ConversionHolder();
        conversionHolder.setPhoneNumber(phoneNumber);
        conversionHolder.setMassage(message);
        conversionHolder.setDeliveryFlag(true);
        conversionHolder.setDate(new Date().getTime());

        conversionHolder.setImageForWhatsApp(imageForWhatsapp);
        if (nameFromNumber.equals("")){
            conversionHolder.setCallerName(phoneNumber);
        }else {
            conversionHolder.setCallerName(nameFromNumber);
        }

        conversionHolder.setCallingFlag(callingFlag);*/

        InsertAsnkTask insertAsnkTask = new InsertAsnkTask(context);
        insertAsnkTask.execute(conversionHolder);


    }

    private static void saveToOnlineDB(ConversionHolder conversionHolder, boolean deliveryFlag, Context context) {

        HashMap<String,Object> hashMap  = new HashMap<>();
        hashMap.put("massage",conversionHolder.getMassage());
        hashMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("date",new Timestamp(new Date()));
        hashMap.put("toUserContact",conversionHolder.getPhoneNumber());
        hashMap.put("deliveryFlag",deliveryFlag);
        hashMap.put("callingFlag",conversionHolder.getCallingFlag());
        hashMap.put("callerName",conversionHolder.getCallerName());
        hashMap.put("imageToSendOnWhatsApp",conversionHolder.getImageForWhatsApp());
        hashMap.put("whatsAppDeliveryFlag",conversionHolder.isWhatsAppDeliveryFlag());
        hashMap.put("imageOrVideo",conversionHolder.getImageOrVideoFlag());

        hashMap.put("whatsappMassage",conversionHolder.getWhatsAppMassage());


        CollectionReference firebaseFirestore = FirebaseFirestore.getInstance().collection("Conversion");

        firebaseFirestore.add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {

                }
            }
        });

    }

    private static void wakeupScreen(Context context, ConversionHolder conversionHolder, String imageToSendOnSMS, String imageOrVideoFlag) {

    //    CustomModel customModel = getJsonData(conversionHolder);
        Call<Object> massageObjCall;
        IApiInterface iApiInterface;
        iApiInterface = RetrofitInstance.getRetrofitClint().create(IApiInterface.class);

        Log.d(TAG, "wakeupScreen: "+imageOrVideoFlag);
        if (imageOrVideoFlag.equals("video")){
            com.appdroid.reply99.whatsappAutoReply.VideoTemplate.CustomModel customModel = getJsonDataForVideoTemp(conversionHolder);
            massageObjCall  = iApiInterface.sendCustomMassageWithVideo("108823118560694",customModel);
        }else {
            CustomModel customModel = getJsonData(conversionHolder);
            massageObjCall  = iApiInterface.sendCustomMassage("108823118560694",customModel);
        }




      //  Call<Object> massageObjCall  = iApiInterface.sendCustomMassage("108823118560694",customModel);

        SharedPreferences sendingFlags = context.getSharedPreferences("SendingFlag",MODE_PRIVATE);


        String smsFlag = sendingFlags.getString("smsFlag","true");



        massageObjCall.enqueue(new Callback<Object>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d("ALFOSDOSJO", "onResponse: "+response);
                if (response.code() == 200){
                    conversionHolder.setWhatsAppDeliveryFlag(true);

                    createNotificationForWhatsApp(conversionHolder,context,"WhatsApp Massage Send Successfully..");
                }else {
                    createNotificationForWhatsApp(conversionHolder,context,"WhatsApp Massage Fail to send");
                    conversionHolder.setWhatsAppDeliveryFlag(false);

                }





                if (smsFlag.equals("true")){
                    PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT_SMS_ACTION_NAME), PendingIntent.FLAG_IMMUTABLE);
                    PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED_SMS_ACTION_NAME), PendingIntent.FLAG_IMMUTABLE);

                    final SMSUtils smsUtils = new SMSUtils();
                    //register for sending and delivery
                    context.getApplicationContext().registerReceiver(smsUtils, new IntentFilter(SMSUtils.SENT_SMS_ACTION_NAME));
                    context.getApplicationContext().registerReceiver(smsUtils, new IntentFilter(DELIVERED_SMS_ACTION_NAME));

                    SmsManager sms = SmsManager.getDefault();
                    ArrayList<String> parts = sms.divideMessage(conversionHolder.getMassage()+" \n\nMedia : "+imageToSendOnSMS);

                    ArrayList<PendingIntent> sendList = new ArrayList<>();
                    sendList.add(sentPI);

                    ArrayList<PendingIntent> deliverList = new ArrayList<>();
                    deliverList.add(deliveredPI);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                            List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                            SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(0);
                            SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);

                            SharedPreferences sharedPreferences = context.getSharedPreferences("defaultMassages", Context.MODE_PRIVATE);
                            String simFlag = sharedPreferences.getString("simFlg","sim1");


                            if (!simFlag.equals("default")){

                                if (simFlag.equals("sim1")) {
                                    SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId())
                                            .sendMultipartTextMessage(conversionHolder.getPhoneNumber(), null, parts, sendList, deliverList); //send with sim1
                                }else {
                                    SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId())
                                            .sendMultipartTextMessage(conversionHolder.getPhoneNumber(), null, parts, sendList, deliverList);
                                }

                            }else {
                                sms.sendMultipartTextMessage(conversionHolder.getPhoneNumber(), null, parts, sendList, deliverList);
                            }

                        }else {
                            sms.sendMultipartTextMessage(conversionHolder.getPhoneNumber(), null, parts, sendList, deliverList);
                        }
                    } else {
                        sms.sendMultipartTextMessage(conversionHolder.getPhoneNumber(), null, parts, sendList, deliverList);
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.unregisterReceiver(smsUtils);
                        }
                    }, 10000);

                    conversionHolder.setDeliveryFlag(true);
                }else {
                    conversionHolder.setDeliveryFlag(false);
                }




                saveDataToDB(conversionHolder,context);






            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d("ALFOSDOSJO", "onFailure: "+t.getLocalizedMessage());
            }
        });

    }


    private void createNotificationForSMS(ConversionHolder conversionHolder, boolean deliveryFlag, Context context) {

        SharedPreferences massageSendingSharedPreferences = context.getSharedPreferences("SendingFlag",MODE_PRIVATE);
        String notificationFlag = massageSendingSharedPreferences.getString("notificationFlag","true");


        if (notificationFlag.equals("true")){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel("myCh","channel",NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(context, ConversionActivity.class);



            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(FlashActivity.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);


            NotificationCompat.Builder builder;

            if (deliveryFlag){
                builder = new NotificationCompat.Builder(context,"myCh")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText("successfully")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("SMS send to : "+conversionHolder.getCallerName());
            }else {

                builder = new NotificationCompat.Builder(context,"myCh")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText("Fail to send SMS")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setContentTitle("Fail to send sms to : "+conversionHolder.getCallerName());


            }

            builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
            Notification notification = builder.build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(1,notification);

        }

    }

    private static void createNotificationForWhatsApp(ConversionHolder conversionHolder, Context context, String massage) {

        Log.d(TAG, "createNotificationForWhatsApp: "+massage);


        SharedPreferences massageSendingSharedPreferences = context.getSharedPreferences("SendingFlag",MODE_PRIVATE);
        String notificationFlag = massageSendingSharedPreferences.getString("notificationFlag","true");


        if (notificationFlag.equals("true")){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel("myCh","channel",NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(context, ConversionActivity.class);



            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(FlashActivity.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);


            NotificationCompat.Builder builder;

            builder = new NotificationCompat.Builder(context,"myCh")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(massage)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("WhatsApp massage send to : "+conversionHolder.getCallerName());



            builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
            Notification notification = builder.build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(2,notification);

            }
        }

    private static CustomModel getJsonData(ConversionHolder conversionHolder) {

        Log.d(TAG, "CustomModel: "+conversionHolder.getImageForWhatsApp());


        CustomModel customModel = new CustomModel();
        customModel.setMessagingProduct("whatsapp");
        customModel.setRecipientType("individual");
        customModel.setTo(conversionHolder.getPhoneNumber());   //  contact too
        customModel.setType("template");

//                customModel.setTemplate();

        Template template = new Template();
        template.setName("auto_reply_third");

        //   template.setName("auto_reply");

        Language language = new Language();
        language.setCode("en");

        template.setLanguage(language);

        List<ComponentsItem> componentsItemsList = new ArrayList<>();


        ComponentsItem componentsItemHeader =  new ComponentsItem();
        componentsItemHeader.setType("header");


        ParametersItem parametersItem = new ParametersItem();
        parametersItem.setType("image");

        Image image  =new Image();
        image.setLink(conversionHolder.getImageForWhatsApp());  // image to send



        parametersItem.setImage(image);  //  parameter item completed..

        List<ParametersItem> parametersItemsListForHeader  = new ArrayList<>();
        parametersItemsListForHeader.add(parametersItem);

        componentsItemHeader.setParameters(parametersItemsListForHeader);




        ComponentsItem componentsItemForBody= new ComponentsItem();
        componentsItemForBody.setType("body");

        List<ParametersItem> parametersItemsForBody = new ArrayList<>();



        ParametersItem parametersItem2 = new ParametersItem();
        parametersItem2.setType("text");
        parametersItem2.setText(conversionHolder.getWhatsAppMassage());  // thsi is adden nantar  ///   massagge to send

        ParametersItem parametersItem1 = new ParametersItem();
        parametersItem1.setType("text");
        parametersItem1.setText(conversionHolder.getCallerName());   ///   caller name to send

        parametersItemsForBody.add(parametersItem1);
        parametersItemsForBody.add(parametersItem2); // thsi is adden nantar

        componentsItemForBody.setParameters(parametersItemsForBody);




        ParametersItem parametersItemForButton = new ParametersItem();
        parametersItemForButton.setText(
                "/nobroker/");
        //   parametersItemForButton.setPayload("a");


      /*  List<ParametersItem> parametersItemsListForButton = new ArrayList<>();
        parametersItemsListForButton.add(parametersItemForButton);

        ComponentsItem componentsItemButton = new ComponentsItem();
        componentsItemButton.setType("button");
        componentsItemButton.setSubType("url");

        componentsItemButton.setIndex("0");
        componentsItemButton.setParameters(parametersItemsListForButton);*/





        componentsItemsList.add(componentsItemHeader);
        componentsItemsList.add(componentsItemForBody);
        //componentsItemsList.add(componentsItemButton);

        template.setComponents(componentsItemsList);

        customModel.setTemplate(template);

        Gson gson = new Gson();
        String jsonFromModel =  gson.toJson(customModel);

        Log.d("ASASAS", "onClick: "+jsonFromModel);

        return customModel;
    }


    private static com.appdroid.reply99.whatsappAutoReply.VideoTemplate.CustomModel getJsonDataForVideoTemp(ConversionHolder conversionHolder) {
        com.appdroid.reply99.whatsappAutoReply.VideoTemplate.CustomModel customModel = new    com.appdroid.reply99.whatsappAutoReply.VideoTemplate.CustomModel();
        customModel.setMessagingProduct("whatsapp");
        customModel.setRecipientType("individual");
        customModel.setTo(conversionHolder.getPhoneNumber());   //  contact too
        customModel.setType("template");

//                customModel.setTemplate();

        com.appdroid.reply99.whatsappAutoReply.VideoTemplate.Template template = new com.appdroid.reply99.whatsappAutoReply.VideoTemplate.Template();
        template.setName("video_template");

        //   template.setName("auto_reply");

        com.appdroid.reply99.whatsappAutoReply.VideoTemplate.Language language = new com.appdroid.reply99.whatsappAutoReply.VideoTemplate.Language();
        language.setCode("en");

        template.setLanguage(language);

        List<com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ComponentsItem> componentsItemsList = new ArrayList<>();


        com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ComponentsItem componentsItemHeader =  new com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ComponentsItem();
        componentsItemHeader.setType("header");


        com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ParametersItem parametersItema = new  com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ParametersItem();
        parametersItema.setType("video");

/*        Image image  =new Image();
        image.setLink(conversionHolder.getImageForWhatsApp());  // image to send*/


        com.appdroid.reply99.whatsappAutoReply.VideoTemplate.Video video = new com.appdroid.reply99.whatsappAutoReply.VideoTemplate.Video();
        video.setLink(conversionHolder.getImageForWhatsApp());



        parametersItema.setVideo(video);  //  parameter item completed..

        List<com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ParametersItem> parametersItemsListForHeader  = new ArrayList<>();
        parametersItemsListForHeader.add(parametersItema);

        componentsItemHeader.setParameters(parametersItemsListForHeader);




        com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ComponentsItem componentsItemForBody= new com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ComponentsItem();
        componentsItemForBody.setType("body");

        List<com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ParametersItem> parametersItemsForBody = new ArrayList<>();



        com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ParametersItem parametersItem2 = new com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ParametersItem();
        parametersItem2.setType("text");
        parametersItem2.setText(conversionHolder.getWhatsAppMassage());  // thsi is adden nantar  ///   massagge to send

    /*    com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ParametersItem parametersItem1 = new com.appdroid.reply99.whatsappAutoReply.VideoTemplate.ParametersItem();
        parametersItem1.setType("text");
        parametersItem1.setText(conversionHolder.getCallerName());   ///   caller name to send*/

        //parametersItemsForBody.add(parametersItem1);
        parametersItemsForBody.add(parametersItem2); // thsi is adden nantar

        componentsItemForBody.setParameters(parametersItemsForBody);




        ParametersItem parametersItemForButton = new ParametersItem();
        parametersItemForButton.setText(
                "/nobroker/");


        componentsItemsList.add(componentsItemHeader);
        componentsItemsList.add(componentsItemForBody);
        //componentsItemsList.add(componentsItemButton);

        template.setComponents(componentsItemsList);

        customModel.setTemplate(template);

        Gson gson = new Gson();
        String jsonFromModel =  gson.toJson(customModel);

        Log.d("ASASAS", "onClick: "+jsonFromModel);

        return customModel;
    }



    private static void sendToWhatsapp(ConversionHolder conversionHolder, Context context) throws UnsupportedEncodingException, InterruptedException {
        PackageManager  packageManager = context.getPackageManager();
        String url = "https://api.whatsapp.com/send?phone="+conversionHolder.getPhoneNumber()+"&text="+ URLEncoder.encode(conversionHolder.getMassage(),"UTF-8");
        Intent intent  = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.whatsapp");
        intent.setData(Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (intent.resolveActivity(packageManager) != null){
                context.startActivity(intent);
                Thread.sleep(1000);
                Log.d(TAG, "sendToWhatsapp: calll");
            }

    }

    public static class InsertAsnkTask extends AsyncTask<ConversionHolder,Void,Void> {
        Context context;
        public InsertAsnkTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(ConversionHolder... massage) {
            UtilityRoomDatabase.getInstance(context)
                    .massagesDao()
                    .insertConversion(massage[0]);
            return null;
        }
    }

}
