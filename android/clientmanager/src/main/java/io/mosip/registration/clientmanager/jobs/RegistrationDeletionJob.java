package io.mosip.registration.clientmanager.jobs;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.mosip.registration.clientmanager.spi.PacketService;

/**
 * Job for deleting old processed registration packets
 * Runs periodically to clean up packets that have been successfully processed
 * by the server and are older than the configured number of days.
 *
 * @since 1.0.0
 */
@SuppressLint("SpecifyJobSchedulerIdRange")
public class RegistrationDeletionJob extends SyncJobServiceBase {

    private static final String TAG = RegistrationDeletionJob.class.getSimpleName();

    @Inject
    PacketService packetService;

    public RegistrationDeletionJob() {
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
            packetService.deleteRegistrationPackets();
            long timeStampInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            logJobTransaction(jobId, timeStampInSeconds);
            Log.d(TAG, TAG + " Completed successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, TAG + " failed", e);
        }
        return false;
    }
}
