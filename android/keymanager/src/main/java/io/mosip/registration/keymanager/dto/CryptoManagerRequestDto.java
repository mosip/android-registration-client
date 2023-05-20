package io.mosip.registration.keymanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CryptoManagerRequestDto {

    private String applicationId;
    private String referenceId;
    private LocalDateTime timeStamp;
    private String data;
    private String salt;
    private String aad;
}
