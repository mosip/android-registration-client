package io.mosip.registration.keymanager.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class JWTSignatureVerifyRequestDto {

    @NotBlank
    private String jwtSignatureData;

    private String actualData;

    /**
     * Application id of decrypting module
     */
    private String applicationId;

    /**
     * Refrence Id
     */
    private String referenceId;

    /**
     * Certificate to be use in JWT Signature verification.
     */
    private String certificateData;

    /**
     * Flag to validate against trust store.
     */
    private Boolean validateTrust;

    /**
     * Domain to be considered to validate trust store
     */
    private String domain;
}
