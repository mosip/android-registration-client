package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.mosip.registration.clientmanager.entity.GlobalParam;

@Dao
public interface GlobalParamDao {

    @Query("select value from global_param where id=:id and status=1")
    String getGlobalParam(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGlobalParam(GlobalParam globalParam);
}
