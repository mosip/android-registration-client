package io.mosip.registration.clientmanager.dto.crypto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
