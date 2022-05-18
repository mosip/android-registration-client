package io.mosip.registration.keymanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CACertificateResponseDto {

    private String status;
    private LocalDateTime timestamp;
}
