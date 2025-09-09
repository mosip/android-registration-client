package io.mosip.registration.clientmanager.repository;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.LocalPreferencesDao;
import io.mosip.registration.clientmanager.entity.LocalPreferences;

public class LocalPreferencesRepository {

    private static final String TAG = LocalPreferencesRepository.class.getSimpleName();
    private LocalPreferencesDao localPreferencesDao;

    @Inject
    public LocalPreferencesRepository(LocalPreferencesDao localPreferencesDao) {
        this.localPreferencesDao = localPreferencesDao;
    }

    /**
     * Get all local configurations by config type
     */
    public List<LocalPreferences> getLocalConfigurationsByType(String configType) {
        try {
            return localPreferencesDao.findByIsDeletedFalseAndConfigType(configType);
        } catch (Exception e) {
            Log.e(TAG, "Error getting local configurations by type: " + configType, e);
            return null;
        }
    }

    /**
     * Get local configurations as a map (name -> value)
     */
    public Map<String, String> getLocalConfigurations() {
        try {
            List<LocalPreferences> localPreferences = localPreferencesDao
                    .findByIsDeletedFalseAndConfigType(RegistrationConstants.PERMITTED_CONFIG_TYPE);
            
            Map<String, String> localConfigMap = new HashMap<>();
            if (localPreferences != null) {
                for (LocalPreferences localPreference : localPreferences) {
                    localConfigMap.put(localPreference.getName(), localPreference.getVal());
                }
            }
            return localConfigMap;
        } catch (Exception e) {
            Log.e(TAG, "Error getting local configurations", e);
            return new HashMap<>();
        }
    }

    /**
     * Find local preference by name
     */
    public LocalPreferences findByIsDeletedFalseAndName(String name) {
        try {
            return localPreferencesDao.findByIsDeletedFalseAndName(name);
        } catch (Exception e) {
            Log.e(TAG, "Error finding local preference by name: " + name, e);
            return null;
        }
    }

    /**
     * Save local preference
     */
    public void save(LocalPreferences localPreference) {
        try {
            localPreferencesDao.insert(localPreference);
            Log.i(TAG, "Saved local preference: " + localPreference.getName());
        } catch (Exception e) {
            Log.e(TAG, "Error saving local preference: " + localPreference.getName(), e);
        }
    }

    /**
     * Update local preference
     */
    public void update(LocalPreferences localPreference) {
        try {
            localPreferencesDao.update(localPreference);
            Log.i(TAG, "Updated local preference: " + localPreference.getName());
        } catch (Exception e) {
            Log.e(TAG, "Error updating local preference: " + localPreference.getName(), e);
        }
    }

    /**
     * Soft delete local preference by name
     */
    public void softDeleteByName(String name) {
        try {
            localPreferencesDao.softDeleteByName(name, System.currentTimeMillis());
            Log.i(TAG, "Soft deleted local preference: " + name);
        } catch (Exception e) {
            Log.e(TAG, "Error soft deleting local preference: " + name, e);
        }
    }
}
