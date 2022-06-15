package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.entity.GlobalParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

public class GlobalParamRepository {

    private static final String TAG = GlobalParamRepository.class.getSimpleName();
    private static Map<String, String> globalParamMap = new HashMap<>();
    private GlobalParamDao globalParamDao;

    @Inject
    public GlobalParamRepository(GlobalParamDao globalParamDao) {
        this.globalParamDao = globalParamDao;
    }

    public String getGlobalParamValue(String id) {
        return globalParamDao.getGlobalParam(id);
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
        GlobalParam globalParam = new GlobalParam(id, id, value, true);
        globalParamDao.insertGlobalParam(globalParam);
        globalParamMap.put(id, value);
    }

    public void saveGlobalParams(List<GlobalParam> globalParam) {
        globalParamDao.insertAll(globalParam);
        refreshGlobalParams();
    }

    public List<GlobalParam> getGlobalParams() {
        return globalParamDao.getGlobalParams();
    }

    private void refreshGlobalParams() {
        List<GlobalParam> globalParams = globalParamDao.getGlobalParams();
        for (GlobalParam globalParam : globalParams) {
            globalParamMap.put(globalParam.getId(), globalParam.getValue());
        }
    }

    public static Boolean getCachedBooleanGlobalParam(String key) {
        String value = getCachedStringGlobalParam(key);
        return value == null ? null : Boolean.parseBoolean(value);
    }

    public static int getCachedIntegerGlobalParam(String key) {
        String value = getCachedStringGlobalParam(key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    public static String getCachedStringGlobalParam(String key) {
        return globalParamMap.get(key);
    }
}
