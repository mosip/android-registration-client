package io.mosip.registration.packetmanager.spi;

import java.util.List;
import java.util.Map;

import io.mosip.registration.packetmanager.dto.PacketWriter.Document;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord;
import io.mosip.registration.packetmanager.dto.PacketWriter.PacketInfo;

/**
 * @Author Anshul Vanawat
 */
public interface PacketWriterService {
    void setField(String id, String fieldName, Object value);

    void setFields(String id, Map<String, String> fields);

    void setBiometric(String id, String fieldName, BiometricRecord biometricRecord);

    void setDocument(String id, String documentName, Document document);

    void addMetaInfo(String id, Map<String, String> metaInfo);

    void addMetaInfo(String id, String key, String value);

    void addAudits(String id, List<Map<String, String>> audits);

    void addAudit(String id, Map<String, String> audit);

    List<PacketInfo> persistPacket(String id, String version, String schemaJson, String source, String process, boolean offlineMode);
}
