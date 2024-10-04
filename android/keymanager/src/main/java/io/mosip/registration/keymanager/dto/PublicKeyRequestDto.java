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
public class PublicKeyRequestDto {
    @NotBlank(message = "Invalid Request")
    String alias;
}
