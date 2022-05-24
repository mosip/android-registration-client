package io.mosip.registration.clientmanager.repository;

import org.json.JSONException;
import org.json.JSONObject;

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

    private SyncJobDefDao syncJobDefDao;

    @Inject
    public SyncJobDefRepository(SyncJobDefDao syncJobDefDao) {
        this.syncJobDefDao = syncJobDefDao;
    }

    public void saveSyncJobDef(JSONObject jsonObject) throws JSONException {
        SyncJobDef syncJobDef = new SyncJobDef(jsonObject.getString("id"));
        syncJobDef.setName(jsonObject.getString("name"));
        syncJobDef.setApiName(jsonObject.getString("apiName"));
        syncJobDef.setParentSyncJobId(jsonObject.getString("parentSyncJobId"));
        syncJobDef.setSyncFreq(jsonObject.getString("syncFreq"));
        syncJobDef.setLockDuration(jsonObject.getString("lockDuration"));
        syncJobDef.setLangCode(jsonObject.getString("langCode"));
        syncJobDef.setIsDeleted(jsonObject.getBoolean("isDeleted"));
        syncJobDef.setIsActive(jsonObject.getBoolean("isActive"));
        syncJobDefDao.insert(syncJobDef);
    }

    public List<SyncJobDef> getAllSyncJobDefList() {
        return this.syncJobDefDao.findAll();
    }

}
