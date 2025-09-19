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
    public List<PermittedLocalConfig> getAllPermittedLocalConfigs() {
        Log.i(TAG, "Getting the list of permitted configurations");
        return permittedLocalConfigRepository.getAllActivePermittedConfigs();
    }

    @Override
    public List<String> getPermittedConfigurations(String configType) {
        Log.i(TAG, "Getting the list of permitted configurations of type " + configType);

        List<PermittedLocalConfig> permittedConfigs =
            permittedLocalConfigRepository.getPermittedConfigsByType(configType);
        Log.i(TAG, "Returning test configurations: " + permittedConfigs);
        List<String> permittedConfigurations = new ArrayList<>();
        if (permittedConfigs != null && !permittedConfigs.isEmpty()) {
            for (PermittedLocalConfig config : permittedConfigs) {
                permittedConfigurations.add(config.getName());
            }
        }
        Log.i(TAG, "response " + permittedConfigurations);
        return permittedConfigurations;
    }

    @Override
    public Map<String, String> getLocalConfigurations() {
        Log.i(TAG, "Getting local configurations");
        return localPreferencesRepository.getLocalConfigurations();
    }

    @Override
    public void modifyConfigurations(Map<String, String> localPreferences) {
        Log.i(TAG, "Modifying " + localPreferences.size() + " configurations");
        
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
        
        Log.i(TAG, "Successfully modified configurations");
    }


    /**
     * Save local preference to database
     */
    private void saveLocalPreference(String name, String value, String configType) {
        LocalPreferences localPreference = new LocalPreferences(UUID.randomUUID().toString());
        localPreference.setName(name);
        localPreference.setVal(value);
        localPreference.setConfigType(configType);
        localPreference.setMachineName("ANDROID_CLIENT"); // TODO: Get actual machine name
        localPreference.setCrBy(RegistrationConstants.JOB_TRIGGER_POINT_USER);
        localPreference.setCrDtime(System.currentTimeMillis());
        localPreference.setIsDeleted(false);
        
        localPreferencesRepository.save(localPreference);
    }
}
