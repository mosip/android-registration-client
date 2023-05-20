package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.SyncJobDef;

/**
 * DAO class for all the Sync Job related details
 *
 * @author Anshul vanawat
 */
@Dao
public interface SyncJobDefDao {

    /**
     * To get all jobs in the List of {@link SyncJobDef}
     *
     * @return list of sync jobs
     */
    @Query("SELECT * FROM sync_job_def where is_deleted == 0 or is_deleted is null order by id desc")
    List<SyncJobDef> findAll();

    /**
     * To get a job in the List of {@link SyncJobDef}
     *
     * @return sync job
     */
    @Query("select * from sync_job_def where is_deleted == 0 or is_deleted is null and id=:jobId")
    SyncJobDef findOneById(String jobId);

    /**
     * To get all the List of active {@link SyncJobDef}
     *
     * @return list active sync jobs
     */
    @Query("select * from sync_job_def where is_deleted == 0 or is_deleted is null and is_active=:isActive")
    List<SyncJobDef> findAllByActiveStatus(Boolean isActive);

    /**
     * Update all the Syncjobs available in the {@link SyncJobDef} list
     *
     * @param jobId    job to be updated
     * @param isActive active or deactivate
     * @return updated syncJobs
     */
    @Query("Update sync_job_def set is_active = :isActive where id = :jobId")
    void updateJobActiveStatus(String jobId, boolean isActive);

    /**
     * To insert sync job list {@link SyncJobDef}
     *
     * @return list active sync jobs
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SyncJobDef syncJobDef);

}
