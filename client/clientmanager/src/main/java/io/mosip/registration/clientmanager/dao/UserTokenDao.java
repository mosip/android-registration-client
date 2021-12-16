package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.UserToken;

@Dao
public interface UserTokenDao {
    @Query("SELECT * FROM Usertoken")
    List<UserToken> getAll();

    @Query("SELECT * FROM Usertoken WHERE id IN (:userIds)")
    public List<UserToken> loadAllByIds(String[] userIds);

    @Query("SELECT * FROM Usertoken WHERE id = :userId")
    public UserToken loadById(String userId);

    @Query("delete from Usertoken")
    public void removeAllUsers();

    @Insert
    public void insertAll(UserToken users);

    @Query("delete from usertoken")
    public void deleteAll();
}
