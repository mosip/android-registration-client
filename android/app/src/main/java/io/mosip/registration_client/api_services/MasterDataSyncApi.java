/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.mosip.registration_client.api_services;

import static android.content.ContentValues.TAG;
import static io.mosip.registration.clientmanager.service.MasterDataServiceImpl.REG_APP_ID;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.ClientManagerConstant;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.constant.PacketTaskStatus;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.FileSignatureDao;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.http.CACertificateDto;
import io.mosip.registration.clientmanager.dto.http.CACertificateResponseDto;
import io.mosip.registration.clientmanager.dto.http.CertificateResponse;
import io.mosip.registration.clientmanager.dto.http.ClientSettingDto;
import io.mosip.registration.clientmanager.dto.http.IdSchemaResponse;
import io.mosip.registration.clientmanager.dto.http.MasterData;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.dto.http.UserDetailResponse;
import io.mosip.registration.clientmanager.entity.FileSignature;
import io.mosip.registration.clientmanager.entity.GlobalParam;
import io.mosip.registration.clientmanager.entity.MachineMaster;
import io.mosip.registration.clientmanager.entity.Registration;
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
import io.mosip.registration.clientmanager.spi.AsyncPacketTaskCallBack;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.keymanager.dto.CACertificateRequestDto;
import io.mosip.registration.keymanager.dto.CertificateRequestDto;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.exception.KeymanagerServiceException;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.keymanager.util.KeyManagerErrorCode;
import io.mosip.registration.packetmanager.util.JsonUtils;
import io.mosip.registration_client.NetworkUtils;
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
    AuditManagerService auditManagerService;
    MasterDataService masterDataService;
    PacketService packetService;
    GlobalParamDao globalParamDao;
    FileSignatureDao fileSignatureDao;
    Context context;
    private String regCenterId;

    @Inject
    public MasterDataSyncApi(ClientCryptoManagerService clientCryptoManagerService, MachineRepository machineRepository, RegistrationCenterRepository registrationCenterRepository, SyncRestService syncRestService, CertificateManagerService certificateManagerService, GlobalParamRepository globalParamRepository, ObjectMapper objectMapper, UserDetailRepository userDetailRepository, IdentitySchemaRepository identitySchemaRepository, Context context, DocumentTypeRepository documentTypeRepository,
                             ApplicantValidDocRepository applicantValidDocRepository,
                             TemplateRepository templateRepository,
                             DynamicFieldRepository dynamicFieldRepository,
                             LocationRepository locationRepository,
                             BlocklistedWordRepository blocklistedWordRepository,
                             SyncJobDefRepository syncJobDefRepository,
                             LanguageRepository languageRepository,
                             JobManagerService jobManagerService,
                             AuditManagerService auditManagerService,
                             MasterDataService masterDataService,
                             PacketService packetService,
                             GlobalParamDao globalParamDao, FileSignatureDao fileSignatureDao) {
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
        this.auditManagerService = auditManagerService;
        this.masterDataService = masterDataService;
        this.packetService = packetService;
        this.globalParamDao = globalParamDao;
        this.fileSignatureDao = fileSignatureDao;
    }


    private void syncPolicyKey(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result, Runnable onFinish) {
        CenterMachineDto centerMachineDto = getRegistrationCenterMachineDetails();
        if (centerMachineDto == null) {
            result.success(syncResult("PolicyKeySync", 5, "policy_key_sync_failed"));
            return;
        }

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
                        if (isManualSync) {
                            Toast.makeText(context, "Policy key Sync Completed", Toast.LENGTH_LONG).show();
                        }
                        result.success(syncResult("PolicyKeySync", 5, ""));
                        onFinish.run();
                    } else {
                        Log.e(TAG, "Policy Sync Failed");
                        if (isManualSync) {
                            Toast.makeText(context, "Policy key Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        result.success(syncResult("PolicyKeySync", 5, "policy_key_sync_failed"));
                    }
                } else {
                    if (isManualSync) {
                        Toast.makeText(context, "Policy key Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
                    }
                    result.success(syncResult("PolicyKeySync", 5, "policy_key_sync_failed"));
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<CertificateResponse>> call, Throwable t) {
                Log.e(TAG, "Policy Sync Failed:", t);
                if (isManualSync) {
                    Toast.makeText(context, "Policy key Sync failed", Toast.LENGTH_LONG).show();
                }
                result.success(syncResult("PolicyKeySync", 5, "policy_key_sync_failed"));
            }
        });
    }

    private void syncGlobalParamsData(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result, Runnable onFinish) throws Exception {
        Log.i(TAG, "config data sync is started");
        String serverVersion = getServerVersionFromConfigs();

        Call<ResponseWrapper<Map<String, Object>>> call = serverVersion.startsWith("1.1.5") ? syncRestService.getV1GlobalConfigs(
                this.clientCryptoManagerService.getMachineName(), BuildConfig.CLIENT_VERSION) : syncRestService.getGlobalConfigs(
                clientCryptoManagerService.getClientKeyIndex(), BuildConfig.CLIENT_VERSION);

        call.enqueue(new Callback<ResponseWrapper<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Map<String, Object>>> call, Response<ResponseWrapper<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        saveGlobalParams(response.body().getResponse());
                        if (isManualSync) {
                            Toast.makeText(context, context.getString(io.mosip.registration.clientmanager.R.string.global_config_sync_completed), Toast.LENGTH_LONG).show();
                        }
                        result.success(syncResult("GlobalParamsSync", 1, ""));
                        onFinish.run();
                    } else {
                        if (isManualSync) {
                            Toast.makeText(context, String.format("%s %s", context.getString(io.mosip.registration.clientmanager.R.string.global_config_sync_failed), error.getMessage()), Toast.LENGTH_LONG).show();
                        }
                        result.success(syncResult("GlobalParamsSync", 1, "global_params_sync_failed"));
                    }
                } else {
                    if (isManualSync) {
                        Toast.makeText(context, String.format("%s. %s:%s", context.getString(io.mosip.registration.clientmanager.R.string.global_config_sync_failed), context.getString(io.mosip.registration.clientmanager.R.string.status_code), String.valueOf(response.code())), Toast.LENGTH_LONG).show();
                    }
                    result.success(syncResult("GlobalParamsSync", 1, "global_params_sync_failed"));
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Map<String, Object>>> call, Throwable t) {
                Log.e(TAG, "Global Params Sync Failed.", t);
                if (isManualSync) {
                    Toast.makeText(context, context.getString(io.mosip.registration.clientmanager.R.string.global_config_sync_failed), Toast.LENGTH_LONG).show();
                }
                result.success(syncResult("GlobalParamsSync", 1, "global_params_sync_failed"));
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

    private void syncUserDetails(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result, Runnable onFinish) throws Exception {
        String serverVersion = getServerVersionFromConfigs();
        if (serverVersion.startsWith("1.1.5")) {
            if (isManualSync) {
                Toast.makeText(context, "User Sync Completed", Toast.LENGTH_LONG).show();
            }
            Log.i(TAG, "Found 115 version, skipping userdetails sync");
            result.success(syncResult("UserDetailsSync", 3, ""));
            return;
        }
        Call<ResponseWrapper<UserDetailResponse>> call = syncRestService.fetchCenterUserDetails(
                this.clientCryptoManagerService.getClientKeyIndex(), BuildConfig.CLIENT_VERSION);
        call.enqueue(new Callback<ResponseWrapper<UserDetailResponse>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<UserDetailResponse>> call, Response<ResponseWrapper<UserDetailResponse>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        saveUserDetails(response.body().getResponse().getUserDetails());
                        if (isManualSync) {
                            Toast.makeText(context, "User Sync Completed", Toast.LENGTH_LONG).show();
                        }
                        result.success(syncResult("UserDetailsSync", 3, ""));
                        onFinish.run();
                    } else {
                        if (isManualSync) {
                            Toast.makeText(context, "User Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        result.success(syncResult("UserDetailsSync", 3, "user_details_sync_failed"));
                    }
                } else {
                    if (isManualSync) {
                        Toast.makeText(context, "User Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
                    }
                    result.success(syncResult("UserDetailsSync", 3, "user_details_sync_failed"));
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<UserDetailResponse>> call, Throwable t) {
                Log.e(TAG, "User Details Sync Failed.", t);
                if (isManualSync) {
                    Toast.makeText(context, "User Sync failed", Toast.LENGTH_LONG).show();
                }
                result.success(syncResult("UserDetailsSync", 3, "user_details_sync_failed"));
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


    private void syncLatestIdSchema(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result, Context context, Runnable onFinish) {
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
                        if (isManualSync) {
                            Toast.makeText(context, "Identity schema and UI Spec Sync Completed", Toast.LENGTH_LONG).show();
                        }
                        result.success(syncResult("LatestIDSchemaSync", 4, ""));
                        onFinish.run();
                    } catch (Exception e) {
                        if (isManualSync) {
                            Toast.makeText(context, "Identity schema and UI Spec Sync failed." +
                                    response.code(), Toast.LENGTH_LONG).show();
                        }
                        result.success(syncResult("LatestIDSchemaSync", 4, "id_schema_sync_failed"));
                    }
                } else {
                    if (isManualSync) {
                        Toast.makeText(context, "Identity schema and UI Spec Sync failed with status code : " +
                                response.code(), Toast.LENGTH_LONG).show();
                    }
                    result.success(syncResult("LatestIDSchemaSync", 4, "id_schema_sync_failed"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failed to sync schema", t);
                if (isManualSync) {
                    Toast.makeText(context, "Identity schema and UI Spec Sync failed", Toast.LENGTH_LONG).show();
                }
                result.success(syncResult("LatestIDSchemaSync", 4, "id_schema_sync_failed"));
            }
        });
    }

    private void syncMasterData(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result, Runnable onFinish, int retryNo) {
        CenterMachineDto centerMachineDto = getRegistrationCenterMachineDetails();

        Map<String, String> queryParams = new HashMap<>();

        try {
            queryParams.put("keyindex", this.clientCryptoManagerService.getClientKeyIndex());
        } catch (Exception e) {
            Log.e(TAG, "MasterData : not able to get client key index", e);
            if (isManualSync) {
                Toast.makeText(context, "Master Sync failed", Toast.LENGTH_LONG).show();
            }
            result.success(syncResult("MasterDataSync", 2, "master_data_sync_failed"));
            onFinish.run();
            return;
        }

        queryParams.put("version", BuildConfig.CLIENT_VERSION);

        if (centerMachineDto != null)
            queryParams.put("regcenterId", centerMachineDto.getCenterId());

        String delta = this.globalParamRepository.getGlobalParamValue(MASTER_DATA_LAST_UPDATED);
        if (delta != null)
            queryParams.put("lastUpdated", delta);

        String serverVersion = getServerVersionFromConfigs();
        Call<ResponseWrapper<ClientSettingDto>> call = serverVersion.startsWith("1.1.5") ? syncRestService.fetchV1MasterData(queryParams) : syncRestService.fetchMasterData(queryParams);

        call.enqueue(new Callback<ResponseWrapper<ClientSettingDto>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<ClientSettingDto>> call, Response<ResponseWrapper<ClientSettingDto>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        saveMasterData(response.body().getResponse(), isManualSync);
                        if (regCenterId != null) {
                            machineRepository.updateMachine(clientCryptoManagerService.getMachineName(), regCenterId);
                        }
                        if (centerMachineDto == null) {
                            if (retryNo < master_data_recursive_sync_max_retry) {
                                Log.i(TAG, "onResponse: MasterData Sync Recursive call : " + retryNo);
                                //rerunning master data to sync completed master data
                                syncMasterData(isManualSync, result, onFinish, retryNo + 1);
                            } else {
                                if (isManualSync) {
                                    Toast.makeText(context, "Master Data Sync failed! Please try again in some time", Toast.LENGTH_LONG).show();
                                }
                                result.success(syncResult("MasterDataSync", 2, "master_data_sync_failed"));
                            }
                        } else {
                            if (isManualSync) {
                                Toast.makeText(context, "Master Data Sync Completed", Toast.LENGTH_LONG).show();
                            }
                            result.success(syncResult("MasterDataSync", 2, ""));
                            onFinish.run();
                        }
                    } else {
                        if (isManualSync) {
                            Toast.makeText(context, "Master Data Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        result.success(syncResult("MasterDataSync", 2, "master_data_sync_failed"));
                    }
                } else {
                    if (isManualSync) {
                        Toast.makeText(context, "Master Data Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
                    }
                    result.success(syncResult("MasterDataSync", 2, "master_data_sync_failed"));
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<ClientSettingDto>> call, Throwable t) {
                Log.e(TAG, "Master Data Sync Failed.", t);
                if (isManualSync) {
                    Toast.makeText(context, "Master Sync failed", Toast.LENGTH_LONG).show();
                }
                result.success(syncResult("MasterDataSync", 2, "master_data_sync_failed"));
            }
        });
    }

    private void saveMasterData(ClientSettingDto clientSettingDto, boolean isManualSync) {
        boolean foundErrors = false;
        boolean applicantValidDocPresent = clientSettingDto.getDataToSync().stream().filter(masterData -> masterData.getEntityName().equalsIgnoreCase("ApplicantValidDocument")).findAny().isPresent();
        for (MasterData masterData : clientSettingDto.getDataToSync()) {
            try {
                switch (masterData.getEntityType()) {
                    case "structured":
                        saveStructuredData(masterData.getEntityName(), masterData.getData(), applicantValidDocPresent);
                        break;
                    case "dynamic":
                        saveDynamicData(masterData.getData());
                    case "script":
                        if (isManualSync) {
                            CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
                            cryptoRequestDto.setValue(masterData.getData());
                            CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);
                            byte[] data = CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue());
                            downloadUrlData(Paths.get(context.getFilesDir().getAbsolutePath(), masterData.getEntityName()), new JSONObject(new String(data)));
                        }
                        break;
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

    private void downloadUrlData(Path path, JSONObject jsonObject) {
        Log.i(TAG, "Started downloading mvel script: " + path.toString());
        try {
            String headers = jsonObject.getString("headers");
            Map<String, String> map = new HashMap<>();
            if (headers != null && !headers.trim().isEmpty()) {
                String[] header = headers.split(",");
                for (String subHeader : header) {
                    if (subHeader.trim().isEmpty())
                        continue;
                    String[] headerValues = subHeader.split(":");
                    map.put(headerValues[0], headerValues[1]);
                }
            }
            long[] range = getFileRange(path);
            map.put("Range", String.format("bytes=%s-%s", (range == null) ? 0 :
                    range[0], (range == null) ? "" : range[1]));

            syncScript(() -> {
                    }, path, jsonObject.getBoolean("encrypted"), jsonObject.getString("url"),
                    map, this.clientCryptoManagerService.getClientKeyIndex());
        } catch (Exception e) {
            Log.e("Failed to download entity file", path.toString(), e);
        }
    }

    private long[] getFileRange(Path path) {
        long[] range = new long[2];
        Optional<FileSignature> signature = fileSignatureDao.findByFileName(path.toFile().getName());
        if (signature.isPresent() && path.toFile().length() < signature.get().getContentLength()) {
            range[0] = path.toFile().length();
            range[1] = signature.get().getContentLength();
            return range;
        }
        return null;
    }

    private void syncScript(Runnable onFinish, Path path, boolean isFileEncrypted, String url, Map<String, String> map, String keyIndex) throws Exception {
        AtomicReference<Integer> contentLength = new AtomicReference<>();
        AtomicReference<String> fileSignature = new AtomicReference<String>();

        Call<ResponseBody> call = syncRestService.downloadScript(url, map, keyIndex);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        long[] range = getFileRange(path);
                        try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile(),
                                (range == null) ? false : true)) {
                            fileSignature.set(response.headers().get("file-signature"));
                            contentLength.set(Integer.valueOf(response.headers().get("content-length")));
                            IOUtils.copy(response.body().byteStream(), fileOutputStream);
                            saveFileSignature(path, isFileEncrypted, fileSignature.get(), contentLength.get());
                            Toast.makeText(context, "Script Sync Completed", Toast.LENGTH_LONG).show();
                            onFinish.run();
                        } catch (Exception e) {
                            Log.e(TAG, "Error in downloading script", e);
                        }
                    } else {
                        Toast.makeText(context, "Script Sync failed " + response.errorBody(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Script Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Script Sync failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveFileSignature(Path path, boolean isFileEncrypted, String signature, Integer contentLength) {
        if (signature == null)
            return;
        FileSignature fileSignature = new FileSignature();
        fileSignature.setSignature(signature);
        fileSignature.setFileName(path.toFile().getName());
        fileSignature.setEncrypted(isFileEncrypted);
        fileSignature.setContentLength(contentLength);
        fileSignatureDao.insert(fileSignature);
    }

    private JSONArray getDecryptedDataList(String data) throws JSONException {
        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
        cryptoRequestDto.setValue(data);
        CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);
        return new JSONArray(new String(CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue())));
    }

    private void saveStructuredData(String entityName, String data, boolean applicantValidDocPresent) throws Exception {
        String serverVersion = getServerVersionFromConfigs();
        String defaultAppTypeCode = this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.DEFAULT_APP_TYPE_CODE);
        Boolean fullSync = this.globalParamRepository.getGlobalParamValue(MASTER_DATA_LAST_UPDATED) == null ? true : false;
        switch (entityName) {
            case "Machine":
                JSONArray machines = getDecryptedDataList(data);
                machineRepository.saveMachineMaster(new JSONObject(machines.getString(0)));
                break;
            case "RegistrationCenter":
                JSONArray centers = getDecryptedDataList(data);
                for (int i = 0; i < centers.length(); i++) {
                    registrationCenterRepository.saveRegistrationCenter(new JSONObject(centers.getString(i)));
                }
                break;
            case "DocumentType":
                JSONArray doctypes = getDecryptedDataList(data);
                for (int i = 0; i < doctypes.length(); i++) {
                    documentTypeRepository.saveDocumentType(new JSONObject(doctypes.getString(i)));
                }
                break;
            case "ApplicantValidDocument":
                JSONArray appValidDocs = getDecryptedDataList(data);
                for (int i = 0; i < appValidDocs.length(); i++) {
                    applicantValidDocRepository.saveApplicantValidDocument(new JSONObject(appValidDocs.getString(i)), defaultAppTypeCode);
                }
                break;
            case "Template":
                JSONArray templates = getDecryptedDataList(data);
                for (int i = 0; i < templates.length(); i++) {
                    templateRepository.saveTemplate(new JSONObject(templates.getString(i)));
                }
                break;
            case "Location":
                JSONArray locations = getDecryptedDataList(data);
                for (int i = 0; i < locations.length(); i++) {
                    JSONObject jsonObject = new JSONObject(locations.getString(i));
                    if (fullSync) {
                        if (jsonObject.getBoolean("isActive")) {
                            locationRepository.saveLocationData(jsonObject);
                        }
                    } else {
                        locationRepository.saveLocationData(jsonObject);
                    }
                }
                break;
            case "LocationHierarchy":
                JSONArray locationHierarchies = getDecryptedDataList(data);
                for (int i = 0; i < locationHierarchies.length(); i++) {
                    locationRepository.saveLocationHierarchyData(new JSONObject(locationHierarchies.getString(i)));
                }
                break;
            case "BlocklistedWords":
            case "BlacklistedWords":
                JSONArray words = getDecryptedDataList(data);
                for (int i = 0; i < words.length(); i++) {
                    blocklistedWordRepository.saveBlocklistedWord(new JSONObject(words.getString(i)));
                }
                break;
            case "SyncJobDef":
                JSONArray syncJobDefsJsonArray = getDecryptedDataList(data);
                for (int i = 0; i < syncJobDefsJsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(syncJobDefsJsonArray.getString(i));

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
                    languageRepository.saveLanguage(new JSONObject(languageJsonArray.getString(i)));
                }
                break;
            case "RegistrationCenterUser":
                JSONArray regCenterUserJsonArray = getDecryptedDataList(data);
                if (serverVersion.startsWith("1.1.5")) {
                    userDetailRepository.saveUserDetail(regCenterUserJsonArray);
                }
                break;
            case "RegistrationCenterMachine":
                JSONArray regCenterMachineJsonArray = getDecryptedDataList(data);
                JSONObject jsonObject = new JSONObject(regCenterMachineJsonArray.getString(0));
                regCenterId = jsonObject.getString("regCenterId");
                break;
            case "ValidDocument":
                JSONArray validDocumentsJsonArray = getDecryptedDataList(data);
                if (!applicantValidDocPresent) {
                    for (int i = 0; i < validDocumentsJsonArray.length(); i++) {
                        applicantValidDocRepository.saveApplicantValidDocument(new JSONObject(validDocumentsJsonArray.getString(i)), defaultAppTypeCode);
                    }
                }
                break;
        }
    }

    private String getServerVersionFromConfigs() {
        return this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION);
    }

    private void saveDynamicData(String data) throws JSONException {
        JSONArray list = getDecryptedDataList(data);
        for (int i = 0; i < list.length(); i++) {
            dynamicFieldRepository.saveDynamicField(new JSONObject(list.getString(i)));
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
        if (globalParamRepository.getGlobalParamValue(MASTER_DATA_LAST_UPDATED) == null) {
            globalParamSyncTime = "LastSyncTimeIsNull";
        } else
            globalParamSyncTime = globalParamRepository.getGlobalParamValue(MASTER_DATA_LAST_UPDATED);
        syncTime = new MasterDataSyncPigeon.SyncTime.Builder()
                .setSyncTime(globalParamSyncTime)
                .build();
        result.success(syncTime);
        return;
    }

    @Override
    public void getPolicyKeySync(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        syncPolicyKey(isManualSync, result, () -> {
            Log.i(TAG, "Policy Key Sync Completed");
        });
    }

    @Override
    public void getGlobalParamsSync(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        try {
            syncGlobalParamsData(isManualSync, result, () -> {
                Log.i(TAG, "Sync Global Params Completed.");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getUserDetailsSync(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        try {
            syncUserDetails(isManualSync, result, () -> {
                Log.i(TAG, "User details sync Completed.");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getIDSchemaSync(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        syncLatestIdSchema(isManualSync, result, context, () -> {
            Log.i(TAG, "ID Schema Sync Completed");
        });
    }

    @Override
    public void getMasterDataSync(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        syncMasterData(isManualSync, result, () -> {
            Log.i(TAG, "Master Data Sync Completed.");
        }, 0);
    }

    private MasterDataSyncPigeon.Sync syncResult(String syncType, int progress, String errorCode) {
        return new MasterDataSyncPigeon.Sync.Builder()
                .setSyncType(syncType)
                .setSyncProgress(Long.valueOf(progress))
                .setErrorCode(errorCode)
                .build();
    }

    @Override
    public void getCaCertsSync(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        syncCACertificates(isManualSync, result, 0);
    }

    @Override
    public void batchJob(@NonNull MasterDataSyncPigeon.Result<String> result) {
        syncRegistrationPackets(this.context);
        result.success("Registration Packet Sync Completed.");
    }

    private void syncRegistrationPackets(Context context) {
        if (NetworkUtils.isNetworkConnected(context)) {
            Log.d(getClass().getSimpleName(), "Sync Packets in main activity");
            Integer batchSize = getBatchSize();
            List<Registration> registrationList = packetService.getRegistrationsByStatus(PacketClientStatus.APPROVED.name(), batchSize);

//          Variable is accessed within inner class. Needs to be declared final also it is modified too in the inner class
//          Solution: using final array variable with one element that can be altered
            final Integer[] remainingPack = {registrationList.size()};

            if (registrationList.isEmpty()) {
                uploadRegistrationPackets(context);
                return;
            }
            for (Registration value : registrationList) {
                try {
                    Log.d(getClass().getSimpleName(), "Syncing " + value.getPacketId());
                    auditManagerService.audit(AuditEvent.SYNC_PACKET, Components.REG_PACKET_LIST);
                    packetService.syncRegistration(value.getPacketId(), new AsyncPacketTaskCallBack() {
                        @Override
                        public void inProgress(String RID) {
                            //Do nothing
                        }

                        @Override
                        public void onComplete(String RID, PacketTaskStatus status) {
                            remainingPack[0] -= 1;
                            Log.d(getClass().getSimpleName(), "Remaining pack" + remainingPack[0]);
                            if (remainingPack[0] == 0) {
                                Log.d(getClass().getSimpleName(), "Last Packet" + RID);
                                uploadRegistrationPackets(context);
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            }
        }
    }

    private void uploadRegistrationPackets(Context context) {
        if (NetworkUtils.isNetworkConnected(context)) {
            Log.d(getClass().getSimpleName(), "Upload Packets in main activity");
            Integer batchSize = getBatchSize();
            List<Registration> registrationList = packetService.getRegistrationsByStatus(PacketClientStatus.SYNCED.name(), batchSize);
            for (Registration value : registrationList) {
                try {
                    Log.d(getClass().getSimpleName(), "Uploading " + value.getPacketId());
                    auditManagerService.audit(AuditEvent.UPLOAD_PACKET, Components.REG_PACKET_LIST);
                    packetService.uploadRegistration(value.getPacketId());
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            }
        }
    }

    private Integer getBatchSize() {
        // Default batch size is 4
        List<GlobalParam> globalParams = globalParamDao.getGlobalParams();
        for (GlobalParam value : globalParams) {
            if (Objects.equals(value.getId(), "mosip.registration.packet_upload_batch_size")) {
                return Integer.parseInt(value.getValue());
            }
        }
        return ClientManagerConstant.DEFAULT_BATCH_SIZE;
    }

    private void syncCACertificates(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result, int retryNo) {
        Call<ResponseWrapper<CACertificateResponseDto>> call = syncRestService.getCACertificates(null,
                BuildConfig.CLIENT_VERSION);
        call.enqueue(new Callback<ResponseWrapper<CACertificateResponseDto>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<CACertificateResponseDto>> call, Response<ResponseWrapper<CACertificateResponseDto>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    String errorMessage = error != null ? error.getMessage() : null;
                    if (errorMessage == null) {
                        try {
                            saveCACertificate(response.body().getResponse().getCertificateDTOList());
                            if (isManualSync) {
                                Toast.makeText(context, "CA Certificate Sync Completed", Toast.LENGTH_LONG).show();
                            }
                            result.success(syncResult("CACertificatesSync", 6, ""));
                        } catch (Throwable t) {
                            Log.e(TAG, "Failed to sync CA certificates", t);
                        }
                    } else {
                        if (isManualSync) {
                            Toast.makeText(context, "CA Certificate Sync failed " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                        result.success(syncResult("CACertificatesSync", 6, "ca_certs_sync_failed"));
                    }
                } else {
                    if (isManualSync) {
                        Toast.makeText(context, "CA Certificate Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
                    }
                    result.success(syncResult("CACertificatesSync", 6, "ca_certs_sync_failed"));
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<CACertificateResponseDto>> call, Throwable t) {
                if (isManualSync) {
                    Toast.makeText(context, "CA Certificate Sync failed", Toast.LENGTH_LONG).show();
                }
                result.success(syncResult("CACertificatesSync", 6, "ca_certs_sync_failed"));
            }
        });
    }

    private void saveCACertificate(List<CACertificateDto> caCertificateDtos) {
        if (caCertificateDtos != null && !caCertificateDtos.isEmpty()) {
            Log.i(TAG, "Started saving cacertificates with size: " + caCertificateDtos.size());
            //Data Fix : As createdDateTime is null sometimes
            caCertificateDtos.forEach(c -> {
                if (c.getCreatedtimes() == null)
                    c.setCreatedtimes(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
            });
            caCertificateDtos.sort((CACertificateDto d1, CACertificateDto d2) -> d1.getCreatedtimes().compareTo(d2.getCreatedtimes()));

            for (CACertificateDto cert : caCertificateDtos) {
                try {
                    if (cert.getPartnerDomain() != null && cert.getPartnerDomain().equals("DEVICE")) {
                        CACertificateRequestDto caCertificateRequestDto = new CACertificateRequestDto();
                        caCertificateRequestDto.setCertificateData(cert.getCertData());
                        caCertificateRequestDto.setPartnerDomain(cert.getPartnerDomain());
                        io.mosip.registration.keymanager.dto.CACertificateResponseDto caCertificateResponseDto = certificateManagerService.uploadCACertificate(caCertificateRequestDto);
                        Log.i(TAG, caCertificateResponseDto.getStatus());
                    }
                } catch (KeymanagerServiceException ex) {
                    Log.e(TAG, "Failed to save CA cert : " + cert.getCertId());
                }
            }
        }
    }
}
