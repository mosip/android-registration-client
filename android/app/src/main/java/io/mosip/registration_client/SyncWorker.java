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

import io.mosip.registration_client.api_services.MasterDataSyncApi;
import io.mosip.registration_client.utils.SyncScheduler;

public class SyncWorker extends Worker {

    public static final String KEY_JOB_API_NAME = "job_api_name";
    private static final String TAG = "SyncWorker";
    private static final long SYNC_TIMEOUT_MINUTES = 10;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
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

            CountDownLatch latch = new CountDownLatch(1);
            syncApi.executeJobByApiName(jobApiName, getApplicationContext(), latch::countDown);

            boolean completed = latch.await(SYNC_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            if (!completed) {
                Log.w(TAG, "Sync timed out: " + jobApiName);
                return Result.retry();
            }

            scheduler.scheduleJob(getApplicationContext(), jobApiName);

            Log.d(TAG, "Background sync completed: " + jobApiName);
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Background sync failed: " + jobApiName, e);
            return Result.retry();
        }
    }
}
