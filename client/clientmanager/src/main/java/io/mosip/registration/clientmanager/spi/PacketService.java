package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.entity.Registration;

import java.util.List;

public interface PacketService {

    List<Registration> getUnSyncedRegistrations();

    List<Registration> getSyncedRegistrations();

    void syncRegistrations(List<Registration> registrations);

    void uploadRegistrations(List<Registration> registrations);

    List<Registration> getAllRegistrations(int page, int pageLimit);

}
