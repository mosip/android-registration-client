package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.FileSignatureDao;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.ReasonListDto;
import io.mosip.registration.clientmanager.dto.http.*;
import io.mosip.registration.clientmanager.dto.registration.GenericDto;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.entity.FileSignature;
import io.mosip.registration.clientmanager.entity.GlobalParam;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.entity.Language;
import io.mosip.registration.clientmanager.entity.Location;
import io.mosip.registration.clientmanager.entity.MachineMaster;
import io.mosip.registration.clientmanager.entity.ReasonList;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.repository.*;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
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
import io.mosip.registration.packetmanager.util.JsonUtils;
import okhttp3.ResponseBody;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Singleton
public class MasterDataServiceImpl implements MasterDataService {

    private static final String TAG = MasterDataServiceImpl.class.getSimpleName();
    private static final String MASTER_DATA_LAST_UPDATED = "masterdata.lastupdated";
    private static final String SYNC_LAST_UPDATED = "sync.lastupdated";
    public static final String REG_APP_ID = "REGISTRATION";
    public static final String KERNEL_APP_ID = "KERNEL";
    private static final String POLICY_KEY_SYNC_FAILED = "policy_key_sync_failed";
    private static final String MASTER_DATA_SYNC_FAILED = "master_data_sync_failed";
    private static final String GLOBAL_PARAMS_SYNC_FAILED = "global_params_sync_failed";
    private static final String ID_SCHEMA_SYNC_FAILED = "id_schema_sync_failed";
    private static final String USER_DETAILS_SYNC_FAILED = "user_details_sync_failed";
    private static final String CA_CERTS_SYNC_FAILED = "ca_certs_sync_failed";
    private static final String SERVER_VERSION_1_1_5 = "1.1.5";

    private final int master_data_recursive_sync_max_retry = 3;

    private ObjectMapper objectMapper;

    private Context context;
    private SyncRestService syncRestService;
    private ClientCryptoManagerService clientCryptoManagerService;
    private MachineRepository machineRepository;
    private ReasonListRepository reasonListRepository;
    private RegistrationCenterRepository registrationCenterRepository;
    private ApplicantValidDocRepository applicantValidDocRepository;
    private DocumentTypeRepository documentTypeRepository;
    private TemplateRepository templateRepository;
    private DynamicFieldRepository dynamicFieldRepository;
    private LocationRepository locationRepository;
    private GlobalParamRepository globalParamRepository;
    private IdentitySchemaRepository identitySchemaRepository;
    private BlocklistedWordRepository blocklistedWordRepository;
    private SyncJobDefRepository syncJobDefRepository;
    private UserDetailRepository userDetailRepository;
    private CertificateManagerService certificateManagerService;
    private LanguageRepository languageRepository;
    private JobManagerService jobManagerService;
    private FileSignatureDao fileSignatureDao;
    private String regCenterId;
    private String result = "";
    SharedPreferences sharedPreferences;

