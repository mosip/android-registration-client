package io.mosip.registration.clientmanager.config;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.lang.reflect.Method;

import io.mosip.registration.clientmanager.dao.*;
import io.mosip.registration.clientmanager.repository.*;
import io.mosip.registration.keymanager.dao.CACertificateStoreDao;
import io.mosip.registration.keymanager.dao.KeyStoreDao;
import io.mosip.registration.keymanager.repository.CACertificateStoreRepository;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoomModuleTest {

    @Mock
    private Application application;

    @Mock
    private ApplicationInfo applicationInfo;

    @Mock
    private Context context;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private SharedPreferences.Editor editor;

    @Mock
    private MasterKey masterKey;

    @Mock
    private ClientDatabase clientDatabase;

    @Mock
    private RegistrationDao registrationDao;

    @Mock
    private ReasonListDao reasonListDao;

    @Mock
    private UserTokenDao userTokenDao;

    @Mock
    private RegistrationCenterDao registrationCenterDao;

    @Mock
    private MachineMasterDao machineMasterDao;

    @Mock
    private DocumentTypeDao documentTypeDao;

    @Mock
    private ApplicantValidDocumentDao applicantValidDocumentDao;

    @Mock
    private DynamicFieldDao dynamicFieldDao;

    @Mock
    private TemplateDao templateDao;

    @Mock
    private KeyStoreDao keyStoreDao;

    @Mock
    private LocationDao locationDao;

    @Mock
    private GlobalParamDao globalParamDao;

    @Mock
    private IdentitySchemaDao identitySchemaDao;

    @Mock
    private LocationHierarchyDao locationHierarchyDao;

    @Mock
    private BlocklistedWordDao blocklistedWordDao;

    @Mock
    private SyncJobDefDao syncJobDefDao;

    @Mock
    private UserDetailDao userDetailDao;

    @Mock
    private UserBiometricDao userBiometricDao;

    @Mock
    private UserPasswordDao userPasswordDao;

    @Mock
    private JobTransactionDao jobTransactionDao;

    @Mock
    private CACertificateStoreDao caCertificateStoreDao;

    @Mock
    private LanguageDao languageDao;

    @Mock
    private AuditDao auditDao;

    @Mock
    private FileSignatureDao fileSignatureDao;

    @Mock
    private ProcessSpecDao processSpecDao;

    @Mock
    private PreRegistrationDataSyncRepositoryDao preRegistrationDataSyncRepositoryDao;

    @Mock
    private File dbFile;

    @Mock
    private SQLiteDatabase existingDb;

    private RoomModule roomModule;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(application.getApplicationContext()).thenReturn(context);
        when(context.getDatabasePath("reg-client")).thenReturn(dbFile);
       // when(sharedPreferences.edit()).thenReturn(editor);
       // when(editor.putString(anyString(), anyString())).thenReturn(editor);
       // when(context.getCacheDir()).thenReturn(new File("/cache"));

        // Default setup for constructor
        applicationInfo.flags = 0; // Set field directly since it's public
        when(sharedPreferences.getString(eq("db_password"), any())).thenReturn("existingPassword");
        when(dbFile.exists()).thenReturn(true);

        // Mock static Room.databaseBuilder and EncryptedSharedPreferences
        RoomDatabase.Builder<ClientDatabase> builder = mock(RoomDatabase.Builder.class);
        try (MockedStatic<Room> room = org.mockito.Mockito.mockStatic(Room.class);
             MockedStatic<EncryptedSharedPreferences> esp = org.mockito.Mockito.mockStatic(EncryptedSharedPreferences.class)) {
            room.when(() -> Room.databaseBuilder(application, ClientDatabase.class, "reg-client"))
                    .thenReturn(builder);
            esp.when(() -> EncryptedSharedPreferences.create(
                            any(Context.class), anyString(), any(MasterKey.class),
                            any(EncryptedSharedPreferences.PrefKeyEncryptionScheme.class),
                            any(EncryptedSharedPreferences.PrefValueEncryptionScheme.class)))
                    .thenReturn(sharedPreferences);

            when(builder.openHelperFactory(any(SupportFactory.class))).thenReturn(builder);
            when(builder.allowMainThreadQueries()).thenReturn(builder);
            when(builder.build()).thenReturn(clientDatabase);

            roomModule = new RoomModule(application, applicationInfo);
        }
    }

    @Test
    public void testConstructor_NewDbPassword_CreatesDatabase() {
        // Arrange
        reset(sharedPreferences, editor); // Reset mocks to avoid interference from @Before
        applicationInfo.flags = 0; // Non-debuggable
        when(sharedPreferences.getString(eq("db_password"), any())).thenReturn(null);
        when(dbFile.exists()).thenReturn(false);
        RoomDatabase.Builder<ClientDatabase> builder = mock(RoomDatabase.Builder.class);
        try (MockedStatic<Room> room = org.mockito.Mockito.mockStatic(Room.class);
             MockedStatic<EncryptedSharedPreferences> esp = org.mockito.Mockito.mockStatic(EncryptedSharedPreferences.class)) {
            room.when(() -> Room.databaseBuilder(application, ClientDatabase.class, "reg-client"))
                    .thenReturn(builder);
            esp.when(() -> EncryptedSharedPreferences.create(
                            any(Context.class), anyString(), any(MasterKey.class),
                            any(EncryptedSharedPreferences.PrefKeyEncryptionScheme.class),
                            any(EncryptedSharedPreferences.PrefValueEncryptionScheme.class)))
                    .thenReturn(sharedPreferences);

            when(builder.openHelperFactory(any(SupportFactory.class))).thenReturn(builder);
            when(builder.allowMainThreadQueries()).thenReturn(builder);
            when(builder.build()).thenReturn(clientDatabase);
            when(sharedPreferences.edit()).thenReturn(editor);
            when(editor.putString(anyString(), anyString())).thenReturn(editor);

            // Act
            RoomModule module = new RoomModule(application, applicationInfo);

            // Assert
            verify(sharedPreferences).getString("db_password", null);
            verify(editor).putString(eq("db_password"), anyString());
            verify(editor).apply();
            verify(builder).openHelperFactory(any(SupportFactory.class));
        }
    }

    @Test
    public void testConstructor_ExistingDbPassword_UsesExisting() {
        // Arrange
        reset(sharedPreferences); // Reset mock to avoid counting @Before invocation
        applicationInfo.flags = 0;
        when(sharedPreferences.getString("db_password", null)).thenReturn("existingPassword");
        when(dbFile.exists()).thenReturn(true);
        RoomDatabase.Builder<ClientDatabase> builder = mock(RoomDatabase.Builder.class);
        try (MockedStatic<Room> room = org.mockito.Mockito.mockStatic(Room.class);
             MockedStatic<EncryptedSharedPreferences> esp = org.mockito.Mockito.mockStatic(EncryptedSharedPreferences.class)) {
            room.when(() -> Room.databaseBuilder(application, ClientDatabase.class, "reg-client"))
                    .thenReturn(builder);
            esp.when(() -> EncryptedSharedPreferences.create(
                            any(Context.class), anyString(), any(MasterKey.class),
                            any(EncryptedSharedPreferences.PrefKeyEncryptionScheme.class),
                            any(EncryptedSharedPreferences.PrefValueEncryptionScheme.class)))
                    .thenReturn(sharedPreferences);

            when(builder.openHelperFactory(any(SupportFactory.class))).thenReturn(builder);
            when(builder.allowMainThreadQueries()).thenReturn(builder);
            when(builder.build()).thenReturn(clientDatabase);

            // Act
            RoomModule module = new RoomModule(application, applicationInfo);

            // Assert
            verify(sharedPreferences).getString("db_password", null);
            verify(builder).openHelperFactory(any(SupportFactory.class));
        }
    }

