package io.mosip.registration.clientmanager.config;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.dao.FileSignatureDao;
import io.mosip.registration.clientmanager.dao.LocalConfigDAO;
import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncDao;
import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncRepositoryDao;
import io.mosip.registration.clientmanager.entity.PreRegistrationList;
import io.mosip.registration.clientmanager.repository.ApplicantValidDocRepository;
import io.mosip.registration.clientmanager.repository.AuditRepository;
import io.mosip.registration.clientmanager.repository.BlocklistedWordRepository;
import io.mosip.registration.clientmanager.repository.DocumentTypeRepository;
import io.mosip.registration.clientmanager.repository.DynamicFieldRepository;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.JobTransactionRepository;
import io.mosip.registration.clientmanager.repository.LanguageRepository;
import io.mosip.registration.clientmanager.repository.LocationRepository;
import io.mosip.registration.clientmanager.repository.MachineRepository;
import io.mosip.registration.clientmanager.repository.ReasonListRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.repository.TemplateRepository;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.clientmanager.repository.UserRoleRepository;
import io.mosip.registration.clientmanager.repository.PermittedLocalConfigRepository;
import io.mosip.registration.clientmanager.service.AuditManagerServiceImpl;
import io.mosip.registration.clientmanager.service.Biometrics095Service;
import io.mosip.registration.clientmanager.service.JobManagerServiceImpl;
import io.mosip.registration.clientmanager.service.JobTransactionServiceImpl;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.service.MasterDataServiceImpl;
import io.mosip.registration.clientmanager.service.PacketServiceImpl;
import io.mosip.registration.clientmanager.service.PreRegistrationDataSyncDaoImpl;
import io.mosip.registration.clientmanager.service.PreRegistrationDataSyncServiceImpl;
import io.mosip.registration.clientmanager.service.RegistrationServiceImpl;
import io.mosip.registration.clientmanager.service.TemplateService;
import io.mosip.registration.clientmanager.service.UserOnboardService;
import io.mosip.registration.clientmanager.service.external.PreRegZipHandlingService;
import io.mosip.registration.clientmanager.service.external.impl.PreRegZipHandlingServiceImpl;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.PreRegistrationDataSyncService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.DateUtil;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.keymanager.repository.CACertificateStoreRepository;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.keymanager.service.CertificateDBHelper;
import io.mosip.registration.keymanager.service.CertificateManagerServiceImpl;
import io.mosip.registration.keymanager.service.CryptoManagerServiceImpl;
import io.mosip.registration.keymanager.service.LocalClientCryptoServiceImpl;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.packetmanager.service.PacketCryptoServiceImpl;
import io.mosip.registration.packetmanager.service.PacketWriterServiceImpl;
import io.mosip.registration.packetmanager.service.PosixAdapterServiceImpl;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import io.mosip.registration.packetmanager.spi.ObjectAdapterService;
import io.mosip.registration.packetmanager.spi.PacketWriterService;
import io.mosip.registration.packetmanager.util.PacketKeeper;
import io.mosip.registration.packetmanager.util.PacketManagerHelper;

@Module
public class AppModule {

    Application application;
    Context appContext;

