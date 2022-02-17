package io.mosip.registration.clientmanager.dto.sbi;

import lombok.Data;

import java.util.List;

@Data
public class SBIResponse {

    private String transactionId;
    private List<BiometricsDto> biometricsDtoList;
}
