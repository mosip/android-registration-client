package io.mosip.registration.packetmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Biometrics {

    private String format;
    private double version;
    private String value;
}
