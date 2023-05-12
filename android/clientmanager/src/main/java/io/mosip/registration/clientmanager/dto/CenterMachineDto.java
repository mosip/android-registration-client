package io.mosip.registration.clientmanager.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CenterMachineDto {

    private String machineId;
    private Boolean machineStatus;
    private String machineName;
    private String centerId;
    private Boolean centerStatus;
    private Map<String, String> centerNames;
    private String machineRefId;
}
