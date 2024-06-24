package io.mosip.registration.clientmanager.config;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.security.SecureRandom;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.BuildConfig;
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
import io.mosip.registration.clientmanager.dao.ProcessSpecDao;
import io.mosip.registration.clientmanager.dao.RegistrationCenterDao;
import io.mosip.registration.clientmanager.dao.RegistrationDao;
import io.mosip.registration.clientmanager.dao.SyncJobDefDao;
import io.mosip.registration.clientmanager.dao.TemplateDao;
import io.mosip.registration.clientmanager.dao.UserBiometricDao;
import io.mosip.registration.clientmanager.dao.UserDetailDao;
import io.mosip.registration.clientmanager.dao.UserPasswordDao;
import io.mosip.registration.clientmanager.dao.UserTokenDao;
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
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.repository.TemplateRepository;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.keymanager.dao.CACertificateStoreDao;
import io.mosip.registration.keymanager.dao.KeyStoreDao;
import io.mosip.registration.keymanager.repository.CACertificateStoreRepository;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;


@Module
public class RoomModule {

    private static final String TAG = RoomModule.class.getSimpleName();
    private static final String DATABASE_NAME = "reg-client";
    private static final String DB_PWD_KEY = "db_password";
    private static final SecureRandom secureRandom = new SecureRandom();

    private ClientDatabase clientDatabase;

    public RoomModule(Application application, ApplicationInfo applicationInfo) {
        Context context = application.getApplicationContext();
        try {
            boolean isDebug = (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            String dbPwd = getEncryptedSharedPreferences(context).getString(DB_PWD_KEY, null);
            Log.i(TAG, "Db password found in sharedPreferences.");
            boolean dbExists = context.getDatabasePath(DATABASE_NAME).exists();
            if (dbPwd == null) {
                Log.i(TAG, "Db password found null. Creating new pwd and storing in SharedPreferences.");
                dbPwd = isDebug ? BuildConfig.DEBUG_PASSWORD : RandomStringUtils.random(20, 0, 0, true, true, null, secureRandom);
                getEncryptedSharedPreferences(context).edit()
                        .putString(DB_PWD_KEY, dbPwd)
                        .apply();
                if (dbExists) {
                    Log.i(TAG, "Db already exists, encrypting existing DB");
                    encryptExistingDb(context, dbPwd);
                }
            }
            clientDatabase = Room.databaseBuilder(application, ClientDatabase.class, DATABASE_NAME)
                    .openHelperFactory(new SupportFactory(dbPwd.getBytes()))
                    .allowMainThreadQueries()
                    .build();
        } catch (Exception e) {
            Log.e(TAG, "Failed initializing the database", e);
        }
    }

    private SharedPreferences getEncryptedSharedPreferences(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "Android Registration Client",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            return sharedPreferences;
        } catch (Exception e) {
            Log.e(TAG, "Error on getting encrypted shared preferences", e);
        }
        return null;
    }

    private void encryptExistingDb(Context context, String passphrase) {
        try {
            SQLiteDatabase.loadLibs(context);
            File originalFile = context.getDatabasePath(DATABASE_NAME);
            File newFile = File.createTempFile("sqlcipherutils", "tmp", context.getCacheDir());
            SQLiteDatabase existing_db = SQLiteDatabase.openDatabase(context.getDatabasePath(DATABASE_NAME).getPath(), "", null, SQLiteDatabase.OPEN_READWRITE);
            existing_db.rawExecSQL("ATTACH DATABASE '" + newFile.getPath() + "' AS encrypted KEY '" + passphrase + "';");
            existing_db.rawExecSQL("SELECT sqlcipher_export('encrypted');");
            existing_db.rawExecSQL("DETACH DATABASE encrypted;");
            existing_db.close();
            originalFile.delete();
            newFile.renameTo(originalFile);
            Log.i(TAG, "Successfully encrypted the existing DB");
        } catch (Exception e) {
            Log.e(TAG, "Failed encrypting the existing database", e);
        }
    }

    @Provides
    @NonNull
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    @Singleton
    @Provides
    ClientDatabase providesRoomDatabase() {
        return clientDatabase;
    }

