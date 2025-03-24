package io.mosip.registration.clientmanager.config;

import android.content.Context;

import androidx.room.Room;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class ClientDatabaseTest {

    private ClientDatabase clientDatabase;
    @Mock
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        clientDatabase = Room.inMemoryDatabaseBuilder(context, ClientDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void tearDown() {
        clientDatabase.close();
    }

    @Test
    public void testGetDatabase_SingletonInstance() {
        ClientDatabase instance1 = ClientDatabase.getDatabase(context);
        ClientDatabase instance2 = ClientDatabase.getDatabase(context);
        assertSame(instance1, instance2);
    }

    @Test
    public void testDestroyDB_CreatesNewInstance() {
        ClientDatabase instance1 = ClientDatabase.getDatabase(context);
        ClientDatabase.destroyDB();
        ClientDatabase instance2 = ClientDatabase.getDatabase(context);
        assertNotSame(instance1, instance2);
    }

    @Test
    public void testDaos_NotNull() {
        assertNotNull(clientDatabase.userTokenDao());
        assertNotNull(clientDatabase.registrationDao());
        assertNotNull(clientDatabase.reasonListDao());
        assertNotNull(clientDatabase.registrationCenterDao());
        assertNotNull(clientDatabase.machineMasterDao());
        assertNotNull(clientDatabase.documentTypeDao());
        assertNotNull(clientDatabase.dynamicFieldDao());
        assertNotNull(clientDatabase.applicantValidDocumentDao());
        assertNotNull(clientDatabase.templateDao());
        assertNotNull(clientDatabase.keyStoreDao());
        assertNotNull(clientDatabase.locationDao());
        assertNotNull(clientDatabase.globalParamDao());
        assertNotNull(clientDatabase.identitySchemaDao());
        assertNotNull(clientDatabase.locationHierarchyDao());
        assertNotNull(clientDatabase.blocklistedWordDao());
        assertNotNull(clientDatabase.syncJobDefDao());
        assertNotNull(clientDatabase.userDetailDao());
        assertNotNull(clientDatabase.userBiometricDao());
        assertNotNull(clientDatabase.userPasswordDao());
        assertNotNull(clientDatabase.jobTransactionDao());
        assertNotNull(clientDatabase.caCertificateStoreDao());
        assertNotNull(clientDatabase.languageDao());
        assertNotNull(clientDatabase.auditDao());
        assertNotNull(clientDatabase.fileSignatureDao());
        assertNotNull(clientDatabase.processSpecDao());
        assertNotNull(clientDatabase.preRegistrationDataSyncRepositoryDao());
    }
}