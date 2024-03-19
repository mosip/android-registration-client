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
    }

    @Override
    public void getPolicyKeySync(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        masterDataService.syncCertificate(() -> {
            Log.i(TAG, "Policy Key Sync Completed");
            result.success(syncResult("PolicyKeySync", 5, masterDataService.onResponseComplete()));
        }, isManualSync);
    }

    @Override
    public void getGlobalParamsSync(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        try {
            masterDataService.syncGlobalParamsData(() -> {
                Log.i(TAG, "Sync Global Params Completed.");
                result.success(syncResult("GlobalParamsSync", 1, masterDataService.onResponseComplete()));
            }, isManualSync);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getUserDetailsSync(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        try {
            masterDataService.syncUserDetails(() -> {
                Log.i(TAG, "User details sync Completed.");
                result.success(syncResult("UserDetailsSync", 3, masterDataService.onResponseComplete()));
            }, isManualSync);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getIDSchemaSync(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        try {
            masterDataService.syncLatestIdSchema(() -> {
                Log.i(TAG, "ID Schema Sync Completed");
                result.success(syncResult("LatestIDSchemaSync", 4, masterDataService.onResponseComplete()));
            }, isManualSync);
        } catch (Exception e) {
            Log.e(TAG, "ID Schema Sync Failed.", e);
            e.printStackTrace();
        }
    }

    @Override
    public void getMasterDataSync(@NonNull Boolean isManualSync, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        try {
            masterDataService.syncMasterData(() -> {
                Log.i(TAG, "Master Data Sync Completed.");
                result.success(syncResult("MasterDataSync", 1, masterDataService.onResponseComplete()));
            }, 0, isManualSync);
        } catch (Exception e) {
            Log.e(TAG, "Master Data Sync Failed.", e);
            e.printStackTrace();
        }

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
        masterDataService.syncCACertificates(() -> {
            Log.i(TAG, "CA Certificate Sync Completed");
            result.success(syncResult("CACertificatesSync", 6, masterDataService.onResponseComplete()));
        }, isManualSync);
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
}
