package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.dto.uispec.FieldValidatorDto;
import io.mosip.registration.clientmanager.dto.uispec.RequiredDto;

import java.util.List;

public interface FieldSpecService {

    /**
     *
     * @param fieldId
     * @param value
     * @param langCode
     * @param validatorList
     * @return
     */
    boolean validateField(String fieldId, String value, String langCode, List<FieldValidatorDto> validatorList);

    /**
     *
     * @param fieldId
     * @param requiredDto
     * @return
     */
    boolean isFieldRequired(String fieldId, List<RequiredDto> requiredDto);

    /**
     *
     * @param fieldId
     * @param requiredDto
     * @return
     */
    boolean isFieldVisible(String fieldId, RequiredDto requiredDto);
}
