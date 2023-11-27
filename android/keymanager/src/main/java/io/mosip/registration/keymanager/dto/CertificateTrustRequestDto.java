package io.mosip.registration.keymanager.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * Partner Certificates Verify Trust Request DTO.
 */
@Data
public class CertificateTrustRequestDto {

    /**
     * Certificate Data of Partner.
     */
    @NotBlank(message = "Invalid Request")
    String certificateData;

    /**
     * Partner Type.
     */
    @NotBlank(message = "Invalid Request")
    String partnerDomain;

}
