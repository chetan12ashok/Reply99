package com.appdroid.reply99.fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appdroid.reply99.R;
import com.appdroid.reply99.WhatsappAccessibilityService;
import com.appdroid.reply99.activity.Dashboard;
import com.appdroid.reply99.activity.settings.SettingsActivity;
import com.appdroid.reply99.adapter.AutoCompleteCountryAdapter;
import com.appdroid.reply99.adapter.SelectedContactsAdepter;
import com.appdroid.reply99.room.ContactHolder;
import com.appdroid.reply99.room.ProductViewModel;
import com.appdroid.reply99.room.UtilityRoomDatabase;
import com.google.android.material.resources.TextAppearance;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


public class MySettings extends Fragment {


    RadioGroup radioGroup, simGroup;
    SharedPreferences.Editor editor,massageSendingEditor;
    SharedPreferences massageSendingSharedPreferences;

    RadioButton sendToAll, blockToAll, sendToOne, sendToOutgoing, sendToIncoming, sendToMissed;
    RadioButton sim1, sim2;
    String oldOne,defaultSim;
    List localList;


    RelativeLayout ignoreContactsCard;
    Dialog dialog;


    ArrayList<ContactHolder> contactList;
    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };


    AutoCompleteCountryAdapter autoCompleteCountryAdapter;
    ProductViewModel productViewModel;

    public static AutoCompleteTextView searchContact;
    ImageView phoneBookBTN;
    TextView okBTN,canselBTN;
    RecyclerView recycler_viewForContacts;
    public static SelectedContactsAdepter contactsAdepter;
    public static List<ContactHolder> listForSelectedContacts;

    CheckBox cbOutgoingCall,cbIncomingCall,cbRejectedMissedCall,cbWhatsapp,cbSMS;
    CardView rulesCard,filterContactsCard,contactBaseReplyCard,settingCard;

    TextView rulesIndicator,ignoreContactsIndicator,filterContactIndicator;

    SwitchMaterial switchForContactBaseReply,notificationSwitch,switchWAReply;

    int count;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 122345){
            if(resultCode == RESULT_OK) {
                List<ContactResult> results = MultiContactPicker.obtainResult(data);

                for (int i =0;i<results.size();i++){

                    ContactHolder contactHolder = new ContactHolder();
                    contactHolder.setName(results.get(i).getDisplayName());
                    contactHolder.setPhoneNumber(results.get(i).getPhoneNumbers().get(0).getNumber());
                    contactHolder.setIgnoreContact(true);
                    listForSelectedContacts.add(contactHolder);
                    //contactList
                    Log.d("MyTag", results.get(i).getPhoneNumbers().get(0).getNumber());
                }

                contactsAdepter.notifyDataSetChanged();

            } else if(resultCode == RESULT_CANCELED){
                System.out.println("User closed the picker without selecting items.");
            }
        }else if (requestCode == 0011){


            Log.d("AAAAA", "onActivityResult:call ");
            if(resultCode == RESULT_OK){
                Toast.makeText(getContext(), "Accepted...", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(), "Denile...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_settings, container, false);

        settingCard = view.findViewById(R.id.settingCard);
        
        editor = getContext().getSharedPreferences("defaultMassages", Context.MODE_PRIVATE).edit();
        massageSendingEditor = getContext().getSharedPreferences("SendingFlag",MODE_PRIVATE).edit();

        sendToAll = view.findViewById(R.id.sendToAll);
        blockToAll = view.findViewById(R.id.blockToAll);
        sendToOne = view.findViewById(R.id.sendToOne);
        sendToOutgoing = view.findViewById(R.id.sendToOutgoing);
        sendToIncoming = view.findViewById(R.id.sendToIncoming);
        sendToMissed = view.findViewById(R.id.sendToMissed);
        contactList = new ArrayList<>();

        contactBaseReplyCard   = view.findViewById(R.id.contactBaseReplyCard);
        switchForContactBaseReply = view.findViewById(R.id.switchForContactBaseReply);
        switchWAReply = view.findViewById(R.id.switchWAReply);
        notificationSwitch = view.findViewById(R.id.notificationSwitch);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("defaultMassages",Context.MODE_PRIVATE);
        oldOne =  sharedPreferences.getString("sendingFlag","sendToAll");
        defaultSim =  sharedPreferences.getString("simFlg","sim1");

        if (oldOne.equals("sendToAll")){
            sendToAll.setChecked(true);

        }else if (oldOne.equals("blockToAll")){
            blockToAll.setChecked(true);
        }else if (oldOne.equals("sendToOne")){
            sendToOne.setChecked(true);
        }else if (oldOne.equals("sendToOutgoing")){
            sendToOutgoing.setChecked(true);
        }else if (oldOne.equals("sendToIncoming")){
            sendToIncoming.setChecked(true);
        }else if (oldOne.equals("sendToMissed")){
            sendToMissed.setChecked(true);
        }



        SubscriptionManager localSubscriptionManager = SubscriptionManager.from(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }


        localList= localSubscriptionManager.getActiveSubscriptionInfoList();


        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.contact_list_dialog_item);
        searchContact = dialog.findViewById(R.id.searchContact);
        phoneBookBTN = dialog.findViewById(R.id.phoneBookBTN);

        recycler_viewForContacts = dialog.findViewById(R.id.recycler_viewForContacts);
        recycler_viewForContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_viewForContacts.hasFixedSize();
        listForSelectedContacts = new ArrayList<>();
        contactsAdepter = new SelectedContactsAdepter(getContext(),listForSelectedContacts);


        cbRejectedMissedCall = view.findViewById(R.id.cbRejectedMissedCall);
        cbIncomingCall = view.findViewById(R.id.cbIncomingCall);
        cbOutgoingCall = view.findViewById(R.id.cbOutgoingCall);

        cbSMS = view.findViewById(R.id.cbSMS);
        cbWhatsapp = view.findViewById(R.id.cbWhatsapp);

        rulesCard = view.findViewById(R.id.rulesCard);
        rulesIndicator = view.findViewById(R.id.rulesIndicator);
        ignoreContactsIndicator = view.findViewById(R.id.ignoreContactsIndicator);
        filterContactIndicator = view.findViewById(R.id.filterContactIndicator);

        filterContactsCard = view.findViewById(R.id.filterContactsCard);


        ignoreContactsCard = view.findViewById(R.id.ignoreContactsCard);
        setFlags();

        setUpDialogForGettingFilterdContacts();

        okBTN = dialog.findViewById(R.id.okBTN);
        canselBTN = dialog.findViewById(R.id.cancel_button);


        settingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }
        });


        simGroup = view.findViewById(R.id.simRadioGroup);
        sim1 = view.findViewById(R.id.simOne);
        sim2 = view.findViewById(R.id.simTwo);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
               switch (i){
                   case R.id.sendToAll :

                       editor.putString("sendingFlag","sendToAll");
                       editor.apply();
                       break;
                   case R.id.blockToAll :

                       editor.putString("sendingFlag","blockToAll");
                       editor.apply();
                       break;
                   case R.id.sendToOne :


                       editor.putString("sendingFlag","sendToOne");
                       editor.apply();
                       break;
                   case R.id.sendToOutgoing :

                       editor.putString("sendingFlag","sendToOutgoing");
                       editor.apply();
                       break;
                   case R.id.sendToIncoming :


                       editor.putString("sendingFlag","sendToIncoming");
                       editor.apply();
                       break;
                   case R.id.sendToMissed :

                       editor.putString("sendingFlag","sendToMissed");
                       editor.apply();
                       break;
               }
            }
        });




        switchForContactBaseReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    massageSendingEditor.putString("sendWithFilterContact","true");
                    ignoreContactsCard.setVisibility(View.VISIBLE);

                    if (count>0){
                        filterContactsCard.setVisibility(View.VISIBLE);
                    }else {
                        filterContactsCard.setVisibility(View.GONE);
                    }


                }else {
                    massageSendingEditor.putString("sendWithFilterContact","false");
                    ignoreContactsCard.setVisibility(View.GONE);
                    filterContactsCard.setVisibility(View.GONE);
                }

                massageSendingEditor.apply();
            }
        });

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (!isAccessibilityOn(getContext(),WhatsappAccessibilityService.class)){
                        massageSendingEditor.putString("notificationFlag","true");
                    }
                }else {
                    massageSendingEditor.putString("notificationFlag","false");
                }
                massageSendingEditor.apply();
            }
        });



      /*  switchWAReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                Log.d("onCheckedChanged", "onCheckedChanged: +"+isAccessibilityOn(getActivity(), WhatsappAccessibilityService.class));

                    if (b){

                        massageSendingEditor.putString("whatsappFlag","true");
                    *//*    if (!isAccessibilityOn(getActivity(), WhatsappAccessibilityService.class)){


                            Dialog dialog  = new Dialog(getContext());
                            dialog.setContentView(R.layout.whatsapp_permition_dialog);
                            TextView decline,accept;
                            decline = dialog.findViewById(R.id.decline);
                            accept = dialog.findViewById(R.id.Accept);

                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    switchWAReply.setChecked(false);
                                }
                            });



                            accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    switchWAReply.setChecked(false);
                                }
                            });

                            decline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    switchWAReply.setChecked(false);
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();


                        }else{
                            massageSendingEditor.putString("whatsappFlag","true");
                        }*//*


                    }else {
                        massageSendingEditor.putString("whatsappFlag","false");
                    }
                    massageSendingEditor.apply();

            }
        });
        */
        cbWhatsapp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    massageSendingEditor.putString("whatsappFlag","true");
                }else {
                    massageSendingEditor.putString("whatsappFlag","false");
                }
                massageSendingEditor.apply();
            }
        });

        cbSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    massageSendingEditor.putString("smsFlag","true");
                }else {
                    massageSendingEditor.putString("smsFlag","false");
                }
                massageSendingEditor.apply();
            }
        });



        if (localList.size()==1){
            sim2.setVisibility(View.GONE);
            sim1.setChecked(true);
            editor.putString("simFlg","sim1");
            editor.apply();
        }
        productViewModel  = new ViewModelProvider(this).get(ProductViewModel.class);

        if (defaultSim.equals("sim1")){
            sim1.setChecked(true);
            sim2.setChecked(false);
        }else if (defaultSim.equals("sim2")){
            sim2.setChecked(true);
            sim1.setChecked(false);
        }


        simGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.simOne:
                        editor.putString("simFlg","sim1");
                        editor.apply();
                        break;
                    case R.id.simTwo:
                        editor.putString("simFlg","sim2");
                        editor.apply();
                        break;
                }
            }
        });

        productViewModel.getContactsForIgnoreList(true).observe(getViewLifecycleOwner(), new Observer<List<ContactHolder>>() {
            @Override
            public void onChanged(List<ContactHolder> contactHolders) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        count = contactHolders.size();
                        if (contactHolders.size()>0){

                            ignoreContactsIndicator.setText(contactHolders.size()+" contacts");
                            ignoreContactsIndicator.setTextColor(getContext().getResources().getColor(R.color.primary));
                            ignoreContactsIndicator.setTypeface(ignoreContactsIndicator.getTypeface(), Typeface.BOLD_ITALIC);

                            if (switchForContactBaseReply.isChecked()){

                                filterContactsCard.setVisibility(View.VISIBLE);
                            }



                        }else {
                            ignoreContactsIndicator.setText("No contacts");
                            filterContactIndicator.setText("Not Selected");
                            massageSendingEditor.putString("filterContact","default");
                            filterContactsCard.setVisibility(View.GONE);
                        }

                        massageSendingEditor.apply();
                    }
                });
            }
        });

        ignoreContactsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getContactList();

                dialog.show();
                getSelectedContacts();

            }
        });

        phoneBookBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MultiContactPicker.Builder(MySettings.this) //Activity/fragment context

                        .hideScrollbar(false) //Optional - default: false
                        .showTrack(true) //Optional - default: true
                        .searchIconColor(Color.WHITE) //Option - default: White
                        .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                        .handleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleTextColor(Color.WHITE) //Optional - default: White
                        .setTitleText("Select Contacts") //Optional - default: Select Contacts
                        .setLoadingType(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                        .limitToColumn(LimitColumn.PHONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                        .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out) //Optional - default: No animation overrides
                        .showPickerForResult(122345);
            }
        });

        okBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  MySettings.InsertAsnkTask insertAsnkTask = new MySettings.InsertAsnkTask(getContext());
                insertAsnkTask.execute(listForSelectedContacts);*/

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UtilityRoomDatabase.getInstance(getContext()).massagesDao().deleteContactsTableForIgnore(true);
                        ((Activity)getContext()).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (listForSelectedContacts.size()>0){
                                    massageSendingEditor.putString("ignoreContacts","true");
                                }else {
                                    massageSendingEditor.putString("ignoreContacts","false");
                                }
                                massageSendingEditor.apply();
                                dialog.dismiss();
                                MySettings.InsertAsnkTask insertAsnkTask = new MySettings.InsertAsnkTask(getContext());
                                insertAsnkTask.execute(listForSelectedContacts);
                            }
                        });
                    }
                }).start();

            //    startActivity(new Intent(getContext(), ContactsList.class));
            }
        });

        canselBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        cbRejectedMissedCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    massageSendingEditor.putString("missedCall","true");
                }else {
                    massageSendingEditor.putString("missedCall","false");
                }

                massageSendingEditor.apply();
            }
        });


        cbIncomingCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    massageSendingEditor.putString("incomingCall","true");
                }else {
                    massageSendingEditor.putString("incomingCall","false");
                }

                massageSendingEditor.apply();
            }
        });


        cbOutgoingCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    massageSendingEditor.putString("outgoingCall","true");
                }else {
                    massageSendingEditor.putString("outgoingCall","false");
                }

                massageSendingEditor.apply();
            }
        });

        rulesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rulesCardFunctionality();
            }
        });

        filterContactsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterContactsFunctionality();
            }
        });



        return view;
    }

    private void setUpDialogForGettingFilterdContacts() {
        Dialog selectContactFilterDialog = new Dialog(getContext());
        selectContactFilterDialog.setContentView(R.layout.contact_list_dialog_item);

    }


    private void setFlags() {
        massageSendingSharedPreferences = getContext().getSharedPreferences("SendingFlag",MODE_PRIVATE);
        String incomingCallFlag = massageSendingSharedPreferences.getString("incomingCall","true");
        String outgoingCallFlag = massageSendingSharedPreferences.getString("outgoingCall","true");
        String missedCallFlag = massageSendingSharedPreferences.getString("missedCall","true");

        String rules = massageSendingSharedPreferences.getString("rule","noRule");
        String filterContactFlag = massageSendingSharedPreferences.getString("filterContact","everyone");


        String notificationFlag = massageSendingSharedPreferences.getString("notificationFlag","true");



        String sendWithFilterContactFlag = massageSendingSharedPreferences.getString("sendWithFilterContact","false");

        String whatsappFlag = massageSendingSharedPreferences.getString("whatsappFlag","true");
        String smsFlag = massageSendingSharedPreferences.getString("smsFlag","true");


        if (notificationFlag.equals("true")){
            notificationSwitch.setChecked(true);
        }else {
            notificationSwitch.setChecked(false);
        }


        if (!smsFlag.equals("false")){
            cbSMS.setChecked(true);
        }else {
            cbSMS.setChecked(false);
        }



        if (!whatsappFlag.equals("false")){
        /*if (isAccessibilityOn(getContext(),WhatsappAccessibilityService.class)){
            switchWAReply.setChecked(true);
        }else {
            switchWAReply.setChecked(false);
        }*/
    //    switchWAReply.setChecked(true);


        cbWhatsapp.setChecked(true);


    }else {
        cbWhatsapp.setChecked(false );
        //switchWAReply.setChecked(false);
    }





        if (sendWithFilterContactFlag.equals("true")){
            ignoreContactsCard.setVisibility(View.VISIBLE);
            filterContactsCard.setVisibility(View.VISIBLE);
            switchForContactBaseReply.setChecked(true);
        }else {
            ignoreContactsCard.setVisibility(View.GONE);
            filterContactsCard.setVisibility(View.GONE);
            switchForContactBaseReply.setChecked(false);
        }


        if (filterContactFlag.equals("notBlockContacts")){

            filterContactIndicator.setText("Everyone except selected contact");

        }else if (filterContactFlag.equals("onlyBlockContacts")){

            filterContactIndicator.setText("Nobody except selected contacts");

        }else {
            filterContactIndicator.setText("Not Selected");
        }

                if (filterContactFlag.equals("noRule")){
                    rulesIndicator.setText("No rule");
                }else if (rules.equals("sendToOne")){
                    rulesIndicator.setText("Once a day");
                }

                if (rules.equals("noRule")){
                    rulesIndicator.setText("No rule");
                }else if (rules.equals("sendToOne")){
                    rulesIndicator.setText("Once a day");
                }

        if (incomingCallFlag.equals("true")){
            cbIncomingCall.setChecked(true);
        }else {
            cbIncomingCall.setChecked(false);
        }

        if (outgoingCallFlag.equals("true")){
            cbOutgoingCall.setChecked(true);
        }else {
            cbOutgoingCall.setChecked(false);
        }

        if (missedCallFlag.equals("true")){
            cbRejectedMissedCall.setChecked(true);
        }else {
            cbRejectedMissedCall.setChecked(false);
        }




    }

    private void filterContactsFunctionality() {
        Dialog filterContactsDialog = new Dialog(getContext());

        filterContactsDialog.setContentView(R.layout.filter_contacts_dialog);
        RadioButton everyoneExceptSelectedContact,nobodyExceptSelectedContacts;
        TextView save,cancel;

        save = filterContactsDialog.findViewById(R.id.save);
        cancel = filterContactsDialog.findViewById(R.id.cancel);

        everyoneExceptSelectedContact = filterContactsDialog.findViewById(R.id.everyoneExceptSelectedContact);
        nobodyExceptSelectedContacts = filterContactsDialog.findViewById(R.id.nobodyExceptSelectedContacts);


        String filterContactFlag = massageSendingSharedPreferences.getString("filterContact","default");
        if (filterContactFlag.equals("notBlockContacts")){
            everyoneExceptSelectedContact.setChecked(true);
        }else if (filterContactFlag.equals("onlyBlockContacts")){
            nobodyExceptSelectedContacts.setChecked(true);
        }




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (everyoneExceptSelectedContact.isChecked()){
                    massageSendingEditor.putString("filterContact","notBlockContacts");
                    massageSendingEditor.apply();

                    filterContactsDialog.dismiss();

                    filterContactIndicator.setText("Everyone except selected contact");
                }else if (nobodyExceptSelectedContacts.isChecked()){
                    massageSendingEditor.putString("filterContact","onlyBlockContacts");
                    massageSendingEditor.apply();
                    filterContactsDialog.dismiss();

                    filterContactIndicator.setText("Nobody except selected contacts");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterContactsDialog.dismiss();
            }
        });


        filterContactsDialog.show();

    }

    private boolean isAccessibilityOn (Context context, Class<? extends AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName () + "/" + clazz.getCanonicalName ();
        try {
            accessibilityEnabled = Settings.Secure.getInt (context.getApplicationContext ().getContentResolver (), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {  }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter (':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString (context.getApplicationContext ().getContentResolver (), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString (settingValue);
                while (colonSplitter.hasNext ()) {
                    String accessibilityService = colonSplitter.next ();

                    if (accessibilityService.equalsIgnoreCase (service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private void rulesCardFunctionality() {

        Dialog rulesDialog = new Dialog(getContext());
        rulesDialog.setContentView(R.layout.reply_rule_dialog);
        RadioButton noRule,once;
        TextView save,cancel;

        noRule = rulesDialog.findViewById(R.id.noRule);
        once = rulesDialog.findViewById(R.id.once);

        save = rulesDialog.findViewById(R.id.save);
        cancel = rulesDialog.findViewById(R.id.cancel);


        String rules = massageSendingSharedPreferences.getString("rule","noRule");
        if (rules.equals("noRule")){
            noRule.setChecked(true);
        }else if (rules.equals("sendToOne")){
            once.setChecked(true);
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noRule.isChecked()){
                    massageSendingEditor.putString("rule","noRule");
                    massageSendingEditor.apply();

                    rulesDialog.dismiss();

                    rulesIndicator.setText("No rule");
                }else if (once.isChecked()){
                    massageSendingEditor.putString("rule","sendToOne");
                    massageSendingEditor.apply();
                    rulesDialog.dismiss();

                    rulesIndicator.setText("Once a day");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rulesDialog.dismiss();
            }
        });

        rulesDialog.show();

    }


    private void getSelectedContacts() {
        Thread  thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                List<ContactHolder> contactsListForSelectedIgnore = UtilityRoomDatabase.getInstance(getContext())
                        .massagesDao()
                        .getContactsList(true);
                Collections.reverse(contactsListForSelectedIgnore);

                listForSelectedContacts.clear();

                listForSelectedContacts.addAll(contactsListForSelectedIgnore);

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        recycler_viewForContacts.setAdapter(contactsAdepter);
                        // Stuff that updates the UI
                        contactsAdepter.notifyDataSetChanged();
                    }
                });


            }
        });
        thread.start();
    }

    private void getContactList() {
        ContentResolver cr = getContext().getContentResolver();

        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            HashSet<String> mobileNoSet = new HashSet<String>();
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    number = number.replace(" ", "");
                    if (!mobileNoSet.contains(number)) {
                        contactList.add(new ContactHolder(name, number));
                        mobileNoSet.add(number);
                        Log.d("hvy", "onCreaterrView  Phone Number: name = " + name
                                + " No = " + number);
                    }
                }


            } finally {
                cursor.close();

                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("SSSSAA", "run: "+contactList.size());
                        autoCompleteCountryAdapter = new AutoCompleteCountryAdapter(getContext(), contactList);
                        searchContact.setAdapter(autoCompleteCountryAdapter);
                    }
                });

            }
        }
    }

    public static class InsertAsnkTask extends AsyncTask<List<ContactHolder>,Void,Void> {
        Context context;
        public InsertAsnkTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(List<ContactHolder>... massage) {
            UtilityRoomDatabase.getInstance(context)
                    .massagesDao()
                    .insertContactForIgnoreList(massage[0]);

            return null;
        }
    }


}