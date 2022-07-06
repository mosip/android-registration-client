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
    void setField(String id, String fieldId, Object value);

    void setBiometric(String id, String fieldId, BiometricRecord biometricRecord);

    void setDocument(String id, String fieldId, Document document);

    void addMetaInfo(String id, String key, Object value);

    void addAudits(String id, List<Map<String, String>> audits);

    void addAudit(String id, Map<String, String> audit);

    String persistPacket(String id, String version, String schemaJson, String source, String process, boolean offlineMode, String refId);
}
