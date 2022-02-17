package io.mosip.registration.clientmanager.dto.sbi;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BiometricsDto {

    private byte[] attributeISO;
    private String bioAttribute;
    private String bioSubType;
    private String modalityName;
    private double qualityScore;
    private boolean isForceCaptured;
    private int numOfRetries;
    private boolean isCaptured;
    private String subType;
    private double sdkScore;
    private String payLoad;
    private String signature;
    private String specVersion;

    public BiometricsDto(@NotNull String modalityName, @NotNull String bioAttribute,
                         @NotNull byte[] attributeISO, @NotNull String specVersion,
                         double qualityScore, @NotNull String decodedPayload, @NotNull String signature) {
        this.modalityName = modalityName;
        this.bioAttribute = bioAttribute;
        this.specVersion = specVersion;
        this.attributeISO = attributeISO;
        this.isCaptured = true;
        this.qualityScore = qualityScore;
        this.payLoad = decodedPayload;
        this.signature = signature;
    }
}