    public AppModule(Application application) {
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

    @Singleton
    @Provides
    public ClientCryptoManagerService provideClientCryptoManagerService(CertificateManagerService certificateManagerService) {
        return new LocalClientCryptoServiceImpl(appContext, certificateManagerService);
    }

    @Singleton
    @Provides
    public CryptoManagerService provideCryptoManagerService(CertificateManagerService certificateManagerService) {
        return new CryptoManagerServiceImpl(appContext, certificateManagerService);
    }

    @Singleton
    @Provides
    public IPacketCryptoService provideIPacketCryptoService(ClientCryptoManagerService clientCryptoManagerService,
                                                            CryptoManagerService cryptoManagerService) {
        return new PacketCryptoServiceImpl(appContext, clientCryptoManagerService, cryptoManagerService);
    }

    @Singleton
    @Provides
    public ObjectAdapterService provideObjectAdapterService(IPacketCryptoService iPacketCryptoService, ObjectMapper objectMapper) {
        return new PosixAdapterServiceImpl(appContext, iPacketCryptoService, objectMapper);
    }

    @Singleton
    @Provides
    public PacketKeeper providePacketKeeper(IPacketCryptoService iPacketCryptoService,
                                            ObjectAdapterService objectAdapterService) {
        return new PacketKeeper(appContext, iPacketCryptoService, objectAdapterService);
    }

    @Singleton
    @Provides
    public PacketManagerHelper providePacketManagerHelper() {
        return new PacketManagerHelper(appContext);
    }

    @Singleton
    @Provides
    public PacketWriterService providePacketWriterService(PacketManagerHelper packetManagerHelper,
                                                          PacketKeeper packetKeeper) {
        return new PacketWriterServiceImpl(appContext, packetManagerHelper, packetKeeper);
    }

    @Singleton
    @Provides
    public MasterDataService provideMasterDataService(ObjectMapper objectMapper, SyncRestService syncRestService, ClientCryptoManagerService clientCryptoManagerService,
                                                      MachineRepository machineRepository,
                                                      ReasonListRepository reasonListRepository,
                                                      RegistrationCenterRepository registrationCenterRepository,
                                                      DocumentTypeRepository documentTypeRepository,
                                                      ApplicantValidDocRepository applicantValidDocRepository,
                                                      TemplateRepository templateRepository,
                                                      DynamicFieldRepository dynamicFieldRepository,
                                                      LocationRepository locationRepository,
                                                      GlobalParamRepository globalParamRepository,
                                                      IdentitySchemaRepository identitySchemaRepository,
                                                      BlocklistedWordRepository blocklistedWordRepository,
                                                      SyncJobDefRepository syncJobDefRepository,
                                                      UserDetailRepository userDetailRepository,
                                                      CertificateManagerService certificateManagerService,
                                                      LanguageRepository languageRepository,
                                                      JobManagerService jobManagerService,
                                                      FileSignatureDao fileSignatureDao,
                                                      PermittedLocalConfigRepository permittedLocalConfigRepository,
                                                      LocalConfigDAO localConfigDAO) {
        return new MasterDataServiceImpl(appContext, objectMapper, syncRestService, clientCryptoManagerService,
                machineRepository, reasonListRepository, registrationCenterRepository, documentTypeRepository, applicantValidDocRepository,
                templateRepository, dynamicFieldRepository, locationRepository,
                globalParamRepository, identitySchemaRepository, blocklistedWordRepository, syncJobDefRepository, userDetailRepository,
                certificateManagerService, languageRepository, jobManagerService, fileSignatureDao, permittedLocalConfigRepository, localConfigDAO);
    }


    @Provides
    @Singleton
    SyncRestUtil provideSyncRestFactory(ClientCryptoManagerService clientCryptoManagerService) {
        return new SyncRestUtil(clientCryptoManagerService);
    }

    @Provides
    @Singleton
    LoginService provideLoginService(ClientCryptoManagerService clientCryptoManagerService, UserDetailRepository userDetailRepository, UserRoleRepository userRoleRepository) {
        return new LoginService(appContext, clientCryptoManagerService, userDetailRepository, userRoleRepository);
    }

    @Provides
    @Singleton
    RegistrationService provideRegistrationService(PacketWriterService packetWriterService,
                                                   RegistrationRepository registrationRepository,
                                                   MasterDataService masterDataService,
                                                   IdentitySchemaRepository identitySchemaRepository,
                                                   ClientCryptoManagerService clientCryptoManagerService,
                                                   KeyStoreRepository keyStoreRepository,
                                                   GlobalParamRepository globalParamRepository,
                                                   AuditManagerService auditManagerService,
                                                   Biometrics095Service biometricService) {
        return new RegistrationServiceImpl(appContext, packetWriterService, registrationRepository,
                masterDataService, identitySchemaRepository, clientCryptoManagerService,
                keyStoreRepository, globalParamRepository, auditManagerService,biometricService);
    }

    @Provides
    @Singleton
    UserInterfaceHelperService provideUserInterfaceHelperService() {
        return new UserInterfaceHelperService(appContext);
    }

    @Provides
    @Singleton
    PacketService providePacketService(RegistrationRepository registrationRepository,
                                       IPacketCryptoService packetCryptoService, SyncRestService syncRestService,
                                       MasterDataService masterDataService, GlobalParamRepository globalParamRepository) {
        return new PacketServiceImpl(appContext, registrationRepository, packetCryptoService, syncRestService,
                masterDataService, globalParamRepository);
    }

    @Provides
    @Singleton
    JobTransactionService provideJobTransactionService(JobTransactionRepository jobTransactionRepository) {
        return new JobTransactionServiceImpl(jobTransactionRepository);
    }

    @Provides
    @Singleton
    CertificateManagerService provideCACertificateManagerService(CertificateDBHelper certificateDBHelper, KeyStoreRepository keyStoreRepository) {
        return new CertificateManagerServiceImpl(appContext, certificateDBHelper, keyStoreRepository);
    }

    @Provides
    @Singleton
    CertificateDBHelper provideCertificateDBHelper(CACertificateStoreRepository caCertificateStoreRepository) {
        return new CertificateDBHelper(caCertificateStoreRepository);
    }

    @Provides
    @Singleton
    AuditManagerService provideAuditManagerService(AuditRepository auditRepository, GlobalParamRepository globalParamRepository) {
        return new AuditManagerServiceImpl(appContext, auditRepository, globalParamRepository);
    }

    @Provides
    @Singleton
    DateUtil provideDateUtil() {
        return new DateUtil(appContext);
    }

    @Provides
    @Singleton
    JobManagerService provideJobManagerService(SyncJobDefRepository syncJobDefRepository, JobTransactionService jobTransactionService, DateUtil dateUtil) {
        return new JobManagerServiceImpl(appContext, syncJobDefRepository, jobTransactionService, dateUtil);
    }

    @Provides
    @Singleton
    Biometrics095Service provideBiometrics095Service(ObjectMapper objectMapper, AuditManagerService auditManagerService,
                                                     GlobalParamRepository globalParamRepository, ClientCryptoManagerService clientCryptoManagerService,
                                                     UserBiometricRepository userBiometricRepository) {
        return new Biometrics095Service(appContext, objectMapper, auditManagerService, globalParamRepository, clientCryptoManagerService, userBiometricRepository);
    }

    @Provides
    @Singleton
    UserOnboardService provideUserOnboardService(ObjectMapper objectMapper, AuditManagerService auditManagerService,
                                                 CertificateManagerService certificateManagerService,
                                                 SyncRestService syncRestService, CryptoManagerService cryptoManagerService, RegistrationService registrationService, UserBiometricRepository userBiometricRepository, ClientCryptoManagerService clientCryptoManagerService, UserDetailRepository userDetailRepository) {
        return new UserOnboardService(appContext, objectMapper, auditManagerService, certificateManagerService, syncRestService,
                cryptoManagerService, registrationService, userBiometricRepository, clientCryptoManagerService, userDetailRepository);
    }

    @Provides
    @Singleton
    TemplateService TemplateService(MasterDataService masterDataService, IdentitySchemaRepository identitySchemaRepository, GlobalParamRepository globalParamRepository) {
        return new TemplateService(appContext, masterDataService, identitySchemaRepository, globalParamRepository);
    }

    @Provides
    @Singleton
    PreRegistrationDataSyncService PreRegistrationDataSyncService(PreRegistrationDataSyncDao preRegistrationDao, MasterDataService masterDataService, SyncRestService syncRestService, PreRegZipHandlingService preRegZipHandlingService, PreRegistrationList preRegistration, GlobalParamRepository globalParamRepository, RegistrationService registrationService) {
        return new PreRegistrationDataSyncServiceImpl(appContext, preRegistrationDao, masterDataService, syncRestService, preRegZipHandlingService, preRegistration, globalParamRepository, registrationService);
    }

    @Provides
    @Singleton
    PreRegistrationDataSyncDao PreRegistrationDataSyncDao(PreRegistrationDataSyncRepositoryDao preRegistrationRepositoryDao) {
        return new PreRegistrationDataSyncDaoImpl(preRegistrationRepositoryDao);
    }

    @Provides
    @Singleton
    PreRegZipHandlingService PreRegZipHandlingService(ApplicantValidDocumentDao applicantValidDocumentDao, IdentitySchemaRepository identitySchemaService, ClientCryptoManagerService clientCryptoFacade, RegistrationService registrationService, CryptoManagerService cryptoManagerService, PacketKeeper packetKeeper, IPacketCryptoService iPacketCryptoService, MasterDataService masterDataService,GlobalParamRepository globalParamRepository) {
        return new PreRegZipHandlingServiceImpl(appContext, applicantValidDocumentDao, identitySchemaService, clientCryptoFacade, registrationService, cryptoManagerService, packetKeeper, iPacketCryptoService, masterDataService,globalParamRepository);
    }

    @Provides
    @Singleton
    PreRegistrationList PreRegistrationList() {
        return new PreRegistrationList();
    }
}