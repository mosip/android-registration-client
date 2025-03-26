package io.mosip.registration.clientmanager.config;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.dao.FileSignatureDao;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(
        sdk = 28,
        manifest = "src/main/AndroidManifest.xml",
        application = Application.class
)
public class AppModuleTest {

    @Mock
    private Application mockApplication;

    @Mock
    private Context mockContext;

    @Mock
    private AssetManager mockAssetManager;

    private AppModule appModule;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        // Use Robolectric's application context with resources
        Application robolectricApp = RuntimeEnvironment.getApplication();
        when(mockApplication.getApplicationContext()).thenReturn(robolectricApp.getApplicationContext());
        when(mockContext.getAssets()).thenReturn(mockAssetManager);
        // Provide a more realistic config.properties mock
        String configProperties = "mosip.crypto.timeout=5000\nkey=value\n"; // Adjust based on actual requirements
        InputStream mockInputStream = new ByteArrayInputStream(configProperties.getBytes());
        when(mockAssetManager.open("config.properties")).thenReturn(mockInputStream);
        appModule = new AppModule(mockApplication);
    }

    @Test
    public void testProvidesApplication() {
        Application result = appModule.providesApplication();
        assertEquals(mockApplication, result);
    }

    @Test
    public void testProvideApplicationContext() {
        Context result = appModule.provideApplicationContext();
        assertEquals(RuntimeEnvironment.getApplication().getApplicationContext(), result);
    }

    @Test
    public void testProvideClientCryptoManagerService() {
        CertificateManagerService certificateManagerService = mock(CertificateManagerService.class);
        ClientCryptoManagerService result = appModule.provideClientCryptoManagerService(certificateManagerService);
        assertNotNull(result);
        assertTrue(result instanceof LocalClientCryptoServiceImpl);
    }

    @Test
    public void testProvideIPacketCryptoService() {
        ClientCryptoManagerService clientCryptoManagerService = mock(ClientCryptoManagerService.class);
        CryptoManagerService cryptoManagerService = mock(CryptoManagerService.class);
        IPacketCryptoService result = appModule.provideIPacketCryptoService(clientCryptoManagerService, cryptoManagerService);
        assertNotNull(result);
        assertTrue(result instanceof PacketCryptoServiceImpl);
    }

    @Test
    public void testProvideObjectAdapterService() {
        IPacketCryptoService iPacketCryptoService = mock(IPacketCryptoService.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        ObjectAdapterService result = appModule.provideObjectAdapterService(iPacketCryptoService, objectMapper);
        assertNotNull(result);
        assertTrue(result instanceof PosixAdapterServiceImpl);
    }

    @Test
    public void testProvidePacketKeeper() throws IOException {
        IPacketCryptoService iPacketCryptoService = mock(IPacketCryptoService.class);
        ObjectAdapterService objectAdapterService = mock(ObjectAdapterService.class);
        PacketKeeper result = appModule.providePacketKeeper(iPacketCryptoService, objectAdapterService);
        assertNotNull(result);
        assertTrue(result instanceof PacketKeeper);
    }

    @Test
    public void testProvidePacketManagerHelper() {
        PacketManagerHelper result = appModule.providePacketManagerHelper();
        assertNotNull(result);
        assertTrue(result instanceof PacketManagerHelper);
    }

    @Test
    public void testProvidePacketWriterService() {
        PacketManagerHelper packetManagerHelper = mock(PacketManagerHelper.class);
        PacketKeeper packetKeeper = mock(PacketKeeper.class);
        PacketWriterService result = appModule.providePacketWriterService(packetManagerHelper, packetKeeper);
        assertNotNull(result);
        assertTrue(result instanceof PacketWriterServiceImpl);
    }

    @Test
    public void testProvideMasterDataService() {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        SyncRestService syncRestService = mock(SyncRestService.class);
        ClientCryptoManagerService clientCryptoManagerService = mock(ClientCryptoManagerService.class);
        MachineRepository machineRepository = mock(MachineRepository.class);
        ReasonListRepository reasonListRepository = mock(ReasonListRepository.class);
        RegistrationCenterRepository registrationCenterRepository = mock(RegistrationCenterRepository.class);
        DocumentTypeRepository documentTypeRepository = mock(DocumentTypeRepository.class);
        ApplicantValidDocRepository applicantValidDocRepository = mock(ApplicantValidDocRepository.class);
        TemplateRepository templateRepository = mock(TemplateRepository.class);
        DynamicFieldRepository dynamicFieldRepository = mock(DynamicFieldRepository.class);
        LocationRepository locationRepository = mock(LocationRepository.class);
        GlobalParamRepository globalParamRepository = mock(GlobalParamRepository.class);
        IdentitySchemaRepository identitySchemaRepository = mock(IdentitySchemaRepository.class);
        BlocklistedWordRepository blocklistedWordRepository = mock(BlocklistedWordRepository.class);
        SyncJobDefRepository syncJobDefRepository = mock(SyncJobDefRepository.class);
        UserDetailRepository userDetailRepository = mock(UserDetailRepository.class);
        CertificateManagerService certificateManagerService = mock(CertificateManagerService.class);
        LanguageRepository languageRepository = mock(LanguageRepository.class);
        JobManagerService jobManagerService = mock(JobManagerService.class);
        FileSignatureDao fileSignatureDao = mock(FileSignatureDao.class);

        MasterDataService result = appModule.provideMasterDataService(objectMapper, syncRestService, clientCryptoManagerService,
                machineRepository, reasonListRepository, registrationCenterRepository, documentTypeRepository,
                applicantValidDocRepository, templateRepository, dynamicFieldRepository, locationRepository,
                globalParamRepository, identitySchemaRepository, blocklistedWordRepository, syncJobDefRepository,
                userDetailRepository, certificateManagerService, languageRepository, jobManagerService, fileSignatureDao);
        assertNotNull(result);
        assertTrue(result instanceof MasterDataServiceImpl);
    }

    @Test
    public void testProvideSyncRestFactory() {
        ClientCryptoManagerService clientCryptoManagerService = mock(ClientCryptoManagerService.class);
        SyncRestUtil result = appModule.provideSyncRestFactory(clientCryptoManagerService);
        assertNotNull(result);
        assertTrue(result instanceof SyncRestUtil);
    }

    @Test
    public void testProvideLoginService() {
        ClientCryptoManagerService clientCryptoManagerService = mock(ClientCryptoManagerService.class);
        UserDetailRepository userDetailRepository = mock(UserDetailRepository.class);
        LoginService result = appModule.provideLoginService(clientCryptoManagerService, userDetailRepository);
        assertNotNull(result);
        assertTrue(result instanceof LoginService);
    }

    @Test
    public void testProvideRegistrationService() {
        PacketWriterService packetWriterService = mock(PacketWriterService.class);
        RegistrationRepository registrationRepository = mock(RegistrationRepository.class);
        MasterDataService masterDataService = mock(MasterDataService.class);
        IdentitySchemaRepository identitySchemaRepository = mock(IdentitySchemaRepository.class);
        ClientCryptoManagerService clientCryptoManagerService = mock(ClientCryptoManagerService.class);
        KeyStoreRepository keyStoreRepository = mock(KeyStoreRepository.class);
        GlobalParamRepository globalParamRepository = mock(GlobalParamRepository.class);
        AuditManagerService auditManagerService = mock(AuditManagerService.class);

        RegistrationService result = appModule.provideRegistrationService(packetWriterService, registrationRepository,
                masterDataService, identitySchemaRepository, clientCryptoManagerService, keyStoreRepository,
                globalParamRepository, auditManagerService);
        assertNotNull(result);
        assertTrue(result instanceof RegistrationServiceImpl);
    }

    @Test
    public void testProvideUserInterfaceHelperService() {
        UserInterfaceHelperService result = appModule.provideUserInterfaceHelperService();
        assertNotNull(result);
        assertTrue(result instanceof UserInterfaceHelperService);
    }

    @Test
    public void testProvidePacketService() {
        RegistrationRepository registrationRepository = mock(RegistrationRepository.class);
        IPacketCryptoService packetCryptoService = mock(IPacketCryptoService.class);
        SyncRestService syncRestService = mock(SyncRestService.class);
        MasterDataService masterDataService = mock(MasterDataService.class);
        GlobalParamRepository globalParamRepository = mock(GlobalParamRepository.class);

        PacketService result = appModule.providePacketService(registrationRepository, packetCryptoService,
                syncRestService, masterDataService, globalParamRepository);
        assertNotNull(result);
        assertTrue(result instanceof PacketServiceImpl);
    }

    @Test
    public void testProvideJobTransactionService() {
        JobTransactionRepository jobTransactionRepository = mock(JobTransactionRepository.class);
        JobTransactionService result = appModule.provideJobTransactionService(jobTransactionRepository);
        assertNotNull(result);
        assertTrue(result instanceof JobTransactionServiceImpl);
    }

    @Test
    public void testProvideCACertificateManagerService() {
        CertificateDBHelper certificateDBHelper = mock(CertificateDBHelper.class);
        KeyStoreRepository keyStoreRepository = mock(KeyStoreRepository.class);
        CertificateManagerService result = appModule.provideCACertificateManagerService(certificateDBHelper, keyStoreRepository);
        assertNotNull(result);
        assertTrue(result instanceof CertificateManagerServiceImpl);
    }

    @Test
    public void testProvideCertificateDBHelper() {
        CACertificateStoreRepository caCertificateStoreRepository = mock(CACertificateStoreRepository.class);
        CertificateDBHelper result = appModule.provideCertificateDBHelper(caCertificateStoreRepository);
        assertNotNull(result);
        assertTrue(result instanceof CertificateDBHelper);
    }

    @Test
    public void testProvideAuditManagerService() {
        AuditRepository auditRepository = mock(AuditRepository.class);
        GlobalParamRepository globalParamRepository = mock(GlobalParamRepository.class);
        AuditManagerService result = appModule.provideAuditManagerService(auditRepository, globalParamRepository);
        assertNotNull(result);
        assertTrue(result instanceof AuditManagerServiceImpl);
    }

    @Test
    public void testProvideDateUtil() {
        DateUtil result = appModule.provideDateUtil();
        assertNotNull(result);
        assertTrue(result instanceof DateUtil);
    }

    @Test
    public void testProvideJobManagerService() {
        SyncJobDefRepository syncJobDefRepository = mock(SyncJobDefRepository.class);
        JobTransactionService jobTransactionService = mock(JobTransactionService.class);
        DateUtil dateUtil = mock(DateUtil.class);
        JobManagerService result = appModule.provideJobManagerService(syncJobDefRepository, jobTransactionService, dateUtil);
        assertNotNull(result);
        assertTrue(result instanceof JobManagerServiceImpl);
    }

    @Test
    public void testProvideBiometrics095Service() {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        AuditManagerService auditManagerService = mock(AuditManagerService.class);
        GlobalParamRepository globalParamRepository = mock(GlobalParamRepository.class);
        ClientCryptoManagerService clientCryptoManagerService = mock(ClientCryptoManagerService.class);
        UserBiometricRepository userBiometricRepository = mock(UserBiometricRepository.class);

        Biometrics095Service result = appModule.provideBiometrics095Service(objectMapper, auditManagerService,
                globalParamRepository, clientCryptoManagerService, userBiometricRepository);
        assertNotNull(result);
        assertTrue(result instanceof Biometrics095Service);
    }

    @Test
    public void testProvideUserOnboardService() {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        AuditManagerService auditManagerService = mock(AuditManagerService.class);
        CertificateManagerService certificateManagerService = mock(CertificateManagerService.class);
        SyncRestService syncRestService = mock(SyncRestService.class);
        CryptoManagerService cryptoManagerService = mock(CryptoManagerService.class);
        RegistrationService registrationService = mock(RegistrationService.class);
        UserBiometricRepository userBiometricRepository = mock(UserBiometricRepository.class);
        ClientCryptoManagerService clientCryptoManagerService = mock(ClientCryptoManagerService.class);
        UserDetailRepository userDetailRepository = mock(UserDetailRepository.class);

        UserOnboardService result = appModule.provideUserOnboardService(objectMapper, auditManagerService,
                certificateManagerService, syncRestService, cryptoManagerService, registrationService,
                userBiometricRepository, clientCryptoManagerService, userDetailRepository);
        assertNotNull(result);
        assertTrue(result instanceof UserOnboardService);
    }

    @Test
    public void testProvideTemplateService() {
        MasterDataService masterDataService = mock(MasterDataService.class);
        IdentitySchemaRepository identitySchemaRepository = mock(IdentitySchemaRepository.class);
        GlobalParamRepository globalParamRepository = mock(GlobalParamRepository.class);

        TemplateService result = appModule.TemplateService(masterDataService, identitySchemaRepository, globalParamRepository);
        assertNotNull(result);
        assertTrue(result instanceof TemplateService);
    }

    @Test
    public void testProvidePreRegistrationDataSyncService() {
        PreRegistrationDataSyncDao preRegistrationDao = mock(PreRegistrationDataSyncDao.class);
        MasterDataService masterDataService = mock(MasterDataService.class);
        SyncRestService syncRestService = mock(SyncRestService.class);
        PreRegZipHandlingService preRegZipHandlingService = mock(PreRegZipHandlingService.class);
        PreRegistrationList preRegistration = mock(PreRegistrationList.class);
        GlobalParamRepository globalParamRepository = mock(GlobalParamRepository.class);
        RegistrationService registrationService = mock(RegistrationService.class);

        PreRegistrationDataSyncService result = appModule.PreRegistrationDataSyncService(preRegistrationDao, masterDataService,
                syncRestService, preRegZipHandlingService, preRegistration, globalParamRepository, registrationService);
        assertNotNull(result);
        assertTrue(result instanceof PreRegistrationDataSyncServiceImpl);
    }

    @Test
    public void testProvidePreRegistrationDataSyncDao() {
        PreRegistrationDataSyncRepositoryDao preRegistrationRepositoryDao = mock(PreRegistrationDataSyncRepositoryDao.class);
        PreRegistrationDataSyncDao result = appModule.PreRegistrationDataSyncDao(preRegistrationRepositoryDao);
        assertNotNull(result);
        assertTrue(result instanceof PreRegistrationDataSyncDaoImpl);
    }

    @Test
    public void testProvidePreRegZipHandlingService() {
        ApplicantValidDocumentDao applicantValidDocumentDao = mock(ApplicantValidDocumentDao.class);
        IdentitySchemaRepository identitySchemaService = mock(IdentitySchemaRepository.class);
        ClientCryptoManagerService clientCryptoFacade = mock(ClientCryptoManagerService.class);
        RegistrationService registrationService = mock(RegistrationService.class);
        CryptoManagerService cryptoManagerService = mock(CryptoManagerService.class);
        PacketKeeper packetKeeper = mock(PacketKeeper.class);
        IPacketCryptoService iPacketCryptoService = mock(IPacketCryptoService.class);
        MasterDataService masterDataService = mock(MasterDataService.class);
        GlobalParamRepository globalParamRepository = mock(GlobalParamRepository.class);

        PreRegZipHandlingService result = appModule.PreRegZipHandlingService(applicantValidDocumentDao, identitySchemaService,
                clientCryptoFacade, registrationService, cryptoManagerService, packetKeeper, iPacketCryptoService,
                masterDataService, globalParamRepository);
        assertNotNull(result);
        assertTrue(result instanceof PreRegZipHandlingServiceImpl);
    }

    @Test
    public void testProvidePreRegistrationList() {
        PreRegistrationList result = appModule.PreRegistrationList();
        assertNotNull(result);
        assertTrue(result instanceof PreRegistrationList);
    }
}