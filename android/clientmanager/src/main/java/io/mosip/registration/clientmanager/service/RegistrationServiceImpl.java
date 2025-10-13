package io.mosip.registration.clientmanager.service;

import static io.mosip.registration.keymanager.util.KeyManagerConstant.EMPTY;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.OTHER_KEY_EXCEPTION;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.OTHER_KEY_FORCE_CAPTURED;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.OTHER_KEY_PAYLOAD;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.OTHER_KEY_RETRIES;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.OTHER_KEY_SDK_SCORE;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.OTHER_KEY_SPEC_VERSION;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.biometrics.util.face.FaceBDIR;
import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.config.SessionManager;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.entity.Audit;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.exception.RegBaseCheckedException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BDBInfo;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIRInfo;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.QualityType;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.RegistryIDType;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.SingleType;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.VersionType;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import io.mosip.registration.packetmanager.dto.PacketWriter.Document;
import io.mosip.registration.packetmanager.dto.SimpleType;
import io.mosip.registration.packetmanager.spi.PacketWriterService;
import io.mosip.registration.packetmanager.util.DateUtils;
import io.mosip.registration.packetmanager.util.PacketManagerConstant;
import lombok.NonNull;

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
    private MasterDataService masterDataService;
    private ClientCryptoManagerService clientCryptoManagerService;
    private KeyStoreRepository keyStoreRepository;
    private GlobalParamRepository globalParamRepository;
    private AuditManagerService auditManagerService;
    public static final String BOOLEAN_FALSE = "false";

    @Inject
    public RegistrationServiceImpl(Context context, PacketWriterService packetWriterService,
                                   RegistrationRepository registrationRepository,
                                   MasterDataService masterDataService,
                                   IdentitySchemaRepository identitySchemaRepository,
                                   ClientCryptoManagerService clientCryptoManagerService,
                                   KeyStoreRepository keyStoreRepository,
                                   GlobalParamRepository globalParamRepository,
                                   AuditManagerService auditManagerService) {
        this.context = context;
        this.registrationDto = null;
        this.packetWriterService = packetWriterService;
        this.registrationRepository = registrationRepository;
        this.masterDataService = masterDataService;
        this.identitySchemaRepository = identitySchemaRepository;
        this.clientCryptoManagerService = clientCryptoManagerService;
        this.keyStoreRepository = keyStoreRepository;
        this.globalParamRepository = globalParamRepository;
        this.auditManagerService = auditManagerService;
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
    public RegistrationDto startRegistration(@NonNull List<String> languages, String flowType, String process) throws Exception {
        if (registrationDto != null) {
            registrationDto.cleanup();
        }

        languages.removeIf(item -> item == null || RegistrationConstants.EMPTY_STRING.equals(item));
        if (languages.isEmpty())
            throw new ClientCheckedException(context, R.string.err_000);

        CenterMachineDto centerMachineDto = this.masterDataService.getRegistrationCenterMachineDetails();
        if (centerMachineDto == null)
            throw new ClientCheckedException(context, R.string.err_001);

        Double version = identitySchemaRepository.getLatestSchemaVersion();
        if (version == null)
            throw new ClientCheckedException(context, R.string.err_002);

        String certificateData = keyStoreRepository.getCertificateData(centerMachineDto.getMachineRefId());
        if (certificateData == null)
            throw new ClientCheckedException(context, R.string.err_008);

        doPreChecksBeforeRegistration(centerMachineDto);

        String timestamp = DateUtils.formatToISOStringWithoutMillis(LocalDateTime.now(ZoneOffset.UTC));
        timestamp = timestamp.replaceAll(":|T|Z|-", "");
        String rid = String.format("%s%s10031%s", centerMachineDto.getCenterId(), centerMachineDto.getMachineId(), timestamp);

        Map<Modality, Integer> bioThresholds = new HashMap<>();
        for(Modality modality : Modality.values()) {
            bioThresholds.put(modality, getAttemptsCount(modality));
        }
        this.registrationDto = new RegistrationDto(rid, flowType, process, version, languages, bioThresholds, rid);

        SharedPreferences.Editor editor = this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE).edit();
        editor.putString(SessionManager.RID, this.registrationDto.getRId());
        editor.commit();

        return this.registrationDto;
    }

    @Override
    public RegistrationDto getRegistrationDto() throws Exception {
        if (this.registrationDto == null) {
            throw new ClientCheckedException(context, R.string.err_004);
        }
        return this.registrationDto;
    }

    @Override
    public void submitRegistrationDto(String makerName) throws Exception {
        if (this.registrationDto == null) {
            throw new ClientCheckedException(context, R.string.err_004);
        }

        List<String> selectedHandles = this.globalParamRepository.getSelectedHandles();
        if(selectedHandles != null) {
            if (this.registrationDto.getFlowType().equals("NEW") ||
                 this.registrationDto.getFlowType().equals("Update")) {
                this.registrationDto.getDemographics().put("selectedHandles", selectedHandles);
            }
        }

//        try {
            String individualBiometricsFieldId = this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.INDIVIDUAL_BIOMETRICS_ID);
            String serverVersion = this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION);

            for (String fieldName : this.registrationDto.getDemographics().keySet()) {
                switch (this.registrationDto.getFlowType()) {
                    case "Update":
                        if (this.registrationDto.getDemographics().get(fieldName) != null && (this.registrationDto.getUpdatableFields().contains(fieldName) ||
                                fieldName.equals("UIN")))
                            packetWriterService.setField(this.registrationDto.getRId(), fieldName, this.registrationDto.getDemographics().get(fieldName));
                        break;
                    case "Correction":
                    case "Lost":
                    case "NEW":
                        if (this.registrationDto.getDemographics().get(fieldName) != null)
                            packetWriterService.setField(this.registrationDto.getRId(), fieldName, this.registrationDto.getDemographics().get(fieldName));
                        break;
                }
            }

            this.registrationDto.getAllDocumentFields().forEach(entry -> {
                Document document = new Document();
                document.setType(entry.getValue().getType());
                document.setFormat(entry.getValue().getFormat());
                document.setRefNumber(entry.getValue().getRefNumber());
                document.setDocument(("pdf".equalsIgnoreCase(entry.getValue().getFormat()))?combineByteArray(entry.getValue().getContent()):convertImageToPDF(entry.getValue().getContent()));
                Log.i(TAG, entry.getKey() + " >> PDF document size :" + document.getDocument().length);
                packetWriterService.setDocument(this.registrationDto.getRId(), entry.getKey(), document);
            });

            if (serverVersion!=null && serverVersion.startsWith("1.1.5")) {
                this.registrationDto.getBestBiometrics(individualBiometricsFieldId, Modality.EXCEPTION_PHOTO).forEach( b -> {
                    Document document = new Document();
                    document.setType("EOP");
                    document.setFormat("jpg");
                    document.setValue("POE_EOP");
                    document.setDocument(convertImageToBytes(b.getBioValue()));
                    Log.i(TAG,"Adding Proof of Exception document with size :" + document.getDocument().length);
                    packetWriterService.setDocument(this.registrationDto.getRId(), "proofOfException", document);
                });
            }

            this.registrationDto.CAPTURED_BIO_FIELDS.forEach( field -> {
                BiometricRecord biometricRecord = getBiometricRecord(field, serverVersion);
                biometricRecord.getSegments().removeIf(Objects::isNull);
                packetWriterService.setBiometric(this.registrationDto.getRId(), field, biometricRecord);
            });

            CenterMachineDto centerMachineDto = this.masterDataService.getRegistrationCenterMachineDetails();

            packetWriterService.addAudits(this.registrationDto.getRId(), getAudits());
            addMetaInfoMap(centerMachineDto.getCenterId(), centerMachineDto.getMachineId(), makerName);

            String containerPath = packetWriterService.persistPacket(this.registrationDto.getRId(),
                    this.registrationDto.getSchemaVersion().toString(),
                    identitySchemaRepository.getSchemaJson(context, this.registrationDto.getSchemaVersion()),
                    SOURCE,
                    this.registrationDto.getProcess(),
                    true, centerMachineDto.getMachineRefId());

            Log.i(TAG, "Packet created : " + containerPath);

            if (containerPath == null || containerPath.trim().isEmpty()) {
                throw new ClientCheckedException(context, R.string.err_005);
            }

            JSONObject additionalInfo = new JSONObject();
            additionalInfo.put("langCode", this.registrationDto.getSelectedLanguages().get(0));
            //TODO add name, phone and email in additional info
            List<String> fullName = new ArrayList<>();
            String fullNameKey = getKey(this.registrationDto, RegistrationConstants.UI_SCHEMA_SUBTYPE_FULL_NAME);
            if(fullNameKey != null) {
                List<String> fullNameKeys = Arrays.asList(fullNameKey.split(RegistrationConstants.COMMA));
                for (String key : fullNameKeys) {
                    Object fullNameObj = this.registrationDto.getDemographics().get(key);
                    fullName.add(getAdditionalInfo(fullNameObj));
                }
            }

            Object emailObj = this.registrationDto.getDemographics().get(getKey(this.registrationDto, RegistrationConstants.UI_SCHEMA_SUBTYPE_EMAIL));
            Object phoneObj = this.registrationDto.getDemographics().get(getKey(this.registrationDto, RegistrationConstants.UI_SCHEMA_SUBTYPE_PHONE));

            additionalInfo.put("name", String.join(" ", fullName));
            additionalInfo.put("email", getAdditionalInfo(emailObj));
            additionalInfo.put("phone", getAdditionalInfo(phoneObj));
            
            registrationRepository.insertRegistration(this.registrationDto.getRId(), containerPath,
                    centerMachineDto.getCenterId(), this.registrationDto.getProcess(), additionalInfo, this.registrationDto.getAdditionalInfoRequestId());

