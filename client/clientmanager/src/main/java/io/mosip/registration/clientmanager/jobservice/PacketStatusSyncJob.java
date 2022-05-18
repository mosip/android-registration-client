package io.mosip.registration.clientmanager.jobservice;

import android.annotation.SuppressLint;
import android.util.Log;

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
    public boolean triggerJob() {
        Log.d(TAG, TAG + " Started");
        try {
            packetService.syncAllPacketStatus();
            return true;
        } catch (Exception e) {
            Log.e(TAG, TAG + " failed", e);
        }
        Log.d(TAG, TAG + " Completed");
        return false;
    }
}