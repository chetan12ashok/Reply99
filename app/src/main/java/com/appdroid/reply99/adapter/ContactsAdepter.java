package com.appdroid.reply99.adapter;

import android.content.Context;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.appdroid.reply99.R;
import com.appdroid.reply99.activity.ContactsList;
import com.appdroid.reply99.room.ContactHolder;
import com.appdroid.reply99.model.MainViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ContactsAdepter extends RecyclerView.Adapter<ContactsAdepter.ViewHolder> implements Filterable {

    Context context;
    List<ContactHolder> list;
    List<ContactHolder> listAll;
    boolean isEnable = false;
    boolean isSelectedAll = false;
    ArrayList<ContactHolder> selectedList = new ArrayList<>();

    MainViewModel mainViewModel;

    public ContactsAdepter(Context context, List<ContactHolder> listss) {
        this.context = context;
        this.list = listss;
        this.listAll = new ArrayList<>(listss);
   }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View view  = LayoutInflater.from(context).inflate(R.layout.contact_item,parent,false);
     mainViewModel = (MainViewModel) new ViewModelProvider((ContactsList)context).get(MainViewModel.class);
     return  new ContactsAdepter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ContactHolder contactHolder  =list.get(position);
        holder.mobileNumber.setText(contactHolder.getPhoneNumber());
        holder.contactName.setText(contactHolder.getName());


        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (!isEnable){


                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            MenuInflater menuInflater  = actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.popup_menu_item,menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

                            isEnable = true;
                            ClickItem(holder);
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

                            int id = menuItem.getItemId();

                            switch (id){

                                case  R.id.done :
                                    Toast.makeText(context, ""+selectedList.size(), Toast.LENGTH_SHORT).show();
                                    break;

                            }
                            return false;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {

                                isEnable =false;
                                isSelectedAll = false;
                                selectedList.clear();
                                notifyDataSetChanged();

                        }
                    };

                    ((AppCompatActivity) view.getContext()).startActionMode(callback);


                }else {
                    ClickItem(holder);
                }

                return true;
            }
        });


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnable){
                    ClickItem(holder);
                }else {
                    Toast.makeText(context, "Click "+holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void ClickItem(ViewHolder holder) {

            ContactHolder contactHolder = list.get(holder.getAdapterPosition());

            if (holder.checkBox.getVisibility() == View.GONE){

                holder.checkBox.setVisibility(View.VISIBLE);
                selectedList.add(contactHolder);
            }else {
                holder.checkBox.setVisibility(View.GONE);

                selectedList.remove(contactHolder);
            }

       // mainViewModel.setSize(""+selectedList.size());
        ContactsList.headerName.setText(""+selectedList.size());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {

        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<ContactHolder> filteredList = new ArrayList<>();

            if (charSequence.toString().isEmpty()){
                filteredList.addAll(listAll);
            }else {
                for (ContactHolder contactHolder : listAll){
                    if (contactHolder.getName().toLowerCase().contains(charSequence.toString().toLowerCase(Locale.ROOT))
                            || contactHolder.getPhoneNumber().contains(charSequence.toString())
                    ){


                        filteredList.add(contactHolder);

                    }
                }
            }

            FilterResults filterResults  = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            list.clear();
            list.addAll((Collection<? extends ContactHolder>) filterResults.values);
            notifyDataSetChanged();

        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView contactName,mobileNumber;
        RelativeLayout layout;
        CheckBox checkBox;
        public ViewHolder(@NonNull View view) {
            super(view);
            contactName = view.findViewById(R.id.contact);
            mobileNumber = view.findViewById(R.id.UserName);
            layout = view.findViewById(R.id.layout);
            checkBox = view.findViewById(R.id.checkbox);
        }
    }
}