//        } finally {
            clearRegistration();
//        }
    }

    private String getKey(RegistrationDto registrationDTO, String subType) throws Exception {
        List<String> key = new ArrayList<>();
        List<FieldSpecDto> schemaFields = identitySchemaRepository.getProcessSpecFields(context, registrationDTO.getProcess());
        for (FieldSpecDto schemaField : schemaFields) {
            if (schemaField.getSubType() != null && schemaField.getSubType().equalsIgnoreCase(subType)) {
                if (subType.equalsIgnoreCase(RegistrationConstants.UI_SCHEMA_SUBTYPE_FULL_NAME)) {
                    key.add(schemaField.getId());
                } else {
                    key.add(schemaField.getId());
                    break;
                }
            }
        }
        return String.join(RegistrationConstants.COMMA, key);
    }

    private byte[] combineByteArray(List<byte[]> byteList) {
        int totalLength = byteList.stream().mapToInt(byteArr -> byteArr.length).sum();
        byte[] result = new byte[totalLength];
        int currentPos = 0;
        for (byte[] byteArr : byteList) {
            System.arraycopy(byteArr, 0, result, currentPos, byteArr.length);
            currentPos += byteArr.length;
        }
        return result;
    }

    private String getAdditionalInfo(Object fieldValue) {
        if(fieldValue == null) { return null; }

        if (fieldValue instanceof List<?>) {
            Optional<SimpleType> demoValueInRequiredLang = ((List<SimpleType>) fieldValue).stream()
                    .filter(valueDTO -> valueDTO.getLanguage().equals(this.registrationDto.getSelectedLanguages().get(0))).findFirst();

            if (demoValueInRequiredLang.isPresent()) {
                return demoValueInRequiredLang.get().getValue();
            }
        }

        if (fieldValue instanceof String) {
            return (String) fieldValue;
        }
        return null;
    }

    private byte[] convertImageToBytes(String bioValue) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(bioValue));
            DataInputStream inputStream = new DataInputStream(bais);
            FaceBDIR faceBDIR = new FaceBDIR(inputStream);
            return faceBDIR.getRepresentation().getRepresentationData().getImageData().getImage();
        } catch (IOException e) {
            Log.e(TAG, "Failed to convert exception image to bytes", e);
        }
        return null;
    }

    @Override
    public void clearRegistration() {
        SharedPreferences.Editor editor = this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE).edit();
        editor.remove(SessionManager.RID);
        editor.commit();
        if (this.registrationDto != null) {
            this.registrationDto.cleanup();
            this.registrationDto = null;
        }
    }

    private BiometricRecord getBiometricRecord(String fieldId, String serverVersion) {
        BiometricRecord biometricRecord = new BiometricRecord();
        this.registrationDto.getBestBiometrics(fieldId, Modality.FINGERPRINT_SLAB_LEFT).forEach( b -> {
            biometricRecord.getSegments().add(buildBIR(b));
        });
        this.registrationDto.getBestBiometrics(fieldId, Modality.FINGERPRINT_SLAB_RIGHT).forEach( b -> {
            biometricRecord.getSegments().add(buildBIR(b));
        });
        this.registrationDto.getBestBiometrics(fieldId, Modality.FINGERPRINT_SLAB_THUMBS).forEach( b -> {
            biometricRecord.getSegments().add(buildBIR(b));
        });
        this.registrationDto.getBestBiometrics(fieldId, Modality.IRIS_DOUBLE).forEach( b -> {
            biometricRecord.getSegments().add(buildBIR(b));
        });
        this.registrationDto.getBestBiometrics(fieldId, Modality.FACE).forEach( b -> {
            biometricRecord.getSegments().add(buildBIR(b));
        });
        if (!serverVersion.startsWith("1.1.5")) {
            this.registrationDto.getBestBiometrics(fieldId, Modality.EXCEPTION_PHOTO).forEach( b -> {
                biometricRecord.getSegments().add(buildBIR(b));
            });
            this.registrationDto.EXCEPTIONS.forEach((key, value) -> {
                value.forEach((v) -> {
                    Modality modality = Modality.getModality(v);
                    String bioSubtype = Modality.getSpecBioSubType(v);
                    String bioValue = null;
                    String specVersion = null;
                    boolean isException = true;
                    String decodedBioResponse = null;
                    String signature = null;
                    boolean isForceCaptured = false;
                    int numOfRetries = 0;
                    double sdkScore = 0;
                    float qualityScore = 0;
                    BiometricsDto exceptionBiometricDto =
                            new BiometricsDto(modality.getSingleType().value(), bioSubtype, bioValue, specVersion, isException, decodedBioResponse, signature, isForceCaptured, numOfRetries, sdkScore, qualityScore);
                    biometricRecord.getSegments().add(buildBIR(exceptionBiometricDto));
                });
            });
        }
        return biometricRecord;
    }

    private void addMetaInfoMap(String centerId, String machineId, String makerId) throws Exception {
        String rid = this.registrationDto.getRId();
        //machine metaInfo
        Map<String, String> metaData = new LinkedHashMap<>();
        metaData.put(PacketManagerConstant.META_MACHINE_ID, machineId);
        metaData.put(PacketManagerConstant.META_CENTER_ID, centerId);
        metaData.put(PacketManagerConstant.META_KEYINDEX, this.clientCryptoManagerService.getClientKeyIndex());
        metaData.put(PacketManagerConstant.META_REGISTRATION_ID, rid);
        metaData.put(PacketManagerConstant.META_APPLICATION_ID, rid);
        metaData.put(PacketManagerConstant.META_CREATION_DATE,
                DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC)));
        metaData.put(PacketManagerConstant.META_CLIENT_VERSION, BuildConfig.CLIENT_VERSION);
        metaData.put(PacketManagerConstant.META_REGISTRATION_TYPE, this.registrationDto.getProcess().toUpperCase());
        metaData.put(PacketManagerConstant.META_PRE_REGISTRATION_ID, null);
        metaData.put("langCodes", String.join(RegistrationConstants.COMMA, this.registrationDto.getSelectedLanguages()));
        packetWriterService.addMetaInfo(rid, "metaData", getLabelValueDTOListString(metaData));

        //Operators metaInfo
        metaData = new LinkedHashMap<>();
        metaData.put(PacketManagerConstant.META_OFFICER_ID, makerId);
        metaData.put(PacketManagerConstant.META_OFFICER_BIOMETRIC_FILE, null);
        metaData.put(PacketManagerConstant.META_SUPERVISOR_ID, makerId);
        metaData.put(PacketManagerConstant.META_SUPERVISOR_BIOMETRIC_FILE, null);
        metaData.put(PacketManagerConstant.META_SUPERVISOR_PWD, "true");
        metaData.put(PacketManagerConstant.META_OFFICER_PWD, "true");
        metaData.put(PacketManagerConstant.META_SUPERVISOR_PIN, BOOLEAN_FALSE);
        metaData.put(PacketManagerConstant.META_OFFICER_PIN, BOOLEAN_FALSE);
        metaData.put(PacketManagerConstant.META_SUPERVISOR_OTP, BOOLEAN_FALSE);
        metaData.put(PacketManagerConstant.META_OFFICER_OTP, BOOLEAN_FALSE);
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_INFO_OPERATIONS_DATA, getLabelValueDTOListString(metaData));

        //other metaInfo
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_LATITUDE, "null");
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.META_LONGITUDE, "null");
        packetWriterService.addMetaInfo(rid, "checkSum", "{}");
        packetWriterService.addMetaInfo(rid, PacketManagerConstant.REGISTRATIONID, rid);

        //biometric device details
        List<Map<String, Object>> capturedRegisteredDevices = new ArrayList<>();
        for(Modality modality : this.registrationDto.BIO_DEVICES.keySet()) {
            capturedRegisteredDevices.add((Map<String, Object>) this.registrationDto.BIO_DEVICES.get(modality));
        }
        packetWriterService.addMetaInfo(rid, "capturedRegisteredDevices", capturedRegisteredDevices);
    }

    private List<Map<String, String>> getLabelValueDTOListString(Map<String, String> metaInfoMap) {
        List<Map<String, String>> labelValueMap = new LinkedList<>();
        for (Map.Entry<String, String> fieldName : metaInfoMap.entrySet()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("label", fieldName.getKey());
            map.put("value", fieldName.getValue());
            labelValueMap.add(map);
        }
        return labelValueMap;
    }

    public List<Map<String, String>> getAudits() {
        List<Map<String, String>> audits = new ArrayList<>();
        String savedPoint = globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.AUDIT_EXPORTED_TILL);
        List<Audit> list = auditManagerService.getAuditLogs(savedPoint==null ? 0 : Long.parseLong(savedPoint));
        for(Audit audit : list) {
            Map<String, String> auditMap = new HashMap<>();
            auditMap.put("uuid", String.valueOf(audit.getUuid()));
            auditMap.put("createdAt", DateUtils.parseEpochToISOString(audit.getCreatedAt()));
            auditMap.put("eventId", audit.getEventId());
            auditMap.put("eventName", audit.getEventName());
            auditMap.put("eventType", audit.getEventType());
            auditMap.put("hostName", audit.getHostName());
            auditMap.put("hostIp", audit.getHostIp());
            auditMap.put("applicationId", audit.getApplicationId());
            auditMap.put("applicationName", audit.getApplicationName());
            auditMap.put("sessionUserId", audit.getSessionUserId());
            auditMap.put("sessionUserName", audit.getSessionUserName());
            auditMap.put("id", audit.getRefId());
            auditMap.put("idType", audit.getRefIdType());
            auditMap.put("createdBy", audit.getCreatedBy());
            auditMap.put("moduleName", audit.getModuleName());
            auditMap.put("moduleId", audit.getModuleId());
            auditMap.put("description", audit.getDescription());
            auditMap.put("actionTimeStamp", DateUtils.parseEpochToISOString(audit.getActionTimeStamp()));
            audits.add(auditMap);
        }
        globalParamRepository.saveGlobalParam(RegistrationConstants.AUDIT_EXPORTED_TILL,
                list.isEmpty() ? null : String.valueOf(list.get(list.size()-1).getActionTimeStamp()));
        return audits;
    }

    private void doPreChecksBeforeRegistration(CenterMachineDto centerMachineDto) throws Exception {
        //free space validation
        long externalSpace = context.getExternalCacheDir().getUsableSpace();
        if ((externalSpace / (1024 * 1024)) < MIN_SPACE_REQUIRED_MB)
            throw new ClientCheckedException(context, R.string.err_006);

        //is machine and center active
        if (centerMachineDto == null || !centerMachineDto.getCenterStatus() || !centerMachineDto.getMachineStatus())
            throw new ClientCheckedException(context, R.string.err_007);
    }

    private byte[] convertImageToPDF(List<byte[]> images) {
        try (PDDocument pdDocument = new PDDocument();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            for (byte[] image : images) {
                PDPage pdPage = new PDPage();
                Log.i(TAG, "image size after compression :" + image.length);
                PDImageXObject pdImageXObject = PDImageXObject.createFromByteArray(pdDocument, image, "");
                int[] scaledDimension = getScaledDimension(pdImageXObject.getWidth(), pdImageXObject.getHeight(),
                        (int) pdPage.getMediaBox().getWidth(), (int) pdPage.getMediaBox().getHeight());
                try (PDPageContentStream contentStream = new PDPageContentStream(pdDocument, pdPage)) {
                    float startx = (pdPage.getMediaBox().getWidth() - scaledDimension[0]) / 2;
                    float starty = (pdPage.getMediaBox().getHeight() - scaledDimension[1]) / 2;
                    contentStream.drawImage(pdImageXObject, startx, starty, scaledDimension[0], scaledDimension[1]);
                }
                pdDocument.addPage(pdPage);
            }
            pdDocument.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, "Failed to convert bufferedImages to PDF", e);
        }
        return null;
    }

    private byte[] getCompressedImage(byte[] image, Float compressionQuality) {
        //TODO compress image
        return image;
    }

    private static int[] getScaledDimension(int originalWidth, int originalHeight, int boundWidth,
                                            int boundHeight) {
        int new_width = originalWidth;
        int new_height = originalHeight;

        // first check if we need to scale width
        if (originalWidth > boundWidth) {
            //scale width to fit
            new_width = boundWidth;
            //scale height to maintain aspect ratio
            new_height = (new_width * originalHeight) / originalWidth;
        }

        // then check if we need to scale even with the new height
        if (new_height > boundHeight) {
            //scale height to fit instead
            new_height = boundHeight;
            //scale width to maintain aspect ratio
            new_width = (new_height * originalWidth) / originalHeight;
        }

        return new int[]{new_width, new_height};
    }

    public BIR buildBIR(BiometricsDto biometricsDto) {
        if(biometricsDto == null)
            return null;
        SingleType singleType = SingleType.fromValue(biometricsDto.getModality());
        byte[] iso = biometricsDto.getBioValue() == null ? null : CryptoUtil.base64decoder.decode(biometricsDto.getBioValue());
        // Format
        RegistryIDType birFormat = new RegistryIDType();
        birFormat.setOrganization(PacketManagerConstant.CBEFF_DEFAULT_FORMAT_ORG);
        birFormat.setType(String.valueOf(Modality.getFormatType(singleType)));

        BiometricType biometricType = BiometricType.fromValue(singleType.value());
        // Algorithm
        RegistryIDType birAlgorithm = new RegistryIDType();
        birAlgorithm.setOrganization(PacketManagerConstant.CBEFF_DEFAULT_ALG_ORG);
        birAlgorithm.setType(PacketManagerConstant.CBEFF_DEFAULT_ALG_TYPE);

        // Quality Type
        QualityType qualityType = new QualityType();
        qualityType.setAlgorithm(birAlgorithm);
        qualityType.setScore((long) biometricsDto.getQualityScore());
        VersionType versionType = new VersionType(1, 1);


        String payLoad = null;
        if (iso != null) {
            int bioValueKeyIndex = biometricsDto.getDecodedBioResponse().indexOf(PacketManagerConstant.BIOVALUE_KEY) + (PacketManagerConstant.BIOVALUE_KEY.length() + 1);
            int bioValueStartIndex = biometricsDto.getDecodedBioResponse().indexOf('"', bioValueKeyIndex);
            int bioValueEndIndex = biometricsDto.getDecodedBioResponse().indexOf('"', (bioValueStartIndex + 1));
            String bioValue = biometricsDto.getDecodedBioResponse().substring(bioValueStartIndex, (bioValueEndIndex + 1));
            payLoad = biometricsDto.getDecodedBioResponse().replace(bioValue, PacketManagerConstant.BIOVALUE_PLACEHOLDER);
        }

        if(singleType == SingleType.FACE || singleType == SingleType.EXCEPTION_PHOTO)
            biometricsDto.setBioSubType(EMPTY);

        return new BIR.BIRBuilder().withBdb(iso == null ? new byte[0] : iso)
                .withVersion(versionType)
                .withCbeffversion(versionType)
                .withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
                .withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(birFormat).withQuality(qualityType)
                        .withType(biometricType).withSubtype(biometricsDto.getBioSubType())
                        .withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
                        .withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).withIndex(UUID.randomUUID().toString())
                        .build())
                .withSb(biometricsDto.getSignature() == null ? new byte[0] : biometricsDto.getSignature().getBytes(StandardCharsets.UTF_8))
                .withOthers(OTHER_KEY_EXCEPTION, iso == null ? "true" : BOOLEAN_FALSE)
                .withOthers(OTHER_KEY_RETRIES, biometricsDto.getNumOfRetries() + EMPTY)
                .withOthers(OTHER_KEY_SDK_SCORE, biometricsDto.getSdkScore() + EMPTY)
                .withOthers(OTHER_KEY_FORCE_CAPTURED, biometricsDto.isForceCaptured() + EMPTY)
                .withOthers(OTHER_KEY_PAYLOAD, payLoad == null ? EMPTY : payLoad)
                .withOthers(OTHER_KEY_SPEC_VERSION, biometricsDto.getSpecVersion() == null ? EMPTY : biometricsDto.getSpecVersion())
                .build();
    }

    private int getAttemptsCount(Modality modality) {
        switch (modality) {
            case FINGERPRINT_SLAB_LEFT:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.LEFT_SLAP_ATTEMPTS_KEY);
            case FINGERPRINT_SLAB_RIGHT:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.RIGHT_SLAP_ATTEMPTS_KEY);
            case FINGERPRINT_SLAB_THUMBS:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.THUMBS_ATTEMPTS_KEY);
            case IRIS_DOUBLE:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.IRIS_ATTEMPTS_KEY);
            case FACE:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.FACE_ATTEMPTS_KEY);
        }
        return 0;
    }
}
