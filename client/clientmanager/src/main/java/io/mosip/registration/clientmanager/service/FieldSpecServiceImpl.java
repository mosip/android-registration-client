package io.mosip.registration.clientmanager.service;

import android.util.Log;
import androidx.annotation.NonNull;
import io.mosip.registration.clientmanager.dto.uispec.FieldValidatorDto;
import io.mosip.registration.clientmanager.dto.uispec.RequiredDto;
import io.mosip.registration.clientmanager.spi.FieldSpecService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import org.mvel2.MVEL;

import javax.inject.Inject;
import java.util.*;

/**
 *
 */
public class FieldSpecServiceImpl implements FieldSpecService {

    private static final String TAG = FieldSpecServiceImpl.class.getSimpleName();
    private static final List<String> SUPPORTED_ENGINE_TYPES = Arrays.asList("regex");

    private RegistrationService registrationService;

    @Inject
    public FieldSpecServiceImpl(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public boolean validateField(@NonNull String fieldId, String value, String langCode,
                                 List<FieldValidatorDto> validatorList) {
        try {
            if(validatorList == null || validatorList.isEmpty())
                return true;

            Optional<FieldValidatorDto> validatorOption = Optional.empty();
            if(langCode != null) {
                validatorOption = validatorList.stream()
                        .filter( v-> langCode.equalsIgnoreCase(v.getLangCode())
                                && SUPPORTED_ENGINE_TYPES.contains(v.getType().toLowerCase()) )
                        .findFirst();
            }

            if(!validatorOption.isPresent()) {
                validatorOption = validatorList.stream()
                        .filter( v-> v.getLangCode() == null &&
                                SUPPORTED_ENGINE_TYPES.contains(v.getType().toLowerCase()))
                        .findFirst();
            }

            if(validatorOption.isPresent()) {
                return value == null ? false : value.trim().matches(validatorOption.get().getValidator());
            }
        } catch (Throwable t) {
            Log.e(TAG, "Failed to validate field value", t);
        }

        //No validators found for the provided langCode
        return true;
    }

    @Override
    public boolean isFieldRequired(@NonNull String fieldId, List<RequiredDto> requiredDto) {
        return false;
    }

    @Override
    public boolean isFieldVisible(@NonNull String fieldId, RequiredDto requiredDto) {
        if(requiredDto == null)
            return true;

        //TODO check for the engine type
        try {
            return MVEL.evalToBoolean(requiredDto.getExpr(), this.registrationService.getRegistrationDto().getMVELDataContext());
        } catch (Exception e) {
            Log.e(TAG, "Failed to validate field visibility", e);
        }
        return true;
    }
}
