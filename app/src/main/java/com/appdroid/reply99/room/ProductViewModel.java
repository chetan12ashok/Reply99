package com.appdroid.reply99.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    UtilityRoomDatabase roomDatabase;
    public ProductViewModel(@NonNull Application application) {
        super(application);
        roomDatabase = UtilityRoomDatabase.getInstance(application.getApplicationContext());

    }
    public LiveData<List<MassageHolder>> getCardsProductsCounts(){
        return roomDatabase.massagesDao().getAllUpdatedMassages();
    }

    public LiveData<List<ContactHolder>> getContactsForIgnoreList(boolean flag){
        return roomDatabase.massagesDao().getContactsForIgnoreList(flag);
    }

    public LiveData<List<MassageHolder>> getWhatsAppMassagesOnly(boolean flag){
        return roomDatabase.massagesDao().getWhatsAppMassages(flag);
    }







}
