package com.appdroid.reply99.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appdroid.reply99.R;
import com.appdroid.reply99.loginActivitys.FlashActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MultiLoginDetector extends AppCompatActivity {
    Button login;
    SharedPreferences.Editor editor;
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mu);

        String action = getIntent().getAction();
        login= findViewById(R.id.loginAgain);
        if (action != null){
            txt = findViewById(R.id.txt);
            txt.setText("Your Account is Deactivated Please Contact Support..");
            login.setVisibility(View.GONE);
        }

        editor = getSharedPreferences("MassagesWithFlags", MODE_PRIVATE).edit();
        editor.putBoolean("serviceFlag",false);
        editor.apply();



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MultiLoginDetector.this, FlashActivity.class));
                finish();
            }
        });
    }
}