package io.mosip.registration.clientmanager.config;

import android.app.Application;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import io.mosip.registration.clientmanager.dao.*;
import io.mosip.registration.clientmanager.repository.*;
import io.mosip.registration.keymanager.dao.KeyStoreDao;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;

import javax.inject.Singleton;


@Module
public class RoomModule {

    private static final String DATABASE_NAME = "reg-client";

    private ClientDatabase clientDatabase;

    public RoomModule(Application application) {
        clientDatabase = Room.databaseBuilder(application, ClientDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
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

    @Provides
    @Singleton
    RegistrationRepository provideRegistrationRepository(RegistrationDao registrationDao) {
        return new RegistrationRepository(registrationDao);
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
}
