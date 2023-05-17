package com.appdroid.reply99.loginActivitys;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appdroid.reply99.R;
import com.appdroid.reply99.model.UserInfo;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    Button sendOTP;
    TextView registerTxt;

    String action;
    UserInfo userInfo;
    CountryCodePicker countryCodePicker;
    TextInputEditText mobNumber;

    Dialog progressDialog;
    private static final int CREDENTIAL_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerTxt = findViewById(R.id.registerTxt);
        sendOTP = findViewById(R.id.sentOtp);
        mobNumber = findViewById(R.id.mobNumber);
        countryCodePicker = findViewById(R.id.ccp);
        countryCodePicker.registerCarrierNumberEditText(mobNumber);

        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);

        Intent intent = getIntent();
        action = intent.getAction();

        if (action != null){

            if (action.equals("updateNumber")){


                userInfo = (UserInfo) intent.getSerializableExtra("all");

            }else {
                userInfo = (UserInfo) intent.getSerializableExtra("all");
                mobNumber.setText(""+userInfo.getContactNumber());
                countryCodePicker.setVisibility(View.GONE);
                progressDialog.show();
                getVerificationCode(userInfo.getContactNumber());
            }


        }

        registerTxt.setText(Html.fromHtml("If you are not registered, for registration <b><u>click here</u></b>."));

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contact = mobNumber.getText().toString().trim().replace(" ", "");
                if (contact.length() == 10){
                    progressDialog.show();

                   // String contact = mobNumber.getText().toString().trim().replace(" ", "");

                    if (action != null){
                        if (action.equals("updateNumber")){
                            getVerificationCode(countryCodePicker.getSelectedCountryCodeWithPlus()+contact);
                        }else {
                            checkContactAllReadyExist(countryCodePicker.getSelectedCountryCodeWithPlus()+contact);
                        }
                    }else {
                        checkContactAllReadyExist(countryCodePicker.getSelectedCountryCodeWithPlus()+contact);
                    }



                }else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter valid mobile number.!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    mobNumber.setError("Please enter valid mobile number.!");
                }

            }
        });

        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
        mobNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    phoneSelection();
                }
            }
        });


    }

    private void phoneSelection() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Credentials.getClient(LoginActivity.this).getHintPickerIntent(hintRequest);

        try
        {
            startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0,new Bundle());
        }
        catch (IntentSender.SendIntentException e)
        {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK)
        {
            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);

            //      String contact = credentials.getId().substring(3).trim().replace(" ", "");
            String text = credentials.getId().substring(3);
            String contact = text.trim().replace(" ", "");
            mobNumber.setText(contact);

        }
        else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE){
            // *** No phone numbers available ***
            Toast.makeText(LoginActivity.this, "No phone numbers found", Toast.LENGTH_LONG).show();
        }


    }


    private void checkContactAllReadyExist(String s) {

        Query query = FirebaseFirestore.getInstance().collection("UserInfo").whereEqualTo("contactNumber",s);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size()>0) {
                    getVerificationCode(s);
                }else {

                    progressDialog.dismiss();

                    Toast.makeText(LoginActivity.this, "Contact number is not registered please register first..!", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(i);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("SSSSSSA", "onFailure: "+e.getLocalizedMessage());
            }
        });
    }


    private void getVerificationCode(String phoneNo){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNo)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {

   /*         Intent intent = new Intent(LoginActivity.this,CreateAccount.class);
            startActivity(intent);
            finish();*/

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
            Log.d("GGGGGGGGGGG", "onVerificationFailed: +"+e.getLocalizedMessage());
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
                Log.d("GGGGGGGGGGG", "onVerificationFailed: +"+e.getLocalizedMessage());
            } else if (e instanceof FirebaseTooManyRequestsException) {

                Log.d("GGGGGGGGGGG", "onVerificationFailed: +"+e.getLocalizedMessage());
            }

        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {

            Toast.makeText(LoginActivity.this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();


            if (action != null){
                if (action.equals("directLogin")) {
                    Intent i = new Intent(getApplicationContext(), OTPActivity.class);
                    i.setAction("directLogin");
                    i.putExtra("code", verificationId);
                    startActivity(i);
                }else if (action.equals("registration")){
                    Intent i = new Intent(getApplicationContext(), OTPActivity.class);
                    i.setAction("registration");
                    i.putExtra("code", verificationId);
                    i.putExtra("all", userInfo);
                    startActivity(i);
                }else if(action.equals("updateNumber")){

                    String contact = mobNumber.getText().toString().trim().replace(" ", "");


                    Intent i = new Intent(getApplicationContext(), OTPActivity.class);
                    userInfo.setContactNumber(countryCodePicker.getSelectedCountryCodeWithPlus()+contact);
                    i.setAction("updateNumber");
                    i.putExtra("all", userInfo);
                    i.putExtra("code", verificationId);
                    startActivity(i);
                }

            }else{
                Intent i = new Intent(getApplicationContext(), OTPActivity.class);
                i.putExtra("code", verificationId);
                startActivity(i);
            }


            finish();

        }
    };


}
