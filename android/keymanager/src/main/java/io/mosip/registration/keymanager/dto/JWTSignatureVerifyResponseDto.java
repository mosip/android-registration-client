package io.mosip.registration.keymanager.dto;

import lombok.Data;

@Data
public class JWTSignatureVerifyResponseDto {

    /**
     * The Signature verification status.
     */
    private boolean signatureValid;

    /**
     * The Signature validation message.
     */
    private String message;

    /**
     * The Trust validation status.
     */
    private String trustValid;

}
