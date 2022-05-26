package io.mosip.registration.app.util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.jobservice.ConfigDataSyncJob;
import io.mosip.registration.clientmanager.jobservice.PacketStatusSyncJob;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.spi.PacketService;
import kotlin.NotImplementedError;

public class JobServiceHelper {

    private static final String TAG = JobServiceHelper.class.getSimpleName();
    Context context;
    JobScheduler jobScheduler;
    PacketService packetService;
    JobTransactionService jobTransactionService;
    DateFormat dateFormat;
    DateFormat timeFormat;

    public JobServiceHelper(Context context, JobScheduler jobScheduler, PacketService packetService, JobTransactionService jobTransactionService) {
        this.context = context;
        this.jobScheduler = jobScheduler;
        this.packetService = packetService;
        this.jobTransactionService = jobTransactionService;

        dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
        timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    }

    /**
     *
     * @param jobId : Id of the job to be scheduled.
     * @param apiName : name of the service to be scheduled.
     * @param syncFreq : syncFreq (Optional). Null or empty to schedule only once
     * @return 1 for success and 0 for failure
     */
    public int scheduleJob(int jobId, String apiName, String syncFreq) {
        Class<?> clientJobService = getJobServiceImplClass(apiName);
        if (clientJobService == null) {
            throw new NotImplementedError("Job service : " + apiName + " not implemented");
        }

        ComponentName componentName = new ComponentName(context, clientJobService);
        JobInfo info;
        if (syncFreq == null || syncFreq.trim().isEmpty()) {
            info = new JobInfo.Builder(jobId, componentName)
                    .setRequiresCharging(false)
                    .setPersisted(false)
                    .build();

        } else {
            info = new JobInfo.Builder(jobId, componentName)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .setPeriodic(15 * 60 * 1000)//TODO set cron
                    .build();
        }
        return jobScheduler.schedule(info);
    }

    public boolean triggerJobService(int jobId, String apiName) {
        JobInfo info = jobScheduler.getPendingJob(jobId);
        if (info == null) {
            //Trigger job only once
            return scheduleJob(jobId, apiName, null) == JobScheduler.RESULT_SUCCESS;
        } else {
            jobScheduler.schedule(info);
            return true;
        }
    }

    public void cancelJob(int jobId) {
        jobScheduler.cancel(jobId);
    }

    public boolean isJobEnabled(int jobId) {
        JobInfo jobinfo = jobScheduler.getPendingJob(jobId);
        if (jobinfo == null) {
            return false;
        }
        return true;
    }

    public boolean isJobImplemented(String jobAPIName) {
        if (getJobServiceImplClass(jobAPIName) == null) {
            return false;
        }
        return true;
    }

    public Class<?> getJobServiceImplClass(String jobAPIName) {
        switch (jobAPIName) {
            case "packetSyncStatusJob":
                return PacketStatusSyncJob.class;
            case "synchConfigDataJob":
                return ConfigDataSyncJob.class;
            default:
                return null;
        }
    }

    public List<SyncJobDef> getAllSyncJobDefList() {
        return packetService.getAllSyncJobDefList();
    }

    public String getLastSyncTime(int jobId) {
        long lastSyncTimeSeconds = jobTransactionService.getLastSyncTime(jobId);
        String lastSync = context.getString(R.string.NA);

        if (lastSyncTimeSeconds > 0) {
            Date lastSyncDate = new Date(TimeUnit.SECONDS.toMillis(lastSyncTimeSeconds));
            String date = dateFormat.format(lastSyncDate);
            String time = timeFormat.format(lastSyncDate);
            lastSync = String.format("%s %s", date, time);
        }
        return lastSync;
    }

    public String getNextSyncTime(int jobId) {
        //TODO implementation
        long nextSyncTimeSeconds = 0;
        String nextSync = context.getString(R.string.NA);

        if (nextSyncTimeSeconds > 0) {
            Date nextSyncTime = new Date(TimeUnit.SECONDS.toMillis(nextSyncTimeSeconds));
            String date = dateFormat.format(nextSyncTime);
            String time = timeFormat.format(nextSyncTime);
            nextSync = String.format("%s %s", date, time);
        }
        return nextSync;
    }
}
