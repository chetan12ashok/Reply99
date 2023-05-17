package com.appdroid.reply99.loginActivitys;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.appdroid.reply99.R;
import com.appdroid.reply99.model.UserInfo;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;
import com.mikhaellopez.lazydatepicker.LazyDatePicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG ="RegisterActivity" ;
    TextView termsTxt;
    TextInputEditText userName,lastName,email,dob,mobNumber;
    TextView loginTxt;
    EditText referralCode;
    Button register;
    CountryCodePicker countryCodePicker;
    private static final int CREDENTIAL_PICKER_REQUEST = 1;
    LazyDatePicker lazyDatePicker;
    boolean ageFlag = false;
    String dateOfBirth;

    String[] splitedStrings;
    List<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d("SSSSSSSSAAAA", "onCreate: "+getPackageName());

        lazyDatePicker = findViewById(R.id.lazyDatePicker);
        lazyDatePicker.setDateFormat(LazyDatePicker.DateFormat.DD_MM_YYYY);
        stringList = new ArrayList<>();

        lazyDatePicker.setOnDatePickListener(new LazyDatePicker.OnDatePickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDatePick(Date dateSelected) {

                Date currentDate = new Date();

                SimpleDateFormat completeFormat = new SimpleDateFormat("dd,MM,yyyy");
                dateOfBirth = completeFormat.format(dateSelected);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
                String inputYear = formatter.format(dateSelected);
                String currentYear = formatter.format(currentDate);




                int diff = Integer.parseInt(currentYear)-Integer.parseInt(inputYear);

                Log.d(TAG, "onDatePick: "+inputYear+"/"+currentYear);

                if (diff<14){
                    Toast.makeText(RegisterActivity.this, "Below 14 Year old allow..!", Toast.LENGTH_SHORT).show();
                    ageFlag = false;
                }else {
                    ageFlag = true;
                }

            }
        });

        findView();

        loginTxt.setText(Html.fromHtml("Already have an account? <b><u>Login here</u></b>"));


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrationFunction();
            }
        });

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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


    private void spliteKeyword(UserInfo userInfo) {
        String lowerString = userName.getText().toString().toLowerCase()+" "+lastName.getText().toString().toLowerCase();

        splitedStrings = lowerString.split(" ");
        for (String s : splitedStrings){


            //    stringList.add(s);

            String appendString ="";
            char[] chars = lowerString.toCharArray();
            for (char ch : chars){
                appendString.trim();
                appendString += ch;
                if (ch == ' '){

                }else {
                    appendString = appendString.replaceAll("\\s", " ");
                    Log.d("TAG", "spliteKeyword: "+appendString.trim());
                    stringList.add(appendString.trim());
                }
            }
            lowerString = lowerString.replace(s,"");
        }

        for (int i = 0; i < stringList.size(); i++){
            stringList.get(i).trim();
        }

        userInfo.setKeywords(stringList);
        checkContactAllReadyExist(userInfo);
        Log.d("AAASASSA", "spliteKeyword: "+stringList);
    }


    private void phoneSelection() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Credentials.getClient(RegisterActivity.this).getHintPickerIntent(hintRequest);

            try
            {
                startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0,new Bundle());
            }
            catch (IntentSender.SendIntentException e)
            {
                Log.d(TAG, "phoneSelection: "+e.getLocalizedMessage());
            }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK)
        {
            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);

      //     String contact = credentials.getId().substring(3).trim().replace(" ", "");
            String contact = credentials.getId().substring(3).trim().replace(" ", "");
            mobNumber.setText(contact);
            String contactF = mobNumber.getText().toString().trim().replace(" ", "");
            mobNumber.setText(contactF);

        }
        else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE)
        {
            // *** No phone numbers available ***
            Toast.makeText(RegisterActivity.this, "No phone numbers found", Toast.LENGTH_LONG).show();
        }


    }

    private void findView() {

        userName = findViewById(R.id.userName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        dob = findViewById(R.id.dob);

        mobNumber = findViewById(R.id.mobNumber);

        loginTxt = findViewById(R.id.loginTxt);
        register  = findViewById(R.id.register);

        countryCodePicker = findViewById(R.id.ccp);
        countryCodePicker.registerCarrierNumberEditText(mobNumber);

    }

    private void checkContactAllReadyExist(UserInfo userInfo) {

        Query query = FirebaseFirestore.getInstance().collection("UserInfo").whereEqualTo("contactNumber",userInfo.getContactNumber());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size()>0) {

                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    i.setAction("directLogin");
                    i.putExtra("all",userInfo);
                    startActivity(i);

                }else {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    i.setAction("registration");
                    i.putExtra("all",userInfo);
                    startActivity(i);
                }
            }
        });
    }

    public void registrationFunction() {

        String contact = mobNumber.getText().toString().trim().replace(" ", "");

        if (!userName.getText().toString().isEmpty() && (contact.length()==10) && !lastName.getText().toString().isEmpty() && ageFlag){

            UserInfo userInfo = new UserInfo();
            userInfo.setUserName(userName.getText().toString());
            userInfo.setContactNumber(countryCodePicker.getSelectedCountryCodeWithPlus()+contact);

            userInfo.setLastName(lastName.getText().toString());
            userInfo.setDob(dateOfBirth);
            userInfo.setEmail(email.getText().toString());

            spliteKeyword(userInfo);

        }else {
            validation();
        }
    }


    private void validation () {

        String contact = mobNumber.getText().toString().trim().replace(" ", "");

        if (userName.getText().toString().isEmpty()){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter your name.!", Snackbar.LENGTH_LONG);
            snackbar.show();
            userName.setError("Please enter your name.!");
        }else if (lastName.getText().toString().isEmpty()){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter your last name..!", Snackbar.LENGTH_LONG);
            snackbar.show();
            lastName.setError("Please enter your last name..!");
        }else if (contact.length() != 10){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter valid mobile number.!", Snackbar.LENGTH_LONG);
            snackbar.show();
            mobNumber.setError("Please enter valid mobile number.!");
        }else if (!ageFlag){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter valid date of birth..!", Snackbar.LENGTH_LONG);
            snackbar.show();
            dob.setError("Please enter valid date of birth..!");
        }
    }
}
