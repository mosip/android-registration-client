package io.mosip.registration.clientmanager.dao;

import java.util.List;
import java.util.Map;

import io.mosip.registration.clientmanager.entity.PermittedLocalConfig;

/**
 * DAO interface for local configuration operations
 * Handles both permitted configurations and local preferences
 */
public interface LocalConfigDAO {

    /**
     * Get all permitted local configurations
     */
    List<PermittedLocalConfig> getAllPermittedLocalConfigs();

    /**
     * Get permitted configurations by type
     */
    List<String> getPermittedConfigurations(String configType);

    /**
     * Get local configurations as a map (name -> value)
     */
    Map<String, String> getLocalConfigurations();

    /**
     * Modify configurations by saving local preferences
     */
    void modifyConfigurations(Map<String, String> localPreferences);

    /**
     * Check if a configuration is permitted for local changes
     */
    boolean isConfigurationPermitted(String configName);
}
