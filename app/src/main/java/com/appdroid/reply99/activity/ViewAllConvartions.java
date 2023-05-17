package com.appdroid.reply99.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.appdroid.reply99.R;
import com.appdroid.reply99.adapter.ConversionAdepter;
import com.appdroid.reply99.room.ConversionHolder;
import com.appdroid.reply99.room.UtilityRoomDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewAllConvartions extends AppCompatActivity {
    RecyclerView recyclerView;
    List<ConversionHolder> list;
    ConversionAdepter conversionAdepter;

    LottieAnimationView animationView;
    TextView noDataFoundMassage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_convartions);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewAllConvartions.this));
        recyclerView.hasFixedSize();

        animationView = findViewById(R.id.animationView);
        noDataFoundMassage = findViewById(R.id.noDataFoundMassage);

        list = new ArrayList<>();
        conversionAdepter = new ConversionAdepter(list,ViewAllConvartions.this);
        getCompletedConversions();
    }
    private void getCompletedConversions() {
        Thread  thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                List<ConversionHolder> conversionHolders = UtilityRoomDatabase.getInstance(ViewAllConvartions.this)
                        .massagesDao()
                        .getAllConversion();
                Collections.reverse(conversionHolders);

                list.clear();

                list.addAll(conversionHolders);



                Log.d("DDDDDDDDD", "run inside run : "+conversionHolders.size());




                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        recyclerView.setAdapter(conversionAdepter);
                        // Stuff that updates the UI

                        if (list.size()==0){
                            animationView.setVisibility(View.VISIBLE);

                            noDataFoundMassage.setVisibility(View.VISIBLE);
                        }else {
                            animationView.setVisibility(View.GONE);
                            noDataFoundMassage.setVisibility(View.GONE);
                        }
                    }
                });

                conversionAdepter.notifyDataSetChanged();
            }
        });
        thread.start();
    }



}