/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package io.mosip.registration_client;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.mosip.registration.clientmanager.config.AppModule;
import io.mosip.registration.clientmanager.config.NetworkModule;
import io.mosip.registration.clientmanager.config.RoomModule;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.entity.GlobalParam;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;

import io.mosip.registration.transliterationmanager.service.TransliterationServiceImpl;
import io.mosip.registration_client.api_services.AuditDetailsApi;
import io.mosip.registration_client.api_services.AuthenticationApi;
import io.mosip.registration_client.api_services.BiometricsDetailsApi;
import io.mosip.registration_client.api_services.CommonDetailsApi;
import io.mosip.registration_client.api_services.DemographicsDetailsApi;
import io.mosip.registration_client.api_services.DocumentDetailsApi;
import io.mosip.registration_client.api_services.DynamicDetailsApi;
import io.mosip.registration_client.api_services.MachineDetailsApi;
import io.mosip.registration_client.api_services.PacketAuthenticationApi;
import io.mosip.registration_client.api_services.MasterDataSyncApi;
import io.mosip.registration_client.api_services.ProcessSpecDetailsApi;
import io.mosip.registration_client.api_services.RegistrationApi;
import io.mosip.registration_client.api_services.TransliterationApi;
import io.mosip.registration_client.api_services.UserDetailsApi;
import io.mosip.registration_client.model.AuditResponsePigeon;
import io.mosip.registration_client.model.AuthResponsePigeon;
import io.mosip.registration_client.model.BiometricsPigeon;
import io.mosip.registration_client.model.CommonDetailsPigeon;
import io.mosip.registration_client.model.DemographicsDataPigeon;
import io.mosip.registration_client.model.DynamicResponsePigeon;
import io.mosip.registration_client.model.MachinePigeon;
import io.mosip.registration_client.model.PacketAuthPigeon;
import io.mosip.registration_client.model.MasterDataSyncPigeon;
import io.mosip.registration_client.model.ProcessSpecPigeon;
import io.mosip.registration_client.model.RegistrationDataPigeon;
import io.mosip.registration_client.model.TransliterationPigeon;
import io.mosip.registration_client.model.UserPigeon;
import io.mosip.registration_client.model.DocumentDataPigeon;

public class MainActivity extends FlutterActivity {
    private static final String REG_CLIENT_CHANNEL = "com.flutter.dev/io.mosip.get-package-instance";

    ObjectWriter ow;
    @Inject
    ClientCryptoManagerService clientCryptoManagerService;
    @Inject
    SyncRestUtil syncRestFactory;
    @Inject
    SyncRestService syncRestService;
    @Inject
    LoginService loginService;
    @Inject
    AuditManagerService auditManagerService;
    @Inject
    MasterDataService masterDataService;

    @Inject
    RegistrationService registrationService;
    @Inject
    PacketService packetService;
    @Inject
    JobTransactionService jobTransactionService;
    @Inject
    JobManagerService jobManagerService;
    @Inject
    IdentitySchemaRepository identitySchemaRepository;
    @Inject
    UserDetailRepository userDetailRepository;
    @Inject
    RegistrationCenterRepository registrationCenterRepository;

    @Inject
    GlobalParamRepository globalParamRepository;

    @Inject
    MachineDetailsApi machineDetailsApi;

    @Inject
    UserDetailsApi userDetailsApi;

    @Inject
    CommonDetailsApi commonDetailsApi;

    @Inject
    AuthenticationApi authenticationApi;

    @Inject
    ProcessSpecDetailsApi processSpecDetailsApi;

    @Inject
    BiometricsDetailsApi biometricsDetailsApi;
    
    @Inject
    PacketAuthenticationApi packetAuthenticationApi;

    @Inject
    RegistrationApi registrationApi;

    @Inject
    DemographicsDetailsApi demographicsDetailsApi;

    @Inject
    DocumentDetailsApi documentDetailsApi;


    @Inject
    DynamicDetailsApi dynamicDetailsApi;

    @Inject
    MasterDataSyncApi masterDataSyncApi;
    @Inject
    AuditDetailsApi auditDetailsApi;

    @Inject
    SyncJobDefRepository syncJobDefRepository;

