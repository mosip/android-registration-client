package io.mosip.registration.clientmanager.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BiometricsDto {

    private String modality;
    private String attribute;
    private boolean isException;
    private String bioResponse;
    private String signature;
    private String bioValue;

}
