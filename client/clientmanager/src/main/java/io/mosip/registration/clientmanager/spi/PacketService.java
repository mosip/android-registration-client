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
     * @return
     */
    void syncAllPacketStatus();

    /**
     *
     * @param
     * @return Packet Status
     */
    String getPacketStatus(String packetId);
}
