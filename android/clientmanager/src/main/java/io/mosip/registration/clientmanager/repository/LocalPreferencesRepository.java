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
        } catch (Exception e) {
            Log.e(TAG, "Error updating local preference: " + localPreference.getName(), e);
        }
    }

    /**
     * Delete local preference by name
     */
    public void delete(LocalPreferences localPreference) {
        try {
            localPreferencesDao.deleteByName(localPreference.getName());
        } catch (Exception e) {
            Log.e(TAG, "Error deleting local preference: " + localPreference.getName(), e);
        }
    }
}
