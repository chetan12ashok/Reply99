package com.appdroid.reply99.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appdroid.reply99.R;
import com.appdroid.reply99.loginActivitys.LoginActivity;
import com.appdroid.reply99.model.UserInfo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    UserInfo userInfo;
    EditText edName,edLastName,edContact,edEmailAddress,edDob;
    CircleImageView profilePic;
    ImageView editPhoto;
    TextView done;

    ActivityResultLauncher<String> launcher;
    private Uri filePathUri;

    Dialog  progressDialog;
    CardView chagneContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        userInfo = (UserInfo) getIntent().getSerializableExtra("all");
        edName = findViewById(R.id.edName);
        edLastName  = findViewById(R.id.edLastName);
        edContact = findViewById(R.id.edContact);
        edEmailAddress = findViewById(R.id.edEmailAddress);
        edDob = findViewById(R.id.edDob);
        profilePic = findViewById(R.id.proPic);
        editPhoto = findViewById(R.id.editPhoto);

        chagneContact = findViewById(R.id.changeC);

        done = findViewById(R.id.done);

        edName.setText(userInfo.getUserName());
        edLastName.setText(userInfo.getLastName());
        edContact.setText(userInfo.getContactNumber());
        edEmailAddress.setText(userInfo.getEmail());
        edDob.setText(userInfo.getDob());

        if (userInfo.getProfilePic() != null){
            if (userInfo.getProfilePic().isEmpty()){
                loadProfilePic(userInfo.getUserName());
            }else {
                Glide.with(EditProfileActivity.this).load(userInfo.getProfilePic()).into(profilePic);
            }
        }else {
            loadProfilePic(userInfo.getUserName());
        }



        progressDialog = new Dialog(EditProfileActivity.this);

        progressDialog.setContentView(R.layout.progress_dialog);

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                startCrop(result);
            }
        });




        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valieded();
            }
        });
        chagneContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                intent.setAction("updateNumber");
                intent.putExtra("all",userInfo);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


                startActivity(intent);


                try {
                    ActivityCompat.finishAffinity(EditProfileActivity.this);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void valieded() {
        if (!edName.getText().toString().isEmpty() && !edLastName.getText().toString().isEmpty() && !edEmailAddress.getText().toString().isEmpty()) {

            if (!edEmailAddress.getText().toString().contains("@gmail.com")){
                edEmailAddress.setError("Please enter valid email address..!");
            }else {

                if (filePathUri != null){

                    progressDialog.show();
                    updateProfileToDB(filePathUri);

                }else {
                    updateInfo("");
                }

            }
        }else if (edName.getText().toString().isEmpty()){
            edName.setError("Please enter your name..!");
        }else if (edLastName.getText().toString().isEmpty()){
            edLastName.setError("Please enter your Last Name..!");
        }else if (edEmailAddress.getText().toString().isEmpty()){
            edEmailAddress.setError("Please enter your email..!");
        }
    }

    private void updateInfo(String imgUri) {
        DocumentReference firebaseFirestore = FirebaseFirestore.getInstance().collection("UserInfo").document(userInfo.getDocId());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userName",edName.getText().toString());
        hashMap.put("lastName",edLastName.getText().toString());
        hashMap.put("email",edEmailAddress.getText().toString());

        if (!imgUri.equals("")){
            hashMap.put("profilePic",imgUri);
        }

        firebaseFirestore.update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "Updated..!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });

    }

    private void loadProfilePic(String userName) {
        char first = userName.charAt(0);

        switch (first){
            case 'A' :
            case 'a' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_a).into(profilePic);
                break;
            case 'B' :
            case 'b' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_b).into(profilePic);
                break;
            case 'C' :
            case 'c' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_c).into(profilePic);
                break;
            case 'D' :
            case 'd' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_d).into(profilePic);
                break;
            case 'E' :
            case 'e' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_e).into(profilePic);
                break;
            case 'F' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_f).into(profilePic);
                break;
            case 'f' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_f).into(profilePic);
                break;
            case 'G' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_g).into(profilePic);
                break;
            case 'g' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_g).into(profilePic);
                break;
            case 'H' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_h).into(profilePic);
                break;
            case 'h' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_h).into(profilePic);
                break;
            case 'I' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_i).into(profilePic);
                break;
            case 'i' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_i).into(profilePic);
                break;
            case 'J' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_j).into(profilePic);
                break;
            case 'j' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_j).into(profilePic);
                break;

            case 'K' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_k).into(profilePic);
                break;
            case 'k' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_k).into(profilePic);
                break;
            case 'L' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_l).into(profilePic);
                break;
            case 'l' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_l).into(profilePic);
                break;
            case 'M' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_m).into(profilePic);
                break;
            case 'm' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_m).into(profilePic);
                break;
            case 'N' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_n).into(profilePic);
                break;
            case 'n' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_n).into(profilePic);
                break;
            case 'O' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_o).into(profilePic);
                break;
            case 'o' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_o).into(profilePic);
                break;
            case 'P' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_p).into(profilePic);
                break;
            case 'p' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_p).into(profilePic);
                break;
            case 'Q' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_q).into(profilePic);
                break;
            case 'q' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_q).into(profilePic);
                break;
            case 'R' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_r).into(profilePic);
                break;
            case 'r' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_r).into(profilePic);
                break;
            case 'S' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_s).into(profilePic);
                break;
            case 's' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_s).into(profilePic);
                break;
            case 'T' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_t).into(profilePic);
                break;
            case 't' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_t).into(profilePic);
                break;
            case 'U' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_u).into(profilePic);
                break;
            case 'u' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_u).into(profilePic);
                break;
            case 'V' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_v).into(profilePic);
                break;
            case 'v' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_v).into(profilePic);
                break;
            case 'W' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_w).into(profilePic);
                break;
            case 'w' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_w).into(profilePic);
                break;
            case 'X' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_x).into(profilePic);
                break;
            case 'x' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_x).into(profilePic);
                break;
            case 'Y' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_y).into(profilePic);
                break;
            case 'y' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_y).into(profilePic);
                break;
            case 'Z' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_z).into(profilePic);
                break;
            case 'z' :
                Glide.with(EditProfileActivity.this).load(R.drawable.ic_z).into(profilePic);
                break;
            default:

                break;
        }

    }

    private void startCrop(Uri imageUri) {
        String desctinationFileNAme = imageUri.getLastPathSegment()+".jpg";
        UCrop uCrop = UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), desctinationFileNAme)));
        uCrop.withAspectRatio(1,1);
        uCrop.withMaxResultSize(450,450);

        filePathUri = UCrop.getOutput(uCrop.getIntent(this));
        if (filePathUri != null){
            Glide.with(EditProfileActivity.this).load(filePathUri).into(profilePic);

        }

        uCrop.start(EditProfileActivity.this);
    }

    private void updateProfileToDB(Uri filePathUri) {

        Log.d("SSSSSA", "updateProfileToDB: "+filePathUri);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference path = storageReference.child("ProfileImagesOfFamilyMembers").child(filePathUri.getLastPathSegment());
        path.putFile(filePathUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                int a = (int) progress;
                Log.d("onProgress", "onProgress: "+a);
        //        progressDialog.setMessage("Registering User : " + a + "%");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String imgUri = uri.toString();

                        updateInfo(imgUri);

                        //finallySaveData(imgUri);
                    }
                });
                progressDialog.dismiss();
            }
        });

    }

    private void finallySaveData(String imgUri) {
        DocumentReference firebaseFirestore = FirebaseFirestore.getInstance().collection("UserInfo").document(userInfo.getDocId());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("profilePic",imgUri);

        firebaseFirestore.update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "Profile Updated..!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });

    }

}
