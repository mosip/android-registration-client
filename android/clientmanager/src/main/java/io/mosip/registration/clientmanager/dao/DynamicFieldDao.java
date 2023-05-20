package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.clientmanager.entity.DynamicField;

import java.util.List;

@Dao
public interface DynamicFieldDao {

    @Query("select * from dynamic_field where name= :name and lang_code = :langCode and is_active=1")
    DynamicField findDynamicFieldByName(String name, String langCode);

    @Query("select * from dynamic_field where name= :name and is_active=1")
    List<DynamicField> findAllDynamicValuesByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DynamicField dynamicField);
}
