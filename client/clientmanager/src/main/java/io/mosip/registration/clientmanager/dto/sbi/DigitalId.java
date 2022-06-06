package io.mosip.registration.clientmanager.dto.sbi;

import lombok.Data;

@Data
public class DigitalId {

    private String serialNo;
    private String make;
    private String model;
    private String type;
    private String deviceSubType;
    private String deviceProvider;
    private String deviceProviderId;
    private String dateTime;
}
