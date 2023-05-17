package com.appdroid.reply99.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

public class AddWhatsAppMessage extends AppCompatActivity {

    private String  formats;
    private Spinner formatSpinner;
    private ArrayAdapter<CharSequence>  formatAdapter;
    TextView chooseFile,fileName;


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
    TextView progressPer;
    String selectedFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);



        sharedPreferences = getSharedPreferences("MassagesWithFlags", Context.MODE_PRIVATE);
        incomingMassage = sharedPreferences.getString("incomingMassageForWhatsapp","");
        outgoingMassage = sharedPreferences.getString("outgoingMassageForWhatsapp","");
        missedCallMassage = sharedPreferences.getString("missedCallMassageForWhatsapp","");
        sendToAllMassage = sharedPreferences.getString("sendToAllMassageForWhatsapp","");

        incomingImage = sharedPreferences.getString("incomingImageForWhatsapp","");
        outgoingImage = sharedPreferences.getString("outgoingImageForWhatsapp","");
        missedCallImage = sharedPreferences.getString("missedCallImageForWhatsapp","");
        sendToAllImage = sharedPreferences.getString("sendToAllImageForWhatsapp","");

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
        fileName = findViewById(R.id.fileName);

        whatsappImageLayout.setVisibility(View.GONE);
        

        

        chooseFile = findViewById(R.id.chooseFile);
        formatSpinner = findViewById(R.id.formatSpinner);
        formatAdapter = ArrayAdapter.createFromResource(this,R.array.array_format,R.layout.spinner_layout);

        formatAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        formatSpinner.setAdapter(formatAdapter);

        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedFormat.equals("Image")){
                    launcher.launch("image/*");
                }else if (selectedFormat.equals("Video")){
                    launcher.launch("video/*");
                }
            }
        });



        formatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFormat = formatSpinner.getSelectedItem().toString();
                if (!selectedFormat.equals("Select Format")){
                    whatsappImageLayout.setVisibility(View.VISIBLE);
                }else {
                    whatsappImageLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




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

            whatsappImageLayout.setVisibility(View.GONE);
            formatSpinner.setVisibility(View.GONE);

            messageED.setText(massageHolder.getMassage());

           /* Glide.with(AddWhatsAppMessage.this).load(massageHolder.getWhatsappImageLink()).into(imageSelected);
            imageSelected.setVisibility(View.VISIBLE);*/

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
                        //   addMassageToDb("","");
                        if (filePathUri != null){
                            saveImageToFirebase();
                        }else {
                            Toast.makeText(AddWhatsAppMessage.this, "Please Select Image OR Video to send on Whatsapp along with massage..!", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        //  updateMassage(massageHolder);
                        if (filePathUri != null){
                            saveImageToFirebase();
                        }else {
                            updateMassage(massageHolder);
                        }

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


        progressDialog = new Dialog(AddWhatsAppMessage.this);

        progressDialog.setContentView(R.layout.progress_dialog);
        progressPer = progressDialog.findViewById(R.id.progressPer);
        progressDialog.setCanceledOnTouchOutside(false);
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null){

                    if (selectedFormat.equals("Image")){
                        startCrop(result);
                    }else if (selectedFormat.equals("Video")){

                        filePathUri = result;
                        fileName.setText(getFileName(filePathUri));

                    }

                }

            }
        });

    }

    private void saveImageToFirebase() {
        progressDialog.show();
        progressPer.setVisibility(View.VISIBLE);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference path = storageReference.child("ImagesFroWhatsApp").child(filePathUri.getLastPathSegment());
        path.putFile(filePathUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                int a = (int) progress;
                Log.d("onProgress", "onProgress: "+a);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressPer.setText(a+"%");
                    }
                });

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
            //Glide.with(AddMessage.this).load(filePathUri).into(imageSelected);
            fileName.setText(getFileName(filePathUri));

          //  imageSelected.setVisibility(View.VISIBLE);
        }

        uCrop.start(AddWhatsAppMessage.this);
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }



    private void updateMassage(MassageHolder massageHolder) {


        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {

                UtilityRoomDatabase.getInstance(AddWhatsAppMessage.this).massagesDao().updateMassage(messageED.getText().toString(), massageHolder.getMassageId(),massageHolder.getWhatsappImageLink());

                AddWhatsAppMessage.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        SharedPreferences.Editor editor = getSharedPreferences("MassagesWithFlags",MODE_PRIVATE).edit();

                        if (sendToIn){
                            editor.putString("incomingMassageForWhatsapp",messageED.getText().toString());
                            editor.putString("incomingImageForWhatsapp",massageHolder.getWhatsappImageLink());

                            if (massageHolder.getContainMedia().equals("Video")){
                                editor.putString("incomingWhatsappImageOrVideo","video");
                            }else {
                                editor.putString("incomingWhatsappImageOrVideo","image");
                            }


                        }else if (sendToOut){
                            editor.putString("outgoingMassageForWhatsapp",messageED.getText().toString());
                            editor.putString("outgoingImageForWhatsapp",massageHolder.getWhatsappImageLink());


                            if (massageHolder.getContainMedia().equals("Video")){
                                editor.putString("outgoingWhatsappImageOrVideo","video");
                            }else {
                                editor.putString("outgoingWhatsappImageOrVideo","image");
                            }



                        }else if (sendToMiss){
                            editor.putString("missedCallMassageForWhatsapp",messageED.getText().toString());
                            editor.putString("missedCallImageForWhatsapp",massageHolder.getWhatsappImageLink());

                            if (massageHolder.getContainMedia().equals("Video")){
                                editor.putString("missedCallWhatsappImageOrVideo","video");
                            }else {
                                editor.putString("missedCallWhatsappImageOrVideo","image");
                            }


                        }else if (sendToAllOneMassage){
                            editor.putString("sendToAllMassageForWhatsapp",messageED.getText().toString());
                            editor.putString("sendToAllImageForWhatsapp",massageHolder.getWhatsappImageLink());

                            if (massageHolder.getContainMedia().equals("Video")){
                                editor.putString("sendToAllWhatsappImageOrVideo","video");
                            }else {
                                editor.putString("sendToAllWhatsappImageOrVideo","image");
                            }

                        }
                        editor.apply();

                        onBackPressed();

                        Toast.makeText(AddWhatsAppMessage.this, "Massage Updated...!", Toast.LENGTH_SHORT).show();
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
/*
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()

                                .setImageUrl(Uri.parse(imgUri))


                                .build())*/




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

        massageHolder.setWhatsAppFlag(true);


        if (selectedFormat.equals("Image")){
            massageHolder.setContainMedia("Image");
        }else {
            massageHolder.setContainMedia("Video");
        }



        AddWhatsAppMessage.InsertAsnkTask insertAsnkTask = new AddWhatsAppMessage.InsertAsnkTask();
        insertAsnkTask.execute(massageHolder);

        onBackPressed();

    }

    public class InsertAsnkTask extends AsyncTask<MassageHolder,Void,Void> {
        @Override
        protected Void doInBackground(MassageHolder... massage) {
            UtilityRoomDatabase.getInstance(AddWhatsAppMessage.this)
                    .massagesDao()
                    .insertMassage(massage[0]);

            AddWhatsAppMessage.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    SharedPreferences.Editor editor = getSharedPreferences("MassagesWithFlags",MODE_PRIVATE).edit();


                    if (sendToIn){
                        editor.putString("incomingMassageForWhatsapp",messageED.getText().toString());
                        editor.putString("incomingImageForWhatsapp",massage[0].getWhatsappImageLink());

                        if (selectedFormat.equals("Video")){
                            editor.putString("incomingWhatsappImageOrVideo","video");
                        }else {
                            editor.putString("incomingWhatsappImageOrVideo","image");
                        }

                    }else if (sendToOut){
                        editor.putString("outgoingMassageForWhatsapp",messageED.getText().toString());
                        editor.putString("outgoingImageForWhatsapp",massage[0].getWhatsappImageLink());

                        if (selectedFormat.equals("Video")){
                            editor.putString("outgoingWhatsappImageOrVideo","video");
                        }else {
                            editor.putString("outgoingWhatsappImageOrVideo","image");
                        }


                    }else if (sendToMiss){
                        editor.putString("missedCallMassageForWhatsapp",messageED.getText().toString());
                        editor.putString("missedCallImageForWhatsapp",massage[0].getWhatsappImageLink());

                        if (selectedFormat.equals("Video")){
                            editor.putString("missedCallWhatsappImageOrVideo","video");
                        }else {
                            editor.putString("missedCallWhatsappImageOrVideo","image");
                        }



                    }else if (sendToAllOneMassage){
                        editor.putString("sendToAllMassageForWhatsapp",messageED.getText().toString());
                        editor.putString("sendToAllImageForWhatsapp",massage[0].getWhatsappImageLink());

                        if (selectedFormat.equals("Video")){
                            editor.putString("sendToAllWhatsappImageOrVideo","video");
                        }else {
                            editor.putString("sendToAllWhatsappImageOrVideo","image");
                        }

                    }


              /*      if (sendToIn){
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
                    }*/
                    editor.apply();

                    Toast.makeText(AddWhatsAppMessage.this, "Massage Added", Toast.LENGTH_SHORT).show();
                }
            });

            return null;
        }
    }


}