package io.mosip.registration.keymanager.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author George T Abraham
 * @author Eric John
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicKeyResponseDto {
    private String publicKey;
}
