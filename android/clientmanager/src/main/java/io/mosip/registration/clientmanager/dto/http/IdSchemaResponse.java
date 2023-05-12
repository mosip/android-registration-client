package io.mosip.registration.clientmanager.dto.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdSchemaResponse {

    private String id;
    private double idVersion;
    private ProcessSpecDto newProcess;
    private String schemaJson;
    private String effectiveFrom;
}
