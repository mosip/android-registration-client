package io.mosip.registration.clientmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenericDto {
    private String code;
    private String name;
    private String langCode;
}
