package io.mosip.registration.clientmanager.repository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.dao.SyncJobTransactionDao;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.entity.SyncJobTransaction;

/**
 * DAO class for all the Sync Job transaction related details
 *
 * @author Anshul vanawat
 */

public class SyncJobTransactionRepository {

    private SyncJobTransactionDao syncJobDefDao;

    @Inject
    public SyncJobTransactionRepository(SyncJobTransactionDao syncJobTransactionDao) {
        this.syncJobDefDao = syncJobTransactionDao;
    }

    public void saveSyncJobTransaction(SyncJobTransaction syncJobTransaction) {
        syncJobDefDao.insert(syncJobTransaction);
    }

    public List<SyncJobTransaction> getAllTransactions() {
        return this.syncJobDefDao.findAll();
    }

    public SyncJobTransaction getSyncJobTransaction(String jobId) {
        return this.syncJobDefDao.findTransactionByJobId(jobId);
    }
}
