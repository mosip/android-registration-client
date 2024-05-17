package io.mosip.registration.clientmanager.service.external.impl;


import static java.io.File.separator;


import android.content.Context;
import android.util.Log;

import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.dao.DocumentTypeDao;
import io.mosip.registration.clientmanager.dto.PreRegistrationDto;
import io.mosip.registration.clientmanager.dto.registration.DocumentDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.entity.DocumentType;
import io.mosip.registration.clientmanager.exception.InvalidMachineSpecIDException;
import io.mosip.registration.clientmanager.exception.RegBaseCheckedException;
import io.mosip.registration.clientmanager.exception.RegBaseUncheckedException;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.service.external.PreRegZipHandlingService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.service.CryptoManagerServiceImpl;
import io.mosip.registration.keymanager.service.LocalClientCryptoServiceImpl;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.packetmanager.dto.PacketWriter.Document;
import io.mosip.registration.packetmanager.dto.SimpleType;
import io.mosip.registration.packetmanager.util.ConfigService;
import io.mosip.registration.packetmanager.util.CryptoUtil;
import lombok.Value;


/**
 * This implementation class to handle the pre-registration data
 *
 * @author balamurugan ramamoorthy
 * @since 1.0.0
 *
 */
public class PreRegZipHandlingServiceImpl implements PreRegZipHandlingService {

    private static final String TAG = PreRegZipHandlingServiceImpl.class.getSimpleName();
    private static final String CONTROLTYPE_DOB = "date";
    private static final String CONTROLTYPE_DOB_AGE = "ageDate";

    private final RegistrationService registrationService;

    private final ApplicantValidDocumentDao applicantValidDocumentDao;

    private final IdentitySchemaRepository identitySchemaService;


    private final LocalClientCryptoServiceImpl clientCryptoFacade;


    private final KeyGenerator keyGenerator;


    private CryptoManagerServiceImpl cryptoCore;

    //@Value(staticConstructor = "${mosip.registration.prereg.packet.entires.limit:15}")
    private int THRESHOLD_ENTRIES = 15;

    //@Value("${mosip.registration.prereg.packet.size.limit:200000}")
    private long THRESHOLD_SIZE = 200000;

    //@Value("${mosip.registration.prereg.packet.threshold.ratio:10}")
    private int THRESHOLD_RATIO = 10;

    Context appContext;

    public PreRegZipHandlingServiceImpl(ApplicantValidDocumentDao applicantValidDocumentDao, IdentitySchemaRepository identitySchemaService, LocalClientCryptoServiceImpl clientCryptoFacade, KeyGenerator keyGenerator, RegistrationService registrationService,CryptoManagerServiceImpl cryptoCore) {
        this.applicantValidDocumentDao = applicantValidDocumentDao;
        this.identitySchemaService = identitySchemaService;
        this.clientCryptoFacade = clientCryptoFacade;
        this.keyGenerator = keyGenerator;
        this.registrationService = registrationService;
        this.cryptoCore = cryptoCore;
    }

