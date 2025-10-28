package io.mosip.registration.clientmanager.service;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.NotImplementedException;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.jobs.ConfigDataSyncJob;
import io.mosip.registration.clientmanager.jobs.DeleteAuditLogsJob;
import io.mosip.registration.clientmanager.jobs.PacketStatusSyncJob;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.util.CronExpressionParser;
import io.mosip.registration.clientmanager.util.DateUtil;

/**
 * @author Anshul vanawat
 * @since 1.0.0
 */

public class JobManagerServiceImpl implements JobManagerService {

    private static final String TAG = JobManagerServiceImpl.class.getSimpleName();
    private static final int JOB_PERIODIC_SECONDS = (15 * 60) * 1000;
    private static final int NUM_LENGTH_LIMIT = 5;

    Context context;
    JobScheduler jobScheduler;
    JobTransactionService jobTransactionService;
    SyncJobDefRepository syncJobDefRepository;
    DateUtil dateUtil;

    public JobManagerServiceImpl(Context context, SyncJobDefRepository syncJobDefRepository, JobTransactionService jobTransactionService, DateUtil dateUtil) {
        this.context = context;
        this.jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        this.syncJobDefRepository = syncJobDefRepository;
        this.jobTransactionService = jobTransactionService;
        this.dateUtil = dateUtil;
    }

    /**
     * Fetches all the SyncJobDef from localDB and schedules or cancels scheduled jobs
     * as per the sync job def.
     */
    @Override
    public void refreshAllJobs() {
        List<SyncJobDef> syncJobDefList = getAllSyncJobDefList();

        for (SyncJobDef jobDef : syncJobDefList) {
            refreshJobStatus(jobDef);
        }
    }

    /**
     * refreshes the passed jobDef
     */
    @Override
    public void refreshJobStatus(SyncJobDef jobDef) {
        int jobId = generateJobServiceId(jobDef.getId());

        boolean isActiveAndImplemented = jobDef.getIsActive() != null && jobDef.getIsActive()
                && isJobImplementedOnRegClient(jobDef.getApiName());

        if (!isActiveAndImplemented) {
            cancelJob(jobId);
            return;
        }

        if (!isJobScheduled(jobId))
            scheduleJob(jobId, jobDef.getApiName(), jobDef.getSyncFreq());
    }

    /**
     * @param jobId    : Id of the job to be scheduled.
     * @param apiName  : name of the service to be scheduled.
     * @param syncFreq : syncFreq (Optional). Null or empty to schedule only once
     * @return 1 for success and 0 for failure
     */
    @Override
    public int scheduleJob(int jobId, String apiName, String syncFreq) {
        Class<?> clientJobService = getJobServiceImplClass(apiName);
        if (clientJobService == null) {
            throw new NotImplementedException("Job service : " + apiName + " not implemented");
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
            long periodMillis = parseSyncFreqToMillis(syncFreq);
            if (periodMillis <= 0) {
                periodMillis = JOB_PERIODIC_SECONDS * 1000L;
            }
            // For testing: force deleteAuditLogsJob to run every 3 minutes
            if (clientJobService == DeleteAuditLogsJob.class) {
                periodMillis = TimeUnit.MINUTES.toMillis(3);
            }
            info = new JobInfo.Builder(jobId, componentName)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .setPeriodic(periodMillis)
                    .build();
        }
        return jobScheduler.schedule(info);
    }

    @Override
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

    @Override
    public void cancelJob(int jobId) {
        jobScheduler.cancel(jobId);
    }

    @Override
    public boolean isJobScheduled(int jobId) {
        JobInfo jobinfo = jobScheduler.getPendingJob(jobId);
        if (jobinfo == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isJobImplementedOnRegClient(String jobAPIName) {
        if (getJobServiceImplClass(jobAPIName) == null) {
            return false;
        }
        return true;
    }

    @Override
    public List<SyncJobDef> getAllSyncJobDefList() {
        return syncJobDefRepository.getAllSyncJobDefList();
    }

    @Override
    public String getLastSyncTime(int jobId) {
        long lastSyncTimeSeconds = jobTransactionService.getLastSyncTime(jobId);
        Log.i(TAG, "lastSyncTimeSeconds=" + lastSyncTimeSeconds);
        String lastSync = context.getString(R.string.NA);

        if (lastSyncTimeSeconds > 0) {
            lastSync = dateUtil.getDateTime(lastSyncTimeSeconds);
        }
        return lastSync;
    }

    @Override
    public String getNextSyncTime(int jobId) {
        SyncJobDef jobDef = getJobDefByJobId(jobId);
        if (jobDef == null) {
            return "NA";
        }

        String cronExpression = jobDef.getSyncFreq();
        // Try cron-based calculation first
        if (CronExpressionParser.isValidCronExpression(cronExpression)) {
            Instant nextExecution = CronExpressionParser.getNextExecutionTime(cronExpression);
            if (nextExecution != null) {
                return dateUtil.getDateTime(nextExecution.toEpochMilli());
            }
        }

        // Fallback: use last sync time + interval
        long lastSyncTimeSeconds = jobTransactionService.getLastSyncTime(jobId);
        if (lastSyncTimeSeconds > 0) {
            return dateUtil.getDateTime(lastSyncTimeSeconds + JOB_PERIODIC_SECONDS);
        }

        return "NA";
    }

    @Override
    public int generateJobServiceId(String syncJobDefId) {
        try {
            String lastCharsWithNumLengthLimit = syncJobDefId.substring(syncJobDefId.length() - NUM_LENGTH_LIMIT);
            return Integer.parseInt(lastCharsWithNumLengthLimit);
        } catch (Exception ex) {
            Log.e(TAG, "Conversion of jobId : " + syncJobDefId + "to int failed for length " + NUM_LENGTH_LIMIT + ex.getMessage());
            throw ex;
        }
    }


    private Class<?> getJobServiceImplClass(String jobAPIName) {
        switch (jobAPIName) {
            case "packetSyncStatusJob":
                return PacketStatusSyncJob.class;
            case "synchConfigDataJob":
                return ConfigDataSyncJob.class;
            case "deleteAuditLogsJob":
                return DeleteAuditLogsJob.class;
            default:
                return null;
        }
    }

    private long parseSyncFreqToMillis(String syncFreq) {
        try {
            String s = syncFreq.trim().toLowerCase();
            if (s.endsWith("ms")) {
                return Long.parseLong(s.substring(0, s.length() - 2));
            } else if (s.endsWith("s")) {
                return Long.parseLong(s.substring(0, s.length() - 1)) * 1000L;
            } else if (s.endsWith("m")) {
                return Long.parseLong(s.substring(0, s.length() - 1)) * 60_000L;
            } else if (s.endsWith("h")) {
                return Long.parseLong(s.substring(0, s.length() - 1)) * 3_600_000L;
            } else if (s.endsWith("d")) {
                return Long.parseLong(s.substring(0, s.length() - 1)) * 86_400_000L;
            }
        } catch (Exception ignored) {}
        return 0L;
    }

    private SyncJobDef getJobDefByJobId(int jobId) {
        List<SyncJobDef> allJobs = syncJobDefRepository.getAllSyncJobDefList();
        for (SyncJobDef jobDef : allJobs) {
            if (jobId == generateJobServiceId(jobDef.getId())) {
                return jobDef;
            }
        }
        return null;
    }
}
