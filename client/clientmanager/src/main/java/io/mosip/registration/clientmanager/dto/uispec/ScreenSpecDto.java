package io.mosip.registration.clientmanager.dto.uispec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScreenSpecDto {

    private String name;
    private Map<String, String> label;
    private List<FieldSpecDto> fields;
    private Integer order;

}
