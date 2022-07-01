package io.mosip.registration.clientmanager.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import androidx.work.Configuration;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.mosip.registration.clientmanager.spi.JobTransactionService;


/**
 * Class for implementing PacketStatusSyncJob service
 *
 * @author Anshul vanawat
 * @since 1.0.0
 */

public abstract class SyncJobServiceBase extends JobService {

    private static final String TAG = SyncJobServiceBase.class.getSimpleName();
    private Thread jobThread;

    @Inject
    JobTransactionService jobTransactionService;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");

        jobThread = new Thread(() -> {
            if (triggerJob(params.getJobId()))
                Log.d(TAG, "Job succeeded");
            else
                Log.d(TAG, "Job failed");
            jobFinished(params, false);
        });
        jobThread.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (jobThread != null && jobThread.isAlive()) {
            jobThread.interrupt();
        }
        return true;
    }

    public abstract boolean triggerJob(int jobId);

    protected void configureBuilder() {
        Configuration.Builder builder = new Configuration.Builder();
        builder.setJobSchedulerJobIdRange(0, 1000);
    }

    protected void logJobTransaction(int jobId, long timeStampInSeconds) {
        try {
            jobTransactionService.LogJobTransaction(jobId, timeStampInSeconds);
        } catch (Exception exception) {
            Log.e(TAG, "Job transaction logging failed");
        }
    }
}