//    @Test
//    public void testConstructor_EncryptExistingDb() throws Exception {
//        // Arrange
//        reset(context, sharedPreferences, editor); // Reset mocks to avoid interference
//        applicationInfo.flags = 0;
//        when(sharedPreferences.getString("db_password", null)).thenReturn(null);
//        when(dbFile.exists()).thenReturn(true);
//        File tempFile = mock(File.class);
//        RoomDatabase.Builder<ClientDatabase> builder = mock(RoomDatabase.Builder.class);
//
//        try (MockedStatic<Room> room = org.mockito.Mockito.mockStatic(Room.class);
//             MockedStatic<File> file = org.mockito.Mockito.mockStatic(File.class);
//             MockedStatic<SQLiteDatabase> sqlite = org.mockito.Mockito.mockStatic(SQLiteDatabase.class);
//             MockedStatic<EncryptedSharedPreferences> esp = org.mockito.Mockito.mockStatic(EncryptedSharedPreferences.class)) {
//            room.when(() -> Room.databaseBuilder(application, ClientDatabase.class, "reg-client"))
//                    .thenReturn(builder);
//            file.when(() -> File.createTempFile("sqlcipherutils", "tmp", new File("/cache")))
//                    .thenReturn(tempFile);
//            sqlite.when(() -> SQLiteDatabase.openDatabase(anyString(), anyString(), any(), anyInt()))
//                    .thenReturn(existingDb);
//            esp.when(() -> EncryptedSharedPreferences.create(
//                            any(Context.class), anyString(), any(MasterKey.class),
//                            any(EncryptedSharedPreferences.PrefKeyEncryptionScheme.class),
//                            any(EncryptedSharedPreferences.PrefValueEncryptionScheme.class)))
//                    .thenReturn(sharedPreferences);
//
//            when(context.getDatabasePath("reg-client")).thenReturn(dbFile);
//            when(sharedPreferences.edit()).thenReturn(editor);
//            when(editor.putString(anyString(), anyString())).thenReturn(editor);
//            when(builder.openHelperFactory(any(SupportFactory.class))).thenReturn(builder);
//            when(builder.allowMainThreadQueries()).thenReturn(builder);
//            when(builder.build()).thenReturn(clientDatabase);
//            doNothing().when(existingDb).rawExecSQL(anyString());
//            when(tempFile.renameTo(dbFile)).thenReturn(true);
//
//            // Act
//            RoomModule module = new RoomModule(application, applicationInfo);
//
//            // Assert
//            verify(context, times(2)).getDatabasePath("reg-client"); // Adjust for expected calls
//            verify(existingDb).rawExecSQL(anyString());
//            verify(tempFile).renameTo(dbFile);
//        }
//    }

