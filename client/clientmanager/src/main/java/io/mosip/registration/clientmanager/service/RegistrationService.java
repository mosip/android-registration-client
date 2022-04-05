package io.mosip.registration.clientmanager.service;

import android.content.Context;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.dao.RegistrationDao;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.DateUtils;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord;
import io.mosip.registration.packetmanager.dto.PacketWriter.Document;
import io.mosip.registration.packetmanager.spi.PacketWriterService;
import io.mosip.registration.packetmanager.util.PacketManagerConstant;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Singleton
public class RegistrationService {

    private static final String TAG = RegistrationService.class.getSimpleName();

    private Context context;
    private RegistrationDto registrationDto;

    @Inject
    PacketWriterService packetWriterService;

    @Inject
    UserInterfaceHelperService userInterfaceHelperService;

    @Inject
    MasterDataService masterDataService;

    @Inject
    RegistrationDao registrationDao;

    @Inject
    public RegistrationService(Context context) {
        this.context = context;
        this.registrationDto = null;
    }

    public void approveRegistration(Registration registration) {
        //TODO
    }

    public void rejectRegistration(Registration registration) {
        //TODO
    }

    public void startRegistration() {
        if(registrationDto !=  null) {
            registrationDto.cleanup();
        }

        String timestamp = DateUtils.formatToISOStringWithoutMillis(LocalDateTime.now(ZoneOffset.UTC));
        timestamp = timestamp.replaceAll(":|T|Z|-", "");
        String rid = String.format("100011007710031%s", timestamp);

        List<String> languages = new ArrayList<>();
        languages.add("eng"); //TODO
        this.registrationDto = new RegistrationDto(rid, "NEW", "1.0", languages);
    }

    public RegistrationDto getRegistrationDto() {
        return this.registrationDto;
    }

    public void submitRegistrationDto() throws Exception {
        this.registrationDto.getAllDemographicFields().forEach( entry -> {
            packetWriterService.setField(this.registrationDto.getRId(), entry.getKey(), entry.getValue());
        });

        this.registrationDto.getAllDocumentFields().forEach(entry -> {
            Document document = new Document();
            document.setType(entry.getValue().getType());
            document.setFormat(entry.getValue().getFormat());
            document.setRefNumber(entry.getValue().getRefNumber());
            //TODO
            packetWriterService.setDocument(this.registrationDto.getRId(), entry.getKey(), document);
        });

        this.registrationDto.getAllBiometricFields().forEach( entry -> {
            String[] parts = entry.getKey().split("_");
            BiometricRecord biometricRecord = new BiometricRecord();
            //TODO
        });

        packetWriterService.addAudits(this.registrationDto.getRId(), getAudits());
        packetWriterService.addMetaInfo(this.registrationDto.getRId(), getMetaInfoMap());

        String containerPath = packetWriterService.persistPacket(this.registrationDto.getRId(),
                this.registrationDto.getSchemaVersion(),
                userInterfaceHelperService.getSchemaJsonFromResource(),
                "REGISTRATION_CLIENT",
                this.registrationDto.getProcess(),
                true);

        if(containerPath == null || containerPath.trim().isEmpty()) {
            throw new Exception("Failed to create registration packet");
        }

        //TODO parse container path and only take packet Id
        Registration registration = new Registration(containerPath);
        registration.setPacketId(this.registrationDto.getRId());
        registration.setCenterId("");
        registration.setClientStatus(PacketClientStatus.CREATED.name());
        registration.setCrDtime(LocalDateTime.now());
        registration.setCrBy("");
        registrationDao.insert(registration);

    }

    private Map<String, String> getMetaInfoMap() {
        Map<String, String> metaData = new LinkedHashMap<>();
        metaData.put(PacketManagerConstant.META_MACHINE_ID, "");
        metaData.put(PacketManagerConstant.META_CENTER_ID, "");
        metaData.put(PacketManagerConstant.META_KEYINDEX, "");
        metaData.put(PacketManagerConstant.META_REGISTRATION_ID, this.registrationDto.getRId());
        metaData.put(PacketManagerConstant.META_APPLICATION_ID, this.registrationDto.getRId());
        metaData.put(PacketManagerConstant.META_CREATION_DATE, "");
        metaData.put(PacketManagerConstant.META_CLIENT_VERSION, "1.0");
        metaData.put(PacketManagerConstant.META_REGISTRATION_TYPE, this.registrationDto.getProcess().toUpperCase());
        metaData.put(PacketManagerConstant.META_PRE_REGISTRATION_ID, null);
        return metaData;
    }

    //TODO replace the logic with valid RID generator
    private String generateRID() {
        String timestamp = DateUtils.formatToISOStringWithoutMillis(LocalDateTime.now(ZoneOffset.UTC));
        timestamp = timestamp.replaceAll(":|T|Z|-", "");
        return String.format("100011007710031%s", timestamp);
    }

    //TODO fetch the saved audits and add them in the packet
    public List<Map<String, String>> getAudits() {
        Map<String, String> auditEntry = new HashMap<>();
        auditEntry.put("date", "date");
        auditEntry.put("message", "message");
        auditEntry.put("actor", "operator");
        List<Map<String, String>> audits = new ArrayList<>();
        audits.add(auditEntry);
        return audits;
    }
}
