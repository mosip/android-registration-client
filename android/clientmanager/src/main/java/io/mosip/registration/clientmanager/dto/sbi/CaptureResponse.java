package io.mosip.registration.clientmanager.dto.sbi;

import lombok.Data;

import java.util.List;

@Data
public class CaptureResponse {

    private List<CaptureRespDetail> biometrics;

}
