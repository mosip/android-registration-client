/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.mosip.registration_client.api_services.MasterDataSyncApi;
import io.mosip.registration_client.utils.SyncScheduler;

/**
 * WorkManager worker that performs a single execution of a sync job.
 *
 * Lifecycle:
 * - Scheduled by {@link SyncScheduler#scheduleJob(Context, String)} with an initial delay
 *   derived from the job's cron expression.
 * - When the WorkManager delay expires, {@link #doWork()} is called.
 * - We resolve the Dagger graph from {@link RegistrationClientApp}, run the job via
 *   {@link MasterDataSyncApi#executeJobByApiName(String, Context, java.util.function.Consumer)}, and wait
 *   for completion (bounded by {@link #SYNC_TIMEOUT_MINUTES}).
 * - When finished, we ask {@link SyncScheduler} to schedule the *next* run for the same job.
 *
 * This keeps each job run independent and lets WorkManager persist and reschedule work
 * across process death and device reboot.
 */
public class SyncWorker extends Worker {

    public static final String KEY_JOB_API_NAME = "job_api_name";
    private static final String TAG = "SyncWorker";
    // WorkManager enforces a ~10 minute execution limit per Worker. Use a slightly
    // lower timeout here so there is enough time left to perform cleanup and
    // return Result.retry() before the Worker is forcibly stopped.
    private static final long SYNC_TIMEOUT_MINUTES = 9;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // The API name of the job we should execute (e.g. "registrationPacketUploadJob").
        String jobApiName = getInputData().getString(KEY_JOB_API_NAME);
        if (jobApiName == null || jobApiName.isEmpty()) {
            Log.e(TAG, "No job API name provided");
            return Result.failure();
        }

        Log.d(TAG, "Starting background sync: " + jobApiName);

        try {
            RegistrationClientApp app = (RegistrationClientApp) getApplicationContext();
            AppComponent component = app.getAppComponent();

            MasterDataSyncApi syncApi = component.masterDataSyncApi();
            SyncScheduler scheduler = component.syncScheduler();

            AtomicBoolean syncSucceeded = new AtomicBoolean(false);
            CountDownLatch latch = new CountDownLatch(1);
            syncApi.executeJobByApiName(jobApiName, getApplicationContext(), success -> {
                syncSucceeded.set(Boolean.TRUE.equals(success));
                latch.countDown();
            });

            boolean completed = latch.await(SYNC_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            if (!completed || !syncSucceeded.get()) {
                Log.w(TAG, "Sync timed out or failed: " + jobApiName);
                return Result.retry();
            }

            scheduler.scheduleJob(getApplicationContext(), jobApiName);

            Log.d(TAG, "Background sync completed: " + jobApiName);
            return Result.success();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.w(TAG, "Background sync interrupted: " + jobApiName, e);
            return Result.retry();
        } catch (Exception e) {
            Log.e(TAG, "Background sync failed: " + jobApiName, e);
            return Result.retry();
        }
    }
}
