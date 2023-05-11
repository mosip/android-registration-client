package io.mosip.registration.clientmanager.dto.sbi;

import lombok.Data;
import java.util.List;

@Data
public class CaptureRequest {

    private String env;
    private String purpose;
    private String specVersion;
    private int timeout;
    private String captureTime;
    private String transactionId;
    private List<CaptureBioDetail> bio;
}
