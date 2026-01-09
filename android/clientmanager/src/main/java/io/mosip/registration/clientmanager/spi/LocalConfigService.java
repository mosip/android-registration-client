package io.mosip.registration.clientmanager.spi;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing local configurations
 */
public interface LocalConfigService {

    /**
     * Get all local configurations as a map (name -> value)
     */
    Map<String, String> getLocalConfigurations();

    /**
     * Modify configurations by saving local preferences
     */
    void modifyConfigurations(Map<String, String> localPreferences);

    /**
     * Get permitted configuration names
     */
    List<String> getPermittedConfiguration();

    /**
     * Get value for a specific local preference by name and config type
     * @param name Preference name
     * @param configType Configuration type (PERMITTED_JOB_TYPE or PERMITTED_CONFIG_TYPE)
     * @return Preference value or null if not found
     */
    String getValue(String name, String configType);

    /**
     * Modify job cron expression
     * @param name Job ID
     * @param value Cron expression value
     */
    void modifyJob(String name, String value);

    /**
     * Get permitted job IDs
     * @return List of permitted job IDs that can be edited
     */
    List<String> getPermittedJobs();
}
