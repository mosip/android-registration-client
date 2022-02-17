package io.mosip.registration.clientmanager.dto.sbi;

import lombok.Data;

@Data
public class SBIRequest {

    private String modality;
    private String[] exceptions;
    private String process;
    private String environment;
    private int timeout;
    private int count;
    private int requestedScore;

}
