package com.appdroid.reply99.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appdroid.reply99.R;
import com.appdroid.reply99.room.ContactHolder;
import com.appdroid.reply99.room.UtilityRoomDatabase;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedContactsAdepter extends RecyclerView.Adapter<SelectedContactsAdepter.ViewHolder> {
    Context context;
    List<ContactHolder> list;


    public SelectedContactsAdepter(Context context, List<ContactHolder> listss) {
        this.context = context;
        this.list = listss;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.contact_item,parent,false);
        return  new SelectedContactsAdepter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactHolder contactHolder  =list.get(position);
        holder.mobileNumber.setText(contactHolder.getPhoneNumber());
        holder.contactName.setText(contactHolder.getName());
        holder.loadProfilePic(contactHolder.getName());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.deleteSelectedProduct(contactHolder);
            }
        });
    }



    @Override
    public int getItemCount() {
       return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView contactName,mobileNumber;
        RelativeLayout layout;
        ImageView delete;
        CircleImageView profilePic;
        public ViewHolder(@NonNull View view) {
            super(view);
            contactName = view.findViewById(R.id.contact);
            mobileNumber = view.findViewById(R.id.UserName);
            layout = view.findViewById(R.id.layout);
            delete = view.findViewById(R.id.delete);
            delete.setVisibility(View.VISIBLE);
            profilePic =view.findViewById(R.id.profileIcon);
        }

        public void deleteSelectedProduct(ContactHolder contactHolder) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UtilityRoomDatabase.getInstance(context).massagesDao().deleteSelectedContact(contactHolder.getContactId());
                    ((Activity)context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            removeAt(getAdapterPosition());
                        }
                    });

                }
            }).start();
        }


        private void loadProfilePic(String userName) {
            char first = userName.charAt(0);

            switch (first){
                case 'A' :
                case 'a' :
                    Glide.with(context).load(R.drawable.ic_a).into(profilePic);
                    break;
                case 'B' :
                case 'b' :
                    Glide.with(context).load(R.drawable.ic_b).into(profilePic);
                    break;
                case 'C' :
                case 'c' :
                    Glide.with(context).load(R.drawable.ic_c).into(profilePic);
                    break;
                case 'D' :
                case 'd' :
                    Glide.with(context).load(R.drawable.ic_d).into(profilePic);
                    break;
                case 'E' :
                case 'e' :
                    Glide.with(context).load(R.drawable.ic_e).into(profilePic);
                    break;
                case 'F' :
                    Glide.with(context).load(R.drawable.ic_f).into(profilePic);
                    break;
                case 'f' :
                    Glide.with(context).load(R.drawable.ic_f).into(profilePic);
                    break;
                case 'G' :
                    Glide.with(context).load(R.drawable.ic_g).into(profilePic);
                    break;
                case 'g' :
                    Glide.with(context).load(R.drawable.ic_g).into(profilePic);
                    break;
                case 'H' :
                    Glide.with(context).load(R.drawable.ic_h).into(profilePic);
                    break;
                case 'h' :
                    Glide.with(context).load(R.drawable.ic_h).into(profilePic);
                    break;
                case 'I' :
                    Glide.with(context).load(R.drawable.ic_i).into(profilePic);
                    break;
                case 'i' :
                    Glide.with(context).load(R.drawable.ic_i).into(profilePic);
                    break;
                case 'J' :
                    Glide.with(context).load(R.drawable.ic_j).into(profilePic);
                    break;
                case 'j' :
                    Glide.with(context).load(R.drawable.ic_j).into(profilePic);
                    break;

                case 'K' :
                    Glide.with(context).load(R.drawable.ic_k).into(profilePic);
                    break;
                case 'k' :
                    Glide.with(context).load(R.drawable.ic_k).into(profilePic);
                    break;
                case 'L' :
                    Glide.with(context).load(R.drawable.ic_l).into(profilePic);
                    break;
                case 'l' :
                    Glide.with(context).load(R.drawable.ic_l).into(profilePic);
                    break;
                case 'M' :
                    Glide.with(context).load(R.drawable.ic_m).into(profilePic);
                    break;
                case 'm' :
                    Glide.with(context).load(R.drawable.ic_m).into(profilePic);
                    break;
                case 'N' :
                    Glide.with(context).load(R.drawable.ic_n).into(profilePic);
                    break;
                case 'n' :
                    Glide.with(context).load(R.drawable.ic_n).into(profilePic);
                    break;
                case 'O' :
                    Glide.with(context).load(R.drawable.ic_o).into(profilePic);
                    break;
                case 'o' :
                    Glide.with(context).load(R.drawable.ic_o).into(profilePic);
                    break;
                case 'P' :
                    Glide.with(context).load(R.drawable.ic_p).into(profilePic);
                    break;
                case 'p' :
                    Glide.with(context).load(R.drawable.ic_p).into(profilePic);
                    break;
                case 'Q' :
                    Glide.with(context).load(R.drawable.ic_q).into(profilePic);
                    break;
                case 'q' :
                    Glide.with(context).load(R.drawable.ic_q).into(profilePic);
                    break;
                case 'R' :
                    Glide.with(context).load(R.drawable.ic_r).into(profilePic);
                    break;
                case 'r' :
                    Glide.with(context).load(R.drawable.ic_r).into(profilePic);
                    break;
                case 'S' :
                    Glide.with(context).load(R.drawable.ic_s).into(profilePic);
                    break;
                case 's' :
                    Glide.with(context).load(R.drawable.ic_s).into(profilePic);
                    break;
                case 'T' :
                    Glide.with(context).load(R.drawable.ic_t).into(profilePic);
                    break;
                case 't' :
                    Glide.with(context).load(R.drawable.ic_t).into(profilePic);
                    break;
                case 'U' :
                    Glide.with(context).load(R.drawable.ic_u).into(profilePic);
                    break;
                case 'u' :
                    Glide.with(context).load(R.drawable.ic_u).into(profilePic);
                    break;
                case 'V' :
                    Glide.with(context).load(R.drawable.ic_v).into(profilePic);
                    break;
                case 'v' :
                    Glide.with(context).load(R.drawable.ic_v).into(profilePic);
                    break;
                case 'W' :
                    Glide.with(context).load(R.drawable.ic_w).into(profilePic);
                    break;
                case 'w' :
                    Glide.with(context).load(R.drawable.ic_w).into(profilePic);
                    break;
                case 'X' :
                    Glide.with(context).load(R.drawable.ic_x).into(profilePic);
                    break;
                case 'x' :
                    Glide.with(context).load(R.drawable.ic_x).into(profilePic);
                    break;
                case 'Y' :
                    Glide.with(context).load(R.drawable.ic_y).into(profilePic);
                    break;
                case 'y' :
                    Glide.with(context).load(R.drawable.ic_y).into(profilePic);
                    break;
                case 'Z' :
                    Glide.with(context).load(R.drawable.ic_z).into(profilePic);
                    break;
                case 'z' :
                    Glide.with(context).load(R.drawable.ic_z).into(profilePic);
                    break;
                default:

                    break;
            }

        }

    }

    private void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }
}

