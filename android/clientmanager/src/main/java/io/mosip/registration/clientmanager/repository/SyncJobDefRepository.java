package io.mosip.registration.clientmanager.repository;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.dao.SyncJobDefDao;
import io.mosip.registration.clientmanager.entity.SyncJobDef;

/**
 * DAO class for all the Sync Job related details
 *
 * @author Anshul vanawat
 */

public class SyncJobDefRepository {

    private final SyncJobDefDao syncJobDefDao;

    @Inject
    public SyncJobDefRepository(SyncJobDefDao syncJobDefDao) {
        this.syncJobDefDao = syncJobDefDao;
    }

    public void saveSyncJobDef(SyncJobDef syncJobDef) {
        syncJobDefDao.insert(syncJobDef);
    }

    public List<SyncJobDef> getAllSyncJobDefList() {
        return this.syncJobDefDao.findAll();
    }

    public List<SyncJobDef> getActiveSyncJobs() {
        List<SyncJobDef> activeJobs = this.syncJobDefDao.findAllByActiveStatus(true);
        return activeJobs;
    }
}
