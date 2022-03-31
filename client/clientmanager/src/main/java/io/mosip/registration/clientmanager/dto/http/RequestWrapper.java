package io.mosip.registration.clientmanager.dto.http;
import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RequestWrapper<T> {
    private String id;
    private String version;
    @Schema(name = "requestTime", type = "dateTime", description = "Time of request generation", example = "2018-12-10T06:12:52.994Z", required = true)
    private LocalDateTime requestTime;
    private Object metadata;
    @NotNull
    @Valid
    private T request;
}