package io.mosip.registration.clientmanager.dto.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Anshul vanawat
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacketStatusUpdateDto {

    private String registrationId;

    private String statusCode;
}
