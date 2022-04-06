package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.entity.Registration;

import java.util.List;

public interface PacketService {

    List<Registration> getUnSyncedRegistrations();

    List<Registration> getSyncedRegistrations();

    void syncRegistration(String packetId) throws Exception;

    void uploadRegistration(String packetId) throws Exception;

    List<Registration> getAllRegistrations(int page, int pageLimit);

}
