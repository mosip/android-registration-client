package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.entity.GlobalParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalParamRepository {

    private static final String TAG = GlobalParamRepository.class.getSimpleName();
    private static Map<String, String> globalParamMap = new HashMap<>();
    private GlobalParamDao globalParamDao;

    @Inject
    public GlobalParamRepository(GlobalParamDao globalParamDao) {
        this.globalParamDao = globalParamDao;
        refreshGlobalParams();
    }

    public String getGlobalParamValue(String id) {
        return globalParamDao.getGlobalParam(id);
    }

    public List<String> getMandatoryLanguageCodes() {
        String value = globalParamMap.getOrDefault(RegistrationConstants.MANDATORY_LANGUAGES_KEY, "eng");
        return Arrays.asList(value.split(RegistrationConstants.COMMA)).stream()
                .map(String::trim)
                .filter(item-> !item.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getOptionalLanguageCodes() {
        String value = globalParamMap.getOrDefault(RegistrationConstants.OPTIONAL_LANGUAGES_KEY, "");
        return Arrays.asList(value.split(RegistrationConstants.COMMA)).stream()
                .map(String::trim)
                .filter(item-> !item.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
    }

    public int getMaxLanguageCount() {
        int maxCount = getCachedIntegerGlobalParam(RegistrationConstants.MAX_LANGUAGES_COUNT_KEY);
        return maxCount > 0 ? maxCount : 1 ;
    }

    public int getMinLanguageCount() {
        int minCount = getCachedIntegerGlobalParam(RegistrationConstants.MIN_LANGUAGES_COUNT_KEY);
        return minCount > 0 ? minCount : 1 ;
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

    public Boolean getCachedBooleanGlobalParam(String key) {
        String value = getCachedStringGlobalParam(key);
        return value == null ? null : Boolean.parseBoolean(value);
    }

    public int getCachedIntegerGlobalParam(String key) {
        String value = getCachedStringGlobalParam(key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    public String getCachedStringGlobalParam(String key) {
        return globalParamMap.get(key);
    }
}
