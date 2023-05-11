package io.mosip.registration.clientmanager.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ConsentDto {

    private String consentText;
    private LocalDateTime acceptedOn;
}
