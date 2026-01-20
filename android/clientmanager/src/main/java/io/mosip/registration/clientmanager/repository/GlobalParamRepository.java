package io.mosip.registration.clientmanager.repository;

import android.util.Log;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.dao.LocalConfigDAO;
import io.mosip.registration.clientmanager.entity.GlobalParam;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GlobalParamRepository {

    private static final String TAG = GlobalParamRepository.class.getSimpleName();
    private static Map<String, String> globalParamMap = new HashMap<>();
    private GlobalParamDao globalParamDao;
    private LocalConfigDAO localConfigDAO;

    @Inject
    public GlobalParamRepository(GlobalParamDao globalParamDao, LocalConfigDAO localConfigDAO) {
        this.globalParamDao = globalParamDao;
        this.localConfigDAO = localConfigDAO;

        refreshConfigurationCache();
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
        // Update the merged cache directly for immediate effect
        globalParamMap.put(id, value);
    }

    public void saveGlobalParams(List<GlobalParam> globalParam) {
        globalParamDao.insertAll(globalParam);
        // Refresh with merged configuration to include any local preferences
        refreshConfigurationCache();
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

    public String getCachedStringAgeGroup(){
        return globalParamMap.get(RegistrationConstants.AGE_GROUP_CONFIG);
    }

    public String getCachedStringMAVELScript(){
        return globalParamMap.getOrDefault(RegistrationConstants.APPLICANT_TYPE_MVEL_SCRIPT,"applicanttype.mvel");
    }

    public String getCachedStringPreRegPacketLocation() {
        return globalParamMap.get(RegistrationConstants.PRE_REG_PACKET_LOCATION);
    }

    public Map<String, Object> getGlobalParamsByPattern(String pattern) {

        List<GlobalParam> globalParams = globalParamDao.findByNameLikeAndIsActiveTrueAndValIsNotNull(pattern);
        Map<String, Object> globalParamMap = new LinkedHashMap<>();

        for (GlobalParam param : globalParams) {
            globalParamMap.put(param.getName(), param.getValue() != null ? param.getValue().trim() : param.getValue());
        }

        return globalParamMap;
    }

    public List<String> getSelectedHandles() {
        String value = globalParamMap.getOrDefault(RegistrationConstants.SELECTED_HANDLES, "");
        return Arrays.asList(value.split(RegistrationConstants.COMMA)).stream()
                .map(String::trim)
                .filter(item-> !item.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    public String getCachedStringForgotPassword() {
        return globalParamMap.get(RegistrationConstants.FORGOT_PASSWORD_URL);
    }

    public String getCachedStringIdleTime() {
        return globalParamMap.get(RegistrationConstants.IDLE_TIME);
    }

    public String getCachedStringRefreshedLoginTime() {
        return globalParamMap.get(RegistrationConstants.REFRESHED_LOGIN_TIME);
    }

    public String getCachedStringGpsDeviceEnableFlag() {
        return globalParamMap.get(RegistrationConstants.GPS_DEVICE_ENABLE_FLAG);
    }

    public String getCachedStringMachineToCenterDistance() {
        return globalParamMap.get(RegistrationConstants.DIST_FRM_MACHINE_TO_CENTER);
    }

    public String getCachedStringOperatorOnboardingBioAttributes() {
        return globalParamMap.get(RegistrationConstants.OPERATOR_ONBOARDING_BIO_ATTRIBUTES);
    }

    public String getCachedStringOnboardYourselfUrl() {
        return globalParamMap.get(RegistrationConstants.ONBOARD_YOURSELF_URL);
    }

    public String getCachedStringRegisteringIndividualUrl() {
        return globalParamMap.get(RegistrationConstants.REGISTERING_INDIVIDUAL_URL);
    }

    public String getCachedStringSyncDataUrl() {
        return globalParamMap.get(RegistrationConstants.SYNC_DATA_URL);
    }

    public String getCachedStringMappingDevicesUrl() {
        return globalParamMap.get(RegistrationConstants.MAPPING_DEVICES_URL);
    }

    public String getCachedStringUploadingDataUrl() {
        return globalParamMap.get(RegistrationConstants.UPLOADING_DATA_URL);
    }

    public String getCachedStringUpdatingBiometricsUrl() {
        return globalParamMap.get(RegistrationConstants.UPDATING_BIOMETRICS_URL);
    }

    public String getCachedStringPasswordLength() {
        return globalParamMap.get(RegistrationConstants.PWORD_LENGTH);
    }

    public String getCachedStringDocumentSize() {
        return globalParamMap.get(RegistrationConstants.DOC_SIZE);
    }

    public String getCachedStringDOBAgeLimit() {
        return globalParamMap.get(RegistrationConstants.MAX_AGE);
    }
    public long getCachedReadTimeout() {
        return parseLongWithDefault(RegistrationConstants.HTTP_API_READ_TIMEOUT);
    }

    public long getCachedWriteTimeout() {
        return parseLongWithDefault(RegistrationConstants.HTTP_API_WRITE_TIMEOUT);
    }

    public String getCachedStringInvalidLoginCount() {
        return globalParamMap.get(RegistrationConstants.INVALID_LOGIN_COUNT);
    }

    public String getCachedStringInvalidLoginTime() {
        return globalParamMap.get(RegistrationConstants.INVALID_LOGIN_TIME);
    }

    public int getCachedIntegerDiskSpaceSize() {
        return getCachedIntegerGlobalParam(RegistrationConstants.DISK_SPACE);
    }

    public int getCachedIntegerPRIDLength(){
        return getCachedIntegerGlobalParam(RegistrationConstants.PRID_LENGTH);
    }

    public int getCachedIntegerUINLength(){
        return getCachedIntegerGlobalParam(RegistrationConstants.UIN_LENGTH);
    }

    public int getCachedIntegerVIDLength(){
        return getCachedIntegerGlobalParam(RegistrationConstants.VID_LENGTH);
    }
    public String getCachedStringDocType() {
        return globalParamMap.get(RegistrationConstants.DOC_TYPE);
    }

    public String getCachedStringAppName() {
        return globalParamMap.get(RegistrationConstants.APP_NAME);
    }

    public String getCachedStringAppId() {
        return globalParamMap.get(RegistrationConstants.APP_ID);
    }

    public String getCachedStringDefaultHostIp() {
        return globalParamMap.get(RegistrationConstants.DEFAULT_HOST_IP);
    }

    public String getCachedStringDefaultHostName() {
        return globalParamMap.get(RegistrationConstants.DEFAULT_HOST_NAME);
    }

    /**
     * Refresh configuration cache by merging global params with local preferences
     */
    public void refreshConfigurationCache() {

        try {
            // Get fresh global parameters from database
            List<GlobalParam> globalParams = globalParamDao.getGlobalParams();
            Map<String, String> freshGlobalParams = new HashMap<>();
            for (GlobalParam globalParam : globalParams) {
                freshGlobalParams.put(globalParam.getId(), globalParam.getValue());
            }

            // Get local preferences (overrides)
            Map<String, String> localConfigs = localConfigDAO.getLocalConfigurations();

            // Merge: local preferences override global parameters
            globalParamMap.clear();
            globalParamMap.putAll(freshGlobalParams);
            globalParamMap.putAll(localConfigs); // Local preferences take precedence
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing configuration cache", e);
        }

    }

    private long parseLongWithDefault(String key) {
        String value = globalParamMap.get(key);
        if (value == null || value.trim().isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to parse long value for key: " + key + ", value: " + value, e);
            return 0L;
        }
    }

    // ========== BioSDK Provider Configuration Methods ==========
    // Property keys are constructed dynamically (not from RegistrationConstants)
    // Pattern: "mosip.biometric.sdk.providers.{modality}.{vendor}.{property}"

    /**
     * Gets the class name for a specific modality provider
     * Property key: "mosip.biometric.sdk.providers.{modality}.{vendor}.classname"
     * @param modality "finger", "iris", or "face"
     * @param vendorName vendor name (e.g., "mockvendor")
     * @return fully qualified class name or null if not found
     */
    public String getBioSDKProviderClassName(String modality, String vendorName) {
        String key = String.format("mosip.biometric.sdk.providers.%s.%s.classname", modality, vendorName);
        return getCachedStringGlobalParam(key);
    }

    /**
     * Gets the version for a specific modality provider
     * Property key: "mosip.biometric.sdk.providers.{modality}.{vendor}.version"
     * @param modality "finger", "iris", or "face"
     * @param vendorName vendor name (e.g., "mockvendor")
     * @return version string or null if not found
     */
    public String getBioSDKProviderVersion(String modality, String vendorName) {
        String key = String.format("mosip.biometric.sdk.providers.%s.%s.version", modality, vendorName);
        return getCachedStringGlobalParam(key);
    }

    /**
     * Gets the arguments for a specific modality provider
     * Property key: "mosip.biometric.sdk.providers.{modality}.{vendor}.args"
     * @param modality "finger", "iris", or "face"
     * @param vendorName vendor name (e.g., "mockvendor")
     * @return arguments string (empty string if not found)
     */
    public String getBioSDKProviderArgs(String modality, String vendorName) {
        String key = String.format("mosip.biometric.sdk.providers.%s.%s.args", modality, vendorName);
        String value = getCachedStringGlobalParam(key);
        return value != null ? value : "";
    }

    /**
     * Gets the threshold for a specific modality provider
     * Property key: "mosip.biometric.sdk.providers.{modality}.{vendor}.threshold"
     * @param modality "finger", "iris", or "face"
     * @param vendorName vendor name (e.g., "mockvendor")
     * @return threshold value as string or null if not found
     */
    public String getBioSDKProviderThreshold(String modality, String vendorName) {
        String key = String.format("mosip.biometric.sdk.providers.%s.%s.threshold", modality, vendorName);
        return getCachedStringGlobalParam(key);
    }

    /**
     * Gets the vendor name for a specific modality
     * Searches for pattern: "mosip.biometric.sdk.providers.{modality}.{vendor}.classname"
     * @param modality "finger", "iris", or "face"
     * @return vendor name or null if not found
     */
    public String getBioSDKVendorName(String modality) {
        String prefix = "mosip.biometric.sdk.providers." + modality + ".";
        String classnameSuffix = ".classname";
        String pattern = prefix + "%" + classnameSuffix;
        
        try {
            Map<String, Object> dbParams = getGlobalParamsByPattern(pattern);
            for (String key : dbParams.keySet()) {
                if (key.startsWith(prefix) && key.endsWith(classnameSuffix)) {
                    // Extract vendor name: mosip.biometric.sdk.providers.{modality}.{vendor}.classname
                    String vendorKey = key.substring(prefix.length());
                    int dotIndex = vendorKey.indexOf('.');
                    if (dotIndex > 0) {
                        String vendorName = vendorKey.substring(0, dotIndex);
                        Log.d(TAG, String.format("Found vendor '%s' for modality '%s'", vendorName, modality));
                        return vendorName;
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error searching for vendor name for modality: " + modality, e);
        }
        
        Log.w(TAG, String.format("No vendor configured for modality: %s", modality));
        return null;
    }
}
