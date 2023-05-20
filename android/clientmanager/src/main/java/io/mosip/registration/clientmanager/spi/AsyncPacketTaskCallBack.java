package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.constant.PacketTaskStatus;

/**
 * @author Anshul vanawat
 * @since 1.0.0
 */

public interface AsyncPacketTaskCallBack {
    void inProgress(String RID);
    void onComplete(String RID, PacketTaskStatus status);
}
