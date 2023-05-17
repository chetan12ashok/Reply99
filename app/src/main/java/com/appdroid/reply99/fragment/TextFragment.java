package com.appdroid.reply99.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appdroid.reply99.R;
import com.appdroid.reply99.activity.Dashboard;
import com.appdroid.reply99.adapter.MassagesAdepter;
import com.appdroid.reply99.room.MassageHolder;
import com.appdroid.reply99.room.ProductViewModel;
import com.appdroid.reply99.room.UtilityRoomDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextFragment extends Fragment {

    RecyclerView recyclerView;
    ProductViewModel productViewModel;
    List<MassageHolder> list;
    MassagesAdepter massagesAdepter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        recyclerView = view.findViewById(R.id.savedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.hasFixedSize();
        productViewModel  = new ViewModelProvider(this).get(ProductViewModel.class);
        list = new ArrayList<>();

        productViewModel.getWhatsAppMassagesOnly(false).observe(getViewLifecycleOwner(), new Observer<List<MassageHolder>>() {
            @Override
            public void onChanged(List<MassageHolder> lists) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list.clear();
                        Collections.reverse(lists);
                        list.addAll(lists);

                        RecyclerView.Adapter adapter = recyclerView.getAdapter();
                        recyclerView.setAdapter(null);
                        recyclerView.setAdapter(adapter);

                        if (lists.size()==0){
                            addDefaultMassage();
                        }

                    }
                });
            }
        });
        massagesAdepter = new MassagesAdepter(list,getContext());
        getAllMassages();
        return view;
    }

    private void getAllMassages() {
        Thread  thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                List<MassageHolder> massageHolders = UtilityRoomDatabase.getInstance(getContext())
                        .massagesDao()
                        .getWhatsAppMassagesNormal(false);
                Collections.reverse(massageHolders);

                list.clear();

                list.addAll(massageHolders);



                Log.d("DDDDDDDDD", "run inside run : "+massageHolders.size());


                /*if (i == 0){
                    savedPostAdapterWithRoomDatabase = new SavedPostAdapterWithRoomDatabase(localRoomSavePostsList,SavedPostsActivity.this);
                    recyclerView.setAdapter(savedPostAdapterWithRoomDatabase);
                    savedPostAdapterWithRoomDatabase.notifyDataSetChanged();
                }else if (i==1){
                    savedPostAdapterWithRoomDatabase.update(localRoomSavePostsList);
                    //  recyclerView.invalidate();
                }*/


                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        recyclerView.setAdapter(massagesAdepter);
                        // Stuff that updates the UI

                    }
                });

                massagesAdepter.notifyDataSetChanged();
            }
        });
        thread.start();
    }

    private void addDefaultMassage() {
        MassageHolder massageHolder = new MassageHolder();
        massageHolder.setMassage(getResources().getString(R.string.defaultMassage));
        massageHolder.setSmsImageLink("https://reply99.page.link/SiJ8");
        massageHolder.setWhatsAppFlag(false);
        massageHolder.setWhatsappImageLink("https://firebasestorage.googleapis.com/v0/b/reply-a8264.appspot.com/o/ImagesFroWhatsApp%2FAutoreply-for-WhatsApp-A-Guide-for-2021-15-1200x634.jpg?alt=media&token=a321836a-9970-4bbe-a765-7abb20ba5a15");
        InsertAsnkTask insertAsnkTask = new InsertAsnkTask();
        insertAsnkTask.execute(massageHolder);
    }

    public class InsertAsnkTask extends AsyncTask<MassageHolder,Void,Void> {
        @Override
        protected Void doInBackground(MassageHolder... massage) {
            UtilityRoomDatabase.getInstance(getContext())
                    .massagesDao()
                    .insertMassage(massage[0]);

            ((Dashboard)getContext()).runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    SharedPreferences.Editor editor = getContext().getSharedPreferences("MassagesWithFlags",MODE_PRIVATE).edit();
                    editor.putString("sendToAllMassage",getResources().getString(R.string.defaultMassage));
                    editor.apply();

                }
            });

            return null;
        }
    }


}