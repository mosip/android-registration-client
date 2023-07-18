package io.mosip.registration_client;


import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
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
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.service.MasterDataServiceImpl;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration_client.api_services.AuthenticationApi;
import io.mosip.registration_client.api_services.CommonDetailsApi;
import io.mosip.registration_client.api_services.MachineDetailsApi;
import io.mosip.registration_client.api_services.ProcessSpecDetailsApi;
import io.mosip.registration_client.api_services.SyncResponseApi;
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
    SyncResponseApi getSyncResponseApi(
            MasterDataServiceImpl masterDataService, ClientCryptoManagerService clientCryptoManagerService, MachineRepository machineRepository, RegistrationCenterRepository registrationCenterRepository,
            SyncRestService syncRestService, CertificateManagerService certificateManagerService, GlobalParamRepository globalParamRepository, ObjectMapper objectMapper, UserDetailRepository userDetailRepository,
            IdentitySchemaRepository identitySchemaRepository, Context context, DocumentTypeRepository documentTypeRepository,
            ApplicantValidDocRepository applicantValidDocRepository,
            TemplateRepository templateRepository,
            DynamicFieldRepository dynamicFieldRepository,
            LocationRepository locationRepository,
            BlocklistedWordRepository blocklistedWordRepository,
            SyncJobDefRepository syncJobDefRepository,
            LanguageRepository languageRepository,
            JobManagerService jobManagerService) {
        return new SyncResponseApi( masterDataService,clientCryptoManagerService,
                machineRepository, registrationCenterRepository,
                syncRestService, certificateManagerService,
                globalParamRepository, objectMapper, userDetailRepository,
                identitySchemaRepository, context,
                documentTypeRepository, applicantValidDocRepository,
                templateRepository, dynamicFieldRepository,
                locationRepository, blocklistedWordRepository,
                syncJobDefRepository, languageRepository,jobManagerService
                );
    }

}

