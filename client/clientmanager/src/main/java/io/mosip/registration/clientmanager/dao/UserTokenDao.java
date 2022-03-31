package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.UserToken;

@Dao
public interface UserTokenDao {
    @Query("SELECT * FROM user_token")
    List<UserToken> getAll();

    @Query("SELECT * FROM user_token WHERE username IN (:userIds)")
    public List<UserToken> loadAllByIds(String[] userIds);

    @Query("SELECT * FROM user_token WHERE username = :userId")
    public UserToken loadById(String userId);

    @Query("delete from user_token")
    public void removeAllUsers();

    @Insert
    public void insertAll(UserToken users);

    @Query("delete from user_token")
    public void deleteAll();
}