    /*
     * (non-Javadoc)
     *
     * @see io.mosip.registration.service.external.PreRegZipHandlingService#
     * extractPreRegZipFile(byte[])
     */
    @Override
    public RegistrationDto extractPreRegZipFile(byte[] preRegZipFile) throws Exception {
//        Log.i(TAG,"extractPreRegZipFile invoked");
//        try{
//            int totalEntries = 0;
//            long totalReadArchiveSize = 0;
//
//            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(preRegZipFile))) {
//                ZipEntry zipEntry;
//                while ((zipEntry = zipInputStream.getNextEntry()) != null) {    //NOSONAR Adding Next ZipEntry here.
//                    totalEntries++;
//                    if (zipEntry.getName().equalsIgnoreCase("ID.json")) {
//                        byte[] idjson = IOUtils.toByteArray(zipInputStream);
//                        double compressionRatio = (double)idjson.length / zipEntry.getCompressedSize();
//                        if(compressionRatio > THRESHOLD_RATIO) {
//                            Log.e(TAG,"compression ratio is more than the threshold");
//                            throw new RegBaseCheckedException("PREREG-SYN-003",
//                                    "Pre-reg zip compressed ratio exceeded");
//                        }
//                        totalReadArchiveSize = totalReadArchiveSize + idjson.length;
//                        parseDemographicJson(new String(idjson));
//                        break;
//                    }
//
//                    if(totalEntries > THRESHOLD_ENTRIES) {
//                        Log.e(TAG,"Number of entries in the packet is more than the threshold");
//                        throw new RegBaseCheckedException("PREREG-SYN-001",
//                                "Entries count in pre-reg zip greater than limit");
//                    }
//
//                    if(totalReadArchiveSize > THRESHOLD_SIZE) {
//                        Log.e(TAG,"Archive size read in the packet is more than the threshold");
//                        throw new RegBaseCheckedException("PREREG-SYN-002",
//                                "Pre-reg zip read size is greater than limit");
//                    }
//                }
//            }
//
//            totalEntries = 0;
//            totalReadArchiveSize = 0;
//
//            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(preRegZipFile))) {
//                ZipEntry zipEntry;
//                while ((zipEntry = zipInputStream.getNextEntry()) != null) {    //NOSONAR Adding Next ZipEntry here.
//                    totalEntries++;
//
//                    String fileName = zipEntry.getName();
//                    validateFilename(fileName, ".");
//                    //if (zipEntry.getName().contains("_")) {
//                    Log.i(TAG,"extractPreRegZipFile zipEntry >>>> {}"+ fileName);
//                    Optional<Map.Entry<String, DocumentDto>> result = this.registrationService.getRegistrationDto().getDocuments().entrySet().stream()
//                            .filter(e -> fileName.equals(e.getValue().getValue().concat(".").concat(e.getValue().getFormat()))).findFirst();
//                    if(result.isPresent()) {
//                        Document documentDto = new Document();
//                       // DocumentDto documentDto = result.get().getValue();
//                        documentDto.setDocument(IOUtils.toByteArray(zipInputStream));
//                        totalReadArchiveSize = totalReadArchiveSize + documentDto.getDocument().length;
//
//                        double compressionRatio = (double)documentDto.getDocument().length / zipEntry.getCompressedSize();
//                        if(compressionRatio > THRESHOLD_RATIO) {
//                            Log.e(TAG,"compression ratio is more than the threshold");
//                            throw new RegBaseCheckedException("PREREG-SYN-003",
//                                    "Pre-reg zip compressed ratio exceeded");
//                        }
//
//                        //List<DocumentType> documentTypes = convertStringListToDocumentTypeList(this.applicantValidDocumentDao.findAllDocTypesByDocCategory(documentDto.getType()));
//                        List<String> strings = this.applicantValidDocumentDao.findAllDocTypesByDocCategory(documentDto.getType());
//                        List<DocumentType> documentTypes = new ArrayList<>();
//
//                        for (String str : strings) {
//                            DocumentType documentType = new DocumentType(); // Assuming DocumentType has a constructor that takes a String
//                            documentTypes.add(documentType);
//                        }
//                        if(Objects.nonNull(documentTypes) && !documentTypes.isEmpty()) {
//                            Log.i(TAG,"{} >>>> documentTypes.get(0).getCode() >>>> {}"+documentDto.getType()
//                                    + documentTypes.get(0).getCode());
//                            documentDto.setType(documentTypes.get(0).getCode());
//                            documentDto.setValue(documentDto.getCategory().concat("_").concat(documentDto.getType()));
//                        }
//                        this.registrationService.getRegistrationDto().addAllDocument(result.get().getKey(), result.get().getValue());
//                        Log.i(TAG,"Added zip entry as document for field >>>> {}"+ result.get().getKey());
//                    }
//                    //}
//
//                    if(totalEntries > THRESHOLD_ENTRIES) {
//                        Log.e(TAG,"Number of entries in the packet is more than the threshold"+ totalEntries);
//                        throw new RegBaseCheckedException("PREREG-SYN-001",
//                                "Entries count in pre-reg zip greater than limit");
//                    }
//
//                    if(totalReadArchiveSize > THRESHOLD_SIZE) {
//                        Log.e(TAG,"Archive size read in the packet is more than the threshold"+ totalReadArchiveSize);
//                        throw new RegBaseCheckedException("PREREG-SYN-002",
//                                "Pre-reg zip read size is greater than limit");
//                    }
//                }
//            }
//
//            Iterator<Entry<String, DocumentDto>> entries = this.registrationService.getRegistrationDto().getDocuments().entrySet().iterator();
//            while (entries.hasNext()) {
//                Entry<String, DocumentDto> entry = entries.next();
//                if (entry.getValue().getContent() == null || entry.getValue().getContent().length == 0) {
//                    entries.remove();
//                }
//            }
//        } catch (IOException exception) {
//            Log.e(TAG,exception.getMessage(), exception);
//            throw new RegBaseCheckedException(exception.getErrorCode(), exception.getCause().getMessage());
//        } catch (Exception exception) {
//            Log.e(TAG,exception.getMessage(), exception);
//            throw new RegBaseUncheckedException("REG-SER-ZCM-203", exception.getMessage());
//        }
        return this.registrationService.getRegistrationDto();
    }



