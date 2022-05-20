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

    private static Map<String, String> globalParamList;

    private static GlobalParamDao globalParamDao;

    @Inject
    public GlobalParamRepository(GlobalParamDao globalParamDao) {
        GlobalParamRepository.globalParamDao = globalParamDao;
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
        GlobalParam globalParam = new GlobalParam(id);
        globalParam.setName(id);
        globalParam.setValue(value);
        globalParam.setStatus(true);
        globalParamDao.insertGlobalParam(globalParam);
        refreshGlobalParams();
    }

    public void saveGlobalParams(List<GlobalParam> globalParam) {
        globalParamDao.insertAll(globalParam);
        refreshGlobalParams();
    }

    public List<GlobalParam> getGlobalParams() {
        return globalParamDao.getGlobalParams();
    }

    private static void refreshGlobalParams() {
        globalParamList = new HashMap<>();
        List<GlobalParam> globalParams =  globalParamDao.getGlobalParams();;
        for (GlobalParam globalParam : globalParams) {
            globalParamList.put(globalParam.getId(), globalParam.getValue());
        }
    }

    public static Boolean getCachedBooleanGlobalParam(String key) {
        String value = getCachedGlobalParamValue(key);
        return Boolean.parseBoolean(value);
    }

    public static int getCachedIntegerGlobalParam(String key) {
        String value = getCachedGlobalParamValue(key);
        return Integer.parseInt(value);
    }

    public static String getCachedStringGlobalParam(String key) {
        return getCachedGlobalParamValue(key);
    }

    private static String getCachedGlobalParamValue(String key) {
        return globalParamList.get(key);
    }
}
