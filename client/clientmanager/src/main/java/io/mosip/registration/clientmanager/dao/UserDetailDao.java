package io.mosip.registration.clientmanager.dao;

import androidx.room.*;
import io.mosip.registration.clientmanager.entity.UserDetail;

import java.util.List;

@Dao
public abstract class UserDetailDao {

    @Query("select * from user_detail")
    public abstract List<UserDetail> getAllUserDetails();

    @Query("delete from user_detail")
    public abstract void deleteAllUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAllUsers(List<UserDetail> users);

    @Transaction
    public void truncateAndInsertAll(List<UserDetail> users) {
        deleteAllUsers();
        insertAllUsers(users);
    }

}
