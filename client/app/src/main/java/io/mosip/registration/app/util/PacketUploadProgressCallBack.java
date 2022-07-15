package io.mosip.registration.app.util;

import io.mosip.registration.clientmanager.constant.PacketTaskStatus;

/**
 * @author Anshul vanawat
 * @since 1.0.0
 */
public interface PacketUploadProgressCallBack {
    void progress(String RID, PacketTaskStatus progress);
}
