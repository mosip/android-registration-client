package io.mosip.registration_client;


import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.dao.LocationDao;
import io.mosip.registration.clientmanager.dao.LocationHierarchyDao;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.service.Biometrics095Service;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.service.TemplateService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration_client.api_services.AuthenticationApi;
import io.mosip.registration_client.api_services.BiometricsDetailsApi;
import io.mosip.registration_client.api_services.CommonDetailsApi;
import io.mosip.registration_client.api_services.DemographicsDetailsApi;
import io.mosip.registration_client.api_services.LocationDetailsApi;
import io.mosip.registration_client.api_services.MachineDetailsApi;
import io.mosip.registration_client.api_services.PacketAuthenticationApi;
import io.mosip.registration_client.api_services.ProcessSpecDetailsApi;
import io.mosip.registration_client.api_services.RegistrationApi;
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
                                                   GlobalParamRepository globalParamRepository,RegistrationService registrationService) {
        return new ProcessSpecDetailsApi(appContext, identitySchemaRepository,
                        globalParamRepository,registrationService);

    }

    @Provides
    @Singleton
    BiometricsDetailsApi getBiometricsDetailsApi(AuditManagerService auditManagerService, ObjectMapper objectMapper, Biometrics095Service biometrics095Service, RegistrationService registrationService) {
        return new BiometricsDetailsApi(auditManagerService, objectMapper,biometrics095Service,registrationService);

    }

    @Provides
    @Singleton
    LocationDetailsApi getLocationDetailsApi(LocationHierarchyDao locationHierarchyDao, LocationDao locationDao) {
        return new LocationDetailsApi(locationHierarchyDao, locationDao);

    }

    @Provides
    @Singleton
    RegistrationApi getRegistrationDataApi(RegistrationService registrationService, TemplateService templateService) {
        return new RegistrationApi(registrationService, templateService);

    }

    @Provides
    @Singleton
    PacketAuthenticationApi getPacketAuthenticationApi(SyncRestService syncRestService, SyncRestUtil syncRestFactory,
                                                       LoginService loginService) {
        return new PacketAuthenticationApi(syncRestService, syncRestFactory, loginService);
    }

    @Provides
    @Singleton
    DemographicsDetailsApi getDemographicsDetailsApi(RegistrationService registrationService) {
        return new DemographicsDetailsApi(registrationService);
    }
}
