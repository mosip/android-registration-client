package io.mosip.registration.clientmanager.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BiometricsDto {

    private String modality;
    private String bioSubType;
    private String bioValue;
    private String specVersion;
    private boolean isException;

    public String getDecodedBioResponse() {
        return decodedBioResponse;
    }

    private String decodedBioResponse;
    private String signature;
    private boolean isForceCaptured;
    private int numOfRetries;
    private double sdkScore;

    public float getQualityScore() {
        return qualityScore;
    }

    private float qualityScore;

    public void setIsException(boolean exception) {
        isException = exception;
    }

    public void setIsForceCaptured(boolean forceCaptured) {
        isForceCaptured = forceCaptured;
    }


    public String getBioSubType() {
        return  bioSubType;
    }
    public String getBioValue(){
        return bioValue;
    }

    public String getModality(){return  modality;}
}
