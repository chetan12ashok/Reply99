package com.appdroid.reply99.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.appdroid.reply99.room.ContactHolder;

public class MainViewModel extends ViewModel {
    MutableLiveData<ContactHolder> mutableLiveData = new MutableLiveData<>();

    String size;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public MutableLiveData<ContactHolder> getMutableLiveData() {
        return mutableLiveData;
    }
}
