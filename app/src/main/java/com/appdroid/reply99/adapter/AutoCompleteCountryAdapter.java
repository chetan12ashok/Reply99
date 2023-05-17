package com.appdroid.reply99.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appdroid.reply99.R;
import com.appdroid.reply99.fragment.MySettings;
import com.appdroid.reply99.room.ContactHolder;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AutoCompleteCountryAdapter extends ArrayAdapter<ContactHolder> {
    private List<ContactHolder> countryListFull;
    Context context;
    CircleImageView profilePic;
    public AutoCompleteCountryAdapter(@NonNull Context context, @NonNull List<ContactHolder> countryList) {
        super(context, 0, countryList);
        countryListFull = new ArrayList<>(countryList);
        this.context = context;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.contact_item, parent, false
            );
        }

        TextView contactName,mobileNumber;
        RelativeLayout layout;


        layout = convertView.findViewById(R.id.layout);
        contactName =convertView.findViewById(R.id.contact);
        mobileNumber = convertView.findViewById(R.id.UserName);
        profilePic = convertView.findViewById(R.id.profileIcon);

        ContactHolder contactHolder = getItem(position);


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                contactHolder.setIgnoreContact(true);
                MySettings.listForSelectedContacts.add(contactHolder);
                MySettings.contactsAdepter.notifyDataSetChanged();
   /*             MySettings.InsertAsnkTask insertAsnkTask = new MySettings.InsertAsnkTask(getContext());
                insertAsnkTask.execute(contactHolder);*/
                MySettings.searchContact.setText("");
            }
        });



        if (contactHolder != null) {
            contactName.setText(contactHolder.getName());
            mobileNumber.setText(contactHolder.getPhoneNumber());
            loadProfilePic(contactHolder.getName());
        }

        return convertView;
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
    


    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<ContactHolder> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(countryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ContactHolder item : countryListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern) || item.getPhoneNumber().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((ContactHolder) resultValue).getName();
        }
    };





}
