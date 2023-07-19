package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.packetmanager.dto.SimpleType;
import io.mosip.registration_client.model.DemographicsDataPigeon;

@Singleton
public class DemographicsDetailsApi implements DemographicsDataPigeon.DemographicsApi {
    private final RegistrationService registrationService;

    @Inject
    public DemographicsDetailsApi(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public void addDemographicField(@NonNull String fieldId, @NonNull String value, @NonNull DemographicsDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().addDemographicField(fieldId, value);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Add field failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void getDemographicField(@NonNull String fieldId, @NonNull DemographicsDataPigeon.Result<String> result) {
        try {
            RegistrationDto registrationDto = this.registrationService.getRegistrationDto();
            registrationDto.getDemographics().forEach((k,v) -> {
                if(k == fieldId){
                    if(v instanceof String){
                        result.success((String) v);
                        return;
                    }
                }
            });
            Log.e(getClass().getSimpleName(), "Get field failed!" + this.registrationService.getRegistrationDto().getDemographics());
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Get field failed!" + Arrays.toString(e.getStackTrace()));
        }
        result.success("");
    }

    @Override
    public void addSimpleTypeDemographicField(@NonNull String fieldId, @NonNull String value, @NonNull String language, @NonNull DemographicsDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().addDemographicField(fieldId, value, language);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Add simple type field failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void getSimpleTypeDemographicField(@NonNull String fieldId, @NonNull String language, @NonNull DemographicsDataPigeon.Result<String> result) {
        try {
            RegistrationDto registrationDto = this.registrationService.getRegistrationDto();
            registrationDto.getDemographics().forEach((k,v) -> {
                if(k == fieldId){
                    if((v instanceof String)){
                        result.success((String) v);
                        return;
                    }else if( v instanceof List){
                        ((List<SimpleType>) v).forEach((value) -> {
                            if(value.getLanguage() == language){
                                result.success(value.getValue());
                                return;
                            }
                        });
                    }
                }
            });
            Log.e(getClass().getSimpleName(), "Get field failed!" + this.registrationService.getRegistrationDto().getDemographics());
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Get field failed!" + Arrays.toString(e.getStackTrace()));
        }
        result.success("");
    }

    @Override
    public void setDateField(@NonNull String fieldId, @NonNull String subType, @NonNull String day, @NonNull String month, @NonNull String year, @NonNull DemographicsDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().setDateField(fieldId, subType, day, month, year);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Add date field failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void removeDemographicField(@NonNull String fieldId, @NonNull DemographicsDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().removeDemographicField(fieldId);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Add date field failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void setConsentField(@NonNull String consentData, @NonNull DemographicsDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().setConsent(consentData);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Add consent dto failed!" + Arrays.toString(e.getStackTrace()));
        }
    }
}
