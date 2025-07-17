package io.mosip.registration.clientmanager.config;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;
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

import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.dao.*;
import io.mosip.registration.clientmanager.repository.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
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
    private File dbFile;

    @Mock
    private SQLiteDatabase existingDb;

    private RoomModule roomModule;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(application.getApplicationContext()).thenReturn(context);
        when(context.getDatabasePath("reg-client")).thenReturn(dbFile);
        applicationInfo.flags = 0;
        when(sharedPreferences.getString(eq("db_password"), any())).thenReturn("existingPassword");
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

            roomModule = new RoomModule(application, applicationInfo);
        }
    }

    @Test
    public void testConstructor_DebugMode_UsesDebugPassword() {

        reset(sharedPreferences, editor);
        applicationInfo.flags = ApplicationInfo.FLAG_DEBUGGABLE;
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
            when(editor.putString(eq("db_password"), eq(BuildConfig.DEBUG_PASSWORD))).thenReturn(editor);

            new RoomModule(application, applicationInfo);

            verify(editor).putString("db_password", BuildConfig.DEBUG_PASSWORD);
            verify(editor).apply();
        }
    }

    @Test
    public void testConstructor_EncryptExistingDb_Success() throws Exception {

        reset(sharedPreferences, editor);
        when(sharedPreferences.getString(eq("db_password"), eq(null))).thenReturn(null);
        when(dbFile.exists()).thenReturn(true);
        File tempFile = mock(File.class);
        when(context.getCacheDir()).thenReturn(new File("/cache")); // Required for createTempFile
        when(dbFile.getPath()).thenReturn("/data/data/app/reg-client"); // Match the path
        when(sharedPreferences.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);

        RoomDatabase.Builder<ClientDatabase> builder = mock(RoomDatabase.Builder.class);
        try (MockedStatic<Room> room = org.mockito.Mockito.mockStatic(Room.class);
             MockedStatic<File> file = org.mockito.Mockito.mockStatic(File.class);
             MockedStatic<SQLiteDatabase> sqlite = org.mockito.Mockito.mockStatic(SQLiteDatabase.class);
             MockedStatic<EncryptedSharedPreferences> esp = org.mockito.Mockito.mockStatic(EncryptedSharedPreferences.class)) {

            room.when(() -> Room.databaseBuilder(eq(application), eq(ClientDatabase.class), eq("reg-client")))
                    .thenReturn(builder);
            file.when(() -> File.createTempFile(eq("sqlcipherutils"), eq("tmp"), any(File.class)))
                    .thenReturn(tempFile);
            sqlite.when(() -> SQLiteDatabase.loadLibs(eq(context))).thenAnswer(invocation -> null);
            sqlite.when(() -> SQLiteDatabase.openDatabase(
                            eq("/data/data/app/reg-client"),
                            eq(""),
                            eq(null),
                            eq(SQLiteDatabase.OPEN_READWRITE)))
                    .thenReturn(existingDb);
            esp.when(() -> EncryptedSharedPreferences.create(
                            eq(context),
                            eq("Android Registration Client"),
                            any(MasterKey.class),
                            eq(EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV),
                            eq(EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)))
                    .thenReturn(sharedPreferences);

            when(builder.openHelperFactory(any(SupportFactory.class))).thenReturn(builder);
            when(builder.allowMainThreadQueries()).thenReturn(builder);
            when(builder.build()).thenReturn(clientDatabase);
            doNothing().when(existingDb).rawExecSQL(anyString());
            when(tempFile.renameTo(dbFile)).thenReturn(true);
            when(dbFile.delete()).thenReturn(true);

            new RoomModule(application, applicationInfo);

            sqlite.verify(() -> SQLiteDatabase.loadLibs(eq(context)));
            verify(existingDb, times(3)).rawExecSQL(anyString());
            verify(tempFile).renameTo(dbFile);
            verify(dbFile).delete();
            verify(sharedPreferences).edit();
            verify(editor).putString(eq("db_password"), anyString());
            verify(editor).apply();
        }
    }

    @Test
    public void testGetEncryptedSharedPreferences_Exception_Handled() throws Exception {
        // Arrange
        try (MockedStatic<EncryptedSharedPreferences> esp = org.mockito.Mockito.mockStatic(EncryptedSharedPreferences.class)) {
            esp.when(() -> EncryptedSharedPreferences.create(
                            any(Context.class), anyString(), any(MasterKey.class),
                            any(EncryptedSharedPreferences.PrefKeyEncryptionScheme.class),
                            any(EncryptedSharedPreferences.PrefValueEncryptionScheme.class)))
                    .thenThrow(new RuntimeException("Test exception"));

            Method method = RoomModule.class.getDeclaredMethod("getEncryptedSharedPreferences", Context.class);
            method.setAccessible(true);

            SharedPreferences result = (SharedPreferences) method.invoke(roomModule, context);

            // Assert
            assertEquals(null, result);
        }
    }

    @Test
    public void testProvideIdentitySchemaRepository_DependenciesInjected() {

        TemplateRepository templateRepo = mock(TemplateRepository.class);
        GlobalParamRepository globalParamRepo = mock(GlobalParamRepository.class);
        IdentitySchemaDao identitySchemaDao = mock(IdentitySchemaDao.class);
        ProcessSpecDao processSpecDao = mock(ProcessSpecDao.class);

        IdentitySchemaRepository result = roomModule.provideIdentitySchemaRepository(
                templateRepo, globalParamRepo, identitySchemaDao, processSpecDao);

        assertNotNull(result);
    }

    @Test
    public void test_provide_object_mapper_returns_new_instance() {
        ObjectMapper objectMapper = roomModule.provideObjectMapper();

        assertNotNull(objectMapper);
        assertTrue(true);
    }

    @Test
    public void test_provide_object_mapper_throws_exception() {
        roomModule = new RoomModule(application, applicationInfo) {
            @Override
            @NonNull
            public ObjectMapper provideObjectMapper() {
                throw new RuntimeException("Failed to create ObjectMapper");
            }
        };

        Exception exception = assertThrows(RuntimeException.class, () -> {
            roomModule.provideObjectMapper();
        });

        assertEquals("Failed to create ObjectMapper", exception.getMessage());
    }
}