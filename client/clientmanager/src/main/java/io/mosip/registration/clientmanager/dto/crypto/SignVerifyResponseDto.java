package io.mosip.registration.clientmanager.dto.crypto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignVerifyResponseDto {
    private boolean verified;
}