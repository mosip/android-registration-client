package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;

import java.util.List;

@Dao
public interface RegistrationCenterDao {

    @Query("select * from registration_center where id = :centerId")
    List<RegistrationCenter> getAllRegistrationCentersById(String centerId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RegistrationCenter registrationCenter);
}