    @Singleton
    @Provides
    RegistrationDao providesRegistrationDao(ClientDatabase clientDatabase) {
        return clientDatabase.registrationDao();
    }

    @Singleton
    @Provides
    UserTokenDao providesUserTokenDao(ClientDatabase clientDatabase) {
        return clientDatabase.userTokenDao();
    }

    @Singleton
    @Provides
    RegistrationCenterDao providesRegistrationCenterDao(ClientDatabase clientDatabase) {
        return clientDatabase.registrationCenterDao();
    }

    @Singleton
    @Provides
    MachineMasterDao providesMachineMasterDao(ClientDatabase clientDatabase) {
        return clientDatabase.machineMasterDao();
    }

    @Singleton
    @Provides
    DocumentTypeDao providesDocumentTypeDao(ClientDatabase clientDatabase) {
        return clientDatabase.documentTypeDao();
    }

    @Singleton
    @Provides
    ApplicantValidDocumentDao providesApplicantValidDocumentDao(ClientDatabase clientDatabase) {
        return clientDatabase.applicantValidDocumentDao();
    }

    @Singleton
    @Provides
    DynamicFieldDao providesDynamicFieldDao(ClientDatabase clientDatabase) {
        return clientDatabase.dynamicFieldDao();
    }

    @Singleton
    @Provides
    TemplateDao providesTemplateDao(ClientDatabase clientDatabase) {
        return clientDatabase.templateDao();
    }

    @Singleton
    @Provides
    KeyStoreDao providesKeyStoreDao(ClientDatabase clientDatabase) {
        return clientDatabase.keyStoreDao();
    }

    @Singleton
    @Provides
    LocationDao providesLocationDao(ClientDatabase clientDatabase) {
        return clientDatabase.locationDao();
    }

    @Singleton
    @Provides
    GlobalParamDao providesGlobalParamDao(ClientDatabase clientDatabase) {
        return clientDatabase.globalParamDao();
    }

    @Singleton
    @Provides
    IdentitySchemaDao providesIdentitySchemaDao(ClientDatabase clientDatabase) {
        return clientDatabase.identitySchemaDao();
    }

    @Singleton
    @Provides
    LocationHierarchyDao providesLocationHierarchyDao(ClientDatabase clientDatabase) {
        return clientDatabase.locationHierarchyDao();
    }

    @Singleton
    @Provides
    BlocklistedWordDao providesBlocklistedWordDao(ClientDatabase clientDatabase) {
        return clientDatabase.blocklistedWordDao();
    }

    @Singleton
    @Provides
    SyncJobDefDao providesSyncJobDefDao(ClientDatabase clientDatabase) {
        return clientDatabase.syncJobDefDao();
    }

    @Singleton
    @Provides
    UserDetailDao providesUserDetailDao(ClientDatabase clientDatabase) {
        return clientDatabase.userDetailDao();
    }

    @Singleton
    @Provides
    UserBiometricDao providesUserBiometricDao(ClientDatabase clientDatabase) {
        return clientDatabase.userBiometricDao();
    }

    @Singleton
    @Provides
    UserPasswordDao providesUserPasswordDao(ClientDatabase clientDatabase) {
        return clientDatabase.userPasswordDao();
    }

    @Singleton
    @Provides
    JobTransactionDao providesJobTransactionDao(ClientDatabase clientDatabase) {
        return clientDatabase.jobTransactionDao();
    }

    @Singleton
    @Provides
    CACertificateStoreDao providesCACertificateStoreDao(ClientDatabase clientDatabase) {
        return clientDatabase.caCertificateStoreDao();
    }

    @Singleton
    @Provides
    LanguageDao providesLanguageDao(ClientDatabase clientDatabase) {
        return clientDatabase.languageDao();
    }

    @Singleton
    @Provides
    AuditDao providesAuditDao(ClientDatabase clientDatabase) {
        return clientDatabase.auditDao();
    }

    @Singleton
    @Provides
    FileSignatureDao providesFileSignatureDao(ClientDatabase clientDatabase) {
        return clientDatabase.fileSignatureDao();
    }

    @Singleton
    @Provides
    ProcessSpecDao providesProcessSpecDao(ClientDatabase clientDatabase) {
        return clientDatabase.processSpecDao();
    }