    /**
     * This method is used to parse the demographic json and converts it into
     * RegistrationDto
     *
     * @param jsonString
     *            - reader for text file
     * @throws RegBaseCheckedException
     *             - holds the cheked exceptions
     */
//    private void parseDemographicJson(String jsonString) throws RegBaseCheckedException {
//        try {
//            if (!StringUtils.isEmpty(jsonString) && validateDemographicInfoObject()) {
//                JSONObject jsonObject = (JSONObject) new JSONObject(jsonString).get("identity");
//                //Always use latest schema, ignoring missing / removed fields
//                RegistrationDto registrationDTO = this.registrationService.getRegistrationDto();
//                List<FieldSpecDto> fieldList = this.identitySchemaService.getAllFieldSpec(registrationDTO.getProcessId(), registrationDTO.getIdSchemaVersion());
//                this.registrationService.getRegistrationDto().clearRegistrationDto();
//
//                for(FieldSpecDto field : fieldList) {
//                    if(field.getId().equalsIgnoreCase("IDSchemaVersion"))
//                        continue;
//
//                    switch (field.getType()) {
//                        case "documentType":
//                            DocumentDto documentDto = new DocumentDto();
//                            if(jsonObject.has(field.getId()) && jsonObject.get(field.getId()) != null) {
//                                JSONObject fieldValue = jsonObject.getJSONObject(field.getId());
//                                documentDto.setCategory(field.getSubType());
//                                documentDto.setOwner("Applicant");
//                                documentDto.setFormat(fieldValue.getString("format"));
//                                documentDto.setType(fieldValue.getString("type"));
//                                documentDto.setValue(fieldValue.getString("value"));
//                                try {
//                                    documentDto.setRefNumber(fieldValue.has("refNumber") ? fieldValue.getString("refNumber") :
//                                            (fieldValue.has("docRefId") ? fieldValue.getString("docRefId") : null));
//                                } catch(JSONException jsonException) {
//                                    Log.e(TAG,"Unable to find Document Refernce Number for Pre-Reg-Sync : ", jsonException);
//                                }
//                                this.registrationService.getRegistrationDto().addDocument(field.getId(), documentDto);
//                            }
//                            break;
//
//                        case "biometricsType":
//                            break;
//
//                        default:
//                            Object fieldValue = getValueFromJson(field.getId(), field.getType(), jsonObject);
//                            if(fieldValue != null) {
//                                switch (field.getControlType()) {
//                                    case CONTROLTYPE_DOB_AGE:
//                                    case CONTROLTYPE_DOB:
//                                        this.registrationService.getRegistrationDto().setDateField(field.getId(), (String)fieldValue, field.getSubType());
//                                        break;
//                                    default:
//                                        this.registrationService.getRegistrationDto().getDemographics().put(field.getId(), fieldValue);
//                                }
//                            }
//                            break;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG,e.getMessage(), e);
//            throw new RegBaseCheckedException(e.getErrorCode(), e.getMessage());
//        }
//    }


//    private Object getValueFromJson(String key, String fieldType, JSONObject jsonObject) throws IOException, JSONException {
//        if(!jsonObject.has(key))
//            return null;
//
//        try {
//            switch (fieldType) {
//                case "string":	return jsonObject.getString(key);
//                case "integer":	return jsonObject.getInt(key);
//                case "number": return jsonObject.getLong(key);
//                case "simpleType":
//                    List<SimpleType> list = new ArrayList<SimpleType>();
//                    for(int i=0;i<jsonObject.getJSONArray(key).length();i++) {
//                        JSONObject object = jsonObject.getJSONArray(key).getJSONObject(i);
//                        list.add(new SimpleType(object.getString("language"), object.getString("value")));
//                    }
//                    return list;
//            }
//        } catch (Throwable t) {
//            Log.e(TAG,"Failed to parse the pre-reg packet field {}"+ key, t);
//        }
//        return null;
//    }
//
//    private boolean validateDemographicInfoObject() throws Exception {
//        return null != this.registrationService.getRegistrationDto() && this.registrationService.getRegistrationDto().getDemographics() != null;
//    }

