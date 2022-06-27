package io.mosip.registration.app.util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

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
    private static final int JOB_PERIODIC_SECONDS = 15 * 60;
    private static final int numLengthLimit = 5;

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
     * Fetches all the SyncJobDef from localDB and schedules or cancels scheduled jobs
     * as per the sync job def.
     */
    public void syncJobServices() {
        List<SyncJobDef> syncJobDefList = getAllSyncJobDefList();

        for (SyncJobDef jobDef : syncJobDefList) {
            int jobId = getId(jobDef.getId());

            boolean isActiveAndImplemented = jobDef.getIsActive() != null && jobDef.getIsActive() && isJobImplementedOnRegClient(jobDef.getApiName());
            boolean isScheduled = isJobScheduled(jobId);

            if (isActiveAndImplemented
                    && !isScheduled) {
                scheduleJob(jobId, jobDef.getApiName(), jobDef.getSyncFreq());
            } else {
                cancelJob(jobId);
            }
        }
    }

    /**
     * @param jobId    : Id of the job to be scheduled.
     * @param apiName  : name of the service to be scheduled.
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
            //To schedule only once
            info = new JobInfo.Builder(jobId, componentName)
                    .setRequiresCharging(false)
                    .setPersisted(false)
                    .build();

        } else {
            //To schedule periodically
            //TODO set cron wise
            info = new JobInfo.Builder(jobId, componentName)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .setPeriodic(JOB_PERIODIC_SECONDS * 1000)
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

    public boolean isJobScheduled(int jobId) {
        JobInfo jobinfo = jobScheduler.getPendingJob(jobId);
        if (jobinfo == null) {
            return false;
        }
        return true;
    }

    public boolean isJobImplementedOnRegClient(String jobAPIName) {
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
        //TODO implementation using CRON job
        long lastSyncTimeSeconds = jobTransactionService.getLastSyncTime(jobId);
        String nextSync = context.getString(R.string.NA);

        if (lastSyncTimeSeconds > 0) {
            long nextSyncTimeSeconds = lastSyncTimeSeconds + JOB_PERIODIC_SECONDS;
            Date nextSyncTime = new Date(TimeUnit.SECONDS.toMillis(nextSyncTimeSeconds));
            String date = dateFormat.format(nextSyncTime);
            String time = timeFormat.format(nextSyncTime);
            nextSync = String.format("%s %s", date, time);
        }
        return nextSync;
    }

    public int getId(String jobId) {
        try {
            String lastCharsWithNumLengthLimit = jobId.substring(jobId.length() - numLengthLimit);
            return Integer.parseInt(lastCharsWithNumLengthLimit);
        } catch (Exception ex) {
            Log.e(TAG, "Conversion of jobId : " + jobId + "to int failed for length " + numLengthLimit + ex.getMessage());
            throw ex;
        }
    }
}
