package io.mosip.registration.clientmanager.config;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import io.mosip.registration.clientmanager.dao.*;
import io.mosip.registration.clientmanager.entity.*;
import io.mosip.registration.keymanager.dao.CACertificateStoreDao;
import io.mosip.registration.keymanager.dao.KeyStoreDao;
import io.mosip.registration.keymanager.entity.CACertificateStore;
import io.mosip.registration.keymanager.entity.KeyStore;

@Database(entities = {UserToken.class, Registration.class, RegistrationCenter.class,
        MachineMaster.class, DocumentType.class, DynamicField.class,
        ApplicantValidDocument.class, Template.class, KeyStore.class,
        Location.class, GlobalParam.class, IdentitySchema.class, LocationHierarchy.class,
        BlocklistedWord.class, SyncJobDef.class, UserDetail.class, JobTransaction.class, CACertificateStore.class},
        version = 1, exportSchema = false)
public abstract class ClientDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "regclient";
    private static ClientDatabase INSTANCE;

    public static ClientDatabase getDatabase(Context context){
        if(INSTANCE == null) {
            synchronized ("") {
                INSTANCE = Room.databaseBuilder(context, ClientDatabase.class, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
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
    public abstract JobTransactionDao jobTransactionDao();
    public abstract CACertificateStoreDao caCertificateStoreDao();

    public static void destroyDB(){
        INSTANCE=null;
    }
}


