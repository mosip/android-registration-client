package io.mosip.registration.keymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

/**
 * @Author George T Abraham
 * @Author Eric John
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignRequestDto {
    @NotBlank(message = "Invalid Request")
    private String data;
}
