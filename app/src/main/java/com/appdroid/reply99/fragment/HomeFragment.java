package com.appdroid.reply99.fragment;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.appdroid.reply99.activity.AddWhatsAppMessage;
import com.appdroid.reply99.activity.ConversionActivityForWhatsAppOnly;
import com.appdroid.reply99.activity.Dashboard;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appdroid.reply99.NotificationService;
import com.appdroid.reply99.R;
import com.appdroid.reply99.activity.AddSMSNewMessage;
import com.appdroid.reply99.activity.ConversionActivity;
import com.appdroid.reply99.activity.ViewAllConvartions;
import com.appdroid.reply99.adapter.MassagesAdepter;
import com.appdroid.reply99.adapter.WhatsAppMassagesAdepter;
import com.appdroid.reply99.model.preferences.PreferencesManager;
import com.appdroid.reply99.model.utils.Constants;
import com.appdroid.reply99.model.utils.CustomDialog;
import com.appdroid.reply99.model.utils.ServieUtils;
import com.appdroid.reply99.room.ConversionHolder;
import com.appdroid.reply99.room.MassageHolder;
import com.appdroid.reply99.room.ProductViewModel;
import com.appdroid.reply99.room.UtilityRoomDatabase;/*
import com.appdroid.reply99.whatsappAutoReply.IApiInterface;
import com.appdroid.reply99.whatsappAutoReply.Language;
import com.appdroid.reply99.whatsappAutoReply.MassageObj;
import com.appdroid.reply99.whatsappAutoReply.RequastModel;
import com.appdroid.reply99.whatsappAutoReply.RetrofitInstance;
import com.appdroid.reply99.whatsappAutoReply.Template;*/
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class HomeFragment extends Fragment {

    FloatingActionButton addMassage;
    RecyclerView recyclerView;
    TextView viewConversations;
    List<MassageHolder> list;
    MassagesAdepter massagesAdepter;
    SwitchMaterial mainAutoReplySwitch;
    private static final int REQ_NOTIFICATION_LISTENER = 100;

    TextView totalCompletedReply,totalFailedReply;

    private PreferencesManager preferencesManager;
    private static final List<MaterialCheckBox> supportedAppsCheckboxes = new ArrayList<>();
    private final List<View> supportedAppsDummyViews = new ArrayList<>();

    ProductViewModel productViewModel;

    SharedPreferences.Editor editor;
    LinearLayout successfulLayout,failedLayout;

    SharedPreferences tourPreferences;
    boolean showTour;

    CardView whatsappMassageCard,nonDeleverWhatsappMassageCard;

    TextView totalWhatsappCompleted,totalWEhatsappFailedReply;

    SpeedDialView speedDialView;

    TabLayout tabLayout;
    ViewPager viewPager2;



    RecyclerView recyclerViewForWhatsAppMassages;
    ProductViewModel productViewModelForWhatsAppMassages;
    List<MassageHolder> listForWhatsAppMassages;
    WhatsAppMassagesAdepter massagesAdepterForWhatsAppMassages;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view =  inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager2 = view.findViewById(R.id.viewPager);

        speedDialView = view.findViewById(R.id.speedDialView);

        productViewModel  = new ViewModelProvider(this).get(ProductViewModel.class);

        editor = getContext().getSharedPreferences("MassagesWithFlags", MODE_PRIVATE).edit();
        tourPreferences = getContext().getSharedPreferences("MassagesWithFlags",MODE_PRIVATE);

        showTour = tourPreferences.getBoolean("showTour",true);

        successfulLayout = view.findViewById(R.id.successfulLayout);
        failedLayout = view.findViewById(R.id.failedLayout);

        recyclerView = view.findViewById(R.id.savedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.hasFixedSize();

        mainAutoReplySwitch = view.findViewById(R.id.mainAutoReplySwitch);

        totalCompletedReply = view.findViewById(R.id.totalCompletedReply);
        totalFailedReply = view.findViewById(R.id.totalFailedReply);
        totalWEhatsappFailedReply = view.findViewById(R.id.totalWEhatsappFailedReply);
        totalWhatsappCompleted = view.findViewById(R.id.totalWhatsappCompleted);
        preferencesManager = PreferencesManager.getPreferencesInstance(getContext());

        nonDeleverWhatsappMassageCard = view.findViewById(R.id.nonDeleverWhatsappMassageCard);
        whatsappMassageCard = view.findViewById(R.id.whatsappMassageCard);

        list = new ArrayList<>();
        massagesAdepter = new MassagesAdepter(list,getContext());
        getAllMassagesForSMS();

        addMassage = view.findViewById(R.id.addMassage);
        viewConversations = view.findViewById(R.id.viewConversations);



        allAboutWhatsAppRecyclerView(view);



        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("SSSSSAFADFAD", "onTabSelected: "+tab.getText());
                if (tab.getText().equals("Whatsapp")){
                    recyclerViewForWhatsAppMassages.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    recyclerViewForWhatsAppMassages.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

       /* ConvartionPagerAdepter convartionPagerAdepter = new ConvartionPagerAdepter(getActivity().getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        convartionPagerAdepter.addFragment(new TextFragment(),"Text");
        convartionPagerAdepter.addFragment(new WhatsappFragment(),"Whatsapp");
        viewPager2.setAdapter(convartionPagerAdepter);

        tabLayout.setupWithViewPager(viewPager2);*/

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.addTxtMessage, R.drawable.ic_message)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.golden, getActivity().getTheme()))
                        .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.white, getActivity().getTheme()))
                        .setLabel(getString(R.string.txtMessage))
                        .setLabelColor(Color.BLUE)
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.addWpMessage, R.drawable.whatsapp_a)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.green, getActivity().getTheme()))
                        .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.white, getActivity().getTheme()))
                        .setLabel(getString(R.string.txtWhatsapp))
                        .setLabelColor(Color.BLUE)
                        .create()
        );

        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()){
                    case R.id.addTxtMessage :
                        startActivity(new Intent(getContext(), AddSMSNewMessage.class));
                        break;

                    case R.id.addWpMessage:

                        startActivity(new Intent(getContext(), AddWhatsAppMessage.class));

                        break;
                }
                return false;
            }
        });

        addMassage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddSMSNewMessage.class));
            }
        });

        viewConversations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewAllConvartions.class);
                startActivity(intent);
            }
        });

        whatsappMassageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ConversionActivityForWhatsAppOnly.class);
                intent.setAction("positive");
                startActivity(intent);
            }
        });

        nonDeleverWhatsappMassageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ConversionActivityForWhatsAppOnly.class);
                intent.setAction("negative");
                startActivity(intent);
            }
        });

        if (showTour){

            Dialog tourConfurmationDialog = new Dialog(getContext());
            tourConfurmationDialog.setContentView(R.layout.intro_dialog);
            TextView skip = tourConfurmationDialog.findViewById(R.id.skip);
            Button yes = tourConfurmationDialog.findViewById(R.id.yes);

            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.putBoolean("showTour",false);
                    editor.apply();
                    tourConfurmationDialog.dismiss();
                }
            });

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tourConfurmationDialog.dismiss();
                    new GuideView.Builder(getContext())
                            .setTitle("Turn On System")
                            .setContentText("By turning on, \nsystem will able send messages \nautomatically to your incoming,\noutgoing and missed calls.")
                            .setTargetView(mainAutoReplySwitch)
                            .setGravity(Gravity.auto)//optional
                            .setDismissType(DismissType.anywhere)
                            .setGuideListener(new GuideListener() {
                                @Override
                                public void onDismiss(View view) {

                                    makeSecoundHind();

                                }
                            })
                            .build()
                            .show();
                }
            });
            tourConfurmationDialog.show();

        }

        successfulLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(),ConversionActivity.class);
                intent.setAction("positive");
                startActivity(intent);

            }
        });

        failedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(),ConversionActivity.class);
                intent.setAction("negative");
                startActivity(intent);

            }
        });

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

        mainAutoReplySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !isListenerEnabled(getContext(), NotificationService.class)) {
//                launchNotificationAccessSettings();
                showPermissionsDialog();
            } else {
                preferencesManager.setServicePref(isChecked);

                if (isChecked) {
                    startNotificationService();
                    editor.putBoolean("serviceFlag",true);
                } else {
                    stopNotificationService();
                    editor.putBoolean("serviceFlag",false);
                }
                editor.apply();
                mainAutoReplySwitch.setText(
                        isChecked
                                ? R.string.mainAutoReplySwitchOnLabel

                                : R.string.mainAutoReplySwitchOffLabel
                );

                setSwitchState();

                // Enable group chat switch only if main switch id ON
              //  groupReplySwitch.setEnabled(isChecked);
            }
        });

        return view;
    }

    private void getWhatsAppMassages() {
        Thread  thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                List<MassageHolder> massageHolders = UtilityRoomDatabase.getInstance(getContext())
                        .massagesDao()
                        .getWhatsAppMassagesNormal(true);
                Collections.reverse(massageHolders);

                listForWhatsAppMassages.clear();

                listForWhatsAppMassages.addAll(massageHolders);



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

                        Log.d("DDDDDDDDD", "run : "+massageHolders.size());
                        recyclerViewForWhatsAppMassages.setAdapter(massagesAdepterForWhatsAppMassages);
                        // Stuff that updates the UI
                        massagesAdepterForWhatsAppMassages.notifyDataSetChanged();
                    }
                });


            }
        });
        thread.start();
    }

    private void allAboutWhatsAppRecyclerView(View view) {

        recyclerViewForWhatsAppMassages = view.findViewById(R.id.whatsAppMassageRecyclerView);
        recyclerViewForWhatsAppMassages.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewForWhatsAppMassages.hasFixedSize();
        productViewModelForWhatsAppMassages  = new ViewModelProvider(this).get(ProductViewModel.class);
        listForWhatsAppMassages = new ArrayList<>();
        massagesAdepterForWhatsAppMassages = new WhatsAppMassagesAdepter(listForWhatsAppMassages,getContext());

        getWhatsAppMassages();
        productViewModelForWhatsAppMassages.getWhatsAppMassagesOnly(true).observe(getViewLifecycleOwner(), new Observer<List<MassageHolder>>() {
            @Override
            public void onChanged(List<MassageHolder> lists) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        listForWhatsAppMassages.clear();
                        Collections.reverse(lists);
                        listForWhatsAppMassages.addAll(lists);

                        Log.d("KKKKDAAS", "run: "+lists.size());

                        RecyclerView.Adapter adapter = recyclerViewForWhatsAppMassages.getAdapter();
                        recyclerViewForWhatsAppMassages.setAdapter(null);
                        recyclerViewForWhatsAppMassages.setAdapter(adapter);

                        if (lists.size()==0){
                            addDefaultMassageForWhatsApp();
                        }

                    }
                });
            }
        });
    }

    private void addDefaultMassageForWhatsApp() {
        MassageHolder massageHolder = new MassageHolder();
        massageHolder.setMassage(getResources().getString(R.string.defaultMassageForWhatsApp));
        massageHolder.setSmsImageLink("https://reply99.page.link/SiJ8");

        massageHolder.setWhatsAppFlag(true);
        massageHolder.setContainMedia("Image");

        massageHolder.setWhatsappImageLink("https://firebasestorage.googleapis.com/v0/b/reply-a8264.appspot.com/o/ImagesFroWhatsApp%2FAutoreply-for-WhatsApp-A-Guide-for-2021-15-1200x634.jpg?alt=media&token=a321836a-9970-4bbe-a765-7abb20ba5a15");
        InsertAsnkTask insertAsnkTask = new InsertAsnkTask("fromWhatsApp");
        insertAsnkTask.execute(massageHolder);
    }

    private void makeSecoundHind() {
        new GuideView.Builder(getContext())
                .setTitle("Tap to")
                .setContentText("View successfully\ndelivered messages.")
                .setTargetView(successfulLayout)
                .setGravity(Gravity.auto)//optional
                .setDismissType(DismissType.anywhere)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        makeThirdHind();
                    }
                })
                .build()
                .show();
    }

    private void makeThirdHind() {
        new GuideView.Builder(getContext())
                .setTitle("Tap to")
                .setContentText("View failed to\ndelivered messages.")
                .setTargetView(failedLayout)
                .setGravity(Gravity.auto)//optional
                .setDismissType(DismissType.anywhere)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        makeAddMassageHint();
                    }
                })
                .build()
                .show();
    }

    private void makeAddMassageHint() {
        new GuideView.Builder(getContext())
                .setTitle("Tap To")
                .setContentText("Add messages to message list.")
                .setTargetView(addMassage)

                .setGravity(Gravity.auto)//optional
                .setDismissType(DismissType.anywhere)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        editor.putBoolean("showTour",false);
                        editor.apply();
                    }
                })
                .build()
                .show();
    }


    @Override
    public void onResume() {
        super.onResume();
        //If user directly goes to Settings and removes notifications permission
        //when app is launched check for permission and set appropriate app state
        if (!isListenerEnabled(getContext(), NotificationService.class)) {
            preferencesManager.setServicePref(false);
        }

        getConversions(true);
        getConversions(false);

        getWhatsAppConversions(true);
        getWhatsAppConversions(false);
        setSwitchState();


    }

    private void getWhatsAppConversions(boolean flag) {
        Thread  thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                List<ConversionHolder> conversionHolders = UtilityRoomDatabase.getInstance(getContext())
                        .massagesDao()
                        .getWhatsAppConversions(flag);
                Collections.reverse(conversionHolders);


                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (flag){
                            totalWhatsappCompleted.setText(""+conversionHolders.size());
                        }else {
                            totalWEhatsappFailedReply.setText(""+conversionHolders.size());
                        }

                    }
                });
            }
        });
        thread.start();
    }

    public class InsertAsnkTask extends AsyncTask<MassageHolder,Void,Void> {
        String flag ;
        public InsertAsnkTask(String fromWhatsApp) {
            this.flag = fromWhatsApp;
        }

        @Override
        protected Void doInBackground(MassageHolder... massage) {
            UtilityRoomDatabase.getInstance(getContext())
                    .massagesDao()
                    .insertMassage(massage[0]);

            ((Dashboard)getContext()).runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    SharedPreferences.Editor editor = getContext().getSharedPreferences("MassagesWithFlags",MODE_PRIVATE).edit();


                    Log.d("doInBackground", "run: "+flag);

                    if (flag.equals("fromWhatsApp")){

                        editor.putString("sendToAllMassageForWhatsapp",massage[0].getMassage());
                        editor.putString("sendToAllImageForWhatsapp",massage[0].getWhatsappImageLink());
                        editor.putString("sendToAllWhatsappImageOrVideo","image");

                    }else {
                        editor.putString("sendToAllMassage",getResources().getString(R.string.defaultMassage));

                    }

                    editor.apply();
                }
            });

            return null;
        }
    }


    private void addDefaultMassage() {
        MassageHolder massageHolder = new MassageHolder();
        massageHolder.setMassage(getResources().getString(R.string.defaultMassage));
        massageHolder.setSmsImageLink("https://reply99.page.link/SiJ8");

        massageHolder.setWhatsappImageLink("https://firebasestorage.googleapis.com/v0/b/reply-a8264.appspot.com/o/ImagesFroWhatsApp%2FAutoreply-for-WhatsApp-A-Guide-for-2021-15-1200x634.jpg?alt=media&token=a321836a-9970-4bbe-a765-7abb20ba5a15");
        InsertAsnkTask insertAsnkTask = new InsertAsnkTask("fromSMS");
        insertAsnkTask.execute(massageHolder);
    }


    private void getConversions(boolean flag) {
        Thread  thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                List<ConversionHolder> conversionHolders = UtilityRoomDatabase.getInstance(getContext())
                        .massagesDao()
                        .getCompletedConversions(flag);
                Collections.reverse(conversionHolders);


                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                            if (flag){
                                totalCompletedReply.setText(""+conversionHolders.size());
                            }else {
                                totalFailedReply.setText(""+conversionHolders.size());
                            }

                    }
                });
            }
        });
        thread.start();
    }


    private void startNotificationService() {
        Log.d("DDDDD", "startNotificationService: "+preferencesManager.isForegroundServiceNotificationEnabled());
        if (preferencesManager.isForegroundServiceNotificationEnabled()) {
            ServieUtils.getInstance(getContext()).startNotificationService();
        }
    }



    private void stopNotificationService() {
        ServieUtils.getInstance(getContext()).stopNotificationService();
    }


    private void showPermissionsDialog() {
        CustomDialog customDialog = new CustomDialog(getContext());
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PERMISSION_DIALOG_TITLE, getString(R.string.permission_dialog_title));
        bundle.putString(Constants.PERMISSION_DIALOG_MSG, getString(R.string.permission_dialog_msg));
        customDialog.showDialog(bundle, null, (dialog, which) -> {
            if (which == -2) {
                //Decline
                showPermissionDeniedDialog();
            } else {
                //Accept
                editor.putBoolean("serviceFlag",true);
                editor.apply();
                launchNotificationAccessSettings();
            }
        });
    }


    private void showPermissionDeniedDialog() {
        CustomDialog customDialog = new CustomDialog(getContext());
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PERMISSION_DIALOG_DENIED_TITLE, getString(R.string.permission_dialog_denied_title));
        bundle.putString(Constants.PERMISSION_DIALOG_DENIED_MSG, getString(R.string.permission_dialog_denied_msg));
        bundle.putBoolean(Constants.PERMISSION_DIALOG_DENIED, true);
        customDialog.showDialog(bundle, null, (dialog, which) -> {
            if (which == -2) {
                //Decline
                setSwitchState();
            } else {
                //Accept
                launchNotificationAccessSettings();
            }
        });
    }


    void setSwitchState() {
        mainAutoReplySwitch.setChecked(preferencesManager.isServiceEnabled());
      //  groupReplySwitch.setEnabled(preferencesManager.isServiceEnabled());
        enableOrDisableEnabledAppsCheckboxes(mainAutoReplySwitch.isChecked());
    }


    private void enableOrDisableEnabledAppsCheckboxes(boolean enabled) {
        for (MaterialCheckBox checkbox : supportedAppsCheckboxes) {
            checkbox.setEnabled(enabled);
        }
        for (View dummyView : supportedAppsDummyViews) {
            dummyView.setVisibility(enabled ? View.GONE : View.VISIBLE);
        }
    }



    public void launchNotificationAccessSettings() {
        //We should remove it few versions later
        enableService(true);//we need to enable the service for it so show in settings

        final String NOTIFICATION_LISTENER_SETTINGS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            NOTIFICATION_LISTENER_SETTINGS = ACTION_NOTIFICATION_LISTENER_SETTINGS;
        } else {
            NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
        }
        Intent i = new Intent(NOTIFICATION_LISTENER_SETTINGS);
        startActivityForResult(i, REQ_NOTIFICATION_LISTENER);
    }

    private void enableService(boolean enable) {
        PackageManager packageManager = getContext().getPackageManager();
        ComponentName componentName = new ComponentName(getContext(), NotificationService.class);
        int settingCode = enable
                ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        // enable dummyActivity (as it is disabled in the manifest.xml)
        packageManager.setComponentEnabledSetting(componentName, settingCode, PackageManager.DONT_KILL_APP);

    }


    public static boolean isListenerEnabled(Context context, Class notificationListenerCls) {
        ComponentName cn = new ComponentName(context, notificationListenerCls);
        String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());
    }


    private void getAllMassagesForSMS() {
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


    @Override
    public void onDestroy() {
        stopNotificationService();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_NOTIFICATION_LISTENER) {
            if (isListenerEnabled(getContext(), NotificationService.class)) {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                startNotificationService();
                preferencesManager.setServicePref(true);
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                preferencesManager.setServicePref(false);
            }
            setSwitchState();
        }
    }

