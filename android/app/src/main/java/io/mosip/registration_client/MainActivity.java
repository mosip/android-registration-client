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

import javax.inject.Inject;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.mosip.registration.clientmanager.config.AppModule;
import io.mosip.registration.clientmanager.config.NetworkModule;
import io.mosip.registration.clientmanager.config.RoomModule;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.constant.PacketTaskStatus;
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
import io.mosip.registration_client.api_services.UserDetailsApi;
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
    SyncJobDefRepository syncJobDefRepository;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("BACKGROUND_TASK_COMPLETE")) {
                fetchRegistrationPackets(context);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createBackgroundTask();
        IntentFilter intentFilter = new IntentFilter("BACKGROUND_TASK_COMPLETE");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver when the activity is destroyed
        unregisterReceiver(broadcastReceiver);
        Intent serviceIntent = new Intent(this, MyBackgroundService.class);
        stopService(serviceIntent);
        Log.d(getClass().getSimpleName(),"Background Service Stopped");
    }

    void createBackgroundTask(){
        Intent serviceIntent = new Intent(this, MyBackgroundService.class);

        // Create a PendingIntent with the appropriate flags
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getForegroundService(
                    this,
                    0,  // Request code
                    serviceIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getService(
                    this,
                    0,  // Request code
                    serviceIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        }

        // Get an instance of AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to trigger your PendingIntent after a certain interval
        long delayMillis = 3600000;  // Example delay of 60 seconds
        long triggerAtMillis = SystemClock.elapsedRealtime() + delayMillis;
        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtMillis,
                delayMillis,
                pendingIntent
        );
    }

    private void fetchRegistrationPackets(Context context) {
        if(NetworkUtils.isNetworkConnected(context)){
            Log.d(getClass().getSimpleName(), "Fetching Packets in main activity");
            List<Registration> registrationList = packetService.getAllRegistrations(1,5);
            Log.e(getClass().getSimpleName(), "Registration : "+ registrationList);

            registrationList = packetService.getRegistrationsByStatus(PacketClientStatus.APPROVED.name());
            registrationList.forEach(value->{
                try {
                    Log.d(getClass().getSimpleName(), "Syncing " + value.getPacketId());
                    packetService.syncRegistration(value.getPacketId());
                }catch (Exception e){
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            });

            registrationList = packetService.getRegistrationsByStatus(PacketClientStatus.SYNCED.name());
            registrationList.forEach(value->{
                try {
                    Log.d(getClass().getSimpleName(), "Uploading " + value.getPacketId());
                    packetService.uploadRegistration(value.getPacketId());
                }catch (Exception e){
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            });
        }
    }

    private void getSyncJobs(){
        List<SyncJobDef> syncJobs = syncJobDefRepository.getAllSyncJobDefList();
        Log.e(getClass().getSimpleName(), syncJobs.toString());
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
        
        DynamicResponsePigeon.DynamicResponseApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), dynamicDetailsApi);
        MasterDataSyncPigeon.SyncApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), masterDataSyncApi);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), REG_CLIENT_CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            switch(call.method) {
                                case "masterDataSync":
                                    new SyncActivityService().clickSyncMasterData(result,
                                            auditManagerService, masterDataService);
                                    getSyncJobs();
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
