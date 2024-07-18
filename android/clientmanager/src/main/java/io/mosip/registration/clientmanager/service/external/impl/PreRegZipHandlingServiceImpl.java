package io.mosip.registration.clientmanager.service.external.impl;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.PreRegistrationDto;
import io.mosip.registration.clientmanager.dto.registration.DocumentDto;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.entity.Location;
import io.mosip.registration.clientmanager.exception.RegBaseCheckedException;
import io.mosip.registration.clientmanager.exception.RegBaseUncheckedException;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.service.external.PreRegZipHandlingService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.packetmanager.dto.SimpleType;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import io.mosip.registration.packetmanager.util.ConfigService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.util.PacketKeeper;



public class PreRegZipHandlingServiceImpl implements PreRegZipHandlingService {

    private static final String TAG = PreRegZipHandlingServiceImpl.class.getSimpleName();
    private static final String CONTROLTYPE_DOB = "date";
    private static final String CONTROLTYPE_DOB_AGE = "ageDate";
    private static final String SEPARATOR = File.separator;
    private static final String ZIP = ".zip";
    private final RegistrationService registrationService;
    private final ApplicantValidDocumentDao applicantValidDocumentDao;
    private final IdentitySchemaRepository identitySchemaService;
    private ClientCryptoManagerService clientCryptoFacade;
    private PacketKeeper packetKeeper;
    private CryptoManagerService cryptoManagerService;

    //@Value(staticConstructor = "${mosip.registration.prereg.packet.entires.limit:15}")
    private int THRESHOLD_ENTRIES = 15;

    //@Value("${mosip.registration.prereg.packet.size.limit:200000}")
    private long THRESHOLD_SIZE = 200000;

    //@Value("${mosip.registration.prereg.packet.threshold.ratio:10}")
    private int THRESHOLD_RATIO = 10;
    private String BASE_LOCATION;

    private IPacketCryptoService iPacketCryptoService;

    private Map<String, DocumentDto> documents;
    MasterDataService masterDataService;

    Context appContext;

    public PreRegZipHandlingServiceImpl(Context appContext,ApplicantValidDocumentDao applicantValidDocumentDao, IdentitySchemaRepository identitySchemaService, ClientCryptoManagerService clientCryptoFacade,RegistrationService registrationService,CryptoManagerService cryptoManagerService,PacketKeeper packetKeeper,IPacketCryptoService iPacketCryptoService,MasterDataService masterDataService) {
        this.appContext = appContext;
        this.applicantValidDocumentDao = applicantValidDocumentDao;
        this.identitySchemaService = identitySchemaService;
        this.clientCryptoFacade = clientCryptoFacade;
        this.registrationService = registrationService;
        this.cryptoManagerService = cryptoManagerService;
        this.packetKeeper = packetKeeper;
        this.iPacketCryptoService = iPacketCryptoService;
        this.documents = new HashMap<>();
        this.masterDataService = masterDataService;
        try {
            initPreRegAdapter(appContext);
        } catch (Exception e) {
            Log.e(TAG, "PreRegAdapter: Failed Initialization", e);
        }
    }


    @Override
    public RegistrationDto extractPreRegZipFile(byte[] preRegZipFile) throws Exception {
        Log.i(TAG,"extractPreRegZipFile invoked");
        try{
            int totalEntries = 0;
            long totalReadArchiveSize = 0;

            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(preRegZipFile))) {
                ZipEntry zipEntry;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {    //NOSONAR Adding Next ZipEntry here.
                    totalEntries++;
                    if (zipEntry.getName().equalsIgnoreCase("ID.json")) {
                        byte[] idjson = IOUtils.toByteArray(zipInputStream);
                        double compressionRatio = (double)idjson.length / zipEntry.getCompressedSize();
                        if(compressionRatio > THRESHOLD_RATIO) {
                            Log.e(TAG,"compression ratio is more than the threshold");
                            throw new RegBaseCheckedException("PREREG-SYN-003",
                                    "Pre-reg zip compressed ratio exceeded");
                        }
                        totalReadArchiveSize = totalReadArchiveSize + idjson.length;
                        parseDemographicJson(new String(idjson));
                        break;
                    }

                    if(totalEntries > THRESHOLD_ENTRIES) {
                        Log.e(TAG,"Number of entries in the packet is more than the threshold");
                        throw new RegBaseCheckedException("PREREG-SYN-001",
                                "Entries count in pre-reg zip greater than limit");
                    }

                    if(totalReadArchiveSize > THRESHOLD_SIZE) {
                        Log.e(TAG,"Archive size read in the packet is more than the threshold");
                        throw new RegBaseCheckedException("PREREG-SYN-002",
                                "Pre-reg zip read size is greater than limit");
                    }
                }
            }

