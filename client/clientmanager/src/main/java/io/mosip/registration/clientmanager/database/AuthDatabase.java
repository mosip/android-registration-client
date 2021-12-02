package io.mosip.registration.clientmanager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import io.mosip.registration.clientmanager.database.entities.UserToken;
import io.mosip.registration.clientmanager.database.objectdao.UserTokenDao;

@Database(entities = {UserToken.class}, version = 1)
public abstract class AuthDatabase extends RoomDatabase {
    //    instance of database
    private static AuthDatabase INSTANCE;

    //    DAOs
    public abstract UserTokenDao userTokenDao();

    //    get database instance
    public static AuthDatabase getDatabase(Context context){
        if(INSTANCE==null){
//            might need to change
            INSTANCE= Room.databaseBuilder(context, AuthDatabase.class,"userdatabase").allowMainThreadQueries().build();
        }

        return INSTANCE;
    }


    //    destroy DB instance
    public static void destroyDB(){
        INSTANCE=null;
    }
}


