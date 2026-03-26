/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.mosip.registration_client.api_services;

import static android.content.ContentValues.TAG;
import static io.mosip.registration.clientmanager.service.MasterDataServiceImpl.KERNEL_APP_ID;
import static io.mosip.registration.clientmanager.service.MasterDataServiceImpl.REG_APP_ID;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dao.FileSignatureDao;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;

import io.mosip.registration.clientmanager.entity.GlobalParam;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;

import io.mosip.registration.clientmanager.dto.ReasonListDto;
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
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.service.JobManagerServiceImpl;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.clientmanager.spi.LocalConfigService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.PreRegistrationDataSyncService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.BioSdkProviderFactory;
import io.mosip.registration.clientmanager.util.CronExpressionParser;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration_client.utils.BatchJob;
import io.mosip.registration_client.MainActivity;
import io.mosip.registration_client.UploadBackgroundService;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.mosip.registration_client.model.MasterDataSyncPigeon;
import io.mosip.registration_client.utils.NetworkUtils;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;

@Singleton
public class MasterDataSyncApi implements MasterDataSyncPigeon.SyncApi {
    private static final String MASTER_DATA_LAST_UPDATED = "masterdata.lastupdated";
    private static final String SYNC_LAST_UPDATED = "sync.lastupdated";
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
    PreRegistrationDataSyncService preRegistrationDataSyncService;
    LocalConfigService localConfigService;
    BioSdkProviderFactory bioSdkProviderFactory;
    Context context;
    private String regCenterId;

    private Activity activity;

    BatchJob batchJob;
    private BinaryMessenger flutterBinaryMessenger;

    private final Object restartLock = new Object();
    private final AtomicInteger runningSyncJobs = new AtomicInteger(0);
    private final AtomicBoolean restartRequested = new AtomicBoolean(false);
    private final Handler restartHandler = new Handler(Looper.getMainLooper());
    private volatile Runnable pendingRestartPrompt;

    @Inject
    public MasterDataSyncApi(ClientCryptoManagerService clientCryptoManagerService,
                             MachineRepository machineRepository,
                             RegistrationCenterRepository registrationCenterRepository,
                             SyncRestService syncRestService,
                             CertificateManagerService certificateManagerService,
                             GlobalParamRepository globalParamRepository,
                             ObjectMapper objectMapper,
                             UserDetailRepository userDetailRepository,
                             IdentitySchemaRepository identitySchemaRepository,
                             Context context,
                             DocumentTypeRepository documentTypeRepository,
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
                             GlobalParamDao globalParamDao,
                             FileSignatureDao fileSignatureDao,
                             PreRegistrationDataSyncService preRegistrationDataSyncService,
                             LocalConfigService localConfigService,
                             BioSdkProviderFactory bioSdkProviderFactory) {
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
        this.preRegistrationDataSyncService = preRegistrationDataSyncService;
        this.localConfigService = localConfigService;
        this.bioSdkProviderFactory = bioSdkProviderFactory;
    }

    public void setCallbackActivity(MainActivity mainActivity, BatchJob batchJob, BinaryMessenger flutterBinaryMessenger) {
        this.activity = mainActivity;
        this.batchJob = batchJob;
        this.flutterBinaryMessenger = flutterBinaryMessenger;
    }

    @Override
    public void getLastSyncTime(@NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.SyncTime> result) {
        MasterDataSyncPigeon.SyncTime syncTime;
        String globalParamSyncTime;
        if (globalParamRepository.getGlobalParamValue(SYNC_LAST_UPDATED) == null) {
            globalParamSyncTime = "LastSyncTimeIsNull";
        } else
            globalParamSyncTime = globalParamRepository.getGlobalParamValue(SYNC_LAST_UPDATED);
        syncTime = new MasterDataSyncPigeon.SyncTime.Builder()
                .setSyncTime(globalParamSyncTime)
                .build();
        result.success(syncTime);
    }

