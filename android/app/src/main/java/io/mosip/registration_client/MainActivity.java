/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package io.mosip.registration_client;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.mosip.registration.clientmanager.config.AppModule;
import io.mosip.registration.clientmanager.config.NetworkModule;
import io.mosip.registration.clientmanager.config.RoomModule;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
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

public class MainActivity extends FlutterActivity {
    private static final String REG_CLIENT_CHANNEL = "com.flutter.dev/io.mosip.get-package-instance";
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
                .build();

        appComponent.inject(this);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        GeneratedPluginRegistrant.registerWith(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), REG_CLIENT_CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            switch(call.method) {
                                case "callComponent":
                                    initializeAppComponent();
                                    break;

                                case "getMachineDetails":
                                    new AboutActivityService().getMachineDetails(clientCryptoManagerService, result);
                                    break;

                                case "validateUsername":
                                    String usernameVal = call.argument("username");
                                    new LoginActivityService().usernameValidation(usernameVal, loginService, result,userDetailRepository);
                                    break;

                                case "login":
                                    String username = call.argument("username");
                                    String password = call.argument("password");
                                    boolean isConnected = call.argument("isConnected");
                                    try {
                                        new LoginActivityService().executeLogin(username, password,
                                                result, syncRestService, syncRestFactory,
                                                loginService, auditManagerService, isConnected);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;

                                case "masterDataSync":
                                    new SyncActivityService().clickSyncMasterData(result,
                                            auditManagerService, masterDataService);
                                    break;
                                case "testingSpec":
                                    testingSpec();
                                case "adityaTestingSpec":
                                    String centerId=call.argument("centerId");
                                    adityaTestingSpec(centerId);

                                default:
                                    result.notImplemented();
                                    break;
                            }
                        }
                );
    }
    public void testingSpec(){
        try{
            System.out.println("!!!!!!!!!!!!!!!!!!");
            System.out.println(identitySchemaRepository.getLatestSchemaVersion());
            String processSpecDto = identitySchemaRepository.getSchemaJson(getApplicationContext(),
                    identitySchemaRepository.getLatestSchemaVersion());
            System.out.println("!!!!!!!!!!!!!!!!!!");
            System.out.println(processSpecDto);
        }catch (Exception e){
            System.out.println("2222222222222222");
            System.out.println(e);
        }
    }
    public void adityaTestingSpec(String centerId){
        try{
            List<RegistrationCenter> registrationCenterList=registrationCenterRepository.getRegistrationCenter(centerId);
            System.out.println("registrationCenterList");
            System.out.println(registrationCenterList.get(0).toString());
        }catch (Exception e) {

        }
    }
}
