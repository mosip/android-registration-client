/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package io.mosip.registration_client;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.ArrayList;
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
import io.mosip.registration_client.api_services.MachineDetailsApi;
import io.mosip.registration_client.api_services.UserDetailsApi;
import io.mosip.registration_client.model.MachinePigeon;
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
                .hostApiModule(new HostApiModule())
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

                                case "getUISchema":
                                    getUISchema(result);
                                    break;

                                case "getNewProcessSpec":
                                    getNewProcessSpec(result);
                                    break;

                                case "getCenterName":
                                    String centerId=call.argument("centerId");
                                    getCenterName(centerId,result);

                                    break;

                                case "getStringValueGlobalParam":
                                    String key=call.argument("key");
                                    getStringValueGlobalParam(key,result);
                                    break;

                                default:
                                    result.notImplemented();
                                    break;
                            }
                        }
                );
    }

    public void getUISchema(MethodChannel.Result result){
        try{

            String schemaJson = identitySchemaRepository.getSchemaJson(getApplicationContext(),
                    identitySchemaRepository.getLatestSchemaVersion());
            result.success(schemaJson.toString());

        }catch (Exception e){
            Log.e(getClass().getSimpleName(), "Error in getUISchema", e);
        }
    }
    public void getNewProcessSpec(MethodChannel.Result result){
        try{

            ProcessSpecDto processSpecDto = identitySchemaRepository.getNewProcessSpec(getApplicationContext(),
                    identitySchemaRepository.getLatestSchemaVersion());
            ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(processSpecDto);
            List<String> processSpecList=new ArrayList<String>();
            processSpecList.add(json);
            result.success(processSpecList);

        }catch (Exception e){
            Log.e(getClass().getSimpleName(), "Error in getNewProcessSpec", e);
        }
    }
    public void getCenterName(String centerId,MethodChannel.Result result){
        try{
            List<RegistrationCenter> registrationCenterList=registrationCenterRepository.getRegistrationCenter(centerId);
            result.success(registrationCenterList.get(0).toString());
        }catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getCenterName", e);
            result.success("");
        }
    }

    public void getStringValueGlobalParam(String key,MethodChannel.Result result){
        try{
            String cachedString=globalParamRepository.getCachedStringGlobalParam(key);
            result.success(cachedString);
        }catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getStringValueGlobalParam", e);
        }
    }
}
