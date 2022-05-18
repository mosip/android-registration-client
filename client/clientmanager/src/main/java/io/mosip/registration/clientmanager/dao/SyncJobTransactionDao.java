package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.SyncJobTransaction;

/**
 * DAO class for all the Sync Job transaction related details
 *
 * @author Anshul vanawat
 */
@Dao
public interface SyncJobTransactionDao {

    /**
     * To get all jobs in the List of {@link SyncJobTransaction}
     *
     * @return list of sync jobs transactions
     */
    @Query("SELECT * FROM sync_job_transaction order by id desc")
    List<SyncJobTransaction> findAll();

    /**
     * To get a job in the List of {@link SyncJobTransaction}
     *
     * @return sync job transaction
     */
    @Query("select * from sync_job_transaction where id=:transactionId")
    SyncJobTransaction findTransactionById(String transactionId);

    /**
     * To get a job in the List of {@link SyncJobTransaction}
     *
     * @return sync job transaction
     */
    @Query("select * from sync_job_transaction where job_id=:jobId")
    SyncJobTransaction findTransactionByJobId(String jobId);

    /**
     * To insert sync job list {@link SyncJobTransaction}
     *
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SyncJobTransaction sync_job_transaction);

}
