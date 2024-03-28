package io.mosip.registration.clientmanager.dto.http;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OnboardResponseWrapper<T> {

    private String id;
    private String version;
    private String responseTime;
    private String transactionID;
    @NotNull
    @Valid
    private T response;
    private List<OnboardError> errors = new ArrayList<>();

}
