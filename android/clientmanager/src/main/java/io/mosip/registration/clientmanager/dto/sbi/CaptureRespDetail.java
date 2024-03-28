package io.mosip.registration.clientmanager.dto.sbi;

import lombok.Data;

@Data
public class CaptureRespDetail {

    private String specVersion;
    private String data;
    private String hash;
    private ErrorDto error;
    private String sessionKey;
    private String thumbprint;

    public CaptureRespDetail() {}

    public CaptureRespDetail(String specVersion, String data, String hash, ErrorDto error, String sessionKey, String thumbprint) {
        this.specVersion = specVersion;
        this.data = data;
        this.hash = hash;
        this.error = error;
        this.sessionKey = sessionKey;
        this.thumbprint = thumbprint;
    }

}
