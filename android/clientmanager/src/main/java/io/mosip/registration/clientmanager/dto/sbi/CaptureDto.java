package io.mosip.registration.clientmanager.dto.sbi;

import lombok.Data;

@Data
public class CaptureDto {

    private String digitalId;
    private String bioType;
    private String deviceCode;
    private String deviceServiceVersion;
    private String bioSubType;
    private String purpose;
    private String env;
    private String bioValue;
    private String transactionId;
    private String timestamp;
    private String requestedScore;
    private float qualityScore;
}
