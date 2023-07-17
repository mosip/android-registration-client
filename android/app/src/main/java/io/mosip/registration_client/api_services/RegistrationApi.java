package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.service.TemplateService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration_client.model.RegistrationDataPigeon;

@Singleton
public class RegistrationApi implements RegistrationDataPigeon.RegistrationDataApi {
    private final RegistrationService registrationService;
    RegistrationDto registrationDto;
    TemplateService templateService;

    @Inject
    public RegistrationApi(RegistrationService registrationService, TemplateService templateService) {
        this.registrationService = registrationService;
        this.templateService = templateService;
    }
    @Override
    public void startRegistration(@NonNull List<String> languages, @NonNull RegistrationDataPigeon.Result<String> result) {
        String response = "";
        try {
            this.registrationDto = registrationService.startRegistration(languages);
            result.success(response);
            return;
        } catch (Exception e) {
            response = e.getMessage();
            Log.e(getClass().getSimpleName(), "Registration start failed");
        }
        result.success(response);
    }

    @Override
    public void checkMVEL(@NonNull String expression, @NonNull RegistrationDataPigeon.Result<Boolean> result) {
        try {
            result.success(true);
            return;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Mvel Evaluation failed!" + Arrays.toString(e.getStackTrace()));
        }
        result.success(false);
    }

    @Override
    public void getPreviewTemplate(@NonNull Boolean isPreview, @NonNull RegistrationDataPigeon.Result<String> result) {
        try {
            this.registrationDto = this.registrationService.getRegistrationDto();
            Log.e(getClass().getSimpleName(), "Template: " + this.registrationDto.getDemographics());
            String template = this.templateService.getTemplate(this.registrationDto, true);
            Log.e(getClass().getSimpleName(), "Template: " + template);
            result.success("");
            return;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch template failed: " + Arrays.toString(e.getStackTrace()));
        }
        Log.e(getClass().getSimpleName(), "Empty template!");
        result.success("");
    }
}

