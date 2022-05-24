package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.clientmanager.entity.Location;

import java.util.List;

@Dao
public interface LocationDao {

    @Query("select name from location where parent_loc_code=:parentLocCode and lang_code=:langCode and is_active=1")
    List<String> findAllLocationByParentLocCode(String parentLocCode, String langCode);

    @Query("select name from location where parent_loc_code is null and lang_code=:langCode and is_active=1")
    List<String> findParentLocation(String langCode);

    @Query("select name from location where hierarchy_level = :level and lang_code=:langCode and is_active=1")
    List<String> findAllLocationByHierarchyLevel(int level, String langCode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Location location);
}
