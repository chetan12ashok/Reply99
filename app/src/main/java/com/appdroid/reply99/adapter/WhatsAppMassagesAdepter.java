package com.appdroid.reply99.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appdroid.reply99.R;
import com.appdroid.reply99.activity.AddWhatsAppMessage;
import com.appdroid.reply99.room.MassageHolder;
import com.appdroid.reply99.room.UtilityRoomDatabase;
import com.bumptech.glide.Glide;

import java.util.List;

public class WhatsAppMassagesAdepter extends RecyclerView.Adapter<WhatsAppMassagesAdepter.ViewHolder> {

    List<MassageHolder> list;
    Context context;
    SharedPreferences sharedPreferences;
    String incomingMassageForWhatsApp,outgoingMassageForWhatsApp,missedCallMassageForWhatsApp,sendToAllMassageForWhatsApp;
    public WhatsAppMassagesAdepter(List<MassageHolder> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_item,parent,false);
        sharedPreferences = context.getSharedPreferences("MassagesWithFlags", MODE_PRIVATE);

        return new WhatsAppMassagesAdepter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        incomingMassageForWhatsApp = sharedPreferences.getString("incomingMassageForWhatsapp","");
        outgoingMassageForWhatsApp = sharedPreferences.getString("outgoingMassageForWhatsapp","");
        missedCallMassageForWhatsApp = sharedPreferences.getString("missedCallMassageForWhatsapp","");
        sendToAllMassageForWhatsApp =  sharedPreferences.getString("sendToAllMassageForWhatsapp","");

        MassageHolder massageHolder = list.get(position);

        if (incomingMassageForWhatsApp.equals(massageHolder.getMassage())){
            holder.status.setText("Set WhatsApp for Incoming call");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.primary));
        }else if (outgoingMassageForWhatsApp.equals(massageHolder.getMassage())){
            holder.status.setText("Set WhatsApp for Outgoing call");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.golden));
        }else if (missedCallMassageForWhatsApp.equals(massageHolder.getMassage())){
            holder.status.setText("Set WhatsApp for Missed call");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }else if (sendToAllMassageForWhatsApp.equals(massageHolder.getMassage())){
            holder.status.setText("Set WhatsApp for All calls");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green));
        }
        else {
            holder.status.setVisibility(View.GONE);
        }



        if (massageHolder.getWhatsappImageLink() != null){
           // holder.image.setVisibility(View.GONE);

            if (massageHolder.getContainMedia().equals("Video")){
                holder.image.setVisibility(View.GONE);
                Glide.with(context).load(massageHolder.getWhatsappImageLink()).into(holder.opecEffect);
                holder.opecEffect.setVisibility(View.VISIBLE);
                holder.playBtn.setVisibility(View.VISIBLE);
            }else {
                Glide.with(context).load(massageHolder.getWhatsappImageLink()).into(holder.image);
                holder.image.setVisibility(View.VISIBLE);
                holder.opecEffect.setVisibility(View.GONE);
                holder.playBtn.setVisibility(View.GONE);
            }


        }else {
            holder.image.setVisibility(View.GONE);
        }

        holder.message.setText(massageHolder.getMassage());


        holder.opecEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(massageHolder.getWhatsappImageLink()),"video/*");
                context.startActivity(intent);
            }
        });


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
                    case R.id.edit:
                        Intent intent = new Intent(context, AddWhatsAppMessage.class);
                        intent.putExtra("massage",massageHolder);
                        context.startActivity(intent);
                        break;
                    case R.id.delete:

                        if (!sendToAllMassageForWhatsApp.equals(massageHolder.getMassage())){
                            holder.deleteMassage(massageHolder);
                        }else {
                            Toast.makeText(context, "Can't delete default massage please edit it..", Toast.LENGTH_SHORT).show();
                        }


                        break;
                    default:
                        return false;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message,status;
        ImageView delete,options,image,opecEffect,playBtn;
        Dialog dialogForFinalMassage;
        Button okBTN;
        PopupMenu popup;

        public ViewHolder(@NonNull View view) {
            super(view);
            message = view.findViewById(R.id.message);
            delete = view.findViewById(R.id.delete);
            options  = view.findViewById(R.id.options);
            status = view.findViewById(R.id.status);
            image = view.findViewById(R.id.image);


            opecEffect = view.findViewById(R.id.opecEffect);
            playBtn = view.findViewById(R.id.playBtn);

            popup = new PopupMenu(context,  options);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_menu_item, popup.getMenu());
        }

        public void deleteMassage(MassageHolder massageHolder) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UtilityRoomDatabase.getInstance(context).massagesDao().deleteByPostDocId(massageHolder.getMassageId());
                    ((Activity)context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            SharedPreferences.Editor editor = context.getSharedPreferences("MassagesWithFlags",MODE_PRIVATE).edit();

                            if (massageHolder.getMassage().equals(incomingMassageForWhatsApp)){
                                editor.putString("incomingMassageForWhatsapp","");
                            }else if (massageHolder.getMassage().equals(outgoingMassageForWhatsApp)){
                                editor.putString("outgoingMassageForWhatsapp","");
                            }else if (massageHolder.getMassage().equals(missedCallMassageForWhatsApp)){
                                editor.putString("missedCallMassageForWhatsapp","");
                            }else if (massageHolder.getMassage().equals(sendToAllMassageForWhatsApp)){
                                editor.putString("sendToAllMassageForWhatsapp","");
                            }
                            editor.apply();

                            Toast.makeText(context, "Massage Deleted...!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).start();
        }
    }
}
