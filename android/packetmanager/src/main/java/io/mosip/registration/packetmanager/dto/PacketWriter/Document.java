package io.mosip.registration.packetmanager.dto.PacketWriter;

import java.io.Serializable;

import lombok.Data;

@Data
public class Document implements Serializable {

    private byte[] document;
    private String value;
    private String type;
    private String format;
    private String refNumber;
}
