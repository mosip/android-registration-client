package io.mosip.registration.clientmanager.dao;

import androidx.room.*;
import io.mosip.registration.clientmanager.entity.UserDetail;

import java.util.List;

@Dao
public abstract class UserDetailDao {

    @Query("select * from user_detail")
    public abstract List<UserDetail> getAllUserDetails();

    @Query("select * from user_detail where id = :id and is_active=1")
    public abstract UserDetail getUserDetail(String id);

    @Query("select count(*) from user_detail where is_active=1")
    public abstract int getUserDetailCount();

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
