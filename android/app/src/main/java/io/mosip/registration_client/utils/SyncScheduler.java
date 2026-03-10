/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client.utils;

import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration_client.SyncWorker;

@Singleton
public class SyncScheduler {

    private static final String TAG = "SyncScheduler";
    private static final long MIN_DELAY_MS = 60_000;

    private final SyncJobDefRepository syncJobDefRepository;
    private final GlobalParamRepository globalParamRepository;
    private final MasterDataService masterDataService;
    private final BatchJob batchJob;

    @Inject
    public SyncScheduler(SyncJobDefRepository syncJobDefRepository,
                         GlobalParamRepository globalParamRepository,
                         MasterDataService masterDataService,
                         BatchJob batchJob) {
        this.syncJobDefRepository = syncJobDefRepository;
        this.globalParamRepository = globalParamRepository;
        this.masterDataService = masterDataService;
        this.batchJob = batchJob;
    }

    public void scheduleJob(Context context, String jobApiName) {
        try {
            long nextExecutionTime = batchJob.getIntervalMillis(jobApiName);
            long delay = nextExecutionTime - System.currentTimeMillis();

            if (delay < MIN_DELAY_MS) {
                Log.w(TAG, jobApiName + " - Delay too small (" + delay + "ms), using minimum");
                delay = MIN_DELAY_MS;
            }

            Data inputData = new Data.Builder()
                    .putString(SyncWorker.KEY_JOB_API_NAME, jobApiName)
                    .build();

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                    .setInputData(inputData)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setConstraints(constraints)
                    .addTag("sync_job")
                    .addTag("sync_job_" + jobApiName)
                    .build();

            String uniqueWorkName = "sync_" + jobApiName;
            WorkManager.getInstance(context)
                    .enqueueUniqueWork(uniqueWorkName, ExistingWorkPolicy.REPLACE, workRequest);

            Log.d(TAG, jobApiName + " - Scheduled, next execution in " + (delay / 1000) + " seconds");
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling job: " + jobApiName, e);
        }
    }

    public void scheduleAllActiveJobs(Context context) {
        new Thread(() -> {
            try {
                CenterMachineDto dto = masterDataService.getRegistrationCenterMachineDetails();
                if (dto == null || dto.getMachineRefId() == null) {
                    Log.w(TAG, "Machine not configured - skipping auto sync initialization");
                    return;
                }

                List<SyncJobDef> activeJobs = syncJobDefRepository.getAllSyncJobDefList();
                Set<String> excludedJobIds = getExcludedJobIds();
                int scheduledCount = 0;

                for (SyncJobDef job : activeJobs) {
                    if (job.getId() == null) continue;
                    if (excludedJobIds.contains(job.getId())) {
                        Log.d(TAG, "Skipping excluded job: " + job.getId());
                        continue;
                    }
                    if (job.getIsActive() != null && job.getIsActive() && job.getApiName() != null) {
                        Log.d(TAG, "Scheduling job: " + job.getApiName() +
                                " (ID: " + job.getId() + ", Cron: " + job.getSyncFreq() + ")");
                        scheduleJob(context, job.getApiName());
                        scheduledCount++;
                    }
                }

                Log.d(TAG, "Scheduled " + scheduledCount + " active jobs via WorkManager");
            } catch (Exception e) {
                Log.e(TAG, "Error scheduling all active jobs", e);
            }
        }).start();
    }

    public void cancelJob(Context context, String jobApiName) {
        String uniqueWorkName = "sync_" + jobApiName;
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName);
        Log.d(TAG, "Cancelled job: " + jobApiName);
    }

    public void cancelAllJobs(Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("sync_job");
        Log.d(TAG, "Cancelled all sync jobs");
    }

    private Set<String> getExcludedJobIds() {
        Set<String> excluded = new HashSet<>();
        addJobIdsFromString(excluded, globalParamRepository.getCachedStringJobsOffline());
        addJobIdsFromString(excluded, globalParamRepository.getCachedStringJobsUntagged());
        return excluded;
    }

    private void addJobIdsFromString(Set<String> target, String value) {
        if (value == null || value.trim().isEmpty()) return;
        for (String jobId : value.split(RegistrationConstants.COMMA)) {
            String trimmed = jobId.trim();
            if (!trimmed.isEmpty()) {
                target.add(trimmed);
            }
        }
    }
}
