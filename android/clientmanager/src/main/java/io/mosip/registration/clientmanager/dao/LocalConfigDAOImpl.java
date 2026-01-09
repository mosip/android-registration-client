package io.mosip.registration.clientmanager.dao;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.entity.LocalPreferences;
import io.mosip.registration.clientmanager.entity.PermittedLocalConfig;
import io.mosip.registration.clientmanager.repository.LocalPreferencesRepository;
import io.mosip.registration.clientmanager.repository.PermittedLocalConfigRepository;

@Singleton
public class LocalConfigDAOImpl implements LocalConfigDAO {

    private static final String TAG = LocalConfigDAOImpl.class.getSimpleName();
    private PermittedLocalConfigRepository permittedLocalConfigRepository;
    private LocalPreferencesRepository localPreferencesRepository;

    @Inject
    public LocalConfigDAOImpl(PermittedLocalConfigRepository permittedLocalConfigRepository,
            LocalPreferencesRepository localPreferencesRepository) {
        this.permittedLocalConfigRepository = permittedLocalConfigRepository;
        this.localPreferencesRepository = localPreferencesRepository;
    }

    @Override
    public List<String> getPermittedConfigurations(String configType) {
        List<PermittedLocalConfig> permittedConfigs = permittedLocalConfigRepository
                .getPermittedConfigsByType(configType);

        List<String> permittedConfigurations = new ArrayList<>();
        if (permittedConfigs != null && !permittedConfigs.isEmpty()) {
            for (PermittedLocalConfig config : permittedConfigs) {
                permittedConfigurations.add(config.getName());
            }
        }
        return permittedConfigurations;
    }

    @Override
    public Map<String, String> getLocalConfigurations() {
        return localPreferencesRepository.getLocalConfigurations();
    }

    @Override
    public void modifyConfigurations(Map<String, String> localPreferences) {

        for (Map.Entry<String, String> entry : localPreferences.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            try {
                saveOrUpdateLocalPreference(name, value, RegistrationConstants.PERMITTED_CONFIG_TYPE);
            } catch (Exception e) {
                Log.e(TAG, "Error modifying configuration: " + name, e);
            }
        }
    }

    @Override
    public String getValue(String name, String configType) {
        try {
            LocalPreferences localPreference = localPreferencesRepository
                    .findByIsDeletedFalseAndNameAndConfigType(name, configType);
            if (localPreference != null && localPreference.getVal() != null) {
                return localPreference.getVal();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting value for: " + name + ", configType: " + configType, e);
        }
        return null;
    }

    @Override
    public void modifyJob(String name, String value) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Job name cannot be null or empty");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Job value cannot be null or empty");
        }

        try {
            saveOrUpdateLocalPreference(name, value, RegistrationConstants.PERMITTED_JOB_TYPE);
        } catch (Exception e) {
            Log.e(TAG, "Error modifying job: " + name, e);
            throw new RuntimeException("Failed to modify job: " + name, e);
        }
    }

    /**
     * Save local preference to database
     * Uses configType-aware lookup to prevent cross-contamination between JOB and CONFIGURATION preferences
     */
    private void saveOrUpdateLocalPreference(String name, String value, String configType) {
        LocalPreferences existingPreference = localPreferencesRepository
                .findByIsDeletedFalseAndNameAndConfigType(name, configType);

        if (existingPreference != null) {
            // Update existing record
            existingPreference.setVal(value);
            existingPreference.setUpdBy(RegistrationConstants.JOB_TRIGGER_POINT_USER);
            existingPreference.setUpdDtimes(System.currentTimeMillis());
            localPreferencesRepository.save(existingPreference);
        } else {
            // Create new record if it doesn't exist
            saveLocalPreference(name, value, configType);
        }
    }

    /**
     * Save local preference to database
     */
    private void saveLocalPreference(String name, String value, String configType) {
        LocalPreferences localPreference = new LocalPreferences(UUID.randomUUID().toString());
        localPreference.setName(name);
        localPreference.setVal(value);
        localPreference.setConfigType(configType);
        localPreference.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_USER);
        localPreference.setCrDtime(System.currentTimeMillis());
        localPreference.setIsDeleted(false);

        localPreferencesRepository.save(localPreference);
    }

    /**
     * Clean up local preferences based on permitted configs.
     * Delete local preference if key is removed from permitted configs.
     * Mark as deleted if key is deactivated in permitted configs.
     */
    public void cleanUpLocalPreferences() {
        List<PermittedLocalConfig> permittedConfigs = permittedLocalConfigRepository
                .getPermittedConfigsByType(RegistrationConstants.PERMITTED_CONFIG_TYPE);

        Map<String, String> localConfigs = getLocalConfigurations();

        Map<String, Boolean> permittedStatusMap = new java.util.HashMap<>();
        for (PermittedLocalConfig config : permittedConfigs) {
            permittedStatusMap.put(config.getName(), config.getIsActive());
        }

        for (String key : localConfigs.keySet()) {
            // Use configType-aware lookup to ensure we only clean up CONFIGURATION type preferences
            LocalPreferences pref = localPreferencesRepository
                    .findByIsDeletedFalseAndNameAndConfigType(key, RegistrationConstants.PERMITTED_CONFIG_TYPE);
            if (pref == null)
                continue;

            if (!permittedStatusMap.containsKey(key)) {
                localPreferencesRepository.delete(pref);
                Log.i(TAG, "Local preference deleted (row removed): " + key);
            } else if (!permittedStatusMap.get(key)) {
                // Key deactivated, mark as deleted
                pref.setIsDeleted(true);
                pref.setUpdBy(RegistrationConstants.JOB_TRIGGER_POINT_USER);
                pref.setUpdDtimes(System.currentTimeMillis());
                localPreferencesRepository.save(pref);
                Log.i(TAG, "Local preference marked deleted (row deactivated): " + key);
            }
        }
    }
}
