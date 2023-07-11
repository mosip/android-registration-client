package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration_client.model.RegistrationDataPigeon;

@Singleton
public class RegistrationApi implements RegistrationDataPigeon.RegistrationDataApi {
    RegistrationService registrationService;
    RegistrationDto registrationDto;

    @Inject
    public RegistrationApi(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public void registration(@NonNull RegistrationDataPigeon.RegistrationData registrationData, @NonNull RegistrationDataPigeon.Result<Boolean> result) {
        List<String> languages = registrationData.getLanguages();
        try {
            this.registrationDto = registrationService.startRegistration(languages);
            String json = registrationData.getDemographicsData();
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> map = mapper.readValue(json, Map.class);
            Log.e(getClass().getSimpleName(), "reg map: " + map);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Registration start failed");
        }

        result.success(true);
    }
}

