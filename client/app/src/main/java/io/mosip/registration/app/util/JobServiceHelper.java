package io.mosip.registration.app.util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import io.mosip.registration.clientmanager.jobservice.PacketStatusSyncJob;
import io.mosip.registration.clientmanager.spi.PacketService;
import kotlin.NotImplementedError;

public class JobServiceHelper {

    private static final String TAG = JobServiceHelper.class.getSimpleName();
    Context context;
    JobScheduler jobScheduler;
    PacketService packetService;

    public JobServiceHelper(Context context, JobScheduler jobScheduler, PacketService packetService) {
        this.context = context;
        this.jobScheduler = jobScheduler;
        this.packetService = packetService;
    }

    public int scheduleJob(int jobId, String apiName) throws ClassNotFoundException {
        Class<?> clientJobService = getJobServiceImplClass(apiName);

        if (clientJobService == null) {
            throw new NotImplementedError("Job service : " + apiName + " not implemented");
        }

        ComponentName componentName = new ComponentName(context, clientJobService);
        JobInfo info = new JobInfo.Builder(jobId, componentName)
                .setRequiresCharging(false)
                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        return jobScheduler.schedule(info);
    }

    public boolean triggerJobService(int jobId) {
        JobInfo info = jobScheduler.getPendingJob(jobId);
        if (info == null) {
            return false;
        }
        jobScheduler.schedule(info);
        return true;
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
            default:
                return null;
        }
    }
}
