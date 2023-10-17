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
        globalParamMap.put(RegistrationConstants.SERVER_VERSION, "1.1.5.3-P3");
        globalParamMap.put(RegistrationConstants.CONSENT_SCREEN_CONTENT, "[{\"id\":\"consentText\",\"inputRequired\":true,\"type\":\"simpleType\",\"minimum\":0,\"maximum\":0,\"description\":\"Consent\",\"label\":{},\"controlType\":\"html\",\"fieldType\":\"default\",\"format\":\"none\",\"validators\":[],\"fieldCategory\":\"evidence\",\"alignmentGroup\":null,\"visible\":null,\"contactType\":null,\"group\":\"consentText\",\"groupLabel\":null,\"changeAction\":null,\"transliterate\":false,\"templateName\":\"Registration Consent\",\"fieldLayout\":null,\"locationHierarchy\":null,\"conditionalBioAttributes\":null,\"required\":true,\"bioAttributes\":null,\"requiredOn\":[],\"subType\":\"consentText\"},{\"id\":\"consent\",\"inputRequired\":true,\"type\":\"string\",\"minimum\":0,\"maximum\":0,\"description\":\"consent accepted\",\"label\":{\"ara\":\"الاسم الكامل الكامل الكامل\",\"fra\":\"J'ai lu et j'accepte les termes et conditions pour partager mes PII\",\"eng\":\"I have read and accept terms and conditions to share my PII\"},\"controlType\":\"checkbox\",\"fieldType\":\"default\",\"format\":\"none\",\"validators\":[],\"fieldCategory\":\"evidence\",\"alignmentGroup\":null,\"visible\":null,\"contactType\":null,\"group\":\"consent\",\"groupLabel\":null,\"changeAction\":null,\"transliterate\":false,\"templateName\":null,\"fieldLayout\":null,\"locationHierarchy\":null,\"conditionalBioAttributes\":null,\"required\":true,\"bioAttributes\":null,\"requiredOn\":[],\"subType\":\"consent\"},{\"id\":\"preferredLang\",\"inputRequired\":true,\"type\":\"string\",\"minimum\":0,\"maximum\":0,\"description\":\"user preferred Language\",\"label\":{\"ara\":\"لغة الإخطار\",\"fra\":\"Langue de notification\",\"eng\":\"Notification Langauge\"},\"controlType\":\"button\",\"fieldType\":\"dynamic\",\"format\":\"none\",\"validators\":[],\"fieldCategory\":\"pvt\",\"alignmentGroup\":\"group1\",\"visible\":null,\"contactType\":null,\"group\":\"PreferredLanguage\",\"groupLabel\":null,\"changeAction\":null,\"transliterate\":false,\"templateName\":null,\"fieldLayout\":null,\"locationHierarchy\":null,\"conditionalBioAttributes\":null,\"required\":true,\"bioAttributes\":null,\"requiredOn\":[],\"subType\":\"preferredLang\"}]");
        globalParamMap.put(RegistrationConstants.INDIVIDUAL_BIOMETRICS_ID, "individualBiometrics");
        globalParamMap.put(RegistrationConstants.INTRODUCER_BIOMETRICS_ID, "guardianBiometrics");
        globalParamMap.put(RegistrationConstants.INFANT_AGEGROUP_NAME, "INFANT");
        globalParamMap.put(RegistrationConstants.AGEGROUP_CONFIG, "{\"INFANT\":{\"bioAttributes\":[\"face\"],\"isGuardianAuthRequired\":true},\"ADULT\":{\"bioAttributes\":[\"leftEye\",\"rightEye\",\"rightIndex\",\"rightLittle\",\"rightRing\",\"rightMiddle\",\"leftIndex\",\"leftLittle\",\"leftRing\",\"leftMiddle\",\"leftThumb\",\"rightThumb\",\"face\"],\"isGuardianAuthRequired\":false},\"SENIOR_CITIZEN\":{\"bioAttributes\":[\"leftEye\",\"rightEye\",\"rightIndex\",\"rightLittle\",\"rightRing\",\"rightMiddle\",\"leftIndex\",\"leftLittle\",\"leftRing\",\"leftMiddle\",\"leftThumb\",\"rightThumb\",\"face\"],\"isGuardianAuthRequired\":false}}");
        globalParamMap.put(RegistrationConstants.ALLOWED_BIO_ATTRIBUTES, "leftEye,rightEye,rightIndex,rightLittle,rightRing,rightMiddle,leftIndex,leftLittle,leftRing,leftMiddle,leftThumb,rightThumb,face");
        globalParamMap.put(RegistrationConstants.DEFAULT_APP_TYPE_CODE, "000");
        return globalParamMap.get(key);
    }
}
