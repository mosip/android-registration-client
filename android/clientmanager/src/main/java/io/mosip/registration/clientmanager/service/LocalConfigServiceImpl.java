package io.mosip.registration.clientmanager.service;

import android.util.Log;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.LocalConfigDAO;
import io.mosip.registration.clientmanager.spi.LocalConfigService;
import io.mosip.registration.clientmanager.util.CronExpressionParser;

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

    @Override
    public String getValue(String name, String configType) {
        return localConfigDAO.getValue(name, configType);
    }

    @Override
    public void modifyJob(String name, String value) {
        // Validate job name is not null or empty
        if (name == null || name.trim().isEmpty()) {
            Log.e(TAG, "Cannot modify job: job name is null or empty");
            throw new IllegalArgumentException("Job name cannot be null or empty");
        }

        // Validate cron expression before persisting to database
        // This prevents invalid cron expressions from being stored, which could cause
        // job scheduling failures or runtime errors when the cron is used
        if (value == null || value.trim().isEmpty()) {
            Log.e(TAG, "Cannot modify job " + name + ": cron expression is null or empty");
            throw new IllegalArgumentException("Cron expression cannot be null or empty");
        }

        if (!CronExpressionParser.isValidCronExpression(value)) {
            Log.e(TAG, "Cannot modify job " + name + ": invalid cron expression: " + value);
            throw new IllegalArgumentException("Invalid cron expression: " + value);
        }

        if (!getPermittedJobs().contains(name)) {
            Log.e(TAG, "Cannot modify job " + name + ": not a permitted job");
            throw new IllegalArgumentException("Job modification not permitted for: " + name);
        }

        // Delegate to DAO only after validation passes
        // The DAO layer handles the transaction-safe persistence
        localConfigDAO.modifyJob(name, value);
    }

    @Override
    public List<String> getPermittedJobs() {
        return localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE);
    }
}
