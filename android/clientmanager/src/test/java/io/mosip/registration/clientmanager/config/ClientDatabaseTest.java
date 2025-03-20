package io.mosip.registration.clientmanager.config;

import android.app.Application;
import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.dao.AuditDao;
import io.mosip.registration.clientmanager.dao.BlocklistedWordDao;
import io.mosip.registration.clientmanager.dao.DocumentTypeDao;
import io.mosip.registration.clientmanager.dao.DynamicFieldDao;
import io.mosip.registration.clientmanager.dao.FileSignatureDao;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.dao.IdentitySchemaDao;
import io.mosip.registration.clientmanager.dao.JobTransactionDao;
import io.mosip.registration.clientmanager.dao.LanguageDao;
import io.mosip.registration.clientmanager.dao.LocationDao;
import io.mosip.registration.clientmanager.dao.LocationHierarchyDao;
import io.mosip.registration.clientmanager.dao.MachineMasterDao;
import io.mosip.registration.clientmanager.dao.ReasonListDao;
import io.mosip.registration.clientmanager.dao.ProcessSpecDao;
import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncRepositoryDao;
import io.mosip.registration.clientmanager.dao.RegistrationCenterDao;
import io.mosip.registration.clientmanager.dao.RegistrationDao;
import io.mosip.registration.clientmanager.dao.SyncJobDefDao;
import io.mosip.registration.clientmanager.dao.TemplateDao;
import io.mosip.registration.clientmanager.dao.UserBiometricDao;
import io.mosip.registration.clientmanager.dao.UserDetailDao;
import io.mosip.registration.clientmanager.dao.UserPasswordDao;
import io.mosip.registration.clientmanager.dao.UserTokenDao;
import io.mosip.registration.keymanager.dao.CACertificateStoreDao;
import io.mosip.registration.keymanager.dao.KeyStoreDao;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class ClientDatabaseTest {

    @Mock
    private Context mockContext;

    private ClientDatabase clientDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockContext = RuntimeEnvironment.getApplication().getApplicationContext();
        ClientDatabase.destroyDB();
    }

    @After
    public void tearDown() {
        ClientDatabase.destroyDB();
    }

    @Test
    public void testGetDatabase_ReturnsSingletonInstance() {

        ClientDatabase instance1 = ClientDatabase.getDatabase(mockContext);
        ClientDatabase instance2 = ClientDatabase.getDatabase(mockContext);

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame("Instances should be the same (singleton)", instance1, instance2);
    }

    @Test
    public void testGetDatabase_CreatesInstanceWhenNull() {

        ClientDatabase instance = ClientDatabase.getDatabase(mockContext);

        assertNotNull(instance);
        assertTrue(instance instanceof ClientDatabase);
    }

    @Test
    public void testBuildDatabase_CreatesNewInstance() {

        ClientDatabase instance = ClientDatabase.buildDatabase(mockContext);

        assertNotNull(instance);
        assertTrue(instance instanceof ClientDatabase);
    }

    @Test
    public void testBuildDatabase_DifferentInstances() {

        ClientDatabase instance1 = ClientDatabase.buildDatabase(mockContext);
        ClientDatabase instance2 = ClientDatabase.buildDatabase(mockContext);

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertNotSame("buildDatabase should create new instances", instance1, instance2);
    }

    @Test
    public void testDestroyDB_ResetsInstance() {

        ClientDatabase instance1 = ClientDatabase.getDatabase(mockContext);
        assertNotNull(instance1);

        ClientDatabase.destroyDB();
        ClientDatabase instance2 = ClientDatabase.getDatabase(mockContext);

        assertNotNull(instance2);
        assertNotSame("New instance should be created after destroy", instance1, instance2);
    }

    @Test
    public void testUserTokenDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        UserTokenDao dao = clientDatabase.userTokenDao();

        assertNotNull(dao);
    }

    @Test
    public void testRegistrationDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        RegistrationDao dao = clientDatabase.registrationDao();

        assertNotNull(dao);
    }

    @Test
    public void testReasonListDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        ReasonListDao dao = clientDatabase.reasonListDao();

        assertNotNull(dao);
    }

    @Test
    public void testRegistrationCenterDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        RegistrationCenterDao dao = clientDatabase.registrationCenterDao();

        assertNotNull(dao);
    }

    @Test
    public void testMachineMasterDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        MachineMasterDao dao = clientDatabase.machineMasterDao();

        assertNotNull(dao);
    }

    @Test
    public void testDocumentTypeDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        DocumentTypeDao dao = clientDatabase.documentTypeDao();

        assertNotNull(dao);
    }

    @Test
    public void testApplicantValidDocumentDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        ApplicantValidDocumentDao dao = clientDatabase.applicantValidDocumentDao();

        assertNotNull(dao);
    }

    @Test
    public void testDynamicFieldDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        DynamicFieldDao dao = clientDatabase.dynamicFieldDao();

        assertNotNull(dao);
    }

    @Test
    public void testTemplateDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        TemplateDao dao = clientDatabase.templateDao();

        assertNotNull(dao);
    }

    @Test
    public void testKeyStoreDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        KeyStoreDao dao = clientDatabase.keyStoreDao();

        assertNotNull(dao);
    }

    @Test
    public void testLocationDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        LocationDao dao = clientDatabase.locationDao();

        assertNotNull(dao);
    }

    @Test
    public void testGlobalParamDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        GlobalParamDao dao = clientDatabase.globalParamDao();

        assertNotNull(dao);
    }

    @Test
    public void testIdentitySchemaDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        IdentitySchemaDao dao = clientDatabase.identitySchemaDao();

        assertNotNull(dao);
    }

    @Test
    public void testLocationHierarchyDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        LocationHierarchyDao dao = clientDatabase.locationHierarchyDao();

        assertNotNull(dao);
    }

    @Test
    public void testBlocklistedWordDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        BlocklistedWordDao dao = clientDatabase.blocklistedWordDao();

        assertNotNull(dao);
    }

    @Test
    public void testSyncJobDefDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        SyncJobDefDao dao = clientDatabase.syncJobDefDao();

        assertNotNull(dao);
    }

    @Test
    public void testUserDetailDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        UserDetailDao dao = clientDatabase.userDetailDao();

        assertNotNull(dao);
    }

    @Test
    public void testUserBiometricDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        UserBiometricDao dao = clientDatabase.userBiometricDao();

        assertNotNull(dao);
    }

    @Test
    public void testUserPasswordDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        UserPasswordDao dao = clientDatabase.userPasswordDao();

        assertNotNull(dao);
    }

    @Test
    public void testJobTransactionDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        JobTransactionDao dao = clientDatabase.jobTransactionDao();

        assertNotNull(dao);
    }

    @Test
    public void testCACertificateStoreDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        CACertificateStoreDao dao = clientDatabase.caCertificateStoreDao();

        assertNotNull(dao);
    }

    @Test
    public void testLanguageDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        LanguageDao dao = clientDatabase.languageDao();

        assertNotNull(dao);
    }

    @Test
    public void testAuditDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        AuditDao dao = clientDatabase.auditDao();

        assertNotNull(dao);
    }

    @Test
    public void testFileSignatureDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        FileSignatureDao dao = clientDatabase.fileSignatureDao();

        assertNotNull(dao);
    }

    @Test
    public void testProcessSpecDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        ProcessSpecDao dao = clientDatabase.processSpecDao();

        assertNotNull(dao);
    }

    @Test
    public void testPreRegistrationDataSyncRepositoryDao_NotNull() {

        clientDatabase = ClientDatabase.getDatabase(mockContext);

        PreRegistrationDataSyncRepositoryDao dao = clientDatabase.preRegistrationDataSyncRepositoryDao();

        assertNotNull(dao);
    }
}