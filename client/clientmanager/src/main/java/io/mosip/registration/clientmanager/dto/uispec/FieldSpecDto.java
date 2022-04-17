package io.mosip.registration.clientmanager.dto.uispec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldSpecDto {

    private String id;
    private Boolean inputRequired;
    private String type;
    private String fieldType;
    private Integer minimum;
    private Integer maximum;
    private Map<String, String> label;
    private String controlType;
    private List<FieldValidatorDto> validators;
    private RequiredDto visible;
    private String templateName;
    private Boolean required;
    private List<String> bioAttributes;
    private String subType;
    private List<RequiredDto> requiredOn;
    private List<ConditionalBioAttrDto> conditionalBioAttributes;
    private boolean isExceptionPhotoRequired;

}
