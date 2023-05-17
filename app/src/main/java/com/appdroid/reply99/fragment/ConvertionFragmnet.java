package com.appdroid.reply99.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.appdroid.reply99.R;
import com.appdroid.reply99.adapter.ConvartionPagerAdepter;
import com.google.android.material.tabs.TabLayout;


public class ConvertionFragmnet extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_convertion_fragmnet, container, false);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager2 = view.findViewById(R.id.viewPager);

        ConvartionPagerAdepter convartionPagerAdepter = new ConvartionPagerAdepter(getFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        convartionPagerAdepter.addFragment(new Completed(),"Completed");
        convartionPagerAdepter.addFragment(new Failed(),"Failed");
        viewPager2.setAdapter(convartionPagerAdepter);


        tabLayout.setupWithViewPager(viewPager2);


        return  view;
    }
}