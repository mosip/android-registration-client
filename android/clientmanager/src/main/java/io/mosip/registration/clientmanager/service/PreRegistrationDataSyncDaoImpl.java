package io.mosip.registration.clientmanager.service;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncDao;
import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncRepositoryDao;
import io.mosip.registration.clientmanager.entity.PreRegistrationList;


public class PreRegistrationDataSyncDaoImpl implements PreRegistrationDataSyncDao {


    private PreRegistrationDataSyncRepositoryDao preRegistrationRepositoryDao;

    private static final String TAG = PreRegistrationDataSyncDaoImpl.class.getSimpleName();

    @Inject
    public PreRegistrationDataSyncDaoImpl(PreRegistrationDataSyncRepositoryDao preRegistrationRepositoryDao){
        this.preRegistrationRepositoryDao = preRegistrationRepositoryDao;
    }


    public PreRegistrationList get(String preRegId) {

        Log.i(TAG,"REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL"+
                "Fetching Pre-Registration");

        return this.preRegistrationRepositoryDao.findByPreRegId(preRegId);

    }

    public PreRegistrationList getById(String id){
        return this.preRegistrationRepositoryDao.getById(id);
    }


    public void save(PreRegistrationList preRegistration) {

        Log.i(TAG,"REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL"+
                "Saving Pre-Registration");

        this.preRegistrationRepositoryDao.insert(preRegistration);
    }

    public List<PreRegistrationList> fetchRecordsToBeDeleted(String startDate) {

        Log.i(TAG,"REGISTRATION - PRE_REGISTRATION_DATA_SYNC_RECORD_FETCH - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL"+
                "Fetch Records that needs to be deleted");

        return this.preRegistrationRepositoryDao.findByAppointmentDateBeforeAndIsDeleted(startDate, false);
    }

    public long update(String id,String updatedBy,String updatedTime) {

        Log.i(TAG,"REGISTRATION - PRE_REGISTRATION_DATA_SYNC_RECORD_UPDATE - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL"
                +"Update the deleted records");

        return this.preRegistrationRepositoryDao.update(id, updatedBy, updatedTime);

    }

    public String getLastPreRegPacketDownloadedTime() {
        Log.i(TAG,"REGISTRATION - PRE_REGISTRATION_GET_DOWNLOADED_TIME - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL"+
              "Delete records started");
        PreRegistrationList preRegistrationList = this.preRegistrationRepositoryDao
                .findTopByOrderByLastUpdatedPreRegTimeStampDesc();
        return preRegistrationList != null ? preRegistrationList.getLastUpdatedPreRegTimeStamp() : null;
    }
}
