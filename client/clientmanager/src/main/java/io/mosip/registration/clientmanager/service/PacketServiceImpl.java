package io.mosip.registration.clientmanager.service;

import io.mosip.registration.clientmanager.dao.RegistrationDao;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.spi.PacketService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class PacketServiceImpl implements PacketService {

    private static final String TAG = PacketServiceImpl.class.getSimpleName();

    @Inject
    RegistrationDao registrationDao;

    @Override
    public List<Registration> getUnSyncedRegistrations() {
        return null;
    }

    @Override
    public List<Registration> getSyncedRegistrations() {
        return null;
    }

    @Override
    public void syncRegistrations(List<Registration> registrations) {

    }

    @Override
    public void uploadRegistrations(List<Registration> registrations) {

    }

    @Override
    public List<Registration> getAllRegistrations(int page, int pageLimit) {
        return null;
    }
}
