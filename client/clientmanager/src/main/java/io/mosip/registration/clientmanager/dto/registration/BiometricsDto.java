package io.mosip.registration.clientmanager.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BiometricsDto {

    private String modality;
    private String attribute;
    private String specVersion;
    private boolean isException;
    private String decodedBioResponse;
    private String signature;
    private String bioValue;
    private float qualityScore;
}
