package io.mosip.registration.clientmanager.repository;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.dao.PermittedLocalConfigDao;
import io.mosip.registration.clientmanager.entity.PermittedLocalConfig;

public class PermittedLocalConfigRepository {

    private static final String TAG = PermittedLocalConfigRepository.class.getSimpleName();
    private PermittedLocalConfigDao permittedLocalConfigDao;

    @Inject
    public PermittedLocalConfigRepository(PermittedLocalConfigDao permittedLocalConfigDao) {
        this.permittedLocalConfigDao = permittedLocalConfigDao;
    }

    /**
     * Save permitted local configurations
     */
    public void savePermittedConfigs(List<PermittedLocalConfig> permittedConfigs) {
        try {
            permittedLocalConfigDao.insertAll(permittedConfigs);
        } catch (Exception e) {
            Log.e(TAG, "Error saving permitted configurations", e);
        }
    }

    /**
     * Get permitted configurations by type
     */
    public List<PermittedLocalConfig> getPermittedConfigsByType(String configType) {
        try {
            return permittedLocalConfigDao.findByIsActiveTrueAndType(configType);
        } catch (Exception e) {
            Log.e(TAG, "Error getting permitted configurations by type: " + configType, e);
            return null;
        }
    }

}
