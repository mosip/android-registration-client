package io.mosip.registration.keymanager.dto;

import lombok.Data;

/**
 * DTO class for certificate verification response.
 */
@Data
public class CertificateTrustResponseDto {

    /**
     * Status of certificate verification.
     */
    private Boolean status;

}
