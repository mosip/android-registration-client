package io.mosip.registration.clientmanager.dto.crypto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignRequestDto {
    @NotBlank(message = "Invalid Request")
    private String data;
}
