package io.mosip.registration.clientmanager.dto.sbi;

import lombok.Data;

@Data
public class CaptureBioDetail {

    private String type;
    private int count;
    private String[] bioSubType;
    private String[] exception;
    private int requestedScore;
    private String deviceId;
    private String deviceSubId;
    private String previousHash;
}
