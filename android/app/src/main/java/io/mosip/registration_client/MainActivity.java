/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package io.mosip.registration_client;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectWriter;

import javax.inject.Inject;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.mosip.registration.clientmanager.config.AppModule;
import io.mosip.registration.clientmanager.config.NetworkModule;
import io.mosip.registration.clientmanager.config.RoomModule;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
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
import io.mosip.registration_client.api_services.CommonDetailsApi;
import io.mosip.registration_client.api_services.MachineDetailsApi;
import io.mosip.registration_client.api_services.ProcessSpecDetailsApi;
import io.mosip.registration_client.api_services.UserDetailsApi;
import io.mosip.registration_client.model.AuthResponsePigeon;
import io.mosip.registration_client.model.CommonDetailsPigeon;
import io.mosip.registration_client.model.MachinePigeon;
import io.mosip.registration_client.model.ProcessSpecPigeon;
import io.mosip.registration_client.model.UserPigeon;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
