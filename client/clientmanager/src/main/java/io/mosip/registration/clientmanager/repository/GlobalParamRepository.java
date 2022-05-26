package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.entity.GlobalParam;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class GlobalParamRepository {

    private GlobalParamDao globalParamDao;

    @Inject
    public GlobalParamRepository(GlobalParamDao globalParamDao) {
        this.globalParamDao = globalParamDao;
    }

    public String getGlobalParamValue(String id) {
        return this.globalParamDao.getGlobalParam(id);
    }

    public List<String> getMandatoryLanguageCodes() {
        //TODO remove hardcoded value and read from global param
        return Arrays.asList("eng");
    }

    public List<String> getOptionalLanguageCodes() {
        //TODO remove hardcoded value and read from global param
        return Arrays.asList("ara", "fra");
    }

    public int getMaxLanguageCount() {
        //TODO remove hardcoded value and read from global param
        return 3;
    }

    public int getMinLanguageCount() {
        //TODO remove hardcoded value and read from global param
        return 1;
    }

    public void saveGlobalParam(String id, String value) {
        GlobalParam globalParam = new GlobalParam(id);
        globalParam.setName(id);
        globalParam.setValue(value);
        globalParam.setStatus(true);
        this.globalParamDao.insertGlobalParam(globalParam);
    }
}
