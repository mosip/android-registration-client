package io.mosip.registration.clientmanager.spi.packet;

import java.util.List;
import java.util.Map;

import io.mosip.registration.clientmanager.dto.packet.Document;
import io.mosip.registration.clientmanager.dto.packet.PacketInfo;
import io.mosip.registration.clientmanager.entities.BiometricRecord;

public interface PacketWriterService {

    public void setField(String id, String fieldName, String value);

    public void setFields(String id, Map<String, String> fields);

    public void setBiometric(String id, String fieldName, BiometricRecord biometricRecord);

    public void setDocument(String id, String documentName, Document document);

    public void addMetaInfo(String id, Map<String, String> metaInfo);

    public void addMetaInfo(String id, String key, String value);

    public void addAudits(String id, List<Map<String, String>> audits);

    public void addAudit(String id, Map<String, String> audit);

    public List<PacketInfo> persistPacket(String id, String version, String schemaJson, String source, String process, boolean offlineMode);

}
