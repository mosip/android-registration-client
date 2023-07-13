package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.service.TemplateService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.packetmanager.dto.SimpleType;
import io.mosip.registration.packetmanager.util.JsonUtils;
import io.mosip.registration_client.model.RegistrationDataPigeon;

@Singleton
public class RegistrationApi implements RegistrationDataPigeon.RegistrationDataApi {
    RegistrationService registrationService;
    RegistrationDto registrationDto;
    TemplateService templateService;

    @Inject
    public RegistrationApi(RegistrationService registrationService, TemplateService templateService) {
        this.registrationService = registrationService;
        this.templateService = templateService;
    }

    @Override
    public void registration(@NonNull RegistrationDataPigeon.RegistrationData registrationData, @NonNull RegistrationDataPigeon.Result<Boolean> result) {
        List<String> languages = registrationData.getLanguages();
        try {
            this.registrationDto = registrationService.startRegistration(languages);
            String json = registrationData.getDemographicsData();
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> map = mapper.readValue(json, Map.class);
//            Log.e(getClass().getSimpleName(), "reg map: " + map);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Registration start failed");
        }

        result.success(true);
    }

    @Override
    public void checkMVEL(@NonNull String data, @NonNull String expression, @NonNull RegistrationDataPigeon.Result<Boolean> result) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            RegistrationDto regDTO = mapper.readValue(data, RegistrationDto.class);
            Map<String, Object> dataContext = regDTO.getMVELDataContext();
            boolean isValid = UserInterfaceHelperService.evaluateMvel(expression, dataContext);
            result.success(isValid);
            return;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Mvel Evaluation failed!" + Arrays.toString(e.getStackTrace()));
        }
        result.success(false);
    }

    @Override
    public void getPreviewTemplate(@NonNull String data, @NonNull Boolean isPreview, @NonNull RegistrationDataPigeon.Result<String> result) {
        try {
            RegistrationDto regDTO = JsonUtils.jsonStringToJavaObject(data, RegistrationDto.class);
            regDTO.getDemographics().forEach((k, v) -> {
                if(v instanceof List) {
                    ObjectMapper mapper = new ObjectMapper();
                    List<SimpleType> simpleTypeList = mapper.convertValue(v, new TypeReference<List<SimpleType>>() {});
                    regDTO.getDemographics().put(k, simpleTypeList);
                }
            });

//            String template = this.templateService.getTemplate(regDTO, true);
            result.success("");
            return;
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e(getClass().getSimpleName(), "Fetch template failed: " + Arrays.toString(e.getStackTrace()));
        }
        Log.e(getClass().getSimpleName(), "Empty template!");
        result.success("");
    }
}

