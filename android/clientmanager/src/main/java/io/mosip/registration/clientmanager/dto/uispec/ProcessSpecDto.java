package io.mosip.registration.clientmanager.dto.uispec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessSpecDto {

    private String id;
    private String flow;
    private Map<String, String> label;
    private Integer order;
    private List<ScreenSpecDto> screens;

}
