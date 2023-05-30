package io.mosip.registration.clientmanager.config;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.dao.AuditDao;
import io.mosip.registration.clientmanager.dao.BlocklistedWordDao;
import io.mosip.registration.clientmanager.dao.DocumentTypeDao;
import io.mosip.registration.clientmanager.dao.DynamicFieldDao;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.dao.IdentitySchemaDao;
import io.mosip.registration.clientmanager.dao.JobTransactionDao;
import io.mosip.registration.clientmanager.dao.LanguageDao;
import io.mosip.registration.clientmanager.dao.LocationDao;
import io.mosip.registration.clientmanager.dao.LocationHierarchyDao;
import io.mosip.registration.clientmanager.dao.MachineMasterDao;
import io.mosip.registration.clientmanager.dao.RegistrationCenterDao;
import io.mosip.registration.clientmanager.dao.RegistrationDao;
import io.mosip.registration.clientmanager.dao.SyncJobDefDao;
import io.mosip.registration.clientmanager.dao.TemplateDao;
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
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.keymanager.dao.CACertificateStoreDao;
import io.mosip.registration.keymanager.dao.KeyStoreDao;
import io.mosip.registration.keymanager.repository.CACertificateStoreRepository;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;


@Module
public class RoomModule {

    private static final String DATABASE_NAME = "reg-client";

    private ClientDatabase clientDatabase;

    public RoomModule(Application application) {
        clientDatabase = Room.databaseBuilder(application, ClientDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
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
    IdentitySchemaRepository provideIdentitySchemaRepository(IdentitySchemaDao identitySchemaDao) {
        return new IdentitySchemaRepository(identitySchemaDao);
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
