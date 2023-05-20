package io.mosip.registration.clientmanager.jobs;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import io.mosip.registration.clientmanager.spi.PacketService;

/**
 * Class for implementing PacketStatusSyncJob service
 *
 * @author Anshul vanawat
 * @since 1.0.0
 */

@SuppressLint("SpecifyJobSchedulerIdRange")
public class PacketStatusSyncJob extends SyncJobServiceBase {

    private static final String TAG = PacketStatusSyncJob.class.getSimpleName();

    @Inject
    PacketService packetService;

    public PacketStatusSyncJob() {
        configureBuilder();
    }

    @Override
    public boolean triggerJob(int jobId) {
        Log.d(TAG, TAG + " Started");
        try {
            packetService.syncAllPacketStatus();
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