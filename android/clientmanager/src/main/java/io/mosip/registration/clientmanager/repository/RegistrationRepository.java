package io.mosip.registration.clientmanager.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.dao.RegistrationDao;
import io.mosip.registration.clientmanager.entity.Registration;
import org.json.JSONObject;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RegistrationRepository {

    private RegistrationDao registrationDao;
    private ObjectMapper objectMapper;

    @Inject
    public RegistrationRepository(RegistrationDao registrationDao, ObjectMapper objectMapper) {
        this.registrationDao = registrationDao;
        this.objectMapper = objectMapper;
    }

    public List<Registration> getAllRegistrations() {
        return this.registrationDao.findAll();
    }

    public Registration getRegistration(String packetId) {
        return this.registrationDao.findOneByPacketId(packetId);
    }

    public void updateServerStatus(String packetId, String serverStatus) {
        this.registrationDao.updateServerStatus(packetId, serverStatus);
    }

    public void updateStatus(String packetId, String serverStatus, String clientStatus) {
        this.registrationDao.updateStatus(packetId, clientStatus, serverStatus);
    }

    public Registration insertRegistration(String packetId, String containerPath, String centerId,
                                           String registrationType, JSONObject additionalInfo) throws Exception {
        Registration registration = new Registration(packetId);
        registration.setFilePath(containerPath);
        registration.setRegType(registrationType);
        registration.setCenterId(centerId);
        registration.setClientStatus(PacketClientStatus.CREATED.name());
        registration.setServerStatus(null);
        registration.setCrDtime(System.currentTimeMillis());
        registration.setCrBy("110006");
        //TODO use objectMapper
        registration.setAdditionalInfo(additionalInfo.toString().getBytes(StandardCharsets.UTF_8));
        this.registrationDao.insert(registration);
        return registration;
    }
}
