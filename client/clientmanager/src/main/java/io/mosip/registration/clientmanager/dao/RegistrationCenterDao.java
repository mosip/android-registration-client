package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;

@Dao
public interface RegistrationCenterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RegistrationCenter registrationCenter);
}
