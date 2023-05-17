package com.appdroid.reply99;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appdroid.reply99.activity.Dashboard;
import com.appdroid.reply99.activity.EditProfileActivity;
import com.appdroid.reply99.loginActivitys.FlashActivity;
import com.appdroid.reply99.model.UserInfo;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    TextView edit,userName,userContact,mailID,dob;

    CircleImageView profilePic;
    UserInfo userInfo;
    NestedScrollView scroller;
    ShimmerFrameLayout shimmer_view_container;
    Button logout;

    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editor = getContext().getSharedPreferences("MassagesWithFlags", MODE_PRIVATE).edit();
        shimmer_view_container = view.findViewById(R.id.shimmer_view_container);
        scroller  = view.findViewById(R.id.scroller);

        userName = view.findViewById(R.id.userName);
        userContact = view.findViewById(R.id.userContact);
        mailID = view.findViewById(R.id.mailID);
        dob = view.findViewById(R.id.dob);
        profilePic = view.findViewById(R.id.userProfilePic);

        edit = view.findViewById(R.id.edit);
        logout = view.findViewById(R.id.logout);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),EditProfileActivity.class);
                intent.putExtra("all",userInfo);
                startActivity(intent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog dialog = new BottomSheetDialog(getContext());
                dialog.setContentView(R.layout.exit_dialog);
                TextView txt = dialog.findViewById(R.id.txt);

                txt.setText("Are you really want to Logout..?");

                TextView no = dialog.findViewById(R.id.no);
                TextView yes = dialog.findViewById(R.id.yes);

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        editor.putBoolean("serviceFlag",false);
                        editor.apply();
                        dialog.dismiss();
                        Intent intent = new Intent(getContext(), FlashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();

                    }
                });

                dialog.show();
            }
        });

        getUserInfo();

        return view;
    }

    private void loadProfilePic(String userName) {
        char first = userName.charAt(0);

        switch (first){
            case 'A' :
            case 'a' :
                Glide.with(getContext()).load(R.drawable.ic_a).into(profilePic);
                break;
            case 'B' :
            case 'b' :
                Glide.with(getContext()).load(R.drawable.ic_b).into(profilePic);
                break;
            case 'C' :
            case 'c' :
                Glide.with(getContext()).load(R.drawable.ic_c).into(profilePic);
                break;
            case 'D' :
            case 'd' :
                Glide.with(getContext()).load(R.drawable.ic_d).into(profilePic);
                break;
            case 'E' :
            case 'e' :
                Glide.with(getContext()).load(R.drawable.ic_e).into(profilePic);
                break;
            case 'F' :
                Glide.with(getContext()).load(R.drawable.ic_f).into(profilePic);
                break;
            case 'f' :
                Glide.with(getContext()).load(R.drawable.ic_f).into(profilePic);
                break;
            case 'G' :
                Glide.with(getContext()).load(R.drawable.ic_g).into(profilePic);
                break;
            case 'g' :
                Glide.with(getContext()).load(R.drawable.ic_g).into(profilePic);
                break;
            case 'H' :
                Glide.with(getContext()).load(R.drawable.ic_h).into(profilePic);
                break;
            case 'h' :
                Glide.with(getContext()).load(R.drawable.ic_h).into(profilePic);
                break;
            case 'I' :
                Glide.with(getContext()).load(R.drawable.ic_i).into(profilePic);
                break;
            case 'i' :
                Glide.with(getContext()).load(R.drawable.ic_i).into(profilePic);
                break;
            case 'J' :
                Glide.with(getContext()).load(R.drawable.ic_j).into(profilePic);
                break;
            case 'j' :
                Glide.with(getContext()).load(R.drawable.ic_j).into(profilePic);
                break;

            case 'K' :
                Glide.with(getContext()).load(R.drawable.ic_k).into(profilePic);
                break;
            case 'k' :
                Glide.with(getContext()).load(R.drawable.ic_k).into(profilePic);
                break;
            case 'L' :
                Glide.with(getContext()).load(R.drawable.ic_l).into(profilePic);
                break;
            case 'l' :
                Glide.with(getContext()).load(R.drawable.ic_l).into(profilePic);
                break;
            case 'M' :
                Glide.with(getContext()).load(R.drawable.ic_m).into(profilePic);
                break;
            case 'm' :
                Glide.with(getContext()).load(R.drawable.ic_m).into(profilePic);
                break;
            case 'N' :
                Glide.with(getContext()).load(R.drawable.ic_n).into(profilePic);
                break;
            case 'n' :
                Glide.with(getContext()).load(R.drawable.ic_n).into(profilePic);
                break;
            case 'O' :
                Glide.with(getContext()).load(R.drawable.ic_o).into(profilePic);
                break;
            case 'o' :
                Glide.with(getContext()).load(R.drawable.ic_o).into(profilePic);
                break;
            case 'P' :
                Glide.with(getContext()).load(R.drawable.ic_p).into(profilePic);
                break;
            case 'p' :
                Glide.with(getContext()).load(R.drawable.ic_p).into(profilePic);
                break;
            case 'Q' :
                Glide.with(getContext()).load(R.drawable.ic_q).into(profilePic);
                break;
            case 'q' :
                Glide.with(getContext()).load(R.drawable.ic_q).into(profilePic);
                break;
            case 'R' :
                Glide.with(getContext()).load(R.drawable.ic_r).into(profilePic);
                break;
            case 'r' :
                Glide.with(getContext()).load(R.drawable.ic_r).into(profilePic);
                break;
            case 'S' :
                Glide.with(getContext()).load(R.drawable.ic_s).into(profilePic);
                break;
            case 's' :
                Glide.with(getContext()).load(R.drawable.ic_s).into(profilePic);
                break;
            case 'T' :
                Glide.with(getContext()).load(R.drawable.ic_t).into(profilePic);
                break;
            case 't' :
                Glide.with(getContext()).load(R.drawable.ic_t).into(profilePic);
                break;
            case 'U' :
                Glide.with(getContext()).load(R.drawable.ic_u).into(profilePic);
                break;
            case 'u' :
                Glide.with(getContext()).load(R.drawable.ic_u).into(profilePic);
                break;
            case 'V' :
                Glide.with(getContext()).load(R.drawable.ic_v).into(profilePic);
                break;
            case 'v' :
                Glide.with(getContext()).load(R.drawable.ic_v).into(profilePic);
                break;
            case 'W' :
                Glide.with(getContext()).load(R.drawable.ic_w).into(profilePic);
                break;
            case 'w' :
                Glide.with(getContext()).load(R.drawable.ic_w).into(profilePic);
                break;
            case 'X' :
                Glide.with(getContext()).load(R.drawable.ic_x).into(profilePic);
                break;
            case 'x' :
                Glide.with(getContext()).load(R.drawable.ic_x).into(profilePic);
                break;
            case 'Y' :
                Glide.with(getContext()).load(R.drawable.ic_y).into(profilePic);
                break;
            case 'y' :
                Glide.with(getContext()).load(R.drawable.ic_y).into(profilePic);
                break;
            case 'Z' :
                Glide.with(getContext()).load(R.drawable.ic_z).into(profilePic);
                break;
            case 'z' :
                Glide.with(getContext()).load(R.drawable.ic_z).into(profilePic);
                break;
            default:

                break;
        }

    }



    private void getUserInfo() {


        Query query  = FirebaseFirestore.getInstance().collection("UserInfo").whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                    userInfo   = snapshot.toObject(UserInfo.class);
                    userInfo.setDocId(snapshot.getId());
                    userName.setText(userInfo.getUserName()+" "+userInfo.getLastName());
                    userContact.setText(userInfo.getContactNumber());
                    mailID.setText(userInfo.getEmail());
                    dob.setText(userInfo.getDob());

                    if (userInfo.getProfilePic() != null){
                        if (userInfo.getProfilePic().isEmpty()){

                            try {

                                loadProfilePic(userInfo.getUserName());
                            }catch (Exception e){

                            }

                        }else {
                            Glide.with(Dashboard.context).load(userInfo.getProfilePic()).placeholder(R.drawable.read_contact).into(profilePic);
                        }
                    }else {

                        try {

                            loadProfilePic(userInfo.getUserName());
                        }catch (Exception e){

                        }


                    }
                    shimmer_view_container.setVisibility(View.GONE);
                    scroller.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}
