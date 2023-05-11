package io.mosip.registration.clientmanager.dto.http;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResponseWrapper<T> {
    private String id;
    private String version;
    private String responsetime;
    private Object metadata;
    @NotNull
    @Valid
    private T response;

    private List<ServiceError> errors = new ArrayList<>();

}