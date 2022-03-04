package io.mosip.registration.clientmanager.dto.crypto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicKeyRequestDto {
    @NotBlank(message = "Invalid Request")
    String serverProfile;
}
