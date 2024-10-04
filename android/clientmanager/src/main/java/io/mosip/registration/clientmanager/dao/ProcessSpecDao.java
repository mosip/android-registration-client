package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.entity.ProcessSpec;

@Dao
public interface ProcessSpecDao {
    @Query("select * from process_spec where id_version=:idVersion and is_active=1 order by order_num asc")
    List<ProcessSpec> getAllProcessSpec(double idVersion);

    @Query("select * from process_spec where id=:processId and id_version=:idVersion and is_active=1 order by order_num asc")
    ProcessSpec getProcessSpecFromProcessId(String processId, double idVersion);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProcessSpec(ProcessSpec processSpec);
}
