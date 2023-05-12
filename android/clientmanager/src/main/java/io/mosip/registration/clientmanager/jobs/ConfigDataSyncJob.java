package io.mosip.registration.clientmanager.jobs;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.mosip.registration.clientmanager.spi.MasterDataService;

/**
 * Class for implementing PacketStatusSyncJob service
 *
 * @author Anshul vanawat
 * @since 1.0.0
 */

@SuppressLint("SpecifyJobSchedulerIdRange")
public class ConfigDataSyncJob extends SyncJobServiceBase {

    private static final String TAG = ConfigDataSyncJob.class.getSimpleName();

    @Inject
    public MasterDataService masterDataService;

    public ConfigDataSyncJob() {
        configureBuilder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    public boolean triggerJob(int jobId) {
        Log.d(TAG, TAG + " Started");
        try {
            masterDataService.syncGlobalParamsData();
            long timeStampInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            logJobTransaction(jobId, timeStampInSeconds);
            return true;
        } catch (Exception e) {
            Log.e(TAG, TAG + " failed", e);
        }
        Log.d(TAG, TAG + " Completed");
        return false;
    }
}
