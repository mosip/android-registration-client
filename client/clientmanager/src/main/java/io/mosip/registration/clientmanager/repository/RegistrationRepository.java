package io.mosip.registration.clientmanager.repository;

import androidx.lifecycle.LiveData;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.dao.RegistrationDao;
import io.mosip.registration.clientmanager.entity.Registration;

import javax.inject.Inject;
import java.util.List;

public class RegistrationRepository {

    private RegistrationDao registrationDao;

    @Inject
    public RegistrationRepository(RegistrationDao registrationDao) {
        this.registrationDao = registrationDao;
    }

    public List<Registration> getAllRegistrations() {
        return this.registrationDao.findAll();
    }

    public Registration insertRegistration(String rid, String containerPath) {
        //TODO parse container path and only take packet Id
        Registration registration = new Registration(containerPath);
        registration.setPacketId(rid);
        registration.setCenterId("");
        registration.setClientStatus(PacketClientStatus.CREATED.name());
        registration.setServerStatus(null);
        registration.setCrDtime(System.currentTimeMillis());
        registration.setCrBy("");
        this.registrationDao.insert(registration);
        return registration;
    }
}
