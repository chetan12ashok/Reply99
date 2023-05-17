package com.appdroid.reply99.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.appdroid.reply99.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class TakePermitionFromUser extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    Button agreeBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_permition_from_user);
        agreeBTN = findViewById(R.id.agreeBTN);
        agreeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requastPermition();
            }
        });
    }

    private void requastPermition() {

        String[] perms = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,Manifest.permission.SEND_SMS,Manifest.permission.READ_CALL_LOG};

        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...

            startActivity(new Intent(TakePermitionFromUser.this,Dashboard.class));

            finish();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this,"We need this permissions for this Work",
                    123, perms);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        String[] permss = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,Manifest.permission.SEND_SMS,Manifest.permission.READ_CALL_LOG};
        if (EasyPermissions.hasPermissions(this, permss)){
            startActivity(new Intent(TakePermitionFromUser.this,Dashboard.class));
            finish();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){

        }
    }
}