package io.mosip.registration.keymanager.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author George T Abraham
 * @Author Eric John
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignVerifyRequestDto {
    @NotBlank(message = "Invalid Request")
    private String data;

    @NotBlank(message = "Invalid Request")
    private String signature;

    @NotBlank(message = "Invalid Request")
    private String publicKey;
}
