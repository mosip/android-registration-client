package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.RegistrationCenter;

@Dao
public interface RegistrationCenterDao {

    @Query("select * from registration_center where id = :centerId")
    List<RegistrationCenter> getAllRegistrationCentersById(String centerId);

    @Query("select * from registration_center where id = :centerId and lang_code = :selectedLangCode")
    RegistrationCenter getRegistrationCenterByCenterIdAndLangCode(String centerId, String selectedLangCode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RegistrationCenter registrationCenter);
}