    @Inject
    public MasterDataServiceImpl(Context context, ObjectMapper objectMapper, SyncRestService syncRestService,
                                 ClientCryptoManagerService clientCryptoManagerService,
                                 MachineRepository machineRepository,
                                 ReasonListRepository reasonListRepository,
                                 RegistrationCenterRepository registrationCenterRepository,
                                 DocumentTypeRepository documentTypeRepository,
                                 ApplicantValidDocRepository applicantValidDocRepository,
                                 TemplateRepository templateRepository,
                                 DynamicFieldRepository dynamicFieldRepository,
                                 LocationRepository locationRepository,
                                 GlobalParamRepository globalParamRepository,
                                 IdentitySchemaRepository identitySchemaRepository,
                                 BlocklistedWordRepository blocklistedWordRepository,
                                 SyncJobDefRepository syncJobDefRepository,
                                 UserDetailRepository userDetailRepository,
                                 CertificateManagerService certificateManagerService,
                                 LanguageRepository languageRepository,
                                 JobManagerService jobManagerService,
                                 FileSignatureDao fileSignatureDao) {
        this.context = context;
        this.objectMapper = objectMapper;
        this.syncRestService = syncRestService;
        this.clientCryptoManagerService = clientCryptoManagerService;
        this.machineRepository = machineRepository;
        this.reasonListRepository = reasonListRepository;
        this.registrationCenterRepository = registrationCenterRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.applicantValidDocRepository = applicantValidDocRepository;
        this.templateRepository = templateRepository;
        this.dynamicFieldRepository = dynamicFieldRepository;
        this.locationRepository = locationRepository;
        this.globalParamRepository = globalParamRepository;
        this.identitySchemaRepository = identitySchemaRepository;
        this.blocklistedWordRepository = blocklistedWordRepository;
        this.syncJobDefRepository = syncJobDefRepository;
        this.userDetailRepository = userDetailRepository;
        this.certificateManagerService = certificateManagerService;
        this.languageRepository = languageRepository;
        this.jobManagerService = jobManagerService;
        this.fileSignatureDao = fileSignatureDao;
        sharedPreferences = this.context.getSharedPreferences(
                this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE);
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
    public void syncCertificate(Runnable onFinish, String applicationId, String referenceId, String setApplicationId, String setReferenceId, boolean isManualSync) {
        CenterMachineDto centerMachineDto = getRegistrationCenterMachineDetails();
        if (centerMachineDto == null) {
            result = POLICY_KEY_SYNC_FAILED;
            onFinish.run();
            return;
        }

        Call<ResponseWrapper<CertificateResponse>> call = syncRestService.getPolicyKey(applicationId,
                referenceId, BuildConfig.CLIENT_VERSION);
        call.enqueue(new Callback<ResponseWrapper<CertificateResponse>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<CertificateResponse>> call, Response<ResponseWrapper<CertificateResponse>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        try {
                            CertificateRequestDto certificateRequestDto = new CertificateRequestDto();
                            certificateRequestDto.setApplicationId(setApplicationId);
                            certificateRequestDto.setReferenceId(setReferenceId);
                            certificateRequestDto.setCertificateData(response.body().getResponse().getCertificate());
                            certificateManagerService.uploadOtherDomainCertificate(certificateRequestDto);
                            if (isManualSync) {
                                Toast.makeText(context, "Policy key Sync Completed", Toast.LENGTH_LONG).show();
                            }
                            result = "";
                            onFinish.run();
                        } catch (Exception e) {
                            result = POLICY_KEY_SYNC_FAILED;
                            Log.e(TAG, "Policy key Sync failed.", e);
                            if (isManualSync) {
                                Toast.makeText(context, "Policy key Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            onFinish.run();
                        }
                    } else {
                        result = POLICY_KEY_SYNC_FAILED;
                        if (isManualSync) {
                            Toast.makeText(context, "Policy key Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        onFinish.run();
                    }
                } else {
                    result = POLICY_KEY_SYNC_FAILED;
                    if (isManualSync) {
                        Toast.makeText(context, "Policy key Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
                    }
                    onFinish.run();
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<CertificateResponse>> call, Throwable t) {
                result = POLICY_KEY_SYNC_FAILED;
                if (isManualSync) {
                    Toast.makeText(context, "Policy key Sync failed", Toast.LENGTH_LONG).show();
                }
                onFinish.run();
            }
        });
    }

    @Override
    public void syncMasterData(Runnable onFinish, int retryNo, boolean isManualSync) {
        CenterMachineDto centerMachineDto = getRegistrationCenterMachineDetails();

        Map<String, String> queryParams = new HashMap<>();

        try {
            queryParams.put("keyindex", this.clientCryptoManagerService.getClientKeyIndex());
        } catch (Exception e) {
            result = MASTER_DATA_SYNC_FAILED;
            Log.e(TAG, "MasterData : not able to get client key index", e);
            if (isManualSync) {
                Toast.makeText(context, "Master Sync failed", Toast.LENGTH_LONG).show();
            }
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
        Call<ResponseWrapper<ClientSettingDto>> call = serverVersion.startsWith(SERVER_VERSION_1_1_5) ? syncRestService.fetchV1MasterData(queryParams) : syncRestService.fetchMasterData(queryParams);

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
                                syncMasterData(onFinish, retryNo + 1, isManualSync);
                            } else {
                                result = MASTER_DATA_SYNC_FAILED;
                                if (isManualSync) {
                                    Toast.makeText(context, "Master Data Sync failed! Please try again in some time", Toast.LENGTH_LONG).show();
                                }
                                onFinish.run();
                            }
                        } else {
                            result = "";
                            if (isManualSync) {
                                Toast.makeText(context, "Master Data Sync Completed", Toast.LENGTH_LONG).show();
                            }
                            onFinish.run();
                        }
                    } else {
                        result = MASTER_DATA_SYNC_FAILED;
                        if (isManualSync) {
                            Toast.makeText(context, "Master Data Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        onFinish.run();
                    }
                } else {
                    result = MASTER_DATA_SYNC_FAILED;
                    if (isManualSync) {
                        Toast.makeText(context, "Master Data Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
                    }
                    onFinish.run();
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<ClientSettingDto>> call, Throwable t) {
                result = MASTER_DATA_SYNC_FAILED;
                if (isManualSync) {
                    Toast.makeText(context, "Master Sync failed", Toast.LENGTH_LONG).show();
                }
                onFinish.run();
            }
        });
    }

    @Override
    public void syncGlobalParamsData(Runnable onFinish, boolean isManualSync) throws Exception {
        Log.i(TAG, "config data sync is started");
        String serverVersion = getServerVersionFromConfigs();

        Call<ResponseWrapper<Map<String, Object>>> call = (serverVersion != null && serverVersion.startsWith(SERVER_VERSION_1_1_5)) ? syncRestService.getV1GlobalConfigs(
                clientCryptoManagerService.getMachineName(), BuildConfig.CLIENT_VERSION) : syncRestService.getGlobalConfigs(
                clientCryptoManagerService.getClientKeyIndex(), BuildConfig.CLIENT_VERSION);
        call.enqueue(new Callback<ResponseWrapper<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Map<String, Object>>> call, Response<ResponseWrapper<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(response.body());
                    if (error == null) {
                        saveGlobalParams(response.body().getResponse());
                        result = "";
                        if (isManualSync) {
                            Toast.makeText(context, context.getString(R.string.global_config_sync_completed), Toast.LENGTH_LONG).show();
                        }
                        onFinish.run();
                    } else {
                        result = GLOBAL_PARAMS_SYNC_FAILED;
                        if (isManualSync) {
                            Toast.makeText(context, String.format("%s %s", context.getString(R.string.global_config_sync_failed), error.getMessage()), Toast.LENGTH_LONG).show();
                        }
                        onFinish.run();
                    }
                } else {
                    result = GLOBAL_PARAMS_SYNC_FAILED;
                    if (isManualSync) {
                        Toast.makeText(context, String.format("%s. %s:%s", context.getString(R.string.global_config_sync_failed), context.getString(R.string.status_code), String.valueOf(response.code())), Toast.LENGTH_LONG).show();
                    }
                    onFinish.run();
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Map<String, Object>>> call, Throwable t) {
                result = GLOBAL_PARAMS_SYNC_FAILED;
                if (isManualSync) {
                    Toast.makeText(context, context.getString(R.string.global_config_sync_failed), Toast.LENGTH_LONG).show();
                }
                onFinish.run();
            }
        });
    }

    @SuppressWarnings("unchecked")
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
            SharedPreferences.Editor editor = this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                    Context.MODE_PRIVATE).edit();
            editor.putString(RegistrationConstants.DEDUPLICATION_ENABLE_FLAG, this.globalParamRepository
                    .getCachedStringGlobalParam(RegistrationConstants.DEDUPLICATION_ENABLE_FLAG));
            editor.apply();
        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage(), exception);
        }
    }

    @SuppressWarnings("unchecked")
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

    @Override
    public void syncLatestIdSchema(Runnable onFinish, boolean isManualSync) {
        Call<ResponseBody> call = syncRestService.getLatestIdSchema(BuildConfig.CLIENT_VERSION, "registration-client");
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String jsonString = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String responseString = jsonObject.get("response").toString();
                        ResponseWrapper<IdSchemaResponse> wrapper = JsonUtils.jsonStringToJavaObject(jsonString,
                                new TypeReference<ResponseWrapper<IdSchemaResponse>>() {
                                });
                        identitySchemaRepository.saveIdentitySchema(context, wrapper.getResponse());
                        saveProcessSpec(wrapper.getResponse(), responseString);
                        result = "";
                        if (isManualSync) {
                            Toast.makeText(context, "Identity schema and UI Spec Sync Completed", Toast.LENGTH_LONG).show();
                        }
                        onFinish.run();
                    } catch (Exception e) {
                        result = ID_SCHEMA_SYNC_FAILED;
                        Log.e(TAG, "Failed to save IDSchema", e);
                        onFinish.run();
                    }
                } else {
                    result = ID_SCHEMA_SYNC_FAILED;
                    if (isManualSync) {
                        Toast.makeText(context, "Identity schema and UI Spec Sync failed with status code : " +
                                response.code(), Toast.LENGTH_LONG).show();
                    }
                    onFinish.run();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result = ID_SCHEMA_SYNC_FAILED;
                Log.e(TAG, "Failed to sync schema", t);
                if (isManualSync) {
                    Toast.makeText(context, "Identity schema and UI Spec Sync failed", Toast.LENGTH_LONG).show();
                }
                onFinish.run();
            }
        });
    }

    private void saveProcessSpec(IdSchemaResponse idSchemaResponse, String jsonString) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.toLowerCase().endsWith("process")) {
                try {
                    ProcessSpecDto processSpecDto = JsonUtils.jsonStringToJavaObject(jsonObject.get(key).toString(),
                            new TypeReference<ProcessSpecDto>() {
                            });
                    identitySchemaRepository.createProcessSpec(context, key, idSchemaResponse.getIdVersion(), processSpecDto);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }


    @Override
    public void syncUserDetails(Runnable onFinish, boolean isManualSync) throws Exception {
        String serverVersion = getServerVersionFromConfigs();
        if (serverVersion.startsWith(SERVER_VERSION_1_1_5)) {
            result = "";
            if (isManualSync) {
                Toast.makeText(context, "User Sync Completed", Toast.LENGTH_LONG).show();
            }
            Log.i(TAG, "Found 115 version, skipping userdetails sync");
            onFinish.run();
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
                        result = "";
                        if (isManualSync) {
                            Toast.makeText(context, "User Sync Completed", Toast.LENGTH_LONG).show();
                        }
                        onFinish.run();
                    } else {
                        result = USER_DETAILS_SYNC_FAILED;
                        if (isManualSync) {
                            Toast.makeText(context, "User Sync failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        onFinish.run();
                    }
                } else {
                    result = USER_DETAILS_SYNC_FAILED;
                    if (isManualSync) {
                        Toast.makeText(context, "User Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
                    }
                    onFinish.run();
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<UserDetailResponse>> call, Throwable t) {
                result = USER_DETAILS_SYNC_FAILED;
                if (isManualSync) {
                    Toast.makeText(context, "User Sync failed", Toast.LENGTH_LONG).show();
                }
                onFinish.run();
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
    public void syncCACertificates(Runnable onFinish, boolean isManualSync) {
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
                            result = "";
                            if (isManualSync) {
                                Toast.makeText(context, "CA Certificate Sync Completed", Toast.LENGTH_LONG).show();
                            }
                            onFinish.run();
                            return;
                        } catch (Throwable t) {
                            Log.e(TAG, "Failed to sync CA certificates", t);
                            errorMessage = t.getMessage();
                        }
                    }
                    result = CA_CERTS_SYNC_FAILED;
                    if (isManualSync) {
                        Toast.makeText(context, "CA Certificate Sync failed " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                    onFinish.run();
                } else {
                    result = CA_CERTS_SYNC_FAILED;
                    if (isManualSync) {
                        Toast.makeText(context, "CA Certificate Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
                    }
                    onFinish.run();
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<CACertificateResponseDto>> call, Throwable t) {
                result = CA_CERTS_SYNC_FAILED;
                if (isManualSync) {
                    Toast.makeText(context, "CA Certificate Sync failed", Toast.LENGTH_LONG).show();
                }
                onFinish.run();
            }
        });
    }

    @Override
    public String onResponseComplete() {
        return result;
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

    @Override
    public List<ReasonListDto> getAllReasonsList(String langCode) {
        List<ReasonListDto> reasonListResponse = new ArrayList<>();
        List<ReasonList> reasonList = reasonListRepository.getAllReasonList(langCode);
        reasonList.forEach(reason -> {
            ReasonListDto reasonListDto = new ReasonListDto();
            reasonListDto.setCode(reason.getCode());
            reasonListDto.setName(reason.getName());
            reasonListDto.setDescription(reason.getDescription());
            reasonListDto.setLangCode(reason.getLangCode());
            reasonListResponse.add(reasonListDto);
        });
        return reasonListResponse;
    }

    @Override
    public Integer getHierarchyLevel(String hierarchyLevelName) {
        return locationRepository.getHierarchyLevel(hierarchyLevelName);
    }

    private void saveMasterData(ClientSettingDto clientSettingDto, boolean isManualSync) {
//        boolean foundErrors = false;
        boolean applicantValidDocPresent = clientSettingDto.getDataToSync().stream().filter(masterData -> masterData.getEntityName().equalsIgnoreCase("ApplicantValidDocument")).findAny().isPresent();
        for (MasterData masterData : clientSettingDto.getDataToSync()) {
            try {
                switch (masterData.getEntityType()) {
                    case "structured":
                        saveStructuredData(masterData.getEntityName(), masterData.getData(), applicantValidDocPresent);
                        break;
                    case "dynamic":
                        saveDynamicData(masterData.getData());
                        break;
                    case "script":
                        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
                        cryptoRequestDto.setValue(masterData.getData());
                        CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);
                        byte[] data = CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue());
                        downloadUrlData(Paths.get(context.getFilesDir().getAbsolutePath(), masterData.getEntityName()), new JSONObject(new String(data)), isManualSync);
                        break;
                }
            } catch (Throwable e) {
//                foundErrors = true;
                Log.e(TAG, "Failed to parse the data", e);
            }
        }
        Log.i(TAG, "Masterdata lastSyncTime : " + clientSettingDto.getLastSyncTime());
        this.globalParamRepository.saveGlobalParam(MASTER_DATA_LAST_UPDATED, clientSettingDto.getLastSyncTime());
//          This is reflected in manual sync as MasterData Last SyncTime updates in 15min intervals only
        this.globalParamRepository.saveGlobalParam(SYNC_LAST_UPDATED, getCurrentTime());
    }

    private String getCurrentTime() {
        return Instant.now().toString();
    }

    private void downloadUrlData(Path path, JSONObject jsonObject, boolean isManualSync) {
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
                    map, this.clientCryptoManagerService.getClientKeyIndex(), isManualSync);
        } catch (Exception e) {
            Log.e("Failed to download entity file", path.toString(), e);
        }
    }

    private void syncScript(Runnable onFinish, Path path, boolean isFileEncrypted, String url, Map<String, String> map, String keyIndex, boolean isManualSync) throws Exception {
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
                            if (isManualSync) {
                                Toast.makeText(context, "Script Sync Completed", Toast.LENGTH_LONG).show();
                            }
                            onFinish.run();
                        } catch (Exception e) {
                            Log.e(TAG, "Error in downloading script", e);
                            if (isManualSync) {
                                Toast.makeText(context, "Script Sync failed " + response.errorBody(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        if (isManualSync) {
                            Toast.makeText(context, "Script Sync failed " + response.errorBody(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    if (isManualSync) {
                        Toast.makeText(context, "Script Sync failed with status code : " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (isManualSync) {
                    Toast.makeText(context, "Script Sync failed", Toast.LENGTH_LONG).show();
                }
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

    private JSONArray getDecryptedDataList(String data) throws JSONException {
        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
        cryptoRequestDto.setValue(data);
        CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);
        return new JSONArray(new String(CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue())));
    }

    private String getServerVersionFromConfigs() {
        return this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION);
    }

    private void saveStructuredData(String entityName, String data, boolean applicantValidDocPresent) throws JSONException {
        String serverVersion = getServerVersionFromConfigs();
        String defaultAppTypeCode = this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.DEFAULT_APP_TYPE_CODE);
        Boolean fullSync = this.globalParamRepository.getGlobalParamValue(MASTER_DATA_LAST_UPDATED) == null ? true : false;
        switch (entityName) {
            case "ReasonList":
                Log.i(getClass().getSimpleName(), getDecryptedDataList(data).toString());
                JSONArray reasons = getDecryptedDataList(data);
                for (int i = 0; i < reasons.length(); i++) {
                    reasonListRepository.saveReasonList(new JSONObject(reasons.getString(i)));
                }
                break;
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
                if (serverVersion.startsWith(SERVER_VERSION_1_1_5)) {
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

    private void saveDynamicData(String data) throws JSONException {
        JSONArray list = getDecryptedDataList(data);
        for (int i = 0; i < list.length(); i++) {
            dynamicFieldRepository.saveDynamicField(new JSONObject(list.getString(i)));
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
    public List<GenericValueDto> getFieldValues(String fieldName, String langCode) {
        return dynamicFieldRepository.getDynamicValues(fieldName, langCode);
    }

    @Override
    public List<GenericValueDto> getFieldValuesByCode(String fieldName, String code) {
        return dynamicFieldRepository.getDynamicValuesByCode(fieldName, code);
    }


    @Override
    public List<GenericValueDto> findLocationByParentHierarchyCode(String parentCode, String langCode) {
        return this.locationRepository.getLocations(parentCode, langCode);
    }

    @Override
    public List<GenericValueDto> findLocationByCode(String code) {
        return this.locationRepository.getLocationsByCode(code);
    }

    @Override
    public List<GenericValueDto> findLocationByHierarchyLevel(int hierarchyLevel, String langCode) {
        return this.locationRepository.getLocationsBasedOnHierarchyLevel(hierarchyLevel, langCode);
    }

    @Override
    public List<String> getDocumentTypes(String categoryCode, String applicantType, String langCode) {
        return this.applicantValidDocRepository.getDocumentTypes(applicantType, categoryCode, langCode);
    }

    @Override
    public String getTemplateContent(String templateName, String langCode) {
        return templateRepository.getTemplate(templateName, langCode);
    }

    @Override
    public String getPreviewTemplateContent(String templateTypeCode, String langCode) {
        return templateRepository.getPreviewTemplate(templateTypeCode, langCode);
    }

    @Override
    public List<Language> getAllLanguages() {
        return languageRepository.getAllLanguages();
    }

    @Override
    public List<Location> findAllLocationsByLangCode(String langCode) {
        return locationRepository.findAllLocationsByLangCode(langCode);
    }

    @Override
    public void saveGlobalParam(String id, String value) {
        globalParamRepository.saveGlobalParam(id, value);
    }

    @Override
    public String getGlobalParamValue(String id) {
        String value = globalParamRepository.getGlobalParamValue(id);
        return value == null ? "" : value;
    }
}
