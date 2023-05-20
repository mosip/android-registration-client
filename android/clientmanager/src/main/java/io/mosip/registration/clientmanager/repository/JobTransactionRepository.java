package io.mosip.registration.clientmanager.repository;

import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.dao.JobTransactionDao;
import io.mosip.registration.clientmanager.entity.JobTransaction;

/**
 * DAO class for all the Job transaction related details
 *
 * @author Anshul vanawat
 */

public class JobTransactionRepository {

    private JobTransactionDao jobTransactionDao;

    @Inject
    public JobTransactionRepository(JobTransactionDao jobTransactionDao) {
        this.jobTransactionDao = jobTransactionDao;
    }

    public void createJobTransaction(JobTransaction jobTransaction) {
        jobTransactionDao.insert(jobTransaction);
    }

    public List<JobTransaction> getAllTransactions() {
        return jobTransactionDao.findAll();
    }

    public JobTransaction getJobTransaction(int jobId) {
        return jobTransactionDao.findTransactionByJobId(jobId);
    }

    public void updateJobTransaction(int jobId, Long lastSyncTime) {
        jobTransactionDao.updateJobTransactionLastSyncTime(jobId, lastSyncTime);
    }
}