//    @Test
//    public void testGetEncryptedSharedPreferences_Success() throws Exception {
//        // Arrange
//        MasterKey.Builder masterKeyBuilder = mock(MasterKey.Builder.class);
//        when(masterKeyBuilder.setKeyScheme(MasterKey.KeyScheme.AES256_GCM)).thenReturn(masterKeyBuilder);
//        when(masterKeyBuilder.build()).thenReturn(masterKey);
//        try (MockedStatic<EncryptedSharedPreferences> esp = org.mockito.Mockito.mockStatic(EncryptedSharedPreferences.class)) {
//            esp.when(() -> EncryptedSharedPreferences.create(
//                            context, "Android Registration Client", masterKey,
//                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM))
//                    .thenReturn(sharedPreferences);
//
//            // Use reflection to invoke private method
//            Method method = RoomModule.class.getDeclaredMethod("getEncryptedSharedPreferences", Context.class);
//            method.setAccessible(true);
//
//            // Act
//            SharedPreferences result = (SharedPreferences) method.invoke(roomModule, context);
//
//            // Assert
//            assertEquals(sharedPreferences, result);
//        }
//    }

    @Test
    public void testProvideObjectMapper_ReturnsInstance() {
        // Act
        ObjectMapper result = roomModule.provideObjectMapper();

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvidesRoomDatabase_ReturnsDatabase() {
        // Act
        ClientDatabase result = roomModule.providesRoomDatabase();

        // Assert
        assertEquals(clientDatabase, result);
    }

    @Test
    public void testProvidesRegistrationDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.registrationDao()).thenReturn(registrationDao);

        // Act
        RegistrationDao result = roomModule.providesRegistrationDao(clientDatabase);

        // Assert
        assertEquals(registrationDao, result);
    }

    @Test
    public void testProvidesReasonListDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.reasonListDao()).thenReturn(reasonListDao);

        // Act
        ReasonListDao result = roomModule.providesReasonListDao(clientDatabase);

        // Assert
        assertEquals(reasonListDao, result);
    }

    @Test
    public void testProvidesUserTokenDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.userTokenDao()).thenReturn(userTokenDao);

        // Act
        UserTokenDao result = roomModule.providesUserTokenDao(clientDatabase);

        // Assert
        assertEquals(userTokenDao, result);
    }

    @Test
    public void testProvidesRegistrationCenterDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.registrationCenterDao()).thenReturn(registrationCenterDao);

        // Act
        RegistrationCenterDao result = roomModule.providesRegistrationCenterDao(clientDatabase);

        // Assert
        assertEquals(registrationCenterDao, result);
    }

    @Test
    public void testProvidesMachineMasterDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.machineMasterDao()).thenReturn(machineMasterDao);

        // Act
        MachineMasterDao result = roomModule.providesMachineMasterDao(clientDatabase);

        // Assert
        assertEquals(machineMasterDao, result);
    }

    @Test
    public void testProvidesDocumentTypeDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.documentTypeDao()).thenReturn(documentTypeDao);

        // Act
        DocumentTypeDao result = roomModule.providesDocumentTypeDao(clientDatabase);

        // Assert
        assertEquals(documentTypeDao, result);
    }

    @Test
    public void testProvidesApplicantValidDocumentDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.applicantValidDocumentDao()).thenReturn(applicantValidDocumentDao);

        // Act
        ApplicantValidDocumentDao result = roomModule.providesApplicantValidDocumentDao(clientDatabase);

        // Assert
        assertEquals(applicantValidDocumentDao, result);
    }

    @Test
    public void testProvidesDynamicFieldDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.dynamicFieldDao()).thenReturn(dynamicFieldDao);

        // Act
        DynamicFieldDao result = roomModule.providesDynamicFieldDao(clientDatabase);

        // Assert
        assertEquals(dynamicFieldDao, result);
    }

    @Test
    public void testProvidesTemplateDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.templateDao()).thenReturn(templateDao);

        // Act
        TemplateDao result = roomModule.providesTemplateDao(clientDatabase);

        // Assert
        assertEquals(templateDao, result);
    }

    @Test
    public void testProvidesKeyStoreDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.keyStoreDao()).thenReturn(keyStoreDao);

        // Act
        KeyStoreDao result = roomModule.providesKeyStoreDao(clientDatabase);

        // Assert
        assertEquals(keyStoreDao, result);
    }

    @Test
    public void testProvidesLocationDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.locationDao()).thenReturn(locationDao);

        // Act
        LocationDao result = roomModule.providesLocationDao(clientDatabase);

        // Assert
        assertEquals(locationDao, result);
    }

    @Test
    public void testProvidesGlobalParamDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.globalParamDao()).thenReturn(globalParamDao);

        // Act
        GlobalParamDao result = roomModule.providesGlobalParamDao(clientDatabase);

        // Assert
        assertEquals(globalParamDao, result);
    }

    @Test
    public void testProvidesIdentitySchemaDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.identitySchemaDao()).thenReturn(identitySchemaDao);

        // Act
        IdentitySchemaDao result = roomModule.providesIdentitySchemaDao(clientDatabase);

        // Assert
        assertEquals(identitySchemaDao, result);
    }

    @Test
    public void testProvidesLocationHierarchyDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.locationHierarchyDao()).thenReturn(locationHierarchyDao);

        // Act
        LocationHierarchyDao result = roomModule.providesLocationHierarchyDao(clientDatabase);

        // Assert
        assertEquals(locationHierarchyDao, result);
    }

    @Test
    public void testProvidesBlocklistedWordDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.blocklistedWordDao()).thenReturn(blocklistedWordDao);

        // Act
        BlocklistedWordDao result = roomModule.providesBlocklistedWordDao(clientDatabase);

        // Assert
        assertEquals(blocklistedWordDao, result);
    }

    @Test
    public void testProvidesSyncJobDefDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.syncJobDefDao()).thenReturn(syncJobDefDao);

        // Act
        SyncJobDefDao result = roomModule.providesSyncJobDefDao(clientDatabase);

        // Assert
        assertEquals(syncJobDefDao, result);
    }

    @Test
    public void testProvidesUserDetailDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.userDetailDao()).thenReturn(userDetailDao);

        // Act
        UserDetailDao result = roomModule.providesUserDetailDao(clientDatabase);

        // Assert
        assertEquals(userDetailDao, result);
    }

    @Test
    public void testProvidesUserBiometricDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.userBiometricDao()).thenReturn(userBiometricDao);

        // Act
        UserBiometricDao result = roomModule.providesUserBiometricDao(clientDatabase);

        // Assert
        assertEquals(userBiometricDao, result);
    }

    @Test
    public void testProvidesUserPasswordDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.userPasswordDao()).thenReturn(userPasswordDao);

        // Act
        UserPasswordDao result = roomModule.providesUserPasswordDao(clientDatabase);

        // Assert
        assertEquals(userPasswordDao, result);
    }

    @Test
    public void testProvidesJobTransactionDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.jobTransactionDao()).thenReturn(jobTransactionDao);

        // Act
        JobTransactionDao result = roomModule.providesJobTransactionDao(clientDatabase);

        // Assert
        assertEquals(jobTransactionDao, result);
    }

    @Test
    public void testProvidesCACertificateStoreDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.caCertificateStoreDao()).thenReturn(caCertificateStoreDao);

        // Act
        CACertificateStoreDao result = roomModule.providesCACertificateStoreDao(clientDatabase);

        // Assert
        assertEquals(caCertificateStoreDao, result);
    }

    @Test
    public void testProvidesLanguageDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.languageDao()).thenReturn(languageDao);

        // Act
        LanguageDao result = roomModule.providesLanguageDao(clientDatabase);

        // Assert
        assertEquals(languageDao, result);
    }

    @Test
    public void testProvidesAuditDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.auditDao()).thenReturn(auditDao);

        // Act
        AuditDao result = roomModule.providesAuditDao(clientDatabase);

        // Assert
        assertEquals(auditDao, result);
    }

    @Test
    public void testProvidesFileSignatureDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.fileSignatureDao()).thenReturn(fileSignatureDao);

        // Act
        FileSignatureDao result = roomModule.providesFileSignatureDao(clientDatabase);

        // Assert
        assertEquals(fileSignatureDao, result);
    }

    @Test
    public void testProvidesProcessSpecDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.processSpecDao()).thenReturn(processSpecDao);

        // Act
        ProcessSpecDao result = roomModule.providesProcessSpecDao(clientDatabase);

        // Assert
        assertEquals(processSpecDao, result);
    }

    @Test
    public void testProvidesPreRegistrationDataSyncRepositoryDao_ReturnsDao() {
        // Arrange
        when(clientDatabase.preRegistrationDataSyncRepositoryDao()).thenReturn(preRegistrationDataSyncRepositoryDao);

        // Act
        PreRegistrationDataSyncRepositoryDao result = roomModule.providesPreRegistrationDataSyncRepository(clientDatabase);

        // Assert
        assertEquals(preRegistrationDataSyncRepositoryDao, result);
    }

    @Test
    public void testProvideRegistrationRepository_ReturnsRepository() {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();

        // Act
        RegistrationRepository result = roomModule.provideRegistrationRepository(registrationDao, objectMapper);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideRegistrationCenterRepository_ReturnsRepository() {
        // Act
        RegistrationCenterRepository result = roomModule.provideRegistrationCenterRepository(registrationCenterDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideMachineRepository_ReturnsRepository() {
        // Act
        MachineRepository result = roomModule.provideMachineRepository(machineMasterDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideDocumentTypeRepository_ReturnsRepository() {
        // Act
        DocumentTypeRepository result = roomModule.provideDocumentTypeRepository(documentTypeDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideApplicantValidDocRepository_ReturnsRepository() {
        // Act
        ApplicantValidDocRepository result = roomModule.provideApplicantValidDocRepository(applicantValidDocumentDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideTemplateRepository_ReturnsRepository() {
        // Act
        TemplateRepository result = roomModule.provideTemplateRepository(templateDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideDynamicFieldRepository_ReturnsRepository() {
        // Act
        DynamicFieldRepository result = roomModule.provideDynamicFieldRepository(dynamicFieldDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideKeyStoreRepository_ReturnsRepository() {
        // Act
        KeyStoreRepository result = roomModule.provideKeyStoreRepository(keyStoreDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideLocationRepository_ReturnsRepository() {
        // Act
        LocationRepository result = roomModule.provideLocationRepository(locationDao, locationHierarchyDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideGlobalParamRepository_ReturnsRepository() {
        // Act
        GlobalParamRepository result = roomModule.provideGlobalParamRepository(globalParamDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideIdentitySchemaRepository_ReturnsRepository() {
        // Arrange
        TemplateRepository templateRepo = new TemplateRepository(templateDao);
        GlobalParamRepository globalParamRepo = new GlobalParamRepository(globalParamDao);

        // Act
        IdentitySchemaRepository result = roomModule.provideIdentitySchemaRepository(templateRepo, globalParamRepo, identitySchemaDao, processSpecDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideBlocklistedWordRepository_ReturnsRepository() {
        // Act
        BlocklistedWordRepository result = roomModule.provideBlocklistedWordRepository(blocklistedWordDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideSyncJobDefRepository_ReturnsRepository() {
        // Act
        SyncJobDefRepository result = roomModule.provideSyncJobDefRepository(syncJobDefDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideUserDetailRepository_ReturnsRepository() {
        // Act
        UserDetailRepository result = roomModule.provideUserDetailRepository(userDetailDao, userTokenDao, userPasswordDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideUserBiometricRepository_ReturnsRepository() {
        // Act
        UserBiometricRepository result = roomModule.provideUserBiometricRepository(userBiometricDao, userDetailDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideJobTransactionRepository_ReturnsRepository() {
        // Act
        JobTransactionRepository result = roomModule.provideJobTransactionRepository(jobTransactionDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideCACertificateStoreRepository_ReturnsRepository() {
        // Act
        CACertificateStoreRepository result = roomModule.provideCACertificateStoreRepository(caCertificateStoreDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideLanguageRepository_ReturnsRepository() {
        // Act
        LanguageRepository result = roomModule.provideLanguageRepository(languageDao);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testProvideAuditRepository_ReturnsRepository() {
        // Act
        AuditRepository result = roomModule.provideAuditRepository(auditDao);

        // Assert
        assertNotNull(result);
    }
}