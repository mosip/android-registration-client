package io.mosip.registration.clientmanager.dto.crypto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoRequestDto {
    @NotBlank(message = "Invalid Request")
    private String value;

    @NotBlank(message = "Invalid Request")
    private String publicKey;
}
