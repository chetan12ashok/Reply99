package com.appdroid.reply99.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.appdroid.reply99.R;
import com.appdroid.reply99.room.ConversionHolder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WackUP extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            this.setTurnScreenOn(true);
            setShowWhenLocked(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(this.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this,null);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |

                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }

        setContentView(R.layout.activity_wack_up);
        Log.d("WackUP", "onCreate:incalll ");
         ConversionHolder conversionHolder= (ConversionHolder) getIntent().getSerializableExtra("all");




      /*  getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);*/

        try {
            sendToWhatsapp(conversionHolder,this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         finish();
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
            Log.d("WackUP", "sendToWhatsapp: calll");
        }




/*

        PackageManager pm=context.getPackageManager();
        try {

           // String toNumber = "+54 11 5555 9999"; // contains spaces.
            String toNumber = conversionHolder.getPhoneNumber();
            toNumber = toNumber.replace("+", "").replace(" ", "");

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "HOLA!");
            sendIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("https://i.stack.imgur.com/j2xQy.png"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setType("text/plain");
            sendIntent.setType("image/*");
            context. startActivity(sendIntent);

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "WhatsApp no esta instalado!", Toast.LENGTH_SHORT).show();
        }
*/



    }



}