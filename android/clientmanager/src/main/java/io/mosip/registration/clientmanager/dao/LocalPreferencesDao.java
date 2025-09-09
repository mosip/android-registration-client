package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.mosip.registration.clientmanager.entity.LocalPreferences;

@Dao
public interface LocalPreferencesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LocalPreferences localPreference);

    @Update
    void update(LocalPreferences localPreference);

    @Query("SELECT * FROM local_preferences WHERE is_deleted = 0 AND config_type = :configType")
    List<LocalPreferences> findByIsDeletedFalseAndConfigType(String configType);

    @Query("SELECT * FROM local_preferences WHERE is_deleted = 0 AND name = :name")
    LocalPreferences findByIsDeletedFalseAndName(String name);

    @Query("SELECT * FROM local_preferences WHERE is_deleted = 0")
    List<LocalPreferences> findByIsDeletedFalse();

    @Query("UPDATE local_preferences SET is_deleted = 1, del_dtimes = :delDtimes WHERE name = :name")
    void softDeleteByName(String name, Long delDtimes);
}
