package io.mosip.registration.clientmanager.jobservice;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import androidx.work.Configuration;

import dagger.android.AndroidInjection;

/**
 * Class for implementing PacketStatusSyncJob service
 *
 * @author Anshul vanawat
 * @since 1.0.0
 */

public abstract class SyncJobServiceBase extends JobService {

    private static final String TAG = SyncJobServiceBase.class.getSimpleName();
    private Thread jobThread;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");

        jobThread = new Thread(() -> {
            if (triggerJob())
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

    public abstract boolean triggerJob();

    protected void configureBuilder() {
        Configuration.Builder builder = new Configuration.Builder();
        builder.setJobSchedulerJobIdRange(0, 1000);
    }
}