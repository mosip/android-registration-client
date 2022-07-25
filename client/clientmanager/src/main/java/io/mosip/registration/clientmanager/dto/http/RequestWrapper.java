package io.mosip.registration.clientmanager.dto.http;
import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RequestWrapper<T> {
    private String id;
    private String version;
    private LocalDateTime requestTime;
    private Object metadata;
    @NotNull
    @Valid
    private T request;
}