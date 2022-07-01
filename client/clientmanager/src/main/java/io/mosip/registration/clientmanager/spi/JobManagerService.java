package io.mosip.registration.clientmanager.spi;

import java.util.List;

import io.mosip.registration.clientmanager.entity.SyncJobDef;

/**
 * @author Anshul vanawat
 * @since 1.0.0
 */

public interface JobManagerService {

    void refreshAllJobs();

    void refreshJobStatus(SyncJobDef jobDef);

    int scheduleJob(int jobId, String apiName, String syncFreq);

    boolean triggerJobService(int jobId, String apiName);

    void cancelJob(int jobId);

    boolean isJobScheduled(int jobId);

    boolean isJobImplementedOnRegClient(String jobAPIName);

    List<SyncJobDef> getAllSyncJobDefList();

    String getLastSyncTime(int jobId);

    String getNextSyncTime(int jobId);

    int generateJobServiceId(String syncJobDefId);
}
