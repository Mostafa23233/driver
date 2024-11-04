package com.darbaast.driver.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.darbaast.driver.data.model.Token;


@Database(version = 1,entities = {Token.class},exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;
    public static AppDatabase getAppDatabase(Context context){
        if(appDatabase==null){
            appDatabase= Room.databaseBuilder(context,AppDatabase.class,"db_darbast_driver").
                    allowMainThreadQueries().build();
        }

        return appDatabase;
    }
    public abstract TokenDao getTokenDao();
}
