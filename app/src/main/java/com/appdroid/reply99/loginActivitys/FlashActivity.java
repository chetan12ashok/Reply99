package com.appdroid.reply99.loginActivitys;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.appdroid.reply99.R;
import com.appdroid.reply99.activity.Dashboard;
import com.appdroid.reply99.activity.TakePermitionFromUser;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.EasyPermissions;

public class FlashActivity extends AppCompatActivity {
    Timer timer;
    Animation topani;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        timer = new Timer();
        topani = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        logo = findViewById(R.id.logo);
        logo.setAnimation(topani);

        String[] perms = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,Manifest.permission.SEND_SMS,Manifest.permission.READ_CALL_LOG};

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null){

                    if (EasyPermissions.hasPermissions(FlashActivity.this, perms)){

                        startActivity(new Intent(FlashActivity.this, Dashboard.class));

                    }else {
                        startActivity(new Intent(FlashActivity.this, TakePermitionFromUser.class));
                    }

                }else {
                    startActivity(new Intent(FlashActivity.this, LoginActivity.class));
                }
                finish();

            }
        },1500);

    }




}
