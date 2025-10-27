package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.LocationValidationService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration_client.model.LocationPigeon;

@Singleton
public class LocationApi implements LocationPigeon.LocationApi {
    private RegistrationService registrationService;
    private GlobalParamRepository globalParamRepository;
    private LocationValidationService locationValidationService;
    private MasterDataService masterDataService;
    private RegistrationCenterRepository registrationCenterRepository;
    
    @Inject
    public LocationApi(RegistrationService registrationService, 
                      GlobalParamRepository globalParamRepository,
                      LocationValidationService locationValidationService,
                      MasterDataService masterDataService,
                      RegistrationCenterRepository registrationCenterRepository) {
        this.registrationService = registrationService;
        this.globalParamRepository = globalParamRepository;
        this.locationValidationService = locationValidationService;
        this.masterDataService = masterDataService;
        this.registrationCenterRepository = registrationCenterRepository;
    }

    @Override
    public void setMachineLocation(@NonNull Double latitude, @NonNull Double longitude, 
                                  @NonNull LocationPigeon.Result<Void> result) {
        try {
            // Just store the location - validation will happen during submission
            this.registrationService.getRegistrationDto().setGeoLocation(longitude, latitude);
            result.success(null);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), 
                "Set machine location failed: " + e.getMessage(), e);
            result.error(e);
        }
    }

    @Override
    public void getGpsEnableFlag(@NonNull LocationPigeon.Result<String> result) {
        String gpsFlag = "";
        try {
            gpsFlag = globalParamRepository.getCachedStringGpsDeviceDisableFlag();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error fetching GPS enable flag", e);
        }
        result.success(gpsFlag);
    }
}