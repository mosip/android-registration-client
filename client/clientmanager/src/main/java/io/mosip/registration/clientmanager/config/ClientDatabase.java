package io.mosip.registration.clientmanager.config;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import io.mosip.registration.clientmanager.entity.UserToken;
import io.mosip.registration.clientmanager.dao.UserTokenDao;

@Database(entities = {UserToken.class}, version = 1, exportSchema = false)
public abstract class ClientDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "regclient";
    private static ClientDatabase INSTANCE;
    public abstract UserTokenDao userTokenDao();


    public static ClientDatabase getDatabase(Context context){
        if(INSTANCE==null) {
            synchronized (INSTANCE) {
                INSTANCE = Room.databaseBuilder(context, ClientDatabase.class, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
    }

    public static void destroyDB(){
        INSTANCE=null;
    }
}


