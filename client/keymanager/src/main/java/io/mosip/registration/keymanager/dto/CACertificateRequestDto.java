package io.mosip.registration.keymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CACertificateRequestDto {

    /**
     * Certificate Data of CA or Sub-CA.
     */
    @NotBlank()
    String certificateData;

    /**
     * Certificate Data of CA or Sub-CA.
     */
    @NotBlank()
    String partnerDomain;
}
