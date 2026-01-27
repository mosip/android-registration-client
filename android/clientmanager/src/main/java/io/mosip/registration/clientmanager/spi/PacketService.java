package io.mosip.registration.clientmanager.spi;

import java.util.List;

import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.entity.SyncJobDef;

public interface PacketService {

    /**
     *
     * @param packetId
     * @throws Exception
     */
    void syncRegistration(String packetId) throws Exception;

    /**
     *
     * @param packetId
     * @param callBack
     * @throws Exception
     */
    void syncRegistration(String packetId, AsyncPacketTaskCallBack callBack) throws Exception;

    /**
     *
     * @param packetId
     * @throws Exception
     */
    void uploadRegistration(String packetId) throws Exception;

    /**
     *
     * @param packetId
     * @param callBack
     * @throws Exception
     */
    void uploadRegistration(String packetId, AsyncPacketTaskCallBack callBack) throws Exception;

    /**
     *
     * @param page
     * @param pageLimit
     * @return
     */
    List<Registration> getAllRegistrations(int page, int pageLimit);

    /**
     *
     * @param page
     * @param pageLimit
     * @return
     */
    List<Registration> getAllNotUploadedRegistrations(int page, int pageLimit);

    /**
     *
     * @param status
     * @return
     */

    List<Registration> getRegistrationsByStatus(String status, Integer batchSize);

    /**
     *
     * @return
     */
    void syncAllPacketStatus();

    /**
     *
     * @param
     * @return Packet Status
     */
    String getPacketStatus(String packetId);

    /**
     * Validate if any registered packet has exceeded the configured approval wait time.
     * @return true if approval wait time is breached, otherwise false.
     */
    boolean isRegisteredPacketApprovalTimeBreached();

    /**
     * Validate if the time since last export/upload of registration packets has exceeded the configured limit.
     * @return true if last export time limit is exceeded, otherwise false.
     */
    boolean validatingLastExportDuration();

    /**
     * Validate if the count of packets yet to be exported has reached the configured maximum limit.
     * @return true if maximum packet count limit is reached, otherwise false.
     */
    boolean isMaxPacketCountLimitReached();

    /**
     * Validate if the count of registered packets pending approval has reached the configured maximum limit.
     * @return true if maximum registered packet count limit is reached, otherwise false.
     */
    boolean isMaxNotApprovedPacketCountLimitReached();

    void deleteRegistrationPackets();
}
