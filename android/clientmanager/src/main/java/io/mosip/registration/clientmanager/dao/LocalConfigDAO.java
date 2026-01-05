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
     * Get value for a specific local preference by name
     * @param name Preference name
     * @return Preference value or null if not found
     */
    String getValue(String name);

    /**
     * Modify job cron expression
     * @param name Job ID
     * @param value Cron expression value
     */
    void modifyJob(String name, String value);

    void cleanUpLocalPreferences();
}
