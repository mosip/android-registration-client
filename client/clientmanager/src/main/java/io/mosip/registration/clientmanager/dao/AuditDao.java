package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.Audit;

@Dao
public interface AuditDao {

    @Query("SELECT * FROM app_audit_log")
    List<Audit> getAll();

    @Insert
    public void insert(Audit audit);

    @Query("delete from app_audit_log")
    public void deleteAll();
}
