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
        List<PermittedLocalConfig> permittedConfigs =
            permittedLocalConfigRepository.getPermittedConfigsByType(configType);

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
                // Soft delete existing record if it exists
                LocalPreferences existingPreference = localPreferencesRepository.findByIsDeletedFalseAndName(name);
                if (existingPreference != null) {
                    localPreferencesRepository.softDeleteByName(name);
                }
                
                // Create new record
                saveLocalPreference(name, value, RegistrationConstants.PERMITTED_CONFIG_TYPE);
                
            } catch (Exception e) {
                Log.e(TAG, "Error modifying configuration: " + name, e);
            }
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
        localPreference.setMachineName("SYSTEM");
        localPreference.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_USER);
        localPreference.setCrDtime(System.currentTimeMillis());
        localPreference.setIsDeleted(false);
        
        localPreferencesRepository.save(localPreference);
    }
}
