package com.appdroid.reply99.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.appdroid.reply99.R;
import com.appdroid.reply99.adapter.ConversionAdepter;
import com.appdroid.reply99.adapter.ConversionAdepterForWhatsApp;
import com.appdroid.reply99.room.ConversionHolder;
import com.appdroid.reply99.room.UtilityRoomDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FailedWhatsAppOnly extends Fragment {

    RecyclerView recyclerView;
    List<ConversionHolder> list;
    ConversionAdepterForWhatsApp conversionAdepterForWhatsApp;
    TextView noDataFoundMassage;
    LottieAnimationView animationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_failed, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.hasFixedSize();
        animationView = view.findViewById(R.id.animationView);
        list = new ArrayList<>();
        conversionAdepterForWhatsApp = new ConversionAdepterForWhatsApp(list,getContext());
        noDataFoundMassage = view.findViewById(R.id.noDataFoundMassage);

        getFailedConversions();
        
        return view;
    }

    private void getFailedConversions() {

        Thread  thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                List<ConversionHolder> conversionHolders = UtilityRoomDatabase.getInstance(getContext())
                        .massagesDao()
                        .getWhatsAppConversions(false);
                Collections.reverse(conversionHolders);

                list.clear();

                list.addAll(conversionHolders);



                Log.d("DDDDDDDDD", "run inside run : "+conversionHolders.size());




                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        recyclerView.setAdapter(conversionAdepterForWhatsApp);

                        if (list.size()==0){
                            animationView.setVisibility(View.VISIBLE);
                            noDataFoundMassage.setVisibility(View.VISIBLE);
                        }else {
                            animationView.setVisibility(View.GONE);
                            noDataFoundMassage.setVisibility(View.GONE);
                        }

                    }
                });
                conversionAdepterForWhatsApp.notifyDataSetChanged();
            }
        });
        thread.start();


    }
}