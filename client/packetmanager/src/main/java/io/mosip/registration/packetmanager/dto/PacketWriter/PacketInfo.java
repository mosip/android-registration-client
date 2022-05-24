package io.mosip.registration.packetmanager.dto.PacketWriter;

import java.io.Serializable;

import lombok.Data;

@Data
public class PacketInfo implements Serializable {

    private String id;
    private String packetName;
    private String source;
    private String process;
    private String refId;
    private String schemaVersion;
    private String signature;
    private String encryptedHash;
    private String providerName;
    private String providerVersion;
    private String creationDate;
}
