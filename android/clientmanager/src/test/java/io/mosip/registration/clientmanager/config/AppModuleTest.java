package io.mosip.registration.clientmanager.config;

import android.app.Application;
import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.dao.FileSignatureDao;
import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncDao;
import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncRepositoryDao;
import io.mosip.registration.clientmanager.entity.PreRegistrationList;
import io.mosip.registration.clientmanager.repository.*;
import io.mosip.registration.clientmanager.service.*;
import io.mosip.registration.clientmanager.service.external.PreRegZipHandlingService;
import io.mosip.registration.clientmanager.spi.*;
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
import static org.mockito.Mockito.*;


public class AppModuleTest {

    @Mock Application mockApplication;
    @Mock Context mockContext;
    @Mock CertificateManagerService certificateManagerService;
    @Mock ClientCryptoManagerService clientCryptoManagerService;
    @Mock CryptoManagerService cryptoManagerService;
    @Mock IPacketCryptoService iPacketCryptoService;
    @Mock ObjectMapper objectMapper;
    @Mock ObjectAdapterService objectAdapterService;
    @Mock PacketKeeper packetKeeper;
    @Mock PacketManagerHelper packetManagerHelper;
    @Mock PacketWriterService packetWriterService;
    @Mock SyncRestService syncRestService;
    @Mock MachineRepository machineRepository;
    @Mock ReasonListRepository reasonListRepository;
    @Mock RegistrationCenterRepository registrationCenterRepository;
    @Mock DocumentTypeRepository documentTypeRepository;
    @Mock ApplicantValidDocRepository applicantValidDocRepository;
    @Mock TemplateRepository templateRepository;
    @Mock DynamicFieldRepository dynamicFieldRepository;
    @Mock LocationRepository locationRepository;
    @Mock GlobalParamRepository globalParamRepository;
    @Mock IdentitySchemaRepository identitySchemaRepository;
    @Mock BlocklistedWordRepository blocklistedWordRepository;
    @Mock SyncJobDefRepository syncJobDefRepository;
    @Mock UserDetailRepository userDetailRepository;
    @Mock LanguageRepository languageRepository;
    @Mock UserRoleRepository userRoleRepository;
    @Mock JobManagerService jobManagerService;
    @Mock FileSignatureDao fileSignatureDao;
    @Mock UserBiometricRepository userBiometricRepository;
    @Mock RegistrationRepository registrationRepository;
    @Mock KeyStoreRepository keyStoreRepository;
    @Mock AuditManagerService auditManagerService;
    @Mock AuditRepository auditRepository;
    @Mock JobTransactionRepository jobTransactionRepository;
    @Mock CertificateDBHelper certificateDBHelper;
    @Mock CACertificateStoreRepository caCertificateStoreRepository;
    @Mock DateUtil dateUtil;
    @Mock PreRegistrationDataSyncDao preRegistrationDataSyncDao;
    @Mock PreRegistrationDataSyncRepositoryDao preRegistrationDataSyncRepositoryDao;
    @Mock PreRegZipHandlingService preRegZipHandlingService;
    @Mock PreRegistrationList preRegistrationList;

