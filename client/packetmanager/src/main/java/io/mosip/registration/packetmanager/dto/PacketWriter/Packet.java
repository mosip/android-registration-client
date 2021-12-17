package io.mosip.registration.packetmanager.dto.PacketWriter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Packet {

    private PacketInfo packetInfo;
    private byte[] packet;
}
