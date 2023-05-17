package com.appdroid.reply99.room;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class ContactHolder {


    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "phoneNumber")
    public String phoneNumber;

    @PrimaryKey(autoGenerate = true)
    private int contactId;

    @ColumnInfo(name = "isIgnoreContact")
    private boolean isIgnoreContact;


    public ContactHolder(String name, String number) {
        this.name = name;
        this.phoneNumber = number;
    }

    public ContactHolder() {
    }

    public ContactHolder(String name, String phoneNumber, int contactId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public boolean isIgnoreContact() {
        return isIgnoreContact;
    }

    public void setIgnoreContact(boolean ignoreContact) {
        isIgnoreContact = ignoreContact;
    }
}
