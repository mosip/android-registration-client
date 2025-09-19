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
}
