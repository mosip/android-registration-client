package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.PermittedLocalConfig;

@Dao
public interface PermittedLocalConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PermittedLocalConfig> permittedConfigs);

    @Query("SELECT * FROM permitted_local_config WHERE is_active = 1 AND is_deleted = 0")
    List<PermittedLocalConfig> findByIsActiveTrue();

    @Query("SELECT * FROM permitted_local_config WHERE is_active = 1 AND is_deleted = 0 AND config_type = :configType")
    List<PermittedLocalConfig> findByIsActiveTrueAndType(String configType);

    @Query("SELECT name FROM permitted_local_config WHERE is_active = 1 AND is_deleted = 0 AND config_type = :configType")
    List<String> getPermittedConfigurationNames(String configType);

    @Query("SELECT * FROM permitted_local_config WHERE name = :name AND is_active = 1 AND is_deleted = 0")
    PermittedLocalConfig findByIsActiveTrueAndName(String name);
}
