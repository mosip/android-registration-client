package io.mosip.registration_client;


import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.service.Biometrics095Service;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration_client.api_services.AuthenticationApi;
import io.mosip.registration_client.api_services.BiometricsDetailsApi;
import io.mosip.registration_client.api_services.CommonDetailsApi;
import io.mosip.registration_client.api_services.MachineDetailsApi;
import io.mosip.registration_client.api_services.ProcessSpecDetailsApi;
import io.mosip.registration_client.api_services.UserDetailsApi;

@Module
public class HostApiModule {


    Application application;
    Context appContext;

    public HostApiModule(Application application) {
        this.application = application;
        this.appContext = application.getApplicationContext();
    }

    @Provides
    Application providesApplication() {
        return application;
    }

    @Provides
    @NonNull
    public Context provideApplicationContext() {
        return appContext;
    }

    @Provides
    @Singleton
    UserDetailsApi getLoginActivityApi(LoginService loginService,
                                       RegistrationCenterRepository registrationCenterRepository) {
        return new UserDetailsApi(loginService, registrationCenterRepository);
    }

    @Provides
    @Singleton
    MachineDetailsApi getMachineDetailsApi(ClientCryptoManagerService clientCryptoManagerService,
                                           RegistrationCenterRepository registrationCenterRepository) {
        return new MachineDetailsApi(clientCryptoManagerService, registrationCenterRepository);
    }


    @Provides
    @Singleton
    AuthenticationApi getAuthenticationApi(SyncRestService syncRestService,
                                           SyncRestUtil syncRestFactory,
                                           LoginService loginService,
                                           AuditManagerService auditManagerService) {
        return new AuthenticationApi(appContext, syncRestService, syncRestFactory,
                        loginService, auditManagerService);
    }

    @Provides
    @Singleton
    CommonDetailsApi getCommonApiImpl(MasterDataService masterDataService){
        return new CommonDetailsApi(masterDataService);
    }
    
    @Provides
    @Singleton
    ProcessSpecDetailsApi getProcessSpecDetailsApi(IdentitySchemaRepository identitySchemaRepository,
                                                   GlobalParamRepository globalParamRepository) {
        return new ProcessSpecDetailsApi(appContext, identitySchemaRepository,
                        globalParamRepository);

    }

    @Provides
    @Singleton
    BiometricsDetailsApi getBiometricsDetailsApi(RegistrationService registrationService, AuditManagerService auditManagerService, GlobalParamRepository globalParamRepository, Biometrics095Service biometrics095Service) {
        return new BiometricsDetailsApi(registrationService,biometrics095Service,globalParamRepository,auditManagerService);

    }

}
