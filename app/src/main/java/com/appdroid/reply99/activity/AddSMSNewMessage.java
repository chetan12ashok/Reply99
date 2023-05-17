package com.appdroid.reply99.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appdroid.reply99.R;
import com.appdroid.reply99.room.MassageHolder;
import com.appdroid.reply99.room.UtilityRoomDatabase;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class AddSMSNewMessage extends AppCompatActivity {
    TextView ok;
    EditText messageED;

    RadioGroup radioGroup;
    RadioButton sendToOutgoing,sendToIncoming,sendToMissed,sendToAll;

    boolean sendToIn = false;
    boolean sendToOut = false;
    boolean sendToMiss = false;
    boolean sendToAllOneMassage = false;
    SharedPreferences sharedPreferences;
    String massage;

    String incomingMassage,outgoingMassage,missedCallMassage,sendToAllMassage;
    String incomingImage,outgoingImage,missedCallImage,sendToAllImage;

    MassageHolder massageHolder;
    TextView characterNumber;

    RelativeLayout whatsappImageLayout;

    ImageView selectImage, imageSelected;

    ActivityResultLauncher<String> launcher;
    private Uri filePathUri;
    Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_message);

        sharedPreferences = getSharedPreferences("MassagesWithFlags", Context.MODE_PRIVATE);
        incomingMassage = sharedPreferences.getString("incomingMassage","");
        outgoingMassage = sharedPreferences.getString("outgoingMassage","");
        missedCallMassage = sharedPreferences.getString("missedCallMassage","");
        sendToAllMassage = sharedPreferences.getString("sendToAllMassage","");

        incomingImage = sharedPreferences.getString("incomingImage","");
        outgoingImage = sharedPreferences.getString("outgoingImage","");
        missedCallImage = sharedPreferences.getString("missedCallImage","");
        sendToAllImage = sharedPreferences.getString("sendToAllImage","");

        massageHolder = (MassageHolder) getIntent().getSerializableExtra("massage");

        radioGroup = findViewById(R.id.radioGroup);
        ok = findViewById(R.id.ok);
        messageED = findViewById(R.id.message);
        sendToIncoming = findViewById(R.id.sendToIncoming);
        sendToMissed = findViewById(R.id.sendToMissed);
        sendToOutgoing = findViewById(R.id.sendToOutgoing);
        sendToAll = findViewById(R.id.sendToAll);
        characterNumber = findViewById(R.id.characterNumber);

        whatsappImageLayout = findViewById(R.id.whatsappImageLayout);
        selectImage = findViewById(R.id.selectImage);
        imageSelected = findViewById(R.id.imageSelected);



        whatsappImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        messageED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                characterNumber.setText("Massage length : "+charSequence.length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (massageHolder != null){

            messageED.setText(massageHolder.getMassage());

            Glide.with(AddSMSNewMessage.this).load(massageHolder.getWhatsappImageLink()).into(imageSelected);
            imageSelected.setVisibility(View.VISIBLE);

            if (incomingMassage.equals(massageHolder.getMassage())){
                sendToIncoming.setChecked(true);
                sendToIn = true;
                sendToOut = false;
                sendToMiss = false;
                sendToAllOneMassage = false;
                sendToIncoming.setEnabled(false);
                sendToOutgoing.setEnabled(false);
                sendToMissed.setEnabled(false);
                sendToAll.setEnabled(false);
            }else if (outgoingMassage.equals(massageHolder.getMassage())){
                sendToOutgoing.setChecked(true);
                sendToIn = false;
                sendToOut = true;
                sendToMiss = false;
                sendToAllOneMassage = false;
                sendToIncoming.setEnabled(false);
                sendToOutgoing.setEnabled(false);
                sendToMissed.setEnabled(false);
                sendToAll.setEnabled(false);
            }else if (missedCallMassage.equals(massageHolder.getMassage())){
                sendToMissed.setChecked(true);
                sendToIn = false;
                sendToOut = false;
                sendToMiss = true;
                sendToAllOneMassage = false;

                sendToIncoming.setEnabled(false);
                sendToOutgoing.setEnabled(false);
                sendToMissed.setEnabled(false);
                sendToAll.setEnabled(false);
            }else if (sendToAllMassage.equals(massageHolder.getMassage())){

                sendToAll.setChecked(true);
                sendToIn = false;
                sendToOut = false;
                sendToMiss = false;
                sendToAllOneMassage = true;

                sendToIncoming.setEnabled(false);
                sendToOutgoing.setEnabled(false);
                sendToMissed.setEnabled(false);
                sendToAll.setEnabled(false);
            }
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!messageED.getText().toString().isEmpty()){
                    if (massageHolder == null){
                      addMassageToDb("","");
                    /*    if (filePathUri != null){
                          saveImageToFirebase();
                        }else {
                            Toast.makeText(AddNewMessage.this, "Please Select Image to send on Whatsapp along with massage..!", Toast.LENGTH_SHORT).show();
                        }*/

                    }else {
                        updateMassage(massageHolder);
                    /*    if (filePathUri != null){
                            saveImageToFirebase();
                        }else {
                            updateMassage(massageHolder);
                        }*/

                    }

                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){

                    case R.id.sendToIncoming:
                        sendToIn = true;
                        sendToOut = false;
                        sendToMiss = false;
                        sendToAllOneMassage = false;
                        break;

                    case R.id.sendToOutgoing:
                        sendToIn = false;
                        sendToOut = true;
                        sendToMiss = false;
                        sendToAllOneMassage = false;
                        break;

                    case R.id.sendToMissed:
                        sendToIn = false;
                        sendToOut = false;
                        sendToMiss = true;
                        sendToAllOneMassage = false;
                        break;
                    case R.id.sendToAll:
                        sendToIn = false;
                        sendToOut = false;
                        sendToMiss = false;
                        sendToAllOneMassage = true;
                        break;

                }
            }
        });


        progressDialog = new Dialog(AddSMSNewMessage.this);

        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null){
                    startCrop(result);
                }

            }
        });
    }

    private void saveImageToFirebase() {
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference path = storageReference.child("ImagesFroWhatsApp").child(filePathUri.getLastPathSegment());
        path.putFile(filePathUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                int a = (int) progress;
                Log.d("onProgress", "onProgress: "+a);
                //progressDialog.setMessage("Registering User : " + a + "%");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String imgUri = uri.toString();

                   //     updateInfo(imgUri);


                        ganerateShortLink(imgUri);







                        //finallySaveData(imgUri);
                    }
                });
              //  progressDialog.dismiss();
            }
        });
    }

    private void startCrop(Uri imageUri) {
        String desctinationFileNAme = imageUri.getLastPathSegment()+".jpg";
        UCrop uCrop = UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), desctinationFileNAme)));
  /*      uCrop.withAspectRatio(1,1);
        uCrop.withMaxResultSize(450,450);*/

        filePathUri = UCrop.getOutput(uCrop.getIntent(this));
        if (filePathUri != null){
            Glide.with(AddSMSNewMessage.this).load(filePathUri).into(imageSelected);
            imageSelected.setVisibility(View.VISIBLE);
        }

        uCrop.start(AddSMSNewMessage.this);
    }

    private void updateMassage(MassageHolder massageHolder) {


        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
               UtilityRoomDatabase.getInstance(AddSMSNewMessage.this).massagesDao().updateMassage(messageED.getText().toString(), massageHolder.getMassageId(),massageHolder.getWhatsappImageLink());

                AddSMSNewMessage.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        SharedPreferences.Editor editor = getSharedPreferences("MassagesWithFlags",MODE_PRIVATE).edit();

                        if (sendToIn){
                            editor.putString("incomingMassage",messageED.getText().toString());
                            editor.putString("incomingImage",massageHolder.getWhatsappImageLink());
                        }else if (sendToOut){
                            editor.putString("outgoingMassage",messageED.getText().toString());
                            editor.putString("outgoingImage",massageHolder.getWhatsappImageLink());
                        }else if (sendToMiss){
                            editor.putString("missedCallMassage",messageED.getText().toString());
                            editor.putString("missedCallImage",massageHolder.getWhatsappImageLink());
                        }else if (sendToAllOneMassage){
                            editor.putString("sendToAllMassage",messageED.getText().toString());
                            editor.putString("sendToAllImage",massageHolder.getWhatsappImageLink());
                        }
                        editor.apply();

                        onBackPressed();

                        Toast.makeText(AddSMSNewMessage.this, "Massage Updated...!", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        thread.start();


    }

    public void ganerateShortLink(String imgUri) {

      //  String permLink = "";

        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(imgUri))
                .setDomainUriPrefix("https://reply99.page.link")



                .setNavigationInfoParameters(new DynamicLink.NavigationInfoParameters.Builder()
                        .setForcedRedirectEnabled(true)
                        .build())

                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()

                                .setImageUrl(Uri.parse(imgUri))


                                .build())




                .buildShortDynamicLink().addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        Uri shortLink = shortDynamicLink.getShortLink();
                        Log.d("buildShortDynamicLink", "onSuccess: "+shortLink);

                        if (massageHolder != null){
                            massageHolder.setWhatsappImageLink(imgUri);
                            massageHolder.setSmsImageLink(String.valueOf(shortLink));

                            updateMassage(massageHolder);
                        }else {
                            addMassageToDb(imgUri,String.valueOf(shortLink));
                        }

                        progressDialog.dismiss();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void addMassageToDb(String whatsappMedia, String smsMedia) {

        MassageHolder massageHolder = new MassageHolder();
        massageHolder.setMassage(messageED.getText().toString());
        massageHolder.setWhatsappImageLink(whatsappMedia);
        massageHolder.setSmsImageLink(smsMedia);
        massageHolder.setWhatsAppFlag(false);
        AddSMSNewMessage.InsertAsnkTask insertAsnkTask = new InsertAsnkTask();
        insertAsnkTask.execute(massageHolder);

        onBackPressed();

    }

    public class InsertAsnkTask extends AsyncTask<MassageHolder,Void,Void> {
        @Override
        protected Void doInBackground(MassageHolder... massage) {
            UtilityRoomDatabase.getInstance(AddSMSNewMessage.this)
                    .massagesDao()
                    .insertMassage(massage[0]);

            AddSMSNewMessage.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    SharedPreferences.Editor editor = getSharedPreferences("MassagesWithFlags",MODE_PRIVATE).edit();

                    if (sendToIn){
                        editor.putString("incomingMassage",messageED.getText().toString());
                        editor.putString("incomingImage",massage[0].getWhatsappImageLink());
                        editor.putString("incomingImageForSMS",massage[0].getSmsImageLink());
                    }else if (sendToOut){
                        editor.putString("outgoingMassage",messageED.getText().toString());
                        editor.putString("outgoingImage",massage[0].getWhatsappImageLink());
                        editor.putString("outgoingImageForSMS",massage[0].getSmsImageLink());
                    }else if (sendToMiss){
                        editor.putString("missedCallMassage",messageED.getText().toString());
                        editor.putString("missedCallImage",massage[0].getWhatsappImageLink());
                        editor.putString("missedCallImageForSMS",massage[0].getSmsImageLink());
                    }else if (sendToAllOneMassage){
                        editor.putString("sendToAllMassage",messageED.getText().toString());
                        editor.putString("sendToAllImage",massage[0].getWhatsappImageLink());
                        editor.putString("sendToAllForSMS",massage[0].getSmsImageLink());
                    }
                    editor.apply();

                    Toast.makeText(AddSMSNewMessage.this, "Massage Added", Toast.LENGTH_SHORT).show();
                }
            });

            return null;
        }
    }
}