    /*
     * (non-Javadoc)
     *
     * @see io.mosip.registration.service.external.PreRegZipHandlingService#
     * encryptAndSavePreRegPacket(java.lang.String, byte[])
     */
    @Override
    public PreRegistrationDto encryptAndSavePreRegPacket(String preRegistrationId, byte[] preRegPacket)
            throws Exception {

        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
        cryptoRequestDto.setValue(io.mosip.registration.keymanager.util.CryptoUtil.base64encoder.encodeToString(preRegPacket));
        // Decrypt the preRegPacket data
        CryptoResponseDto decryptedPacketData = this.clientCryptoFacade.decrypt(cryptoRequestDto);

        if(decryptedPacketData == null) {
            throw new InvalidMachineSpecIDException("Invalid Machine Spec ID found");
        }
        byte[] decodedBytes = io.mosip.registration.keymanager.util.CryptoUtil.base64decoder.decode(decryptedPacketData.toString());

        // Generate AES Session Key
        final SecretKey symmetricKey = this.keyGenerator.generateKey();
        byte[] iv = null;

        byte[] encryptedData = this.cryptoCore.generateAadAndEncryptData(symmetricKey, Arrays.toString(decodedBytes));

        Log.i(TAG,"Pre Registration packet Encrypted");

        String filePath = storePreRegPacketToDisk(preRegistrationId, encryptedData);

        PreRegistrationDto preRegistrationDTO = new PreRegistrationDto();
        preRegistrationDTO.setPacketPath(filePath);
        preRegistrationDTO.setSymmetricKey(CryptoUtil.encodeToURLSafeBase64(symmetricKey.getEncoded()));
        preRegistrationDTO.setEncryptedPacket(encryptedData);
        preRegistrationDTO.setPreRegId(preRegistrationId);
        return preRegistrationDTO;

    }

    /*
     * (non-Javadoc)
     *
     * @see io.mosip.registration.service.external.PreRegZipHandlingService#
     * storePreRegPacketToDisk(java.lang.String, byte[])
     */
    @Override
    public String storePreRegPacketToDisk(String preRegistrationId, byte[] encryptedPacket)
            throws RegBaseCheckedException, RegBaseUncheckedException {
        try {
            // Generate the file path for storing the Encrypted Packet
            String filePath = String
                    .valueOf(ConfigService.getProperty("mosip.registration.registration_pre_reg_packet_location",appContext))
                    .concat(separator).concat(preRegistrationId).concat(".zip");
            // Storing the Encrypted Registration Packet as zip
            FileUtils.copyToFile(new ByteArrayInputStream(encryptedPacket),
                    FileUtils.getFile(FilenameUtils.getFullPath(filePath) + FilenameUtils.getName(filePath)));

            Log.i(TAG, "Pre Registration Encrypted packet saved");

            return filePath;
        } catch (IOException exception) {
            Log.e(TAG,exception.getMessage(), exception);
            throw new RuntimeException(exception);
        } catch (RuntimeException runtimeException) {
            Log.e(TAG,runtimeException.getMessage(), runtimeException);
            throw new RegBaseUncheckedException("REG-SER-STM-211",
                    runtimeException.toString(), runtimeException);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see io.mosip.registration.service.external.PreRegZipHandlingService#
     * decryptPreRegPacket(java.lang.String, byte[])
     */
    @Override
    public byte[] decryptPreRegPacket(String symmetricKey, byte[] encryptedPacket) {
        byte[] secret = CryptoUtil.decodeBase64(symmetricKey);
        SecretKey secretKey = new SecretKeySpec(secret, 0 , secret.length, "AES");
        return cryptoCore.symmetricDecrypt(secretKey, encryptedPacket, null);
    }

    private String validateFilename(String filename, String intendedDir) throws IOException {
        File f = new File(filename);
        String canonicalPath = f.getCanonicalPath();

        File iD = new File(intendedDir);
        String canonicalID = iD.getCanonicalPath();

        if (canonicalPath.startsWith(canonicalID)) {
            return canonicalPath;
        } else {
            throw new IllegalStateException("File is outside extraction target directory.");
        }
    }

}
