package io.mosip.registration.clientmanager.service;

import android.util.Log;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dao.LocalConfigDAO;
import io.mosip.registration.clientmanager.spi.LocalConfigService;

@Singleton
public class LocalConfigServiceImpl implements LocalConfigService {

    private static final String TAG = LocalConfigServiceImpl.class.getSimpleName();
    private LocalConfigDAO localConfigDAO;

    @Inject
    public LocalConfigServiceImpl(LocalConfigDAO localConfigDAO) {
        this.localConfigDAO = localConfigDAO;
    }

    @Override
    public Map<String, String> getLocalConfigurations() {
        Log.i(TAG, "Getting local configurations");
        return localConfigDAO.getLocalConfigurations();
    }

    @Override
    public void modifyConfigurations(Map<String, String> localPreferences) {
        Log.i(TAG, "Modifying " + localPreferences.size() + " configurations");
        localConfigDAO.modifyConfigurations(localPreferences);
        Log.i(TAG, "Successfully modified configurations");
    }

    @Override
    public boolean isConfigurationPermitted(String configName) {
        return localConfigDAO.isConfigurationPermitted(configName);
    }

    @Override
    public List<String> getPermittedConfigurationNames() {
        return localConfigDAO.getPermittedConfigurations(io.mosip.registration.clientmanager.constant.RegistrationConstants.PERMITTED_CONFIG_TYPE);
    }
}
