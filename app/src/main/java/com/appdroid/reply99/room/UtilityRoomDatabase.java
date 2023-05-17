package com.appdroid.reply99.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MassageHolder.class,ConversionHolder.class, ContactHolder.class},version = 1)
public abstract class UtilityRoomDatabase  extends RoomDatabase {
    public abstract SavedMassagesDao massagesDao();

    private static volatile  UtilityRoomDatabase INSTANCE;
    public static UtilityRoomDatabase getInstance(Context context){
        if (INSTANCE == null){
            synchronized (UtilityRoomDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    UtilityRoomDatabase.class,"database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}