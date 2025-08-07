package io.mosip.registration.clientmanager.dto.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.SettingsSpecDto;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdSchemaResponse {

    private String id;
    private double idVersion;
    private ProcessSpecDto newProcess;
    private List<FieldSpecDto> schema;
    private String schemaJson;
    private String effectiveFrom;
    private List<SettingsSpecDto> settings;
}
