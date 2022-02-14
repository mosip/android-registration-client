package io.mosip.registration.packetmanager.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BiometricsException {

    private String type;
    private String missingBiometric;
    private String reason;
    private String exceptionType;
    private String individualType;
}
