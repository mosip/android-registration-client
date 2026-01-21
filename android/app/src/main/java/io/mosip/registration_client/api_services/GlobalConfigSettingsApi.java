package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.spi.LocalConfigService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration_client.model.GlobalConfigSettingsPigeon;

@Singleton
public class GlobalConfigSettingsApi implements GlobalConfigSettingsPigeon.GlobalConfigSettingsApi {

    MasterDataService masterDataService;

    LocalConfigService localConfigService;

    GlobalParamRepository globalParamRepository;

    @Inject
    public GlobalConfigSettingsApi(MasterDataService masterDataService, LocalConfigService localConfigService, GlobalParamRepository globalParamRepository) {
        this.masterDataService = masterDataService;
        this.localConfigService = localConfigService;
        this.globalParamRepository = globalParamRepository;
    }

    @Override
    public void getRegistrationParams(@NonNull GlobalConfigSettingsPigeon.Result<Map<String, Object>> result) {
        try {
            Map<String, Object> response = masterDataService.getRegistrationParams();
            result.success(response);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error fetching registration params.", e);
            result.error(e);
        }
    }

    @Override
    public void getLocalConfigurations(@NonNull GlobalConfigSettingsPigeon.Result<Map<String, String>> result) {
        try {
            Map<String, String> response = localConfigService.getLocalConfigurations();
            result.success(response);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error fetching local configurations.", e);
            result.error(e);
        }
    }

    @Override
    public void getPermittedConfigurationNames(@NonNull GlobalConfigSettingsPigeon.Result<List<String>> result) {
        try {
            List<String> response = localConfigService.getPermittedConfiguration();
            result.success(response);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error fetching permitted configuration names.", e);
            result.error(e);
        }
    }

    @Override
    public void modifyConfigurations(@NonNull Map<String, String> localPreferences, @NonNull GlobalConfigSettingsPigeon.Result<Void> result) {
        try {
            localConfigService.modifyConfigurations(localPreferences);
            result.success(null);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error modifying configurations.", e);
            result.error(e);
        }
    }

    @Override
    public void getGpsEnableFlag(@NonNull GlobalConfigSettingsPigeon.Result<String> result) {
        String gpsFlag = "";
        try {
            gpsFlag = globalParamRepository.getCachedStringGpsDeviceEnableFlag();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error fetching GPS enable flag", e);
        }
        result.success(gpsFlag);
    }

    @Override
    public void getPRIDLength(@NonNull GlobalConfigSettingsPigeon.Result<Long> result) {
        int pridLength = 0;
        try {
            pridLength = globalParamRepository.getCachedIntegerPRIDLength();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error fetching PRID length", e);
            result.error(e);
        }
        result.success((long) pridLength);
    }

    @Override
    public void getUINLength(@NonNull GlobalConfigSettingsPigeon.Result<Long> result) {
        int uinLength = 0;
        try {
            uinLength = globalParamRepository.getCachedIntegerUINLength();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error fetching UIN length", e);
            result.error(e);
        }
        result.success((long) uinLength);
    }

    @Override
    public void getVIDLength(@NonNull GlobalConfigSettingsPigeon.Result<Long> result) {
        int vidLength = 0;
        try {
            vidLength = globalParamRepository.getCachedIntegerVIDLength();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error fetching VID length", e);
            result.error(e);
        }
        result.success((long) vidLength);

    }


}