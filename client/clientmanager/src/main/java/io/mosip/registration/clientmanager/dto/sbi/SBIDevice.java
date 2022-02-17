package io.mosip.registration.clientmanager.dto.sbi;

import lombok.Data;

@Data
public class SBIDevice {

    private DigitalId digitalId;
    private String[] specVersion;
    private String deviceStatus;
    private String certification;
    private String firmWare;
    private String[] deviceSubId;
    private String purpose;

}