// Custome Model Working On  auto_reply_tem_sec without call to action Buttons


/*    private CustomModel getJsonData() {
        CustomModel customModel = new CustomModel();
        customModel.setMessagingProduct("whatsapp");
        customModel.setRecipientType("individual");
        customModel.setTo("+918625909193");
        customModel.setType("template");

//                customModel.setTemplate();

        Template template = new Template();
        template.setName("auto_reply_tem_sec");

        //   template.setName("auto_reply");

        Language language = new Language();
        language.setCode("en");

        template.setLanguage(language);

        List<ComponentsItem> componentsItemsList = new ArrayList<>();


        ComponentsItem componentsItemHeader =  new ComponentsItem();
        componentsItemHeader.setType("header");


        ParametersItem parametersItem = new ParametersItem();
        parametersItem.setType("image");

        Image  image  =new Image();
        image.setLink("https://www.wishesmsg.com/wp-content/uploads/Out-Of-Office-Messages.jpg");



        parametersItem.setImage(image);  //  parameter item completed..

        List<ParametersItem> parametersItemsListForHeader  = new ArrayList<>();
        parametersItemsListForHeader.add(parametersItem);

        componentsItemHeader.setParameters(parametersItemsListForHeader);




        ComponentsItem componentsItemForBody= new ComponentsItem();
        componentsItemForBody.setType("body");

        List<ParametersItem> parametersItemsForBody = new ArrayList<>();



        ParametersItem parametersItem2 = new ParametersItem();
        parametersItem2.setType("text");
        parametersItem2.setText("This is an auto reply");  // thsi is adden nantar

        ParametersItem parametersItem1 = new ParametersItem();
        parametersItem1.setType("text");
        parametersItem1.setText("Harsha Sharma");

        parametersItemsForBody.add(parametersItem1);
        parametersItemsForBody.add(parametersItem2); // thsi is adden nantar

        componentsItemForBody.setParameters(parametersItemsForBody);




        ParametersItem parametersItemForButton = new ParametersItem();
        parametersItemForButton.setText("https://www.nobroker.in/");
        //   parametersItemForButton.setPayload("a");


        List<ParametersItem> parametersItemsListForButton = new ArrayList<>();
        parametersItemsListForButton.add(parametersItemForButton);

        ComponentsItem componentsItemButton = new ComponentsItem();
        componentsItemButton.setType("button");
        componentsItemButton.setSubType("url");

        componentsItemButton.setIndex("0");
        componentsItemButton.setParameters(parametersItemsListForButton);





        componentsItemsList.add(componentsItemHeader);
        componentsItemsList.add(componentsItemForBody);
        //  componentsItemsList.add(componentsItemButton);

        template.setComponents(componentsItemsList);

        customModel.setTemplate(template);

        Gson gson = new Gson();
        String jsonFromModel =  gson.toJson(customModel);

        Log.d("ASASAS", "onClick: "+jsonFromModel);

        return customModel;
    }*/






}