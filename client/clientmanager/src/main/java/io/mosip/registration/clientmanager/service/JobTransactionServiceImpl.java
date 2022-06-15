package io.mosip.registration.clientmanager.service;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.entity.JobTransaction;
import io.mosip.registration.clientmanager.repository.JobTransactionRepository;
import io.mosip.registration.clientmanager.spi.JobTransactionService;

/**
 * @author Anshul vanawat
 * @since 1.0.0
 */

@Singleton
public class JobTransactionServiceImpl implements JobTransactionService {

    private static final String TAG = JobTransactionService.class.getSimpleName();

    private JobTransactionRepository jobTransactionRepository;

    @Inject
    public JobTransactionServiceImpl(JobTransactionRepository jobTransactionRepository) {
        this.jobTransactionRepository = jobTransactionRepository;
    }

    @Override
    public void LogJobTransaction(int jobId, long syncTime) {
        JobTransaction jobTransaction = jobTransactionRepository.getJobTransaction(jobId);
        if (jobTransaction == null) {
            jobTransaction = new JobTransaction(jobId, syncTime);
            jobTransactionRepository.createJobTransaction(jobTransaction);
        } else {
            jobTransactionRepository.updateJobTransaction(jobId, syncTime);
        }
    }

    @Override
    public long getLastSyncTime(int jobId) {
        JobTransaction jobTransaction = jobTransactionRepository.getJobTransaction(jobId);
        if (jobTransaction == null) {
            return 0;
        } else {
            return jobTransaction.getLastSyncTime();
        }
    }
}
