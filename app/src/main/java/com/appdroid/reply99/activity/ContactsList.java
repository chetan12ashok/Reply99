package com.appdroid.reply99.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;

import com.appdroid.reply99.R;
import com.appdroid.reply99.adapter.ContactsAdepter;
import com.appdroid.reply99.room.ContactHolder;

import java.util.ArrayList;
import java.util.HashSet;

public class ContactsList extends AppCompatActivity {

    RecyclerView recyclerView;
    ContactsAdepter contactsAdepter;

    SearchView searchView;
    ArrayList<ContactHolder> contactList;


    public static  TextView headerName;


    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        recyclerView = findViewById(R.id.recycler_view_contacts_list);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactList = new ArrayList<>();
        searchView = findViewById(R.id.searchView);

        headerName = findViewById(R.id.header);
      /*  contactsAdepter = new ContactsAdepter(ContactsList.this,contactList);
        recyclerView.setAdapter(contactsAdepter);*/
        getContactList();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("PPPPPPPPP", "onQueryTextChange: "+newText);
                contactsAdepter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private void getContactList() {
        ContentResolver cr = getContentResolver();

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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        contactsAdepter = new ContactsAdepter(ContactsList.this,contactList);
                        recyclerView.setAdapter(contactsAdepter);
                        contactsAdepter.notifyDataSetChanged();
                    }
                });

            }
        }
    }

}