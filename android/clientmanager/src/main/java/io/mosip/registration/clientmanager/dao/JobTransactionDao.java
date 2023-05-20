package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.JobTransaction;

/**
 * DAO class for all the Job transaction related details
 *
 * @author Anshul vanawat
 */
@Dao
public interface JobTransactionDao {

    /**
     * To get all jobs in the List of {@link JobTransaction}
     *
     * @return list of sync jobs transactions
     */
    @Query("SELECT * FROM job_transaction order by job_id desc")
    List<JobTransaction> findAll();

    /**
     * To get a job in the List of {@link JobTransaction}
     *
     * @return sync job transaction
     */
    @Query("select * from job_transaction where job_id=:jobId")
    JobTransaction findTransactionByJobId(int jobId);

    /**
     * To insert sync job transaction {@link JobTransaction}
     *
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JobTransaction sync_job_transaction);

    @Query("Update job_transaction set last_sync_time = :lastSyncTime where job_id = :jobId")
    void updateJobTransactionLastSyncTime(int jobId, Long lastSyncTime);
}