    @Provides
    @Singleton
    RegistrationRepository provideRegistrationRepository(RegistrationDao registrationDao, ObjectMapper objectMapper) {
        return new RegistrationRepository(registrationDao, objectMapper);
    }

    @Provides
    @Singleton
    RegistrationCenterRepository provideRegistrationCenterRepository(RegistrationCenterDao registrationCenterDao) {
        return new RegistrationCenterRepository(registrationCenterDao);
    }

    @Provides
    @Singleton
    MachineRepository provideMachineRepository(MachineMasterDao machineMasterDao) {
        return new MachineRepository(machineMasterDao);
    }

    @Provides
    @Singleton
    DocumentTypeRepository provideDocumentTypeRepository(DocumentTypeDao documentTypeDao) {
        return new DocumentTypeRepository(documentTypeDao);
    }

    @Provides
    @Singleton
    ApplicantValidDocRepository provideApplicantValidDocRepository(ApplicantValidDocumentDao
                                                                   applicantValidDocumentDao) {
        return new ApplicantValidDocRepository(applicantValidDocumentDao);
    }

    @Provides
    @Singleton
    TemplateRepository provideTemplateRepository(TemplateDao templateDao) {
        return new TemplateRepository(templateDao);
    }

    @Provides
    @Singleton
    DynamicFieldRepository provideDynamicFieldRepository(DynamicFieldDao dynamicFieldDao) {
        return new DynamicFieldRepository(dynamicFieldDao);
    }

    @Provides
    @Singleton
    KeyStoreRepository provideKeyStoreRepository(KeyStoreDao keyStoreDao) {
        return new KeyStoreRepository(keyStoreDao);
    }

    @Provides
    @Singleton
    LocationRepository provideLocationRepository(LocationDao locationDao, LocationHierarchyDao locationHierarchyDao) {
        return new LocationRepository(locationDao, locationHierarchyDao);
    }

    @Provides
    @Singleton
    GlobalParamRepository provideGlobalParamRepository(GlobalParamDao globalParamDao) {
        return new GlobalParamRepository(globalParamDao);
    }

    @Provides
    @Singleton
    IdentitySchemaRepository provideIdentitySchemaRepository(TemplateRepository templateRepository, GlobalParamRepository globalParamRepository, IdentitySchemaDao identitySchemaDao, ProcessSpecDao processSpecDao) {
        return new IdentitySchemaRepository(templateRepository, globalParamRepository, identitySchemaDao, processSpecDao);
    }

    @Provides
    @Singleton
    BlocklistedWordRepository provideBlocklistedWordRepository(BlocklistedWordDao blocklistedWordDao) {
        return new BlocklistedWordRepository(blocklistedWordDao);
    }

    @Provides
    @Singleton
    SyncJobDefRepository provideSyncJobDefRepository(SyncJobDefDao syncJobDefDao) {
        return new SyncJobDefRepository(syncJobDefDao);
    }

    @Provides
    @Singleton
    UserDetailRepository provideUserDetailRepository(UserDetailDao userDetailDao, UserTokenDao userTokenDao,
                                                     UserPasswordDao userPasswordDao) {
        return new UserDetailRepository(userDetailDao, userTokenDao, userPasswordDao);
    }

    @Provides
    @Singleton
    UserBiometricRepository provideUserBiometricRepository(UserBiometricDao userBiometricDao, UserDetailDao userDetailDao) {
        return new UserBiometricRepository(userBiometricDao, userDetailDao);
    }

    @Provides
    @Singleton
    JobTransactionRepository provideJobTransactionRepository(JobTransactionDao jobTransactionDao) {
        return new JobTransactionRepository(jobTransactionDao);
    }

    @Provides
    @Singleton
    CACertificateStoreRepository provideCACertificateStoreRepository(CACertificateStoreDao caCertificateStoreDao) {
        return new CACertificateStoreRepository(caCertificateStoreDao);
    }

    @Provides
    @Singleton
    LanguageRepository provideLanguageRepository(LanguageDao languageDao) {
        return new LanguageRepository(languageDao);
    }


    @Provides
    @Singleton
    AuditRepository provideAuditRepository(AuditDao auditDao) {
        return new AuditRepository(auditDao);
    }
}
