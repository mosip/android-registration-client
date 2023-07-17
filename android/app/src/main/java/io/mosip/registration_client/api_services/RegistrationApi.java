package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.service.TemplateService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.packetmanager.util.JsonUtils;
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
    public void evaluateMVEL(@NonNull String fieldData, @NonNull String expression, @NonNull RegistrationDataPigeon.Result<Boolean> result) {
        try {
            FieldSpecDto fieldSpecDto = JsonUtils.jsonStringToJavaObject(fieldData, new TypeReference<FieldSpecDto>() {});
            this.registrationDto = this.registrationService.getRegistrationDto();
            boolean isFieldVisible = UserInterfaceHelperService.isFieldVisible(fieldSpecDto, this.registrationDto.getMVELDataContext());
            result.success(isFieldVisible);
            return;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Object Mapping error: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(false);
    }

    @Override
    public void getPreviewTemplate(@NonNull Boolean isPreview, @NonNull RegistrationDataPigeon.Result<String> result) {
        try {
            this.registrationDto = this.registrationService.getRegistrationDto();
            String template = this.templateService.getTemplate(this.registrationDto, true);
            result.success("");
            return;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch template failed: " + Arrays.toString(e.getStackTrace()));
        }
        Log.e(getClass().getSimpleName(), "Empty template!");
        result.success("");
    }
}

