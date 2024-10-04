package io.mosip.registration.clientmanager.dto.http;

import org.json.JSONObject;
import java.util.List;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    @NotBlank(message = "Invalid Request")
    private String url;

    private JSONObject body;

    private List<String> header;

    @NotBlank(message = "Invalid Request")
    private boolean authRequired;

    @NotBlank(message = "Invalid Request")
    private boolean isSignRequired;

    @NotBlank(message = "Invalid Request")
    private boolean isRequestSignRequired;
}