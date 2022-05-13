package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.entity.SyncJobDef;

import java.util.List;

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
     * @throws Exception
     */
    void uploadRegistration(String packetId) throws Exception;

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
    List<SyncJobDef> getAllSyncJobDefList();

    /**
     *
     * @return
     */
    void syncAllPacketStatus();
}
