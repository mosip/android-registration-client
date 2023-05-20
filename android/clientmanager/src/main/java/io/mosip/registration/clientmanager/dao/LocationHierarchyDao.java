package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.clientmanager.entity.LocationHierarchy;

@Dao
public interface LocationHierarchyDao {

    @Query("select level from location_hierarchy where level_name = :levelName limit 1")
    Integer getHierarchyLevelFromName(String levelName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LocationHierarchy locationHierarchy);

}
