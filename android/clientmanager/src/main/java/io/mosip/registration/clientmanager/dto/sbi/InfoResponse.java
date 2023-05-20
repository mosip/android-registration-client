package io.mosip.registration.clientmanager.dto.sbi;

import lombok.Data;

@Data
public class InfoResponse {

    private String deviceInfo;
    private ErrorDto error;
}
