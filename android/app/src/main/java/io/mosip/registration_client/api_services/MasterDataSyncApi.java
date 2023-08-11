package io.mosip.registration_client.api_services;

import static android.content.ContentValues.TAG;
import static io.mosip.registration.clientmanager.service.MasterDataServiceImpl.REG_APP_ID;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.http.CertificateResponse;
import io.mosip.registration.clientmanager.dto.http.ClientSettingDto;
import io.mosip.registration.clientmanager.dto.http.IdSchemaResponse;
import io.mosip.registration.clientmanager.dto.http.MasterData;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.dto.http.UserDetailResponse;
import io.mosip.registration.clientmanager.entity.GlobalParam;
import io.mosip.registration.clientmanager.entity.MachineMaster;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.repository.ApplicantValidDocRepository;
import io.mosip.registration.clientmanager.repository.BlocklistedWordRepository;
import io.mosip.registration.clientmanager.repository.DocumentTypeRepository;
import io.mosip.registration.clientmanager.repository.DynamicFieldRepository;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.LanguageRepository;
import io.mosip.registration.clientmanager.repository.LocationRepository;
import io.mosip.registration.clientmanager.repository.MachineRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.repository.TemplateRepository;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.keymanager.dto.CertificateRequestDto;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.util.JsonUtils;
import io.mosip.registration_client.model.MasterDataSyncPigeon;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class MasterDataSyncApi implements MasterDataSyncPigeon.SyncApi {
    private static final String MASTER_DATA_LAST_UPDATED = "masterdata.lastupdated";
    private final int master_data_recursive_sync_max_retry = 3;
    SyncRestService syncRestService;
    CertificateManagerService certificateManagerService;
    RegistrationCenterRepository registrationCenterRepository;
    MachineRepository machineRepository;
    ClientCryptoManagerService clientCryptoManagerService;
    GlobalParamRepository globalParamRepository;
    ObjectMapper objectMapper;
    UserDetailRepository userDetailRepository;
    IdentitySchemaRepository identitySchemaRepository;
    DocumentTypeRepository documentTypeRepository;
    ApplicantValidDocRepository applicantValidDocRepository;
    TemplateRepository templateRepository;
    DynamicFieldRepository dynamicFieldRepository;
    LocationRepository locationRepository;
    BlocklistedWordRepository blocklistedWordRepository;
    SyncJobDefRepository syncJobDefRepository;
    LanguageRepository languageRepository;
    JobManagerService jobManagerService;
    Context context;

    @Inject
    public MasterDataSyncApi(ClientCryptoManagerService clientCryptoManagerService, MachineRepository machineRepository, RegistrationCenterRepository registrationCenterRepository, SyncRestService syncRestService, CertificateManagerService certificateManagerService, GlobalParamRepository globalParamRepository, ObjectMapper objectMapper, UserDetailRepository userDetailRepository, IdentitySchemaRepository identitySchemaRepository, Context context, DocumentTypeRepository documentTypeRepository,
                             ApplicantValidDocRepository applicantValidDocRepository,
                             TemplateRepository templateRepository,
                             DynamicFieldRepository dynamicFieldRepository,
                             LocationRepository locationRepository,
                             BlocklistedWordRepository blocklistedWordRepository,
                             SyncJobDefRepository syncJobDefRepository,
                             LanguageRepository languageRepository,
                             JobManagerService jobManagerService) {
        this.clientCryptoManagerService = clientCryptoManagerService;
        this.machineRepository = machineRepository;
        this.registrationCenterRepository = registrationCenterRepository;
        this.syncRestService = syncRestService;
        this.certificateManagerService = certificateManagerService;
        this.globalParamRepository = globalParamRepository;
        this.objectMapper = objectMapper;
        this.userDetailRepository = userDetailRepository;
        this.identitySchemaRepository = identitySchemaRepository;
        this.context = context;
        this.documentTypeRepository = documentTypeRepository;
        this.applicantValidDocRepository = applicantValidDocRepository;
        this.templateRepository = templateRepository;
        this.dynamicFieldRepository = dynamicFieldRepository;
        this.locationRepository = locationRepository;
        this.blocklistedWordRepository = blocklistedWordRepository;
        this.syncJobDefRepository = syncJobDefRepository;
        this.languageRepository = languageRepository;
        this.jobManagerService = jobManagerService;
    }


    private void syncPolicyKey(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        CenterMachineDto centerMachineDto = getRegistrationCenterMachineDetails();
        if (centerMachineDto == null)
            return;

        Call<ResponseWrapper<CertificateResponse>> call = syncRestService.getPolicyKey(REG_APP_ID,
                centerMachineDto.getMachineRefId(), BuildConfig.CLIENT_VERSION);
        call.enqueue(new Callback<ResponseWrapper<CertificateResponse>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<CertificateResponse>> call, Response<ResponseWrapper<CertificateResponse>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        CertificateRequestDto certificateRequestDto = new CertificateRequestDto();
                        certificateRequestDto.setApplicationId("REGISTRATION");
                        certificateRequestDto.setReferenceId(centerMachineDto.getMachineRefId());
                        certificateRequestDto.setCertificateData(response.body().getResponse().getCertificate());
                        certificateManagerService.uploadOtherDomainCertificate(certificateRequestDto);
                        Log.i(TAG, "Policy Sync");
                        result.success(syncResult("PolicyKeySync", 5, ""));
                        return;
                    }
                    Log.e(TAG, "Policy Sync Failed:" + error.toString());
                    result.success(syncResult("PolicyKeySync", 5, "policy_key_sync_failed"));
                    return;
                }
                result.success(syncResult("PolicyKeySync", 5, "policy_key_sync_failed"));
                return;
            }

            @Override
            public void onFailure(Call<ResponseWrapper<CertificateResponse>> call, Throwable t) {
                Log.e(TAG,"Policy Sync Failed:", t);
                result.success(syncResult("PolicyKeySync", 5, "policy_key_sync_failed"));
                return;
            }
        });
    }

    private void syncGlobalParamsData(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) throws Exception {
        Log.i(TAG, "config data sync is started");
        Call<ResponseWrapper<Map<String, Object>>> call = syncRestService.getGlobalConfigs(
                clientCryptoManagerService.getClientKeyIndex(), BuildConfig.CLIENT_VERSION);
        call.enqueue(new Callback<ResponseWrapper<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Map<String, Object>>> call, Response<ResponseWrapper<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        saveGlobalParams(response.body().getResponse());
                        result.success(syncResult("GlobalParamsSync", 1, ""));
                        return;
                    } else
                    result.success(syncResult("GlobalParamsSync", 1, "global_params_sync_failed"));
                    return;
                } else
                result.success(syncResult("GlobalParamsSync", 1, "global_params_sync_failed"));
                return;
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Map<String, Object>>> call, Throwable t) {
                result.success(syncResult("GlobalParamsSync", 1, "global_params_sync_failed"));
                return;
            }
        });
    }

    private void saveGlobalParams(Map<String, Object> responseMap) {
        try {
            Map<String, String> globalParamMap = new HashMap<>();

            if (responseMap.get("configDetail") != null) {
                Map<String, Object> configDetailJsonMap = (Map<String, Object>) responseMap.get("configDetail");

                if (configDetailJsonMap != null && configDetailJsonMap.get("registrationConfiguration") != null) {
                    String encryptedConfigs = configDetailJsonMap.get("registrationConfiguration").toString();
                    parseToMap(getParams(encryptedConfigs), globalParamMap);
                }
            }

            List<GlobalParam> globalParamList = new ArrayList<>();
            for (Map.Entry<String, String> entry : globalParamMap.entrySet()) {
                GlobalParam globalParam = new GlobalParam(entry.getKey(), entry.getKey(), entry.getValue(), true);
                globalParamList.add(globalParam);
            }

            globalParamRepository.saveGlobalParams(globalParamList);
        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage(), exception);
        }
    }

    private void parseToMap(Map<String, Object> map, Map<String, String> globalParamMap) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();

            if (entry.getValue() instanceof HashMap) {
                parseToMap((HashMap<String, Object>) entry.getValue(), globalParamMap);
            } else {
                globalParamMap.put(key, String.valueOf(entry.getValue()));
            }
        }
    }

    private Map<String, Object> getParams(String encodedCipher) {
        try {
            CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
            cryptoRequestDto.setValue(encodedCipher);
            CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);

            byte[] data = CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue());
            Map<String, Object> paramMap = objectMapper.readValue(data, HashMap.class);
            return paramMap;
        } catch (IOException e) {
            Log.e(TAG, "Failed to decrypt and parse config response >> ", e);
        }
        return Collections.EMPTY_MAP;
    }

    private void syncUserDetails(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) throws Exception {
        Call<ResponseWrapper<UserDetailResponse>> call = syncRestService.fetchCenterUserDetails(
                this.clientCryptoManagerService.getClientKeyIndex(), BuildConfig.CLIENT_VERSION);
        call.enqueue(new Callback<ResponseWrapper<UserDetailResponse>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<UserDetailResponse>> call, Response<ResponseWrapper<UserDetailResponse>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        saveUserDetails(response.body().getResponse().getUserDetails());
                        MasterDataSyncPigeon.Sync sync = syncResult("UserDetailsSync", 3, "");
                        result.success(syncResult("UserDetailsSync", 3, ""));
                        return;
                    } else
                    result.success(syncResult("UserDetailsSync", 3, "user_details_sync_failed"));
                    return;
                } else
                result.success(syncResult("UserDetailsSync", 3, "user_details_sync_failed"));
                return;
            }

            @Override
            public void onFailure(Call<ResponseWrapper<UserDetailResponse>> call, Throwable t) {

                result.success(syncResult("UserDetailsSync", 3, "user_details_sync_failed"));
                return;
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


    private void syncLatestIdSchema(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result, Context context) {
        Call<ResponseBody> call = syncRestService.getLatestIdSchema(BuildConfig.CLIENT_VERSION, "registration-client");
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
                        result.success(syncResult("LatestIDSchemaSync", 4, ""));
                        return;

                    } catch (Exception e) {
                        result.success(syncResult("LatestIDSchemaSync", 4, "id_schema_sync_failed"));
                        Log.e(TAG, "Failed to save IDSchema", e);
                    }
                } else
                result.success(syncResult("LatestIDSchemaSync", 4, "id_schema_sync_failed"));

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failed to sync schema", t);
                result.success(syncResult("LatestIDSchemaSync", 4, "id_schema_sync_failed"));
            }
        });
    }

    private void syncMasterData(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result, int retryNo) {
        CenterMachineDto centerMachineDto = getRegistrationCenterMachineDetails();

        Map<String, String> queryParams = new HashMap<>();

        try {
            queryParams.put("keyindex", this.clientCryptoManagerService.getClientKeyIndex());
        } catch (Exception e) {
            Log.e(TAG, "MasterData : not able to get client key index", e);
            result.success(syncResult("MasterDataSync", 2, "master_data_sync_failed"));
            return;
        }

        queryParams.put("version", BuildConfig.CLIENT_VERSION);

        if (centerMachineDto != null)
            queryParams.put("regcenterId", centerMachineDto.getCenterId());

        String delta = this.globalParamRepository.getGlobalParamValue(MASTER_DATA_LAST_UPDATED);
        if (delta != null)
            queryParams.put("lastUpdated", delta);

        Call<ResponseWrapper<ClientSettingDto>> call = syncRestService.fetchMasterData(queryParams);

        call.enqueue(new Callback<ResponseWrapper<ClientSettingDto>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<ClientSettingDto>> call, Response<ResponseWrapper<ClientSettingDto>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        saveMasterData(response.body().getResponse());
                        if (centerMachineDto == null) {
                            if (retryNo < master_data_recursive_sync_max_retry) {
                                Log.i(TAG, "onResponse: MasterData Sync Recursive call : " + retryNo);
                                //rerunning master data to sync completed master data
                                syncMasterData(result, retryNo + 1);
                            } else {
                                result.success(syncResult("MasterDataSync", 2, "master_data_sync_failed"));
                                return;
                            }
                        } else {
                            result.success(syncResult("MasterDataSync", 2, ""));
                            return;
                        }
                    } else
                    result.success(syncResult("MasterDataSync", 2, "master_data_sync_failed"));
                    return;
                } else
                result.success(syncResult("MasterDataSync", 2, "master_data_sync_failed"));
                return;
            }

            @Override
            public void onFailure(Call<ResponseWrapper<ClientSettingDto>> call, Throwable t) {
                result.success(syncResult("MasterDataSync", 2, "master_data_sync_failed"));
            }
        });
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
                    JSONObject jsonObject = syncJobDefsJsonArray.getJSONObject(i);

                    SyncJobDef syncJobDef = new SyncJobDef(jsonObject.getString("id"));
                    syncJobDef.setName(jsonObject.getString("name"));
                    syncJobDef.setApiName(jsonObject.getString("apiName"));
                    syncJobDef.setParentSyncJobId(jsonObject.getString("parentSyncJobId"));
                    syncJobDef.setSyncFreq(jsonObject.getString("syncFreq"));
                    syncJobDef.setLockDuration(jsonObject.getString("lockDuration"));
                    syncJobDef.setLangCode(jsonObject.getString("langCode"));
                    syncJobDef.setIsDeleted(jsonObject.getBoolean("isDeleted"));
                    syncJobDef.setIsActive(jsonObject.getBoolean("isActive"));

                    syncJobDefRepository.saveSyncJobDef(syncJobDef);
