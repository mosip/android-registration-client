package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.http.*;
import io.mosip.registration.clientmanager.dto.registration.GenericDto;
import io.mosip.registration.clientmanager.entity.GlobalParam;
import io.mosip.registration.clientmanager.entity.MachineMaster;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import io.mosip.registration.clientmanager.repository.*;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.keymanager.dto.CACertificateRequestDto;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.exception.KeymanagerServiceException;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.keymanager.spi.CACertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.keymanager.util.KeyManagerErrorCode;
import io.mosip.registration.packetmanager.util.JsonUtils;
import okhttp3.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class MasterDataServiceImpl implements MasterDataService {

    private static final String TAG = MasterDataServiceImpl.class.getSimpleName();
    private static final String MASTER_DATA_LAST_UPDATED = "masterdata.lastupdated";
    public static final String REG_APP_ID = "REGISTRATION";
    public static final String KERNEL_APP_ID = "KERNEL";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Context context;
    private SyncRestService syncRestService;
    private ClientCryptoManagerService clientCryptoManagerService;
    private MachineRepository machineRepository;
    private RegistrationCenterRepository registrationCenterRepository;
    private ApplicantValidDocRepository applicantValidDocRepository;
    private DocumentTypeRepository documentTypeRepository;
    private TemplateRepository templateRepository;
    private DynamicFieldRepository dynamicFieldRepository;
    private KeyStoreRepository keyStoreRepository;
    private LocationRepository locationRepository;
    private GlobalParamRepository globalParamRepository;
    private IdentitySchemaRepository identitySchemaRepository;
    private BlocklistedWordRepository blocklistedWordRepository;
    private SyncJobDefRepository syncJobDefRepository;
    private UserDetailRepository userDetailRepository;
    private CACertificateManagerService caCertificateManagerService;

    @Inject
    public MasterDataServiceImpl(Context context, SyncRestService syncRestService,
                                 ClientCryptoManagerService clientCryptoManagerService,
                                 MachineRepository machineRepository,
                                 RegistrationCenterRepository registrationCenterRepository,
                                 DocumentTypeRepository documentTypeRepository,
                                 ApplicantValidDocRepository applicantValidDocRepository,
                                 TemplateRepository templateRepository,
                                 DynamicFieldRepository dynamicFieldRepository,
                                 KeyStoreRepository keyStoreRepository,
                                 LocationRepository locationRepository,
                                 GlobalParamRepository globalParamRepository,
                                 IdentitySchemaRepository identitySchemaRepository,
                                 BlocklistedWordRepository blocklistedWordRepository,
                                 SyncJobDefRepository syncJobDefRepository,
                                 UserDetailRepository userDetailRepository,
                                 CACertificateManagerService caCertificateManagerService) {
        this.context = context;
        this.syncRestService = syncRestService;
        this.clientCryptoManagerService = clientCryptoManagerService;
        this.machineRepository = machineRepository;
        this.registrationCenterRepository = registrationCenterRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.applicantValidDocRepository = applicantValidDocRepository;
        this.templateRepository = templateRepository;
        this.dynamicFieldRepository = dynamicFieldRepository;
        this.keyStoreRepository = keyStoreRepository;
        this.locationRepository = locationRepository;
        this.globalParamRepository = globalParamRepository;
        this.identitySchemaRepository = identitySchemaRepository;
        this.blocklistedWordRepository = blocklistedWordRepository;
        this.syncJobDefRepository = syncJobDefRepository;
        this.userDetailRepository = userDetailRepository;
        this.caCertificateManagerService = caCertificateManagerService;
    }

    @Override
    public CenterMachineDto getRegistrationCenterMachineDetails() {
        CenterMachineDto centerMachineDto = null;
        MachineMaster machineMaster = this.machineRepository.getMachine(clientCryptoManagerService.getMachineName());
        if (machineMaster == null)
            return centerMachineDto;

        List<RegistrationCenter> centers = this.registrationCenterRepository.getRegistrationCenter(machineMaster.getRegCenterId());
        if (centers == null || centers.isEmpty())
            return centerMachineDto;

        centerMachineDto = new CenterMachineDto();
        centerMachineDto.setMachineId(machineMaster.getId());
        centerMachineDto.setMachineName(machineMaster.getName());
        centerMachineDto.setMachineStatus(machineMaster.getIsActive());
        centerMachineDto.setCenterId(centers.get(0).getId());
        centerMachineDto.setCenterStatus(centers.get(0).getIsActive());
        centerMachineDto.setMachineRefId(centerMachineDto.getCenterId() + "_" + centerMachineDto.getMachineId());
        centerMachineDto.setCenterNames(centers.stream().collect(Collectors.toMap(RegistrationCenter::getLangCode, RegistrationCenter::getName)));
        return centerMachineDto;
    }

    @Override
    public void manualSync() {
        try {
            syncMasterData();
            syncCertificate();
            syncLatestIdSchema();
            //syncUserDetails();
            syncCACertificates();
        } catch (Exception ex) {
            Log.e(TAG, "Data Sync failed", ex);
            Toast.makeText(context, "Data Sync failed", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void syncCertificate() {
        CenterMachineDto centerMachineDto = getRegistrationCenterMachineDetails();
        if (centerMachineDto == null)
            return;

        Call<ResponseWrapper<CertificateResponse>> call = syncRestService.getCertificate(REG_APP_ID,
                centerMachineDto.getMachineRefId());
        call.enqueue(new Callback<ResponseWrapper<CertificateResponse>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<CertificateResponse>> call, Response<ResponseWrapper<CertificateResponse>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        keyStoreRepository.saveKeyStore(centerMachineDto.getMachineRefId(), response.body().getResponse().getCertificate());
                        Toast.makeText(context, "Policy key Sync Completed", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(context, "Policy key Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Policy key Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseWrapper<CertificateResponse>> call, Throwable t) {
                Toast.makeText(context, "Policy key Sync failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void syncMasterData() throws Exception {
        CenterMachineDto centerMachineDto = getRegistrationCenterMachineDetails();

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("keyindex", this.clientCryptoManagerService.getClientKeyIndex());
        queryParams.put("version", BuildConfig.CLIENT_VERSION);

        if (centerMachineDto != null)
            queryParams.put("regcenterId", centerMachineDto.getCenterId());

        String delta = this.globalParamRepository.getGlobalParamValue(MASTER_DATA_LAST_UPDATED);
        if (delta != null)
            queryParams.put("lastUpdated", delta);

        Call<ResponseWrapper<ClientSettingDto>> call = syncRestService.fetchMasterDate(queryParams);

        call.enqueue(new Callback<ResponseWrapper<ClientSettingDto>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<ClientSettingDto>> call, Response<ResponseWrapper<ClientSettingDto>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        saveMasterData(response.body().getResponse());
                        Toast.makeText(context, "Master Data Sync Completed", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(context, "Master Data Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Master Data Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseWrapper<ClientSettingDto>> call, Throwable t) {
                Toast.makeText(context, "Master Sync failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void syncGlobalParamsData() throws Exception {

        Log.i(TAG, "config data sync is started");

        Call<ResponseWrapper<Map<String, Object>>> call = syncRestService.getGlobalConfigs(clientCryptoManagerService.getClientKeyIndex());

        call.enqueue(new Callback<ResponseWrapper<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Map<String, Object>>> call, Response<ResponseWrapper<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        saveGlobalParams(response.body().getResponse());
                        Toast.makeText(context, "Global config sync Completed", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(context, "Global config sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "Global config sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(context, "Global config sync failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveGlobalParams(Map<String, Object> responseMap) {

        try {

            Map<String, String> globalParamMap = new HashMap<>();

            if (responseMap.get("configDetail") != null) {
                Map<String, Object> configDetailJsonMap = (Map<String, Object>) responseMap.get("configDetail");

                if (configDetailJsonMap.get("registrationConfiguration") != null) {
                    parseToMap(getParams(configDetailJsonMap.get("registrationConfiguration").toString().trim()),
                            globalParamMap);
                }
            }

            List<GlobalParam> globalParamList = globalParamRepository.getGlobalParams();

            globalParamRepository.saveGlobalParams(globalParamList);

        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage(), exception);
        }
    }

    @SuppressWarnings("unchecked")
    private void parseToMap(Map<String, Object> map, Map<String, String> globalParamMap) {
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();

                if (entry.getValue() instanceof HashMap) {
                    parseToMap((HashMap<String, Object>) entry.getValue(), globalParamMap);
                } else {
                    globalParamMap.put(key, String.valueOf(entry.getValue()));
                }
            }
        }
    }

    private Map<String, Object> getParams(String encodedCipher) {
        try {
            //TODO implementation pending
            CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
            cryptoRequestDto.setValue(new String(CryptoUtil.base64decoder.decode(encodedCipher)));
            CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);

            byte[] data = CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue());
            Map<String, Object> paramMap = objectMapper.readValue(data, HashMap.class);
            return paramMap;
        } catch (IOException e) {
            Log.e(TAG,"Failed to decrypt and parse config response >> ", e);
        }
        return null;
    }

    @Override
    public void syncLatestIdSchema() throws Exception {
        Call<ResponseBody> call = syncRestService.getLatestIdSchema();
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        ResponseWrapper<IdSchemaResponse> wrapper = JsonUtils.jsonStringToJavaObject(response.body().string(),
                                new TypeReference<ResponseWrapper<IdSchemaResponse>>() {
                                });
                        identitySchemaRepository.saveIdentitySchema(context, wrapper.getResponse());
                        Toast.makeText(context, "Identity schema and UI Spec Sync Completed", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Log.e(TAG, "Failed to save IDSchema", e);
                    }
                } else
                    Toast.makeText(context, "Identity schema and UI Spec Sync failed with status code : " +
                            response.code(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failed to sync schema", t);
                Toast.makeText(context, "Identity schema and UI Spec Sync failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void syncUserDetails() throws Exception {
        Call<ResponseWrapper<UserDetailResponse>> call = syncRestService.fetchCenterUserDetails(this.clientCryptoManagerService.getClientKeyIndex());
        call.enqueue(new Callback<ResponseWrapper<UserDetailResponse>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<UserDetailResponse>> call, Response<ResponseWrapper<UserDetailResponse>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        saveUserDetails(response.body().getResponse().getUserDetails());
                        Toast.makeText(context, "User Sync Completed", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(context, "User Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "User Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseWrapper<UserDetailResponse>> call, Throwable t) {
                Toast.makeText(context, "User Sync failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserDetails(String encData) {
        try {
            userDetailRepository.saveUserDetail(getDecryptedDataList(encData));
        } catch (Throwable t) {
            Log.e(TAG, "Failed to save synced user details", t);
        }
    }

    @Override
    public void syncCACertificates() {

        Call<ResponseWrapper<CACertificateResponseDto>> call = syncRestService.getCACertificates(null);
        call.enqueue(new Callback<ResponseWrapper<CACertificateResponseDto>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<CACertificateResponseDto>> call, Response<ResponseWrapper<CACertificateResponseDto>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    String errorMessage = error != null ? error.getMessage() : null;
                    if (errorMessage == null) {
                        try {
                            saveCACertificate(response.body().getResponse().getCertificateDTOList());
                            Toast.makeText(context, "CA Certificate Sync Completed", Toast.LENGTH_LONG).show();
                            return;
                        } catch (Throwable t) {
                            Log.e(TAG, "Failed to sync CA certificates", t);
                            errorMessage = t.getMessage();
                        }
                    }
                    Toast.makeText(context, "CA Certificate Sync failed " + errorMessage, Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(context, "CA Certificate Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseWrapper<CACertificateResponseDto>> call, Throwable t) {
                Toast.makeText(context, "CA Certificate Sync failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveCACertificate(List<CACertificateDto> caCertificateDtos) {
        if(caCertificateDtos != null && !caCertificateDtos.isEmpty()) {
            //Data Fix : As createdDateTime is null sometimes
            caCertificateDtos.forEach(c -> {
                if(c.getCreatedtimes() == null)
                    c.setCreatedtimes(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
            });
            caCertificateDtos.sort((CACertificateDto d1, CACertificateDto d2) -> d1.getCreatedtimes().compareTo(d2.getCreatedtimes()));

            for(CACertificateDto cert : caCertificateDtos) {
                String errorCode = null;
                try {
                    if(cert.getPartnerDomain() != null && cert.getPartnerDomain().equals("DEVICE")) {
                        CACertificateRequestDto caCertificateRequestDto = new CACertificateRequestDto();
                        caCertificateRequestDto.setCertificateData(cert.getCertData());
                        caCertificateRequestDto.setPartnerDomain(cert.getPartnerDomain());
                        io.mosip.registration.keymanager.dto.CACertificateResponseDto caCertificateResponseDto = caCertificateManagerService.uploadCACertificate(caCertificateRequestDto);
                        Log.i(TAG, caCertificateResponseDto.getStatus());
                    }
                } catch (KeymanagerServiceException ex) {
                    errorCode = ex.getErrorCode();
                }

                if(errorCode != null && !errorCode.equals(KeyManagerErrorCode.CERTIFICATE_EXIST_ERROR.getErrorCode()))
                    throw new KeymanagerServiceException(errorCode, errorCode);
            }
        }
    }

    @Override
    public Integer getHierarchyLevel(String hierarchyLevelName) {
        return locationRepository.getHierarchyLevel(hierarchyLevelName);
    }

    private void saveMasterData(ClientSettingDto clientSettingDto) {
        boolean foundErrors = false;
        for (MasterData masterData : clientSettingDto.getDataToSync()) {
            try {
                switch (masterData.getEntityType()) {
                    case "structured":
                        saveStructuredData(masterData.getEntityName(), masterData.getData());
                        break;
                    case "dynamic":
                        saveDynamicData(masterData.getData());
                }
            } catch (Throwable e) {
                foundErrors = true;
                Log.e(TAG, "Failed to parse the data", e);
            }
        }

        if (!foundErrors) {
            Log.i(TAG, "Masterdata lastSyncTime : " + clientSettingDto.getLastSyncTime());
            this.globalParamRepository.saveGlobalParam(MASTER_DATA_LAST_UPDATED, clientSettingDto.getLastSyncTime());
        }
    }

    private JSONArray getDecryptedDataList(String data) throws JSONException {
        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
        cryptoRequestDto.setValue(data);
        CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);
        return new JSONArray(new String(CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue())));
    }

    private void saveStructuredData(String entityName, String data) throws JSONException {
        switch (entityName) {
            case "Machine":
                JSONArray machines = getDecryptedDataList(data);
                machineRepository.saveMachineMaster(machines.getJSONObject(0));
                break;
            case "RegistrationCenter":
                JSONArray centers = getDecryptedDataList(data);
                for (int i = 0; i < centers.length(); i++) {
                    registrationCenterRepository.saveRegistrationCenter(centers.getJSONObject(i));
                }
                break;
            case "DocumentType":
                JSONArray doctypes = getDecryptedDataList(data);
                for (int i = 0; i < doctypes.length(); i++) {
                    documentTypeRepository.saveDocumentType(doctypes.getJSONObject(i));
                }
                break;
            case "ApplicantValidDocument":
                JSONArray appValidDocs = getDecryptedDataList(data);
                for (int i = 0; i < appValidDocs.length(); i++) {
                    applicantValidDocRepository.saveApplicantValidDocument(appValidDocs.getJSONObject(i));
                }
                break;
            case "Template":
                JSONArray templates = getDecryptedDataList(data);
                for (int i = 0; i < templates.length(); i++) {
                    templateRepository.saveTemplate(templates.getJSONObject(i));
                }
                break;
            case "Location":
                JSONArray locations = getDecryptedDataList(data);
                for (int i = 0; i < locations.length(); i++) {
                    locationRepository.saveLocationData(locations.getJSONObject(i));
                }
                break;
            case "LocationHierarchy":
                JSONArray locationHierarchies = getDecryptedDataList(data);
                for (int i = 0; i < locationHierarchies.length(); i++) {
                    locationRepository.saveLocationHierarchyData(locationHierarchies.getJSONObject(i));
                }
                break;
            case "BlocklistedWords":
                JSONArray words = getDecryptedDataList(data);
                for (int i = 0; i < words.length(); i++) {
                    blocklistedWordRepository.saveBlocklistedWord(words.getJSONObject(i));
                }
                break;
            case "SyncJobDef":
                JSONArray syncJobDefsJsonArray = getDecryptedDataList(data);
                for (int i = 0; i < syncJobDefsJsonArray.length(); i++) {
                    syncJobDefRepository.saveSyncJobDef(syncJobDefsJsonArray.getJSONObject(i));
                }
                break;
        }
    }

    private void saveDynamicData(String data) throws JSONException {
        JSONArray list = getDecryptedDataList(data);
        for (int i = 0; i < list.length(); i++) {
            dynamicFieldRepository.saveDynamicField(list.getJSONObject(i));
        }
    }

    /**
     * TODO - Currently stubbed to support dependent tasks
     */
    @Override
    public List<GenericDto> getAllLocationHierarchyLevels(String langCode) {
        List<GenericDto> list = new ArrayList<>();
        list.add(new GenericDto("Country", langCode));
        list.add(new GenericDto("Region", langCode));
        list.add(new GenericDto("Province", langCode));
        list.add(new GenericDto("City", langCode));
        list.add(new GenericDto("Postal Code", langCode));
        return list;
    }


    @Override
    public List<String> getFieldValues(String fieldName, String langCode) {
        return dynamicFieldRepository.getDynamicValues(fieldName, langCode);
    }


    @Override
    public List<String> findLocationByParentHierarchyCode(String parentCode, String langCode) {
        return this.locationRepository.getLocations(parentCode, langCode);
    }

    @Override
    public List<String> findLocationByHierarchyLevel(String hierarchyLevelName, String langCode) {
        Integer level = getHierarchyLevel(hierarchyLevelName);
        if (level == null)
            return Collections.EMPTY_LIST;
        return this.locationRepository.getLocationsBasedOnHierarchyLevel(level, langCode);
    }

    @Override
    public List<String> getDocumentTypes(String categoryCode, String applicantType, String langCode) {
        return this.applicantValidDocRepository.getDocumentTypes(applicantType, categoryCode, langCode);
    }

    @Override
    public String getTemplateContent(String templateName, String langCode) {
        return templateRepository.getTemplate(templateName, langCode);
    }
}
