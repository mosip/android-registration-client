package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.entity.GlobalParam;

import java.util.List;

import javax.inject.Inject;

public class GlobalParamRepository {

    private GlobalParamDao globalParamDao;

    @Inject
    public GlobalParamRepository(GlobalParamDao globalParamDao) {
        this.globalParamDao = globalParamDao;
    }

    public String getGlobalParamValue(String id) {
        return this.globalParamDao.getGlobalParam(id);
    }

    public void saveGlobalParam(String id, String value) {
        GlobalParam globalParam = new GlobalParam(id);
        globalParam.setName(id);
        globalParam.setValue(value);
        globalParam.setStatus(true);
        this.globalParamDao.insertGlobalParam(globalParam);
    }

    public List<GlobalParam> getGlobalParams() {
        return this.globalParamDao.getGlobalParams();
    }
}