    @Inject
    GlobalParamDao globalParamDao;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("REGISTRATION_PACKET_SYNC")) {
                syncRegistrationPackets(context);
            }
            if (intent.getAction().equals("REGISTRATION_PACKET_UPLOAD")) {
                uploadRegistrationPackets(context);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent serviceIntentSync = new Intent(this, SyncBackgroundService.class);
        createBackgroundTask(serviceIntentSync, "registrationPacketSyncJob");
        IntentFilter intentFilterSync = new IntentFilter("REGISTRATION_PACKET_SYNC");
        registerReceiver(broadcastReceiver, intentFilterSync);

        Intent serviceIntentUpload = new Intent(this, UploadBackgroundService.class);
        createBackgroundTask(serviceIntentUpload, "registrationPacketUploadJob");
        IntentFilter intentFilterUpload = new IntentFilter("REGISTRATION_PACKET_UPLOAD");
        registerReceiver(broadcastReceiver, intentFilterUpload);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    void createBackgroundTask(Intent intent, String api){
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getForegroundService(
                    this,
                    0,  // Request code
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getService(
                    this,
                    0,  // Request code
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            long alarmTime = getIntervalMillis(api);
            long currentTime = System.currentTimeMillis();
            long delay = alarmTime > currentTime ? alarmTime - currentTime : alarmTime - currentTime;
            Log.d(getClass().getSimpleName(), String.valueOf(delay)+ " Next Execution");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
            }
        }
    }

    private void syncRegistrationPackets(Context context) {
        if(NetworkUtils.isNetworkConnected(context)){
            Log.d(getClass().getSimpleName(), "Sync Packets in main activity");
            Integer batchSize = getBatchSize();
            List<Registration> registrationList = packetService.getRegistrationsByStatus(PacketClientStatus.APPROVED.name(), batchSize);
            for (Registration value : registrationList) {
                try {
                    Log.d(getClass().getSimpleName(), "Syncing " + value.getPacketId());
                    packetService.syncRegistration(value.getPacketId());
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            }
        }
    }

    private void uploadRegistrationPackets(Context context) {
        if(NetworkUtils.isNetworkConnected(context)){
            Log.d(getClass().getSimpleName(), "Upload Packets in main activity");
            Integer batchSize = getBatchSize();
            List<Registration>  registrationList = packetService.getRegistrationsByStatus(PacketClientStatus.SYNCED.name(), batchSize);
            for (Registration value : registrationList) {
                try {
                    Log.d(getClass().getSimpleName(), "Uploading " + value.getPacketId());
                    packetService.uploadRegistration(value.getPacketId());
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            }
        }
    }

    private Integer getBatchSize(){
        List<GlobalParam> globalParams = globalParamDao.getGlobalParams();
        for (GlobalParam value : globalParams) {
            if (Objects.equals(value.getId(), "mosip.registration.packet_upload_batch_size")) {
                return Integer.parseInt(value.getValue());
            }
        }
        return 4;
    }

    private long getIntervalMillis(String api){
        AtomicLong alarmTime = new AtomicLong(System.currentTimeMillis()+60000);
        List<SyncJobDef> syncJobs = syncJobDefRepository.getAllSyncJobDefList();
        for (SyncJobDef value : syncJobs) {
            if (Objects.equals(value.getApiName(), api)) {
                Log.d(getClass().getSimpleName(), String.valueOf(value.getSyncFreq()) + " Cron Expression");
                alarmTime.set(CronParserUtil.getNextExecutionTimeInMillis(String.valueOf(value.getSyncFreq())));
            }
        }
        return alarmTime.get();
    }

    public void initializeAppComponent() {
        AppComponent appComponent = DaggerAppComponent.builder()
                .application(getApplication())
                .networkModule(new NetworkModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .appModule(new AppModule(getApplication()))
                .hostApiModule(new HostApiModule(getApplication()))
                .build();

        appComponent.inject(this);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        initializeAppComponent();
        MachinePigeon.MachineApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), machineDetailsApi);
        UserPigeon.UserApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), userDetailsApi);
        CommonDetailsPigeon.CommonDetailsApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(),commonDetailsApi);
        AuthResponsePigeon.AuthResponseApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), authenticationApi);
        ProcessSpecPigeon.ProcessSpecApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), processSpecDetailsApi);
        BiometricsPigeon.BiometricsApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(),biometricsDetailsApi);
        biometricsDetailsApi.setCallbackActivity(this);
        RegistrationDataPigeon.RegistrationDataApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), registrationApi);
        PacketAuthPigeon.PacketAuthApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), packetAuthenticationApi);
        DemographicsDataPigeon.DemographicsApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), demographicsDetailsApi);
        DocumentDataPigeon.DocumentApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), documentDetailsApi);

        TransliterationPigeon.TransliterationApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(),new TransliterationApi(new TransliterationServiceImpl()));
        DynamicResponsePigeon.DynamicResponseApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), dynamicDetailsApi);
        MasterDataSyncPigeon.SyncApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), masterDataSyncApi);
        AuditResponsePigeon.AuditResponseApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), auditDetailsApi);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), REG_CLIENT_CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            switch(call.method) {
                                case "masterDataSync":
                                    new SyncActivityService().clickSyncMasterData(result,
                                            auditManagerService, masterDataService);
                                    break;
                                default:
                                    result.notImplemented();
                                    break;
                            }
                        }
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    biometricsDetailsApi.parseDiscoverResponse(data.getExtras());
                    break;
                case 2:
                    biometricsDetailsApi.parseDeviceInfoResponse(data.getExtras());
                    break;
                case 3:
                    biometricsDetailsApi.parseRCaptureResponse(data.getExtras());
                    break;
            }
        }
    }
}
