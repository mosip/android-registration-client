package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import io.mosip.registration.clientmanager.entity.UserPassword;

@Dao
public abstract class UserPasswordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertUserPassword(UserPassword userPassword);

    @Query("select * from user_pwd where usr_id = :id")
    public abstract UserPassword getUserPassword(String id);

    @Query("delete from user_pwd where usr_id= :id")
    public abstract void deleteUserPassword(String id);
}
