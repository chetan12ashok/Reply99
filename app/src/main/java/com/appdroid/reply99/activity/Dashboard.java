package com.appdroid.reply99.activity;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.appdroid.reply99.ProfileFragment;
import com.appdroid.reply99.R;
import com.appdroid.reply99.WhatsappAccessibilityService;
import com.appdroid.reply99.fragment.HomeFragment;
import com.appdroid.reply99.fragment.MySettings;
import com.appdroid.reply99.loginActivitys.FlashActivity;
import com.appdroid.reply99.model.UserInfo;
import com.appdroid.reply99.room.MassageHolder;
import com.appdroid.reply99.room.ProductViewModel;
import com.appdroid.reply99.room.UtilityRoomDatabase;
import com.appdroid.reply99.service.KeepAliveService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    LinearLayout homeLay, settingLay, profileLay;
    ImageView homeIcon, settingIcon, profileIcon;
    TextView homeTxt, settingTxt, profileTxt;

    private int iconTint;
    private int primary;
    ProductViewModel productViewModel;

    boolean backPress;
    public static Context context;

    BottomSheetDialog backPressBottomSheat;
    TextView yes,no;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);


        backPressBottomSheat = new BottomSheetDialog(Dashboard.this);
        backPressBottomSheat.setContentView(R.layout.exit_dialog);
        backPressBottomSheat.setCanceledOnTouchOutside(true);

        yes = backPressBottomSheat.findViewById(R.id.yes);
        no = backPressBottomSheat.findViewById(R.id.no);



        context = Dashboard.this;
        Resources res = getResources();
        iconTint = res.getColor(R.color.iconTint);
        primary = res.getColor(R.color.primary);

        homeLay = findViewById(R.id.homeLay);
        settingLay = findViewById(R.id.settingLay);
        profileLay = findViewById(R.id.profileLay);

        homeIcon = findViewById(R.id.homeIcon);
        settingIcon = findViewById(R.id.settingIcon);
        profileIcon = findViewById(R.id.profileIcon);

        homeTxt = findViewById(R.id.homeTxt);
        settingTxt = findViewById(R.id.settingTxt);
        profileTxt = findViewById(R.id.profileTxt);

        homeIcon.setImageResource(R.drawable.ic_home_color);
        homeTxt.setTextColor(primary);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

        homeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeIcon.setImageResource(R.drawable.ic_home_color);
                settingIcon.setImageResource(R.drawable.ic_settings);
                profileIcon.setImageResource(R.drawable.ic_user);

                homeTxt.setTextColor(primary);
                settingTxt.setTextColor(iconTint);
                profileTxt.setTextColor(iconTint);

                HomeFragment homeFragment =new HomeFragment();
                homeFragment.setRetainInstance(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
            }
        });
        settingLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeIcon.setImageResource(R.drawable.ic_home);
                settingIcon.setImageResource(R.drawable.ic_settings_color);
                profileIcon.setImageResource(R.drawable.ic_user);

                homeTxt.setTextColor(iconTint);
                settingTxt.setTextColor(primary);
                profileTxt.setTextColor(iconTint);

                MySettings settingFragment =new MySettings();
                settingFragment.setRetainInstance(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, settingFragment).commit();
            }
        });
        profileLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeIcon.setImageResource(R.drawable.ic_home);
                settingIcon.setImageResource(R.drawable.ic_settings);
                profileIcon.setImageResource(R.drawable.ic_user_color);

                homeTxt.setTextColor(iconTint);
                settingTxt.setTextColor(iconTint);
                profileTxt.setTextColor(primary);

                ProfileFragment profileFragment =new ProfileFragment();
                profileFragment.setRetainInstance(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();

             /*   ConvertionFragmnet convertionFragmnet =new ConvertionFragmnet();
                convertionFragmnet.setRetainInstance(true);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, convertionFragmnet,"ConvertionFragmnet").commit();*/

            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                //finish();
              //  System.exit(0);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backPressBottomSheat.dismiss();
            }
        });




        checkUserLoginOnse();
    }


    @Override
    public void onBackPressed() {
        if (backPressBottomSheat.isShowing()){
            backPressBottomSheat.dismiss();

            SharedPreferences sendMassageRefrance = getApplicationContext().getSharedPreferences("MassagesWithFlags",MODE_PRIVATE);
            boolean serviceFlag = sendMassageRefrance.getBoolean("serviceFlag",true);
            if (serviceFlag){
                Intent myIntent = new Intent(getApplicationContext(), KeepAliveService.class);
                myIntent.setAction("com.appdroid.reply99.activity.Dashboard");
                PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 2);
                alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            super.onBackPressed();




        }else {
            backPressBottomSheat.show();
        }

    }

    private void checkUserLoginOnse() {

        String currentDeviceId = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);


        Query query = FirebaseFirestore.getInstance().collection("UserInfo")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getUid()).limit(1);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot snapshot : value.getDocuments()){
                  UserInfo userInfo =  snapshot.toObject(UserInfo.class);
                  userInfo.setDocId(snapshot.getId());




                  if (!userInfo.getDeviceId().equals(currentDeviceId)){

                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {

                              startActivity(new Intent(Dashboard.this,MultiLoginDetector.class));
                              finish();
                          //    showLogoutDialog();
                          }
                      });

                  }else if(!userInfo.isActivationFlag()){
                      Intent intent = new Intent(Dashboard.this,MultiLoginDetector.class);
                      intent.setAction("activation");
                      startActivity(intent);
                      finish();
                  }
                }
            }
        });

    }

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(Dashboard.this);
        dialog.setContentView(R.layout.multi_login_dialog);
        Button login = dialog.findViewById(R.id.loginAgain);
        dialog.show();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Dashboard.this, FlashActivity.class));
                finish();
            }
        });
    }
}