package io.mosip.registration.clientmanager.spi;

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
     * Check if a configuration is permitted for local changes
     */
    boolean isConfigurationPermitted(String configName);

    /**
     * Get permitted configuration names
     */
    java.util.List<String> getPermittedConfigurationNames();
}
