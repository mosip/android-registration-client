package io.mosip.registration.clientmanager.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.LocalConfigDAO;
import io.mosip.registration.clientmanager.spi.LocalConfigService;

@Singleton
public class LocalConfigServiceImpl implements LocalConfigService {

    private LocalConfigDAO localConfigDAO;

    @Inject
    public LocalConfigServiceImpl(LocalConfigDAO localConfigDAO) {
        this.localConfigDAO = localConfigDAO;
    }

    @Override
    public Map<String, String> getLocalConfigurations() {
        return localConfigDAO.getLocalConfigurations();
    }

    @Override
    public void modifyConfigurations(Map<String, String> localPreferences) {
        localConfigDAO.modifyConfigurations(localPreferences);
    }

    @Override
    public List<String> getPermittedConfiguration() {
        return localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_CONFIG_TYPE);
    }
}
