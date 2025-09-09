package io.mosip.registration.clientmanager.repository;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.PermittedLocalConfigDao;
import io.mosip.registration.clientmanager.entity.PermittedLocalConfig;

public class PermittedLocalConfigRepository {

    private static final String TAG = PermittedLocalConfigRepository.class.getSimpleName();
    private PermittedLocalConfigDao permittedLocalConfigDao;

    @Inject
    public PermittedLocalConfigRepository(PermittedLocalConfigDao permittedLocalConfigDao) {
        this.permittedLocalConfigDao = permittedLocalConfigDao;
    }

    /**
     * Save permitted local configurations
     */
    public void savePermittedConfigs(List<PermittedLocalConfig> permittedConfigs) {
        try {
            permittedLocalConfigDao.insertAll(permittedConfigs);
            Log.i(TAG, "Saved " + permittedConfigs.size() + " permitted configurations");
        } catch (Exception e) {
            Log.e(TAG, "Error saving permitted configurations", e);
        }
    }

    /**
     * Get all active permitted configurations
     */
    public List<PermittedLocalConfig> getAllActivePermittedConfigs() {
        try {
            return permittedLocalConfigDao.findByIsActiveTrue();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all permitted configurations", e);
            return null;
        }
    }

    /**
     * Get permitted configurations by type
     */
    public List<PermittedLocalConfig> getPermittedConfigsByType(String configType) {
        try {
            return permittedLocalConfigDao.findByIsActiveTrueAndType(configType);
        } catch (Exception e) {
            Log.e(TAG, "Error getting permitted configurations by type: " + configType, e);
            return null;
        }
    }

    /**
     * Get permitted configuration names by type (for UI permission checks)
     */
    public List<String> getPermittedConfigurationNames(String configType) {
        try {
            List<PermittedLocalConfig> configs = permittedLocalConfigDao.findByIsActiveTrueAndType(configType);
            List<String> names = new java.util.ArrayList<>();
            if (configs != null) {
                for (PermittedLocalConfig config : configs) {
                    names.add(config.getName());
                }
            }
            return names;
        } catch (Exception e) {
            Log.e(TAG, "Error getting permitted configuration names for type: " + configType, e);
            return null;
        }
    }

    /**
     * Check if a specific configuration is permitted for local changes
     */
    public boolean isConfigurationPermitted(String configName) {
        try {
            List<PermittedLocalConfig> configs = permittedLocalConfigDao.findByIsActiveTrue();
            if (configs != null) {
                for (PermittedLocalConfig config : configs) {
                    if (config.getName().equals(configName)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if configuration is permitted: " + configName, e);
            return false;
        }
    }

    /**
     * Get permitted configuration names for CONFIGURATION type (most common)
     */
    public List<String> getPermittedConfigurationNames() {
        return getPermittedConfigurationNames(RegistrationConstants.PERMITTED_CONFIG_TYPE);
    }
}
