package io.mosip.registration.clientmanager.dto.uispec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConditionalBioAttrDto {

    private String ageGroup;
    private String process;
    private String validationExpr;
    private List<String> bioAttributes = null;

}
