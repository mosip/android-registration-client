package io.mosip.registration.clientmanager.config;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

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
import io.mosip.registration.clientmanager.entity.ApplicantValidDocument;
import io.mosip.registration.clientmanager.entity.Audit;
import io.mosip.registration.clientmanager.entity.BlocklistedWord;
import io.mosip.registration.clientmanager.entity.DocumentType;
import io.mosip.registration.clientmanager.entity.DynamicField;
import io.mosip.registration.clientmanager.entity.GlobalParam;
import io.mosip.registration.clientmanager.entity.IdentitySchema;
import io.mosip.registration.clientmanager.entity.JobTransaction;
import io.mosip.registration.clientmanager.entity.Language;
import io.mosip.registration.clientmanager.entity.Location;
import io.mosip.registration.clientmanager.entity.LocationHierarchy;
import io.mosip.registration.clientmanager.entity.MachineMaster;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.entity.Template;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.clientmanager.entity.UserPassword;
import io.mosip.registration.clientmanager.entity.UserToken;
import io.mosip.registration.keymanager.dao.CACertificateStoreDao;
import io.mosip.registration.keymanager.dao.KeyStoreDao;
import io.mosip.registration.keymanager.entity.CACertificateStore;
import io.mosip.registration.keymanager.entity.KeyStore;

@Database(entities = {UserToken.class, Registration.class, RegistrationCenter.class,
        MachineMaster.class, DocumentType.class, DynamicField.class,
        ApplicantValidDocument.class, Template.class, KeyStore.class,
        Location.class, GlobalParam.class, IdentitySchema.class, LocationHierarchy.class,
        BlocklistedWord.class, SyncJobDef.class, UserDetail.class, UserPassword.class, JobTransaction.class,
        CACertificateStore.class, Language.class, Audit.class},
        version = 1, exportSchema = false)
public abstract class ClientDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "regclient";
    private static ClientDatabase INSTANCE;

    public synchronized static ClientDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    public static ClientDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, ClientDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public abstract UserTokenDao userTokenDao();

    public abstract RegistrationDao registrationDao();

    public abstract RegistrationCenterDao registrationCenterDao();

    public abstract MachineMasterDao machineMasterDao();

    public abstract DocumentTypeDao documentTypeDao();

    public abstract ApplicantValidDocumentDao applicantValidDocumentDao();

    public abstract DynamicFieldDao dynamicFieldDao();

    public abstract TemplateDao templateDao();

    public abstract KeyStoreDao keyStoreDao();

    public abstract LocationDao locationDao();

    public abstract GlobalParamDao globalParamDao();

    public abstract IdentitySchemaDao identitySchemaDao();

    public abstract LocationHierarchyDao locationHierarchyDao();

    public abstract BlocklistedWordDao blocklistedWordDao();

    public abstract SyncJobDefDao syncJobDefDao();

    public abstract UserDetailDao userDetailDao();

    public abstract UserPasswordDao userPasswordDao();

    public abstract JobTransactionDao jobTransactionDao();

    public abstract CACertificateStoreDao caCertificateStoreDao();

    public abstract LanguageDao languageDao();

    public abstract AuditDao auditDao();

    public static void destroyDB() {
        INSTANCE = null;
    }
}