    private AppModule appModule;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockApplication.getApplicationContext()).thenReturn(mockContext);
        appModule = new AppModule(mockApplication);
    }

    @Test
    public void testProvidesApplication() {
        assertEquals(mockApplication, appModule.providesApplication());
    }

    @Test
    public void testProvideApplicationContext() {
        assertEquals(mockContext, appModule.provideApplicationContext());
    }

    @Test
    public void testProvideClientCryptoManagerService() {
        ClientCryptoManagerService service = appModule.provideClientCryptoManagerService(certificateManagerService);
        assertNotNull(service);
        assertTrue(service instanceof LocalClientCryptoServiceImpl);
    }

    @Test
    public void testProvideIPacketCryptoService() {
        IPacketCryptoService service = appModule.provideIPacketCryptoService(clientCryptoManagerService, cryptoManagerService);
        assertNotNull(service);
        assertTrue(service instanceof PacketCryptoServiceImpl);
    }

    @Test
    public void testProvideObjectAdapterService() {
        ObjectAdapterService service = appModule.provideObjectAdapterService(iPacketCryptoService, objectMapper);
        assertNotNull(service);
        assertTrue(service instanceof PosixAdapterServiceImpl);
    }

    @Test
    public void testProvidePacketWriterService() {
        PacketWriterService service = appModule.providePacketWriterService(packetManagerHelper, packetKeeper);
        assertNotNull(service);
        assertTrue(service instanceof PacketWriterServiceImpl);
    }

    @Test
    public void testProvideMasterDataService() {
        MasterDataService service = appModule.provideMasterDataService(
                objectMapper, syncRestService, clientCryptoManagerService, machineRepository, reasonListRepository,
                registrationCenterRepository, documentTypeRepository, applicantValidDocRepository, templateRepository,
                dynamicFieldRepository, locationRepository, globalParamRepository, identitySchemaRepository,
                blocklistedWordRepository, syncJobDefRepository, userDetailRepository, certificateManagerService,
                languageRepository, jobManagerService, fileSignatureDao
        );
        assertNotNull(service);
        assertTrue(service instanceof MasterDataServiceImpl);
    }

    @Test
    public void testProvideSyncRestFactory() {
        SyncRestUtil util = appModule.provideSyncRestFactory(clientCryptoManagerService);
        assertNotNull(util);
    }

    @Test
    public void testProvideLoginService() {
        LoginService service = appModule.provideLoginService(clientCryptoManagerService, userDetailRepository,userRoleRepository);
        assertNotNull(service);
    }

    @Test
    public void testProvideRegistrationService() {
        RegistrationService service = appModule.provideRegistrationService(
                packetWriterService, registrationRepository, mock(MasterDataService.class), identitySchemaRepository,
                clientCryptoManagerService, keyStoreRepository, globalParamRepository, auditManagerService
        );
        assertNotNull(service);
        assertTrue(service instanceof RegistrationServiceImpl);
    }

    @Test
    public void testProvideUserInterfaceHelperService() {
        UserInterfaceHelperService service = appModule.provideUserInterfaceHelperService();
        assertNotNull(service);
    }

    @Test
    public void testProvidePacketService() {
        PacketService service = appModule.providePacketService(
                registrationRepository, iPacketCryptoService, syncRestService, mock(MasterDataService.class), globalParamRepository
        );
        assertNotNull(service);
        assertTrue(service instanceof PacketServiceImpl);
    }

    @Test
    public void testProvideJobTransactionService() {
        JobTransactionService service = appModule.provideJobTransactionService(jobTransactionRepository);
        assertNotNull(service);
        assertTrue(service instanceof JobTransactionServiceImpl);
    }

    @Test
    public void testProvideCertificateDBHelper() {
        CertificateDBHelper helper = appModule.provideCertificateDBHelper(caCertificateStoreRepository);
        assertNotNull(helper);
    }

    @Test
    public void testProvideAuditManagerService() {
        AuditManagerService service = appModule.provideAuditManagerService(auditRepository, globalParamRepository);
        assertNotNull(service);
        assertTrue(service instanceof AuditManagerServiceImpl);
    }

    @Test
    public void testProvideJobManagerService() {
        JobManagerService service = appModule.provideJobManagerService(syncJobDefRepository, mock(JobTransactionService.class), dateUtil);
        assertNotNull(service);
        assertTrue(service instanceof JobManagerServiceImpl);
    }

    @Test
    public void testProvideBiometrics095Service() {
        Biometrics095Service service = appModule.provideBiometrics095Service(
                objectMapper, auditManagerService, globalParamRepository, clientCryptoManagerService, userBiometricRepository
        );
        assertNotNull(service);
        assertTrue(service instanceof Biometrics095Service);
    }

    @Test
    public void testProvideUserOnboardService() {
        UserOnboardService service = appModule.provideUserOnboardService(
                objectMapper, auditManagerService, certificateManagerService, syncRestService, cryptoManagerService,
                mock(RegistrationService.class), userBiometricRepository, clientCryptoManagerService, userDetailRepository
        );
        assertNotNull(service);
        assertTrue(service instanceof UserOnboardService);
    }

    @Test
    public void testTemplateService() {
        TemplateService service = appModule.TemplateService(
                mock(MasterDataService.class), identitySchemaRepository, globalParamRepository
        );
        assertNotNull(service);
    }

    @Test
    public void testPreRegistrationDataSyncService() {
        PreRegistrationDataSyncService service = appModule.PreRegistrationDataSyncService(
                preRegistrationDataSyncDao, mock(MasterDataService.class), syncRestService, preRegZipHandlingService,
                preRegistrationList, globalParamRepository, mock(RegistrationService.class)
        );
        assertNotNull(service);
    }

    @Test
    public void testPreRegistrationDataSyncDao() {
        PreRegistrationDataSyncDao dao = appModule.PreRegistrationDataSyncDao(preRegistrationDataSyncRepositoryDao);
        assertNotNull(dao);
    }

    @Test
    public void testPreRegZipHandlingService() {
        PreRegZipHandlingService service = appModule.PreRegZipHandlingService(
                mock(ApplicantValidDocumentDao.class), identitySchemaRepository, clientCryptoManagerService,
                mock(RegistrationService.class), cryptoManagerService, packetKeeper, iPacketCryptoService,
                mock(MasterDataService.class), globalParamRepository
        );
        assertNotNull(service);
    }

    @Test
    public void testPreRegistrationList() {
        PreRegistrationList list = appModule.PreRegistrationList();
        assertNotNull(list);
    }

    @Test
    public void test_provide_crypto_manager_service_with_null_app_context() {
        appModule.appContext = null;

        Exception exception = assertThrows(NullPointerException.class, () -> {
            appModule.provideCryptoManagerService(certificateManagerService);
        });

        assertNotNull(exception);
    }

    @Test
    public void test_provide_packet_keeper_with_null_app_context() {
        appModule.appContext = null;
        IPacketCryptoService mockCryptoService = mock(IPacketCryptoService.class);
        ObjectAdapterService mockAdapterService = mock(ObjectAdapterService.class);

        assertThrows(NullPointerException.class, () -> {
            appModule.providePacketKeeper(mockCryptoService, mockAdapterService);
        });
    }

    @Test
    public void test_provide_packet_manager_helper_with_null_context() {
        appModule.appContext = null;

        assertThrows(NullPointerException.class, () -> {
            appModule.providePacketManagerHelper();
        });
    }


}
