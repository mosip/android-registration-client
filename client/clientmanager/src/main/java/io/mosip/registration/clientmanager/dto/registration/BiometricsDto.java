package io.mosip.registration.clientmanager.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BiometricsDto {

    private String modality;
    private String bioSubType;
    private String bioValue;
    private String specVersion;
    private boolean isException;
    private String decodedBioResponse;
    private String signature;
    private boolean isForceCaptured;
    private int numOfRetries;
    private double sdkScore;
    private float qualityScore;
}
