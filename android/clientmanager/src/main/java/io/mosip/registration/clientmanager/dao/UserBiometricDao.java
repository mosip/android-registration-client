package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.UserBiometric;
import io.mosip.registration.clientmanager.entity.UserDetail;

@Dao
public interface UserBiometricDao {

    @Query("select * from user_biometric where usr_id=:id")
    List<UserBiometric> findByUsrId(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserBiometric userBiometric);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllUserBiometrics(List<UserBiometric> userBiometrics);

    @Query("delete from user_biometric where usr_id=:userId")
    void deleteByUsrId(String userId);
}
