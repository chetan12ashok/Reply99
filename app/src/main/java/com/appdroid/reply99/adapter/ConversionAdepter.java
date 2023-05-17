package com.appdroid.reply99.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appdroid.reply99.R;
import com.appdroid.reply99.room.ConversionHolder;
import com.appdroid.reply99.room.UtilityRoomDatabase;
import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversionAdepter extends RecyclerView.Adapter<ConversionAdepter.ViewHolder> {

    List<ConversionHolder> list;
    Context context;
    long todayTime;
    public ConversionAdepter(List<ConversionHolder> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.convertion_item,parent,false);
        todayTime = System.currentTimeMillis();
        return new ConversionAdepter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ConversionHolder conversionHolder = list.get(position);
        holder.contact.setText(conversionHolder.getCallerName());
        holder.message.setText(conversionHolder.getMassage());
        holder.loadProfilePic(conversionHolder.getCallerName());


        Glide.with(context).load(conversionHolder.getImageForWhatsApp()).into(holder.imageSelected);

     //   holder.imageSelected.setVisibility(View.GONE);

        if (conversionHolder.getCallingFlag().equals("Incoming")){

           holder.status.setText("Incoming Call");
           holder.status.setTextColor(ContextCompat.getColor(context, R.color.primary));

        }else if (conversionHolder.getCallingFlag().equals("Outgoing")){

            holder.status.setText("Outgoing Call");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.golden));


        }else if (conversionHolder.getCallingFlag().equals("Missed")){

            holder.status.setText("Missed Call");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));

        }

        try {
            String dateString = DateFormat.format("dd/MM/yyyy", new Date(conversionHolder.getDate())).toString();

            long difference = (todayTime) - (conversionHolder.getDate());

            int numOfDay = (int) (difference/(1000*60*60*24));
            int min = (int) (difference/(1000*60));
            int hours = (int) (difference/(1000*60*60));

            Log.d("TAG", "onBindViewHolder: "+min);
            if (min>=60){
                if (hours >= 24){
                    if (numOfDay>=2){
                        holder.date.setText(dateString);
                    }else {
                        holder.date.setText(numOfDay+" Days ago");
                    }
                }else {
                    holder.date.setText(hours+" Hours ago");
                }
            }else if (min == 0){
                holder.date.setText("A few moments ago");
            }else{
                holder.date.setText(min+" Minutes ago");
            }
        }catch (Exception e){
            Log.d("TAG", "onBindViewHolder: "+e.getLocalizedMessage());
        }

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.popup.show();
            }
        });

        holder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.delete:
                        holder.deleteMassage(conversionHolder);

                        break;
                    default:
                        return false;
                }
                return false;
            }
        });

        Log.d("conversionHolder", "onBindViewHolder: "+conversionHolder.isWhatsAppDeliveryFlag());
/*

        if (conversionHolder.isWhatsAppDeliveryFlag()){
            holder.whatsAppSuccessCard.setVisibility(View.VISIBLE);
            holder.whatsappFailCard.setVisibility(View.GONE);
        }else {
            holder.whatsAppSuccessCard.setVisibility(View.GONE);
            holder.whatsappFailCard.setVisibility(View.VISIBLE);
        }
*/


            holder.whatsAppSuccessCard.setVisibility(View.GONE);
            holder.whatsappFailCard.setVisibility(View.GONE);




        if (conversionHolder.isDeliveryFlag()){
            holder.textSuccessCard.setVisibility(View.VISIBLE);
            holder.textFailCard.setVisibility(View.GONE);
        }else {
            holder.textSuccessCard.setVisibility(View.GONE);
            holder.textFailCard.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public int getItemViewType(int position) {
        return  position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message,contact,date,status;
        ImageView options,imageSelected;
        CircleImageView profilePic;
        PopupMenu popup;
        CardView whatsappFailCard,whatsAppSuccessCard,textFailCard,textSuccessCard;
        public ViewHolder(@NonNull View view) {
            super(view);
            message = view.findViewById(R.id.massage);
            contact = view.findViewById(R.id.contact);
            date = view.findViewById(R.id.date);
            profilePic = view.findViewById(R.id.proPic);
            status = view.findViewById(R.id.status);
            options = view.findViewById(R.id.options);
            imageSelected = view.findViewById(R.id.imageSelected);

            imageSelected.setVisibility(View.GONE);
            whatsappFailCard = view.findViewById(R.id.whatsappFailCard);
            whatsAppSuccessCard = view.findViewById(R.id.whatsAppSuccessCard);
            textFailCard = view.findViewById(R.id.textFailCard);
            textSuccessCard = view.findViewById(R.id.textSuccessCard);

            popup = new PopupMenu(context,  options);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_menu_item_for_convertion_options, popup.getMenu());
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

        public void deleteMassage(ConversionHolder conversionHolder) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UtilityRoomDatabase.getInstance(context).massagesDao().deleteConversion(conversionHolder);
                    ((Activity)context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            removeAt(getAdapterPosition());
                            Toast.makeText(context, "Deleted...!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

}
