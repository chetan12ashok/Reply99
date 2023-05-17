package com.appdroid.reply99.loginActivitys;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.appdroid.reply99.R;
import com.appdroid.reply99.Utils.OtpReceivedInterface;
import com.appdroid.reply99.Utils.SmsBroadcastReceiver;
import com.appdroid.reply99.activity.Dashboard;
import com.appdroid.reply99.activity.TakePermitionFromUser;
import com.appdroid.reply99.activity.main.MainActivity;
import com.appdroid.reply99.model.UserInfo;
import com.appdroid.reply99.room.MassageHolder;
import com.appdroid.reply99.room.ProductViewModel;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class OTPActivity extends AppCompatActivity {

    Button login;
    TextView termsTxt;
    TextInputEditText otp;
    String codeFromServer,action;
    Dialog progressDialog;
    UserInfo userInfo;

    SmsBroadcastReceiver mSmsBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);




        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);

        Intent i =getIntent();

        codeFromServer = i.getStringExtra("code");
        action = i.getAction();

        if (action != null){
            if (action.equals("registration") || action.equals("updateNumber")){
                userInfo = (UserInfo) i.getSerializableExtra("all");
            }
        }


        termsTxt = findViewById(R.id.termsTxt);
        login = findViewById(R.id.login);
        otp = findViewById(R.id.otp);

        mSmsBroadcastReceiver = new SmsBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(mSmsBroadcastReceiver, intentFilter);


        mSmsBroadcastReceiver.setOnOtpListeners(new OtpReceivedInterface() {
            @Override
            public void onOtpReceived(String otsp) {
                String otpFromMassage = otsp.substring(0, 6);
                otp.setText(otpFromMassage);


                codeVerification();

                Log.d("SSSSSSSSAAAAA", "onOtpReceived: "+otsp);
            }

            @Override
            public void onOtpTimeout() {
                Log.d("SSSSSSSSAAAAA", "time  out: ");
            }
        });


        startSMSListener();

        termsTxt.setText(Html.fromHtml("By registering, you are accepting all the <b><u>Terms and Conditions</u></b>."));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                codeVerification();
            }
        });
        termsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }
    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
              /*  layoutInput.setVisibility(View.GONE);
                layoutVerify.setVisibility(View.VISIBLE);*/
              //  Toast.makeText(OTPActivity.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                Toast.makeText(OTPActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }



    private void codeVerification(){



        String code = otp.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeFromServer, code);


        if (action != null){
            if (action.equals("updateNumber")){
                if (credential.getSmsCode().equals(code)){
                    updateContactOnly(credential);
                }else {
                    Toast.makeText(this, "Please enter valid otp..!", Toast.LENGTH_SHORT).show();
                }

            }else {
                signInWithPhoneAuthCredential(credential);
            }
        }else {
            signInWithPhoneAuthCredential(credential);
        }



    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    if (action != null){

                        Log.d("LACHETAN", "onComplete: "+action);
                        if (action.equals("directLogin")){


                            updateCurrentDeviceId();


                        }else if (action.equals("registration")){
                            saveData();
                        }
                    }else {
                      /*  Intent intent = new Intent(OTPActivity.this, Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();*/

                        updateCurrentDeviceId();
                    }
                }else {

                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Invalid otp.!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    otp.setError("Invalid otp.!");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("addOnFailureListener", "onFailure: "+e.getLocalizedMessage());
            }
        });
    }

    private void updateContactOnly(PhoneAuthCredential credential) {

        FirebaseAuth.getInstance().getCurrentUser().updatePhoneNumber(credential)
                .addOnCompleteListener(new OnCompleteListener <Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task <Void> task) {
                                               if (task.isSuccessful()) {

                                                   HashMap<String,Object> hashMap = new HashMap<>();
                                                   hashMap.put("contactNumber",userInfo.getContactNumber());
                                                   hashMap.put("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                   DocumentReference documentReference = FirebaseFirestore.getInstance().collection("UserInfo").document(userInfo.getDocId());
                                                   documentReference.update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           if (task.isSuccessful()){
                                                               Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Contact Updated...!", Snackbar.LENGTH_LONG);
                                                               snackbar.show();

                                                               Log.d("CAQQQQQQ", "onComplete: ");

                                                               Intent intent = new Intent(OTPActivity.this, TakePermitionFromUser.class);
                                                               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                               startActivity(intent);
                                                               progressDialog.dismiss();

                                                               finishAffinity();


                                                           }else {
                                                               Log.d("CAQQQQQQ", "isSuccessful: false ");

                                                           }
                                                       }
                                                   });
                                                   // Update Successfully
                                               } else {

                                                   Log.d("CAQQQQQQ", "onComplete: else ");
                                                   // Failed
                                               }
                                           }
                                       }
                );
    }

    private void updateCurrentDeviceId() {
        String deviceId = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);




        Query query = FirebaseFirestore.getInstance().collection("UserInfo")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getUid()).limit(1);


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                    HashMap<String,Object> hashMap  = new HashMap<>();
                    hashMap.put("deviceId",deviceId);

                    Task<Void> firebaseFirestore = FirebaseFirestore.getInstance().collection("UserInfo")
                            .document(snapshot.getId()).update(hashMap);

                    firebaseFirestore.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Intent intent = new Intent(OTPActivity.this, TakePermitionFromUser.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finishAffinity();
                            }
                        }
                    });
                }
            }
        });




    }

    private void saveData() {
        Log.d("LACHETAN", "onComplete: "+userInfo.getKeywords().size());
        String deviceId = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);


        HashMap<String,Object> hashMap  = new HashMap<>();
        hashMap.put("userName",userInfo.getUserName());
        hashMap.put("contactNumber",userInfo.getContactNumber());
        hashMap.put("keywords",userInfo.getKeywords());
        hashMap.put("activationFlag",true);
        hashMap.put("lastName",userInfo.getLastName());
        hashMap.put("email",userInfo.getEmail());
        hashMap.put("dob",userInfo.getDob());
        hashMap.put("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());

        hashMap.put("deviceId",deviceId);


        Task<DocumentReference> firebaseFirestore = FirebaseFirestore.getInstance().collection("UserInfo").add(hashMap);
        firebaseFirestore.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()){

                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Welcome to Family..!", Snackbar.LENGTH_LONG);
                        snackbar.show();

                        Intent intent = new Intent(OTPActivity.this, TakePermitionFromUser.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finishAffinity();
                    }
            }
        });

    }


}
