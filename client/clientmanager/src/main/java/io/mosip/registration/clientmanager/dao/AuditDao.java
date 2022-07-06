package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.Audit;

/**
 * The Audit Entity class with required fields to be captured and recorded
 *
 * @author Sahil Gaikwad
 * @author Anshul Vanawat
 */

@Dao
public interface AuditDao {

    @Query("SELECT * FROM app_audit_log where log_dtimes > :fromLogDateTime order by action_dtimes asc")
    List<Audit> getAll(long fromLogDateTime);

    @Insert
    void insert(Audit audit);

    @Query("delete from app_audit_log where action_dtimes < :tillLogDateTime")
    void deleteAll(long tillLogDateTime);
}
