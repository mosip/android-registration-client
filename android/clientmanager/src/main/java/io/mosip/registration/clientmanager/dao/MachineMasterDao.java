package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import io.mosip.registration.clientmanager.entity.MachineMaster;

@Dao
public interface MachineMasterDao {

    @Query("select * from machine_master where lower(name) = lower(:machine)")
    MachineMaster findMachineByName(String machine);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MachineMaster machineMaster);

    @Query("UPDATE machine_master SET reg_center_id = (:regCenterId) WHERE lower(name) = lower(:machineName)")
    void updateMachine(String machineName, String regCenterId);
}
