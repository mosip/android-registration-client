package io.mosip.registration.clientmanager.dto.registration;

import io.mosip.registration.clientmanager.constant.AuthMode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OperatorDto {

    private String username;
    private AuthMode authMode;
    private List<BiometricsDto> biometrics;
    private String reviewComment;
}
