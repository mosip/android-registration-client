package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.UserBiometric;

@Dao
public interface UserBiometricDao {

    @Query("select * from user_biometric where usr_id=:id")
    List<UserBiometric> findByUsrId(String id);

    /**
     * Retrieves list of operator biometrics from storage  for biometric type
     * @param biometricType biometric type
     * @return {@link List<UserBiometric>}
     */
    @Query("select * from user_biometric where LOWER(bmtyp_code) = LOWER(:biometricType)")
    List<UserBiometric> findAll(String biometricType);

    /**
     * Retrieves list of operator biometrics from storage for biometric type, excluding current user
     * @param biometricType biometric type
     * @param userId current user ID to exclude
     * @return {@link List<UserBiometric>}
     */
    @Query("select * from user_biometric where LOWER(bmtyp_code) = LOWER(:biometricType) and usr_id != :userId")
    List<UserBiometric> findAllOtherUsersByBiometricType(String biometricType, String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserBiometric userBiometric);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllUserBiometrics(List<UserBiometric> userBiometrics);

    @Query("delete from user_biometric where usr_id=:userId")
    void deleteByUsrId(String userId);
}
