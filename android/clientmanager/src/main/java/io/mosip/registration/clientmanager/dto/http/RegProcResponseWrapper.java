package io.mosip.registration.clientmanager.dto.http;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class RegProcResponseWrapper<T> {

    private String id;
    private String version;
    private String responsetime;
    @NotNull
    @Valid
    private T response;
    private List<ServiceError> errors = new ArrayList<>();

}
