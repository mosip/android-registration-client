package io.mosip.registration.clientmanager.dto.sbi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceDto {

    private String deviceId;
    private String deviceStatus;
    private String firmware;
    private String certification;
    private String serviceVersion;
    private int[] deviceSubId;
    private String callbackId;
    private String digitalId;
    private String deviceCode;
    private String[] specVersion;
    private String env;
    private String purpose;
    private ErrorDto error;
}