    @Override
    public void getPolicyKeySync(@NonNull Boolean isManualSync, @NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        if (isManualSync && isExcludedJob(jobId)) {
            result.success(syncResult("PolicyKeySync", 5, ""));
            return;
        }
        onSyncJobStart();
        CenterMachineDto centerMachineDto = masterDataService.getRegistrationCenterMachineDetails();
        if (centerMachineDto == null) {
            onSyncJobComplete(jobId, false, isManualSync);
            result.success(syncResult("PolicyKeySync", 5, "policy_key_sync_failed"));
            return;
        }

        try {
            masterDataService.syncCertificate(() -> {
                String errorCode = masterDataService.onResponseComplete();
                boolean success = errorCode == null || errorCode.isEmpty();
                onSyncJobComplete(jobId, success, isManualSync);
                result.success(syncResult("PolicyKeySync", 5, errorCode));
            }, REG_APP_ID, centerMachineDto.getMachineRefId(), REG_APP_ID, centerMachineDto.getMachineRefId(), isManualSync, jobId);
        } catch (Exception e) {
            e.printStackTrace();
            onSyncJobComplete(jobId, false, isManualSync);
        }
    }

    @Override
    public void getGlobalParamsSync(@NonNull Boolean isManualSync, @NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        if (isManualSync && isExcludedJob(jobId)) {
            result.success(syncResult("GlobalParamsSync", 1, ""));
            return;
        }

        onSyncJobStart();
        try {
            masterDataService.syncGlobalParamsData(() -> {
                auditManagerService.audit(
                        AuditEvent.SYNC_CLIENT_STATE,
                        Components.REGISTRATION
                );
                Log.i(TAG, "Sync Global Params Completed.");
                bioSdkProviderFactory.initialize();
                String errorCode = masterDataService.onResponseComplete();
                boolean success = errorCode == null || errorCode.isEmpty();
                onSyncJobComplete(jobId, success, isManualSync);
                result.success(syncResult("GlobalParamsSync", 1, errorCode));
            }, isManualSync, jobId);
        } catch (Exception e) {
            e.printStackTrace();
            onSyncJobComplete(jobId, false, isManualSync);
        }
    }

    @Override
    public void getUserDetailsSync(@NonNull Boolean isManualSync, @NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        if (isManualSync && isExcludedJob(jobId)) {
            result.success(syncResult("UserDetailsSync", 3, ""));
            return;
        }
        onSyncJobStart();
        try {
            masterDataService.syncUserDetails(() -> {
                auditManagerService.audit(
                        AuditEvent.SYNC_USER_DETAILS,
                        Components.REGISTRATION
                );
                Log.i(TAG, "User details sync Completed.");
                String errorCode = masterDataService.onResponseComplete();
                boolean success = errorCode == null || errorCode.isEmpty();
                onSyncJobComplete(jobId, success, isManualSync);
                result.success(syncResult("UserDetailsSync", 3, errorCode));
            }, isManualSync, jobId);
        } catch (Exception e) {
            e.printStackTrace();
            onSyncJobComplete(jobId, false, isManualSync);
        }
    }

    @Override
    public void getIDSchemaSync(@NonNull Boolean isManualSync, @NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        if (isManualSync && isExcludedJob(jobId)) {
            result.success(syncResult("LatestIDSchemaSync", 4, ""));
            return;
        }
        onSyncJobStart();
        try {
            masterDataService.syncLatestIdSchema(() -> {
                Log.i(TAG, "ID Schema Sync Completed");
                String errorCode = masterDataService.onResponseComplete();
                boolean success = errorCode == null || errorCode.isEmpty();
                onSyncJobComplete(jobId, success, isManualSync);
                result.success(syncResult("LatestIDSchemaSync", 4, errorCode));
            }, isManualSync);
        } catch (Exception e) {
            Log.e(TAG, "ID Schema Sync Failed.", e);
            e.printStackTrace();
            onSyncJobComplete(jobId, false, isManualSync);
            result.error(e);
        }
    }

