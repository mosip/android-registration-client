package io.mosip.registration.clientmanager.service;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncDao;
import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncRepositoryDao;
import io.mosip.registration.clientmanager.entity.PreRegistrationList;

/**
 * {@link PreRegistrationDataSyncDao}
 *
 * @author YASWANTH S
 * @since 1.0.0
 *
 */

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

    public PreRegistrationList getById(long id){
        return this.preRegistrationRepositoryDao.getById(id);
    }


    public long save(PreRegistrationList preRegistration) {

        Log.i(TAG,"REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL"+
                "Saving Pre-Registration");

        return this.preRegistrationRepositoryDao.save(preRegistration);
    }

    public List<PreRegistrationList> fetchRecordsToBeDeleted(String startDate) {

        Log.i(TAG,"REGISTRATION - PRE_REGISTRATION_DATA_SYNC_RECORD_FETCH - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL"+
                "Fetch Records that needs to be deleted");

        return this.preRegistrationRepositoryDao.findByAppointmentDateBeforeAndIsDeleted(startDate, false);
    }

//    public long update(PreRegistrationList preReg) {
//
//        Log.i(TAG,"REGISTRATION - PRE_REGISTRATION_DATA_SYNC_RECORD_UPDATE - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL"
//                +"Update the deleted records");
//
//        return this.preRegistrationRepositoryDao.update(preReg);
//
//    }


//    public void deleteAll(List<PreRegistrationList> preRegistrationLists) {
//        Log.i(TAG,"REGISTRATION - PRE_REGISTRATION_DATA_SYNC_RECORD_UPDATE - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL"+
//               "Delete records started");
//        /* Parase List to Iterable */
//        Iterable<PreRegistrationList> iterablePreRegistrationList = preRegistrationLists;
//
//        this.preRegistrationRepositoryDao.deleteInBatch(iterablePreRegistrationList);
//        Log.i(TAG,"REGISTRATION - PRE_REGISTRATION_DATA_SYNC_RECORD_UPDATE - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL"+
//           "delete records ended");
//
//    }


//    public List<PreRegistrationList> getAllPreRegPackets() {
//
//        return this.preRegistrationRepositoryDao.findAll();
//    }


    public String getLastPreRegPacketDownloadedTime() {
        Log.i(TAG,"REGISTRATION - PRE_REGISTRATION_GET_DOWNLOADED_TIME - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL"+
              "Delete records started");
        PreRegistrationList preRegistrationList = this.preRegistrationRepositoryDao
                .findTopByOrderByLastUpdatedPreRegTimeStampDesc();
        return preRegistrationList != null ? preRegistrationList.getLastUpdatedPreRegTimeStamp() : null;
    }
}