//                    jobManagerService.refreshJobStatus(syncJobDef);
                }
                break;
            case "Language":
                JSONArray languageJsonArray = getDecryptedDataList(data);
                for (int i = 0; i < languageJsonArray.length(); i++) {
                    languageRepository.saveLanguage(languageJsonArray.getJSONObject(i));
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


    public CenterMachineDto getRegistrationCenterMachineDetails() {

        CenterMachineDto centerMachineDto = null;
        MachineMaster machineMaster = this.machineRepository.getMachine(this.clientCryptoManagerService.getMachineName());
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
    public void getLastSyncTime(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.SyncTime> result) {
        MasterDataSyncPigeon.SyncTime syncTime;
        String globalParamSyncTime;
        if(globalParamRepository.getGlobalParamValue(MASTER_DATA_LAST_UPDATED) == null){
            globalParamSyncTime = "LastSyncTimeIsNull";
        }
        else
            globalParamSyncTime = globalParamRepository.getGlobalParamValue(MASTER_DATA_LAST_UPDATED);
        syncTime = new MasterDataSyncPigeon.SyncTime.Builder()
                .setSyncTime(globalParamSyncTime)
                .build();
        result.success(syncTime);
        return;
    }

    @Override
    public void getPolicyKeySync(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        syncPolicyKey(result);
        return;
    }

    @Override
    public void getGlobalParamsSync(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        try {
            syncGlobalParamsData(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void getUserDetailsSync(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        try {
            syncUserDetails(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void getIDSchemaSync(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        syncLatestIdSchema(result, context);
        return;
    }

    @Override
    public void getMasterDataSync(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        syncMasterData(result, 0);
    }
    private MasterDataSyncPigeon.Sync syncResult(String syncType, int progress, String errorCode){
        return  new MasterDataSyncPigeon.Sync.Builder()
                .setSyncType(syncType)
                .setSyncProgress(Long.valueOf(progress))
                .setErrorCode(errorCode)
                .build();
    }
}
