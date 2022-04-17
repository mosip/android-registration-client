package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.packetmanager.util.DateUtils;
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
public class RegistrationServiceImpl implements RegistrationService {

    private static final String TAG = RegistrationServiceImpl.class.getSimpleName();
    private static final String SOURCE = "REGISTRATION_CLIENT";
    private static final int MIN_SPACE_REQUIRED_MB = 50;

    private Context context;
    private RegistrationDto registrationDto;

    private RegistrationRepository registrationRepository;
    private IdentitySchemaRepository identitySchemaRepository;
    private PacketWriterService packetWriterService;
    private UserInterfaceHelperService userInterfaceHelperService;
    private MasterDataService masterDataService;

    @Inject
    public RegistrationServiceImpl(Context context, PacketWriterService packetWriterService,
                                   UserInterfaceHelperService userInterfaceHelperService,
                                   RegistrationRepository registrationRepository,
                                   MasterDataService masterDataService,
                                   IdentitySchemaRepository identitySchemaRepository) {
        this.context = context;
        this.registrationDto = null;
        this.packetWriterService = packetWriterService;
        this.userInterfaceHelperService = userInterfaceHelperService;
        this.registrationRepository = registrationRepository;
        this.masterDataService = masterDataService;
        this.identitySchemaRepository = identitySchemaRepository;
    }

    @Override
    public void approveRegistration(Registration registration) {
        //TODO
    }

    @Override
    public void rejectRegistration(Registration registration) {
        //TODO
    }

    @Override
    public RegistrationDto startRegistration(List<String> languages) throws Exception {
        if(registrationDto !=  null) {
            registrationDto.cleanup();
        }

        if(languages.isEmpty())
            throw new Exception("Language is mandatory to begin registration");

        CenterMachineDto centerMachineDto = this.masterDataService.getRegistrationCenterMachineDetails();
        if(centerMachineDto == null)
            throw new Exception("Required master data not found");

        Double version = identitySchemaRepository.getLatestSchemaVersion();
        if(version == null)
            throw new Exception("No Schema found");

        doPreChecksBeforeRegistration(centerMachineDto);

        String timestamp = DateUtils.formatToISOStringWithoutMillis(LocalDateTime.now(ZoneOffset.UTC));
        timestamp = timestamp.replaceAll(":|T|Z|-", "");
        String rid = String.format("%s%s10031%s", centerMachineDto.getCenterId(), centerMachineDto.getMachineId(), timestamp);

        this.registrationDto = new RegistrationDto(rid, "NEW", version, languages);
        return this.registrationDto;
    }

    @Override
    public RegistrationDto getRegistrationDto() throws Exception {
        if(this.registrationDto == null) {
            throw new Exception("Registration not started !");
        }
        return this.registrationDto;
    }

    @Override
    public void submitRegistrationDto() throws Exception {
        if(this.registrationDto == null) {
            throw new Exception("Registration not started !");
        }

        try {
            this.registrationDto.getAllDemographicFields().forEach( entry -> {
                packetWriterService.setField(this.registrationDto.getRId(), entry.getKey(), entry.getValue());
            });

            this.registrationDto.getAllDocumentFields().forEach(entry -> {
                Document document = new Document();
                document.setType(entry.getValue().getType());
                document.setFormat(entry.getValue().getFormat());
                document.setRefNumber(entry.getValue().getRefNumber());
                document.setDocument(entry.getValue().getContent());
                packetWriterService.setDocument(this.registrationDto.getRId(), entry.getKey(), document);
            });

            this.registrationDto.getAllBiometricFields().forEach( entry -> {
                String[] parts = entry.getKey().split("_");
                BiometricRecord biometricRecord = new BiometricRecord();
                //TODO set biometric record
            });

            CenterMachineDto centerMachineDto = this.masterDataService.getRegistrationCenterMachineDetails();

            packetWriterService.addAudits(this.registrationDto.getRId(), getAudits());
            addMetaInfoMap(centerMachineDto.getCenterId(), centerMachineDto.getMachineId());

            String containerPath = packetWriterService.persistPacket(this.registrationDto.getRId(),
                    this.registrationDto.getSchemaVersion().toString(),
                    identitySchemaRepository.getSchemaJson(context, this.registrationDto.getSchemaVersion()),
                    SOURCE,
                    this.registrationDto.getProcess(),
                    true, centerMachineDto.getMachineRefId());

            Log.i(TAG, "Packet created here : " + containerPath);

            if(containerPath == null || containerPath.trim().isEmpty()) {
                throw new Exception("Failed to create registration packet");
            }

            registrationRepository.insertRegistration(this.registrationDto.getRId(), containerPath,
                    centerMachineDto.getCenterId(), "NEW");

        } finally {
            this.registrationDto.cleanup();
            this.registrationDto = null;
        }
    }

    @Override
    public void clearRegistration() {
        if(this.registrationDto != null) {
            this.registrationDto.cleanup();
            this.registrationDto = null;
        }
    }

    private Map<String, String> addMetaInfoMap(String centerId, String machineId) {
        String rid = this.registrationDto.getRId();
        Map<String, String> metaData = new LinkedHashMap<>();
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_MACHINE_ID, machineId);
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_CENTER_ID, centerId);
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_KEYINDEX, "");
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_REGISTRATION_ID, this.registrationDto.getRId());
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_APPLICATION_ID, this.registrationDto.getRId());
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_CREATION_DATE,
                DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC)));
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_CLIENT_VERSION, "1.0");
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_REGISTRATION_TYPE, this.registrationDto.getProcess().toUpperCase());
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_PRE_REGISTRATION_ID, null);
        return metaData;
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

    private void doPreChecksBeforeRegistration(CenterMachineDto centerMachineDto) throws Exception {
        //free space validation
        long externalSpace = context.getExternalCacheDir().getUsableSpace();
        if( (externalSpace / (1024*1024)) < MIN_SPACE_REQUIRED_MB )
            throw new Exception("Minimum required space is not available");

        //is machine and center active
        if(centerMachineDto == null || !centerMachineDto.getCenterStatus() || !centerMachineDto.getMachineStatus())
            throw new Exception("Registrations are not allowed");
    }
}
