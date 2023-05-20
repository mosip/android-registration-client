package io.mosip.registration.keymanager.dto;

import lombok.Data;

@Data
public class CertificateRequestDto {

    private String applicationId;
    private String referenceId;
    private String certificateData;
}