            totalEntries = 0;
            totalReadArchiveSize = 0;

            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(preRegZipFile))) {
                ZipEntry zipEntry;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {    //NOSONAR Adding Next ZipEntry here.
                    totalEntries++;

                    String fileName = zipEntry.getName();
                    validateFilename(fileName, ".");
                    //if (zipEntry.getName().contains("_")) {
                    Optional<Entry<String, DocumentDto>> result = this.registrationService.getRegistrationDto().getDocuments().entrySet().stream()
                            .filter(e -> {
                                return fileName.equals(e.getValue().getValue().concat(".").concat(e.getValue().getFormat()));
                            }).findFirst();

                    if(result.isPresent()) {
                        byte[] entryData = readZipInputStreamToByteArray(zipInputStream);
                        DocumentDto documentDto = result.get().getValue();

                        totalReadArchiveSize = totalReadArchiveSize +IOUtils.toByteArray(zipInputStream).length;

                        double compressionRatio = (double)IOUtils.toByteArray(zipInputStream).length / zipEntry.getCompressedSize();
                        if(compressionRatio > THRESHOLD_RATIO) {
                            Log.e(TAG,"compression ratio is more than the threshold");
                            throw new RegBaseCheckedException("PREREG-SYN-003",
                                    "Pre-reg zip compressed ratio exceeded");
                        }

                        // Find the index of the underscore character
                        int underscoreIndex = documentDto.getValue().toString().indexOf('_');

                        // Extract the substring from the beginning of the string to the underscore
                        String croppedString = documentDto.getValue().toString().substring(0, underscoreIndex);

                        List<String> documentTypes = this.applicantValidDocumentDao.findAllDocTypesByDocCategory(croppedString);
                        List<String> documentNameType = new ArrayList<>();

                        documentTypes.forEach(e -> {
                            List<String> dataType = this.applicantValidDocumentDao.findAllDocTypesByCode(e);
                            documentNameType.add(dataType.get(0));
                        });

                        this.registrationService.getRegistrationDto().addDocument(result.get().getKey(), documentDto.getType(),documentDto.getFormat(), documentDto.getRefNumber(),entryData);
                        Log.i(TAG,"Added zip entry as document for field >>>> {}"+ result.get().getKey());
                    }
                    //}

                    if(totalEntries > THRESHOLD_ENTRIES) {
                        Log.e(TAG,"Number of entries in the packet is more than the threshold"+ totalEntries);
                        throw new RegBaseCheckedException("PREREG-SYN-001",
                                "Entries count in pre-reg zip greater than limit");
                    }

                    if(totalReadArchiveSize > THRESHOLD_SIZE) {
                        Log.e(TAG,"Archive size read in the packet is more than the threshold"+ totalReadArchiveSize);
                        throw new RegBaseCheckedException("PREREG-SYN-002",
                                "Pre-reg zip read size is greater than limit");
                    }
                }
            }

            Iterator<Entry<String, DocumentDto>> entries = this.registrationService.getRegistrationDto().getDocuments().entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, DocumentDto> entry = entries.next();
                if (entry.getValue().getContent() == null || entry.getValue().getContent().size() == 0) {
                    entries.remove();
                }
            }
        } catch (IOException exception) {
            Log.e(TAG,exception.getMessage(), exception);
           // throw new RegBaseCheckedException(exception.getErrorCode(), exception.getCause().getMessage());
        } catch (Exception exception) {
            Log.e(TAG,exception.getMessage(), exception);
            throw new RegBaseUncheckedException("REG-SER-ZCM-203", exception.getMessage());
        }
        return this.registrationService.getRegistrationDto();
    }

    public static byte[] readZipInputStreamToByteArray(ZipInputStream zipInputStream) throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = zipInputStream.read(buffer)) > 0) {
            byteArray.write(buffer, 0, len);
        }
        return byteArray.toByteArray();
    }

    private void parseDemographicJson(String jsonString) throws RegBaseCheckedException {
        try {
            if (!StringUtils.isEmpty(jsonString) && validateDemographicInfoObject()) {

                JSONObject jsonObject = (JSONObject) new JSONObject(jsonString).get("identity");
                //Always use latest schema, ignoring missing / removed fields
                RegistrationDto registrationDto = this.registrationService.getRegistrationDto();
                List<FieldSpecDto> fieldList = this.identitySchemaService.getAllFieldSpec(appContext, registrationDto.getSchemaVersion());
                this.registrationService.getRegistrationDto().getDocuments().clear();
                this.registrationService.getRegistrationDto().getDemographics().clear();

                for(FieldSpecDto field : fieldList) {
                    if(field.getId().equalsIgnoreCase("IDSchemaVersion"))
                        continue;

                    switch (field.getType()) {
                        case "documentType":
                            if(jsonObject.has(field.getId()) && jsonObject.get(field.getId()) != null) {
                                JSONObject fieldValue = jsonObject.getJSONObject(field.getId());

                               this.registrationService.getRegistrationDto().addWithoutDocument(field.getId(), fieldValue.optString("type",""),fieldValue.optString("format",""),fieldValue.optString("value",""),fieldValue.optString("refNumber",""));
                            }
                            break;

                        case "biometricsType":
                            break;

                        default:
                            Object fieldValue = getValueFromJson(field.getId(), field.getType(), jsonObject);
                            if(fieldValue != null) {
                                switch (field.getControlType()) {
                                    case CONTROLTYPE_DOB_AGE:
                                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

                                        // Parse the string into a LocalDate object
                                        LocalDate date = LocalDate.parse(fieldValue.toString(), dateFormatter);

                                        // Extract year, month, and day
                                        String year = String.valueOf(date.getYear());
                                        String month = String.valueOf(date.getMonthValue());
                                        String day = String.valueOf(date.getDayOfMonth());

                                        this.registrationService.getRegistrationDto().setDateField(field.getId(),field.getSubType(), day, month, year);
                                        break;
                                    case CONTROLTYPE_DOB:
                                       // this.registrationService.getRegistrationDto().setDateField(field.getId(), (String)fieldValue, field.getSubType());
                                        break;
                                    default:
                                        this.registrationService.getRegistrationDto().getDemographics().put(field.getId(), fieldValue);
                                }
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG,e.getMessage(), e);
            throw new RegBaseCheckedException(e.getMessage(), e.getMessage());
        }
    }


    private Object getValueFromJson(String key, String fieldType, JSONObject jsonObject) throws IOException, JSONException {
        if(!jsonObject.has(key))
            return null;

        try {
            switch (fieldType) {
                case "string":	return jsonObject.getString(key);
                case "integer":	return jsonObject.getInt(key);
                case "number": return jsonObject.getLong(key);
                case "simpleType":
                    List<SimpleType> list = new ArrayList<SimpleType>();

                    for(int i=0;i<jsonObject.getJSONArray(key).length();i++) {
                        JSONObject object = jsonObject.getJSONArray(key).getJSONObject(i);
                        String language = object.optString("language", "");
                        String value = object.optString("value", "");

                        List<GenericValueDto> genericValueList = this.masterDataService.getFieldValues(key, language);
                        List<Location> locationList = this.masterDataService.findAllLocationsByLangCode("eng");
                        Map<String, String> hierarchyMap = new HashMap<>();
                        locationList.forEach((locationHierarchy) -> {
                            String levelName = locationHierarchy.getHierarchyName();
                            int level = locationHierarchy.getHierarchyLevel();
                            hierarchyMap.put("" + level, levelName);
                        });

                        List<String> reverseHierarchy = new ArrayList<>();
                        hierarchyMap.forEach((keysData, values) -> {
                            reverseHierarchy.add(values.toLowerCase());
                        });

                        int index = reverseHierarchy.indexOf(key);
                        List<GenericValueDto> genericValueListDynamic = null;

                        if(index == 1) {
                        genericValueListDynamic = this.masterDataService.findLocationByHierarchyLevel(index, language);
                       }
                        String updatedName = "";
                        for (GenericValueDto dto : genericValueList) {
                            if (dto.getCode().equalsIgnoreCase(value)) {
                                updatedName = dto.getName();
                                break;
                            }
                        }
                        if(genericValueListDynamic!=null) {
                            for (GenericValueDto dto : genericValueListDynamic) {
                                if (dto.getCode().equalsIgnoreCase(value)) {
                                    updatedName = dto.getName();
                                    break;
                                }
                            }
                        }
                        list.add(new SimpleType(object.optString("language", ""), object.optString("value",""), updatedName!= "" ? updatedName : value));
                    }
                    return list;
            }
        } catch (Throwable t) {
            Log.e(TAG,"Failed to parse the pre-reg packet field {}"+ key, t);
        }
        return null;
    }

    private boolean validateDemographicInfoObject() throws Exception {
        if (this.registrationService == null) {
            return false;
        }
        try {
            RegistrationDto  registrationDto = this.registrationService.getRegistrationDto();
            if (registrationDto == null || registrationDto.getDemographics() == null) {
                Log.e(TAG, "Demographic information is null");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG,"demographic validation..."+e.getMessage());
            throw new RuntimeException(e);
        }

        //return true;
        return null != this.registrationService.getRegistrationDto() && this.registrationService.getRegistrationDto().getDemographics() != null;
    }

    @Override
    public PreRegistrationDto encryptAndSavePreRegPacket(String preRegistrationId, String preRegPacket, CenterMachineDto centerMachineDto) {
        PreRegistrationDto preRegistrationDto = new PreRegistrationDto();
        try{
            CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
            cryptoRequestDto.setValue(preRegPacket);
            // Decrypt the preRegPacket data
            CryptoResponseDto decryptedPacketData = clientCryptoFacade.decrypt(cryptoRequestDto);

            byte[] decodedBytes = CryptoUtil.base64decoder.decode(decryptedPacketData.getValue());

            KeyGenerator keyGenerator = cryptoManagerService.generateAESKey(256);
            // Generate AES Session Key
            final SecretKey symmetricKey =  keyGenerator.generateKey();

            byte[] encryptedData = this.cryptoManagerService.symmetricEncryptWithRandomIV(symmetricKey, decodedBytes,null);

            Log.i(TAG,"Pre Registration packet Encrypted "+encryptedData);

            String filePath = storePreRegPacketToDisk(preRegistrationId, encryptedData,centerMachineDto);

            Log.i(TAG,"Pre Registration file path "+filePath);

            preRegistrationDto.setPacketPath(filePath);
            preRegistrationDto.setSymmetricKey(CryptoUtil.encodeToURLSafeBase64(symmetricKey.getEncoded()));
            preRegistrationDto.setEncryptedPacket(encryptedData);
            preRegistrationDto.setPreRegId(preRegistrationId);
            return  preRegistrationDto;
        } catch (Exception e){
            Log.e(TAG, "exception occurred while encrypt and save packet", e);
        }
        return preRegistrationDto;
    }

    @Override
    public String storePreRegPacketToDisk(String preRegistrationId, byte[] encryptedPacket, CenterMachineDto centerMachineDto)
            throws RegBaseUncheckedException {
        try {
            String PRE_REG_PACKET = ConfigService.getProperty("mosip.registration.registration_pre_reg_packet_location", appContext);

            // Generate the file path for storing the Encrypted Packet
            File localFilePath = new File(this.appContext.getFilesDir() + SEPARATOR + PRE_REG_PACKET);
            if(!localFilePath.exists()){
                if (localFilePath.mkdirs()) {
                    System.out.println("Parent directories created: " + localFilePath.getPath());
                } else {
                    System.out.println("Failed to create parent directories: " + localFilePath.getPath());
                }
            }

            File filePath = new File(localFilePath, preRegistrationId + ZIP);

            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(filePath))) {
                ZipEntry entry = new ZipEntry("data.enc");
                zipOut.putNextEntry(entry);
                zipOut.write(encryptedPacket);
                zipOut.closeEntry();
            }

            try {
                FileUtils.copyToFile(new ByteArrayInputStream(encryptedPacket), filePath);
                System.out.println("File written successfully!");
            } catch (IOException e) {
                System.err.println("Error writing file: " + e.getMessage());
                // Handle the exception (e.g., log the error or retry)
            }

            Log.i(TAG, "Pre Registration Encrypted packet saved");

            return filePath.getCanonicalPath();
        } catch (IOException exception) {
            Log.e(TAG,exception.getMessage(), exception);
            throw new RuntimeException(exception);
        } catch (RuntimeException runtimeException) {
            Log.e(TAG,runtimeException.getMessage(), runtimeException);
            throw new RegBaseUncheckedException("REG-SER-STM-211",
                    runtimeException.toString(), runtimeException);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public byte[] decryptPreRegPacket(String symmetricKey, byte[] encryptedPacket) throws Exception {
        byte[] secret = CryptoUtil.decodeBase64(symmetricKey);
        SecretKey secretKey = new SecretKeySpec(secret, 0 , secret.length, "AES");
        return this.cryptoManagerService.symmetricDecrypt(secretKey, encryptedPacket,null);
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

    private void initPreRegAdapter(Context context) {
        this.appContext = context;

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            String location = ConfigService.getProperty("objectstore.base.location", context);

            File file = new File(Environment.getExternalStorageDirectory() + SEPARATOR + location);

            if (!file.exists()) {
                file.mkdirs();
            }

            BASE_LOCATION = file.getAbsolutePath();
        } else {
            Log.e(TAG, "External Storage not mounted");
        }
        Log.i(TAG, "initLocalClientCryptoService: Initialization call successful");
    }

}
