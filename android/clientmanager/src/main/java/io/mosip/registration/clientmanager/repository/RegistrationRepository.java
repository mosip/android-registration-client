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

    public List<Registration> getAllNotUploadedRegistrations() {
        return this.registrationDao.findAllNotUploaded();
    }

    public List<Registration> getRegistrationsByStatus(String status, Integer batchSize) {
        return this.registrationDao.findRegistrationByStatus(status, batchSize);
    }

    public int getAllRegistrationByStatus(String status) {
        return this.registrationDao.findAllRegistrationByStatus(status);
    }

    public int getAllRegistrationByPendingStatus(String syncedStatus, String approvedStatus) {
        return this.registrationDao.findRegistrationCountBySyncedStatusAndApprovedStatus(syncedStatus, approvedStatus);
    }

    public int findRegistrationCountBySyncedStatus(String syncedStatus) {
        return this.registrationDao.findRegistrationCountBySyncedStatus(syncedStatus);
    }

    public int getAllCreatedPacketStatus() {
        return this.registrationDao.getAllCreatedPacketStatus();
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
                                           String registrationType, JSONObject additionalInfo, String additionalInfoReqId, String rid, String applicationId) throws Exception {
        Registration registration = new Registration(packetId);
        registration.setFilePath(containerPath);
        registration.setRegType(registrationType);
        registration.setCenterId(centerId);
        registration.setClientStatus(PacketClientStatus.CREATED.name());
        registration.setServerStatus(null);
        registration.setCrDtime(System.currentTimeMillis());
        registration.setCrBy("110006");
        registration.setId(rid);
        registration.setAppId(applicationId);
        //TODO use objectMapper
        registration.setAdditionalInfo(additionalInfo.toString().getBytes(StandardCharsets.UTF_8));
        registration.setAdditionalInfoReqId(additionalInfoReqId);
        this.registrationDao.insert(registration);
        return registration;
    }

    public void deleteRegistration(String packetId) {
        this.registrationDao.delete(packetId);
    }

    public void updateSupervisorReview(String packetId, String supervisorStatus, String supervisorComment) {
        this.registrationDao.updateSupervisorReview(packetId, supervisorStatus, supervisorComment);
    }
}
