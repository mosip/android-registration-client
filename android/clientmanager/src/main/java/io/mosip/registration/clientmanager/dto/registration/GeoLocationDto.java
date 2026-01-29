package io.mosip.registration.clientmanager.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeoLocationDto {
    private Double longitude;
    private Double latitude;
}