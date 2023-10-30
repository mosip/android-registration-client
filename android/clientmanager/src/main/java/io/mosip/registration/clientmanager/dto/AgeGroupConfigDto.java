package io.mosip.registration.clientmanager.dto;

import java.util.List;

import lombok.Data;

@Data
public class AgeGroupConfigDto {

    private List<String> bioAttributes;
    private Boolean isGuardianAuthRequired;

}
