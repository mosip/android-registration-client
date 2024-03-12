/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.service.Biometrics095Service;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.service.TemplateService;
import io.mosip.registration.clientmanager.service.UserOnboardService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.clientmanager.repository.ApplicantValidDocRepository;
import io.mosip.registration.clientmanager.repository.BlocklistedWordRepository;
import io.mosip.registration.clientmanager.repository.DocumentTypeRepository;
import io.mosip.registration.clientmanager.repository.DynamicFieldRepository;
import io.mosip.registration.clientmanager.repository.LanguageRepository;
import io.mosip.registration.clientmanager.repository.LocationRepository;
import io.mosip.registration.clientmanager.repository.MachineRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.repository.TemplateRepository;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.clientmanager.service.MasterDataServiceImpl;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
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
                                       RegistrationCenterRepository registrationCenterRepository,
                                       MasterDataService masterDataService,
                                       AuditManagerService auditManagerService) {
        return new UserDetailsApi(loginService, registrationCenterRepository, masterDataService, auditManagerService);
    }

    @Provides
    @Singleton
    MachineDetailsApi getMachineDetailsApi(ClientCryptoManagerService clientCryptoManagerService,
                                           RegistrationCenterRepository registrationCenterRepository,
                                           AuditManagerService auditManagerService) {
        return new MachineDetailsApi(clientCryptoManagerService, registrationCenterRepository, auditManagerService);
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
    CommonDetailsApi getCommonApiImpl(MasterDataService masterDataService, AuditManagerService auditManagerService){
        return new CommonDetailsApi(masterDataService, auditManagerService);
    }
    

    @Provides
    @Singleton
    ProcessSpecDetailsApi getProcessSpecDetailsApi(IdentitySchemaRepository identitySchemaRepository,
                                                   GlobalParamRepository globalParamRepository,RegistrationService registrationService,
                                                   AuditManagerService auditManagerService) {
        return new ProcessSpecDetailsApi(appContext, identitySchemaRepository,
                        globalParamRepository,registrationService, auditManagerService);

    }

    @Provides
    @Singleton
    BiometricsDetailsApi getBiometricsDetailsApi(AuditManagerService auditManagerService, ObjectMapper objectMapper, Biometrics095Service biometrics095Service, RegistrationService registrationService, GlobalParamRepository globalParamRepository, UserOnboardService userOnboardService) {
        return new BiometricsDetailsApi(auditManagerService, objectMapper,biometrics095Service,registrationService,globalParamRepository,userOnboardService);

    }

    @Provides
    @Singleton
    RegistrationApi getRegistrationDataApi(RegistrationService registrationService, TemplateService templateService,
                                           AuditManagerService auditManagerService) {
        return new RegistrationApi(registrationService, templateService, auditManagerService);

    }

    @Provides
    @Singleton
    PacketAuthenticationApi getPacketAuthenticationApi(SyncRestService syncRestService, SyncRestUtil syncRestFactory,
                                                       LoginService loginService, PacketService packetService,
                                                       AuditManagerService auditManagerService) {
        return new PacketAuthenticationApi(syncRestService, syncRestFactory, loginService, packetService, auditManagerService);
    }

    @Provides
    @Singleton
    DemographicsDetailsApi getDemographicsDetailsApi(RegistrationService registrationService, AuditManagerService auditManagerService) {
        return new DemographicsDetailsApi(registrationService, auditManagerService);
    }

    @Provides
    @Singleton
    DynamicDetailsApi getDynamicDetailsApi(MasterDataService masterDataService, AuditManagerService auditManagerService) {
        return new DynamicDetailsApi(masterDataService, auditManagerService);
    }

    @Provides
    @Singleton
    DocumentDetailsApi getDocumentDetailsApi(RegistrationService registrationService, AuditManagerService auditManagerService) {
        return new DocumentDetailsApi(registrationService, auditManagerService);
    }
    @Provides
    @Singleton
    MasterDataSyncApi getSyncResponseApi(
            ClientCryptoManagerService clientCryptoManagerService, MachineRepository machineRepository, RegistrationCenterRepository registrationCenterRepository,
            SyncRestService syncRestService, CertificateManagerService certificateManagerService, GlobalParamRepository globalParamRepository, ObjectMapper objectMapper, UserDetailRepository userDetailRepository,
            IdentitySchemaRepository identitySchemaRepository, DocumentTypeRepository documentTypeRepository,
            ApplicantValidDocRepository applicantValidDocRepository,
            TemplateRepository templateRepository,
            DynamicFieldRepository dynamicFieldRepository,
            LocationRepository locationRepository,
            BlocklistedWordRepository blocklistedWordRepository,
            SyncJobDefRepository syncJobDefRepository,
            LanguageRepository languageRepository,
            JobManagerService jobManagerService,
            AuditManagerService auditManagerService) {
        return new MasterDataSyncApi( clientCryptoManagerService,
                machineRepository, registrationCenterRepository,
                syncRestService, certificateManagerService,
                globalParamRepository, objectMapper, userDetailRepository,
                identitySchemaRepository, appContext,
                documentTypeRepository, applicantValidDocRepository,
                templateRepository, dynamicFieldRepository,
                locationRepository, blocklistedWordRepository,
                syncJobDefRepository, languageRepository,jobManagerService,
                auditManagerService
                );
    }

    @Provides
    @Singleton
    AuditDetailsApi getAuditDetailsApi(AuditManagerService auditManagerService) {
        return new AuditDetailsApi(auditManagerService);
    }
}