    @Override
    public void getMasterDataSync(@NonNull Boolean isManualSync, @NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        auditManagerService.audit(AuditEvent.NAV_SYNC_DATA, Components.REGISTRATION);
        if (isManualSync && isExcludedJob(jobId)) {
            result.success(syncResult("MasterDataSync", 2, ""));
            return;
        }
        onSyncJobStart();
        try {
            masterDataService.syncMasterData(() -> {
                auditManagerService.audit(AuditEvent.SYNC_MASTER_DATA,Components.REGISTRATION);
                Log.i(TAG, "Master Data Sync Completed.");
                String errorCode = masterDataService.onResponseComplete();
                boolean success = errorCode == null || errorCode.isEmpty();
                onSyncJobComplete(jobId, success, isManualSync);
                result.success(syncResult("MasterDataSync", 2, errorCode));
            }, 0, isManualSync, jobId);
        } catch (Exception e) {
            Log.e(TAG, "Master Data Sync Failed.", e);
            e.printStackTrace();
            onSyncJobComplete(jobId, false, isManualSync);
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
    public void getCaCertsSync(@NonNull Boolean isManualSync, @NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        if (isManualSync && isExcludedJob(jobId)) {
            result.success(syncResult("CACertificatesSync", 6, ""));
            return;
        }
        onSyncJobStart();
        try {
            masterDataService.syncCACertificates(() -> {
                Log.i(TAG, "CA Certificate Sync Completed");
                resetAlarm("registrationPacketUploadJob");
                String errorCode = masterDataService.onResponseComplete();
                boolean success = errorCode == null || errorCode.isEmpty();
                onSyncJobComplete(jobId, success, isManualSync);
                result.success(syncResult("CACertificatesSync", 6, errorCode));
            }, isManualSync, jobId);
        } catch (Exception e) {
            Log.e(TAG, "CA Certificate Sync Failed.", e);
            e.printStackTrace();
            onSyncJobComplete(jobId, false, isManualSync);
            result.error(e);
        }
    }

    @Override
    public void batchJob(@NonNull MasterDataSyncPigeon.Result<String> result) {
        batchJob.syncRegistrationPackets(this.context, null);
        result.success("Registration Packet Sync Completed.");
    }

    @Override
    public void getReasonList(@NonNull String langCode, @NonNull MasterDataSyncPigeon.Result<List<String>> result) {
        List<ReasonListDto> reasons = masterDataService.getAllReasonsList(langCode);
        List<String> reasonList = new ArrayList();
        for (int i = 0; i < reasons.size(); i++) {
            reasonList.add(reasons.get(i).getName().toString());
        }
        result.success(reasonList);
    }

    @Override
    public void getPreRegIds(@NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<String> result) {
        onSyncJobStart();
        if (NetworkUtils.isNetworkConnected(this.context)) {
            try {
                preRegistrationDataSyncService.fetchPreRegistrationIds(() -> {
                    Log.i(TAG, "Application Id's Sync Completed");
                    result.success("Application Id's Sync Completed.");
                    onSyncJobComplete(jobId, true, false);
                }, jobId);
            } catch (Exception e) {
                e.printStackTrace();
                onSyncJobComplete(jobId, false, false);
            }
        } else {
            onSyncJobComplete(jobId, false, false);
        }
    }


    @Override
    public void getKernelCertsSync(@NonNull Boolean isManualSync, @NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<MasterDataSyncPigeon.Sync> result) {
        if (isManualSync && isExcludedJob(jobId)) {
            result.success(syncResult("KernelCertsSync", 7, ""));
            return;
        }
        onSyncJobStart();
        try {
            masterDataService.syncCertificate(() -> {
                Log.i(TAG, "Policy Key Sync Completed");
                String errorCode = masterDataService.onResponseComplete();
                boolean success = errorCode == null || errorCode.isEmpty();
                onSyncJobComplete(jobId, success, isManualSync);
                result.success(syncResult("KernelCertsSync", 7, errorCode));
            }, KERNEL_APP_ID, "SIGN", "SERVER-RESPONSE", "SIGN-VERIFY", isManualSync, jobId);
        } catch (Exception e) {
            e.printStackTrace();
            onSyncJobComplete(jobId, false, isManualSync);
        }
    }

    @Override
    public void getSyncAndUploadInProgressStatus(@NonNull MasterDataSyncPigeon.Result<Boolean> result) {
        result.success(batchJob.getInProgressStatus());
    }

    void resetAlarm(String api) {
        Intent intent = new Intent(activity, UploadBackgroundService.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getForegroundService(
                    activity,
                    0,  // Request code
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getService(
                    activity,
                    0,  // Request code
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        }
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Intent permissionIntent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                permissionIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                activity.startActivity(permissionIntent);
            }
            long alarmTime = batchJob.getIntervalMillis(api);
            long currentTime = System.currentTimeMillis();
            long delay = alarmTime > currentTime ? alarmTime - currentTime : alarmTime - currentTime;
            Log.d(getClass().getSimpleName(), String.valueOf(delay) + " Next Execution");

//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 30000, pendingIntent);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
            }
        }
    }

    @Override
    public void deleteAuditLogs(@NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<Boolean> result) {
        try {
            onSyncJobStart();
            boolean deletedRes = auditManagerService.deleteAuditLogs();
            // Also persist timestamps so UI can show Last/Next immediately when triggered manually
            try {
                if(deletedRes){
                    masterDataService.logLastSyncCompletionDateTime(jobId);
                    Toast.makeText(context, "Deleted Audit logs", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to store CA certificates sync last sync time", e);
                Toast.makeText(context, "Failed to Deleted Audit logs", Toast.LENGTH_LONG).show();
            }
            onSyncJobComplete(jobId, deletedRes, true);
            result.success(deletedRes);
        } catch (Exception e) {
            onSyncJobComplete(jobId, false, true);
            result.error(e);
            Toast.makeText(context, "Failed to deleted Audit logs", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void deletePreRegRecords(@NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<Boolean> result) {
        try {
            onSyncJobStart();
            // Call fetchAndDeleteRecords from PreRegistrationDataSyncService
            preRegistrationDataSyncService.fetchAndDeleteRecords();
            masterDataService.logLastSyncCompletionDateTime(jobId);
            Toast.makeText(context, "Deleted Pre-reg records", Toast.LENGTH_LONG).show();
            onSyncJobComplete(jobId, true, true);
            result.success(true);
        } catch (Exception e) {
            onSyncJobComplete(jobId, false, true);
            result.error(e);
            Toast.makeText(context, "Failed to deleted Pre-reg records", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void deleteRegistrationPackets(@NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<Boolean> result) {
        try {
            onSyncJobStart();
            packetService.deleteRegistrationPackets();
            masterDataService.logLastSyncCompletionDateTime(jobId);
            Toast.makeText(context, "Deleted Registration packets", Toast.LENGTH_LONG).show();
            onSyncJobComplete(jobId, true, true);
            result.success(true);
        } catch (Exception e) {
            onSyncJobComplete(jobId, false, true);
            Log.e(TAG, "Failed to delete registration packets", e);
            result.error(e);
            Toast.makeText(context, "Failed to delete Registration packets", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void syncPacketStatus(@NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<Boolean> result) {
        try {
            onSyncJobStart();
            packetService.syncAllPacketStatus();
            masterDataService.logLastSyncCompletionDateTime(jobId);
            Log.i(TAG, "Packet status sync job completed");
            onSyncJobComplete(jobId, true, true);
            result.success(true);
        } catch (Exception e) {
            onSyncJobComplete(jobId, false, true);
            Log.e(TAG, "Failed to sync packet status", e);
            result.error(e);
        }
    }

    @Override
    public void getLastSyncTimeByJobId(@NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<String> result) {
        int syncJobId = jobManagerService.generateJobServiceId(jobId);
        String lastSyncTime = jobManagerService.getLastSyncTime(syncJobId);
        result.success(lastSyncTime);
    }

    @Override
    public void getNextSyncTimeByJobId(@NonNull String jobId, @NonNull MasterDataSyncPigeon.Result<String> result) {
        int syncJobId = jobManagerService.generateJobServiceId(jobId);
        String nextSyncTime = jobManagerService.getNextSyncTime(syncJobId);
        result.success(nextSyncTime);
    }

    @Override
    public void getActiveSyncJobs(@NonNull MasterDataSyncPigeon.Result<List<String>> result) {
        try {
            auditManagerService.audit(AuditEvent.SYNCJOB_INFO_FETCH, Components.JOB_SERVICE);
        } catch (Exception e) {
            Log.e(TAG, "Audit logging failed for SYNCJOB_INFO_FETCH", e);
        }
        List<SyncJobDef> list = syncJobDefRepository.getActiveSyncJobs();
        List<String> value = new ArrayList<>();
        try {
            for (SyncJobDef job : list) {
                if (job.getId() == null) {
                    continue;
                }
                value.add(objectMapper.writeValueAsString(job));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to serialize active sync jobs", e);
        }
        result.success(value);
    }

    @Override
    public void getPermittedJobs(@NonNull MasterDataSyncPigeon.Result<List<String>> result) {
        try {
            List<String> permittedJobs = localConfigService.getPermittedJobs();
            result.success(permittedJobs != null ? permittedJobs : new ArrayList<>());
        } catch (Exception e) {
            Log.e(TAG, "Failed to get permitted jobs", e);
            result.error(e);
        }
    }

    @Override
    public void isValidCronExpression(@NonNull String cronExpression, @NonNull MasterDataSyncPigeon.Result<Boolean> result) {
        try {
            boolean isValid = CronExpressionParser.isValidCronExpression(cronExpression);
            result.success(isValid);
        } catch (Exception e) {
            Log.e(TAG, "Error validating cron expression", e);
            result.success(false);
        }
    }

    @Override
    public void modifyJobCronExpression(@NonNull String jobId, @NonNull String cronExpression, @NonNull MasterDataSyncPigeon.Result<Boolean> result) {
        try {
            localConfigService.modifyJob(jobId, cronExpression);
            
            // Fetch specific sync job definition by jobId
            SyncJobDef jobDef = syncJobDefRepository.getSyncJobDefById(jobId);
            
            if (jobDef != null) {
                // Refresh job status to apply new cron expression
                // This will reschedule JobScheduler jobs with new cron
                jobManagerService.refreshJobStatus(jobDef);
                
                // For AlarmManager-based jobs (like batch jobs), reschedule immediately
                if (jobDef.getApiName() != null && activity != null) {
                    // Reschedule using MainActivity's createBackgroundTask via broadcast
                    Intent rescheduleIntent = new Intent("RESCHEDULE_JOB");
                    rescheduleIntent.putExtra(UploadBackgroundService.EXTRA_JOB_API_NAME, jobDef.getApiName());
                    context.sendBroadcast(rescheduleIntent);
                    Log.d(TAG, "Sent reschedule broadcast for job: " + jobDef.getApiName());
                }
            }
            
            result.success(true);
        } catch (Exception e) {
            Log.e(TAG, "Failed to modify job cron expression", e);
            result.error(e);
        }
    }

    @Override
    public void getValue(@NonNull String name, @NonNull MasterDataSyncPigeon.Result<String> result) {
        try {
            // getValue is used for retrieving job cron expressions, so use PERMITTED_JOB_TYPE
            String value = localConfigService.getValue(name, RegistrationConstants.PERMITTED_JOB_TYPE);
            result.success(value);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get value for: " + name, e);
            result.error(e);
        }
    }

    // Execute job based on API name
    public void executeJobByApiName(String jobApiName, Context context) {
        new Thread(() -> {
            try {

                // Get job ID from database for tracking last/next sync
                String jobId = getJobIdByApiName(jobApiName);
                onSyncJobStart();

                // Execute appropriate sync job
                switch (jobApiName) {
                    case "registrationPacketUploadJob":
                        batchJob.syncRegistrationPackets(context, () -> {
                            Log.d(getClass().getSimpleName(), "Registration packet upload job completed");
                            onSyncJobComplete(jobId, true, false);
                        });
                        break;
                    case "packetSyncStatusJob":
                        packetService.syncAllPacketStatus();
                        onSyncJobComplete(jobId, true, false);
                        break;
                    case "masterSyncJob":
                        masterDataService.syncMasterData(() -> {
                            Log.d(getClass().getSimpleName(), "Master data sync callback");
                            String errorCode = masterDataService.onResponseComplete();
                            boolean success = errorCode == null || errorCode.isEmpty();
                            onSyncJobComplete(jobId, success, false);
                        }, 0, false, jobId);
                        break;
                    case "synchConfigDataJob":
                        masterDataService.syncGlobalParamsData(() -> {
                            Log.d(getClass().getSimpleName(), "Config data sync callback");
                            String errorCode = masterDataService.onResponseComplete();
                            boolean success = errorCode == null || errorCode.isEmpty();
                            onSyncJobComplete(jobId, success, false);
                        }, false, jobId);
                        break;
                    case "userDetailServiceJob":
                        masterDataService.syncUserDetails(() -> {
                            Log.d(getClass().getSimpleName(), "User details sync callback");
                            String errorCode = masterDataService.onResponseComplete();
                            boolean success = errorCode == null || errorCode.isEmpty();
                            onSyncJobComplete(jobId, success, false);
                        }, false, jobId);
                        break;
                    case "keyPolicySyncJob":
                        CenterMachineDto centerMachineDto = masterDataService.getRegistrationCenterMachineDetails();
                        if (centerMachineDto != null && centerMachineDto.getMachineRefId() != null) {
                            masterDataService.syncCertificate(() -> {
                                Log.d(getClass().getSimpleName(), "Policy key sync callback");
                                String errorCode = masterDataService.onResponseComplete();
                                boolean success = errorCode == null || errorCode.isEmpty();
                                onSyncJobComplete(jobId, success, false);
                            }, REG_APP_ID, centerMachineDto.getMachineRefId(), REG_APP_ID, centerMachineDto.getMachineRefId(), false, jobId);
                        } else {
                            Log.w(getClass().getSimpleName(), "Skipping keyPolicySyncJob - machine details not available");
                            onSyncJobComplete(jobId, false, false);
                        }
                        break;
                    case "publicKeySyncJob":
                        // Public key sync for KERNEL app (SIGN certificates)
                        masterDataService.syncCertificate(() -> {
                            Log.d(getClass().getSimpleName(), "Public key sync callback");
                            String errorCode = masterDataService.onResponseComplete();
                            boolean success = errorCode == null || errorCode.isEmpty();
                            onSyncJobComplete(jobId, success, false);
                        }, KERNEL_APP_ID, "SIGN", "SERVER-RESPONSE", "SIGN-VERIFY", false, jobId);
                        break;
                    case "syncCertificateJob":
                        // CA certificate sync
                        masterDataService.syncCACertificates(() -> {
                            Log.d(getClass().getSimpleName(), "CA cert sync callback");
                            String errorCode = masterDataService.onResponseComplete();
                            boolean success = errorCode == null || errorCode.isEmpty();
                            onSyncJobComplete(jobId, success, false);
                        }, false, jobId);
                        break;
                    case "preRegistrationDataSyncJob":
                        preRegistrationDataSyncService.fetchPreRegistrationIds(() -> {
                            Log.i(TAG, "Application Id's Sync Completed");
                            onSyncJobComplete(jobId, true, false);
                        }, jobId);
                        break;

                    case "deleteAuditLogsJob":
                        auditManagerService.deleteAuditLogs();
                        masterDataService.logLastSyncCompletionDateTime(jobId);
                        onSyncJobComplete(jobId, true, false);
                        break;

                    case "preRegistrationPacketDeletionJob":
                        preRegistrationDataSyncService.fetchAndDeleteRecords();
                        masterDataService.logLastSyncCompletionDateTime(jobId);
                        onSyncJobComplete(jobId, true, false);
                        break;

                    case "registrationDeletionJob":
                        packetService.deleteRegistrationPackets();
                        masterDataService.logLastSyncCompletionDateTime(jobId);
                        Log.i(TAG, "Registration packet deletion job completed");
                        onSyncJobComplete(jobId, true, false);
                        break;
                    default:
                        Log.w(getClass().getSimpleName(), "Unknown job: " + jobApiName);
                        onSyncJobComplete(jobId, false, false);
                }
                Log.d(getClass().getSimpleName(), "Completed: " + jobApiName);
            } catch (Exception e) {
                onSyncJobComplete(getJobIdByApiName(jobApiName), false, false);
                Log.e(getClass().getSimpleName(), "Job failed: " + jobApiName, e);
            }
        }).start();
    }

    private String getJobIdByApiName(String apiName) {
        try {
            SyncJobDef job = syncJobDefRepository.getSyncJobDefByApiName(apiName);
            if (job != null) {
                return job.getId();
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error getting job ID for: " + apiName, e);
        }
        return ""; // Return empty string if not found
    }

    private Set<String> getExcludedJobIds() {
        Set<String> excluded = new HashSet<>();
        addJobIdsFromString(excluded, globalParamRepository.getCachedStringJobsOffline());
        addJobIdsFromString(excluded, globalParamRepository.getCachedStringJobsUntagged());
        return excluded;
    }

    private void addJobIdsFromString(Set<String> target, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        for (String jobId : value.split(RegistrationConstants.COMMA)) {
            String trimmed = jobId.trim();
            if (!trimmed.isEmpty()) {
                target.add(trimmed);
            }
        }
    }

    /** True if job is in offline or untagged list (excluded from auto sync and manual sync execution). */
    private boolean isExcludedJob(String jobId) {
        return jobId != null && !jobId.trim().isEmpty() && getExcludedJobIds().contains(jobId.trim());
    }

    private void onSyncJobStart() {
        runningSyncJobs.incrementAndGet();
        cancelPendingRestartPrompt();
    }

    private void onSyncJobComplete(String jobId, boolean success, boolean isManualSync) {
        boolean shouldPrompt = false;
        synchronized (restartLock) {
            if (runningSyncJobs.get() > 0) {
                runningSyncJobs.decrementAndGet();
            }
            // Only request restart when a restartable job completed successfully during manual sync
            if (success && isRestartableJob(jobId) && isManualSync) {
                restartRequested.set(true);
            }
            if (runningSyncJobs.get() == 0 && restartRequested.get()) {
                shouldPrompt = true;
            }
        }
        if (shouldPrompt) {
            scheduleRestartPrompt();
        }
    }

    private boolean isRestartableJob(String jobId) {
        if (jobId == null || jobId.trim().isEmpty()) {
            return false;
        }
        String value = globalParamRepository.getCachedStringJobsRestart();
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        String target = jobId.trim();
        for (String id : value.split(RegistrationConstants.COMMA)) {
            if (target.equals(id.trim())) {
                return true;
            }
        }
        return false;
    }

    private void scheduleRestartPrompt() {
        synchronized (restartLock) {
            // Ensure any previously scheduled prompt is cancelled before scheduling a new one
            cancelPendingRestartPromptLocked();
            pendingRestartPrompt = () -> {
                boolean showPrompt = false;
                synchronized (restartLock) {
                    if (runningSyncJobs.get() == 0 && restartRequested.get()) {
                        restartRequested.set(false);
                        showPrompt = true;
                    }
                }
                if (showPrompt) {
                    showRestartDialog();
                }
            };
            // Delay to allow any queued syncs to start
            restartHandler.postDelayed(pendingRestartPrompt, 2000);
        }
    }

    private void cancelPendingRestartPrompt() {
        synchronized (restartLock) {
            cancelPendingRestartPromptLocked();
        }
    }

    /**
     * Internal helper that assumes the caller already holds restartLock.
     */
    private void cancelPendingRestartPromptLocked() {
        if (pendingRestartPrompt != null) {
            restartHandler.removeCallbacks(pendingRestartPrompt);
            pendingRestartPrompt = null;
        }
    }

    private static final String SYNC_RESTART_CHANNEL = "io.mosip.registration_client/sync_restart";

    private void showRestartDialog() {
        if (flutterBinaryMessenger == null) {
            Log.w(TAG, "Restart prompt skipped: Flutter binary messenger not set");
            return;
        }
        try {
            new MethodChannel(flutterBinaryMessenger, SYNC_RESTART_CHANNEL)
                    .invokeMethod("showRestartDialog", null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to request Flutter restart dialog", e);
        }
    }
}
