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
     * Get value for a specific local configuration by name
     * @param name Configuration name
     * @return Configuration value or null if not found
     */
    String getValue(String name);

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
