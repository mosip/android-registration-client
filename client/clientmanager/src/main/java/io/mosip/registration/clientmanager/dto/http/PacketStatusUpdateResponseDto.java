package io.mosip.registration.clientmanager.dto.http;

import java.util.List;
import lombok.Data;

/**
 *
 * @author Anshul vanawat
 *
 */
@Data
public class PacketStatusUpdateResponseDto {

    /** The packet status update list. */
    private List<PacketStatusUpdateDto> packetStatusUpdateList;
}