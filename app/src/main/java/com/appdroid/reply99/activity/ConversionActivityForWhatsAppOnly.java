package com.appdroid.reply99.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.appdroid.reply99.R;
import com.appdroid.reply99.adapter.ConvartionPagerAdepter;
import com.appdroid.reply99.fragment.Completed;
import com.appdroid.reply99.fragment.CompletedWhatsAppOnly;
import com.appdroid.reply99.fragment.Failed;
import com.appdroid.reply99.fragment.FailedWhatsAppOnly;
import com.google.android.material.tabs.TabLayout;

public class ConversionActivityForWhatsAppOnly extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);

        ConvartionPagerAdepter convartionPagerAdepter = new ConvartionPagerAdepter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        convartionPagerAdepter.addFragment(new CompletedWhatsAppOnly(),"WhatsApp Completed");
        convartionPagerAdepter.addFragment(new FailedWhatsAppOnly(),"WhatsApp Failed");
        viewPager2.setAdapter(convartionPagerAdepter);


        tabLayout.setupWithViewPager(viewPager2);

        String action = getIntent().getAction();
        if (action != null){
            if (action.equals("positive")){
               TabLayout.Tab tab = tabLayout.getTabAt(0);
               tabLayout.selectTab(tab);

            }else {
                TabLayout.Tab tab = tabLayout.getTabAt(1);
                tabLayout.selectTab(tab);
            }
        }


    }
}