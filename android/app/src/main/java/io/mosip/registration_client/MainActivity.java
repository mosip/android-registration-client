/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client;

import android.app.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.inject.Inject;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;

import io.mosip.registration.transliterationmanager.service.TransliterationServiceImpl;
import io.mosip.registration_client.api_services.AuditDetailsApi;
import io.mosip.registration_client.api_services.AuthenticationApi;
import io.mosip.registration_client.api_services.BiometricsDetailsApi;
import io.mosip.registration_client.api_services.CommonDetailsApi;
import io.mosip.registration_client.api_services.DashBoardDetailsApi;
import io.mosip.registration_client.api_services.DemographicsDetailsApi;
import io.mosip.registration_client.api_services.DocumentCategoryApi;
import io.mosip.registration_client.api_services.DocumentDetailsApi;
import io.mosip.registration_client.api_services.DynamicDetailsApi;
import io.mosip.registration_client.api_services.GlobalConfigSettingsApi;
import io.mosip.registration_client.api_services.MachineDetailsApi;
import io.mosip.registration_client.api_services.PacketAuthenticationApi;
import io.mosip.registration_client.api_services.MasterDataSyncApi;
import io.mosip.registration_client.api_services.ProcessSpecDetailsApi;
import io.mosip.registration_client.api_services.RegistrationApi;
import io.mosip.registration_client.api_services.SecureScreenApi;
import io.mosip.registration_client.api_services.TransliterationApi;
import io.mosip.registration_client.api_services.UserDetailsApi;
import io.mosip.registration_client.model.AuditResponsePigeon;
import io.mosip.registration_client.model.AuthResponsePigeon;
import io.mosip.registration_client.model.BiometricsPigeon;
import io.mosip.registration_client.model.CommonDetailsPigeon;
import io.mosip.registration_client.model.DashBoardPigeon;
import io.mosip.registration_client.model.DemographicsDataPigeon;
import io.mosip.registration_client.model.DocumentCategoryPigeon;
import io.mosip.registration_client.model.DynamicResponsePigeon;
import io.mosip.registration_client.model.GlobalConfigSettingsPigeon;
import io.mosip.registration_client.model.MachinePigeon;
import io.mosip.registration_client.model.PacketAuthPigeon;
import io.mosip.registration_client.model.MasterDataSyncPigeon;
import io.mosip.registration_client.model.ProcessSpecPigeon;
import io.mosip.registration_client.model.RegistrationDataPigeon;
import io.mosip.registration_client.model.SecureScreenPigeon;
import io.mosip.registration_client.model.TransliterationPigeon;
import io.mosip.registration_client.model.UserPigeon;
import io.mosip.registration_client.model.DocumentDataPigeon;
import io.mosip.registration_client.utils.BatchJob;
import io.mosip.registration_client.utils.SyncScheduler;

import android.content.Intent;


public class MainActivity extends FlutterActivity {
    private static final String REG_CLIENT_CHANNEL = "com.flutter.dev/io.mosip.get-package-instance";

    @Inject
    AuditManagerService auditManagerService;

    @Inject
    RegistrationService registrationService;

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
    DocumentCategoryApi documentCategoryApi;

    @Inject
    DashBoardDetailsApi dashBoardDetailsApi;

    @Inject
    BatchJob batchJob;

    @Inject
    GlobalConfigSettingsApi globalConfigSettingsApi;

    @Inject
    SecureScreenApi secureScreenApi;

    @Inject
    SyncScheduler syncScheduler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initializeAppComponent() {
        RegistrationClientApp app = (RegistrationClientApp) getApplication();
        AppComponent appComponent = app.getAppComponent();
        appComponent.inject(this);
        syncScheduler.scheduleAllActiveJobs(getApplicationContext());
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        initializeAppComponent();
        auditManagerService.audit(AuditEvent.NAV_REDIRECT_HOME, Components.REGISTRATION);
        MachinePigeon.MachineApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), machineDetailsApi);
        UserPigeon.UserApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), userDetailsApi);
        CommonDetailsPigeon.CommonDetailsApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(),commonDetailsApi);
        AuthResponsePigeon.AuthResponseApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), authenticationApi);
        ProcessSpecPigeon.ProcessSpecApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), processSpecDetailsApi);
        BiometricsPigeon.BiometricsApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(),biometricsDetailsApi);
        biometricsDetailsApi.setCallbackActivity(this);
        RegistrationDataPigeon.RegistrationDataApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), registrationApi);
        PacketAuthPigeon.PacketAuthApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), packetAuthenticationApi);
        packetAuthenticationApi.setCallbackActivity(this);
        DemographicsDataPigeon.DemographicsApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), demographicsDetailsApi);
        DocumentDataPigeon.DocumentApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), documentDetailsApi);
        DocumentCategoryPigeon.DocumentCategoryApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), documentCategoryApi);
        DashBoardPigeon.DashBoardApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), dashBoardDetailsApi);

        TransliterationPigeon.TransliterationApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(),new TransliterationApi(new TransliterationServiceImpl()));
        DynamicResponsePigeon.DynamicResponseApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), dynamicDetailsApi);
        batchJob.setCallbackActivity(this);
        MasterDataSyncPigeon.SyncApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), masterDataSyncApi);
        masterDataSyncApi.setCallbackActivity(this, flutterEngine.getDartExecutor().getBinaryMessenger());
        AuditResponsePigeon.AuditResponseApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), auditDetailsApi);
        GlobalConfigSettingsPigeon.GlobalConfigSettingsApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), globalConfigSettingsApi);
        SecureScreenPigeon.SecureScreenApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), secureScreenApi);
        secureScreenApi.setCallbackActivity(this);
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
                case 4:
                    biometricsDetailsApi.parseDiscoverResponseForList(data.getExtras());
                    break;
                case 5:
                    biometricsDetailsApi.handleDeviceInfoResponseForList(data.getExtras());
                    break;
            }
        }
    }
}
