package io.mosip.registration.clientmanager.dto.http;

import org.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTO{
    @NotBlank(message = "Invalid Request")
    private String url;

    private JSONObject body;

    @NotBlank(message = "Invalid Request")
    private ArrayList<String> header;

    @NotBlank(message = "Invalid Request")
    private boolean authRequired;

    @NotBlank(message = "Invalid Request")
    private Boolean isSignRequired;

    @NotBlank(message = "Invalid Request")
    private boolean isRequestSignRequired;
}