package io.mosip.registration.packetmanager.util;

import static io.mosip.registration.packetmanager.util.PacketManagerConstant.CREATION_DATE;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.ENCRYPTED_HASH;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.ID;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.PACKET_NAME;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.PROCESS;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.PROVIDER_NAME;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.PROVIDER_VERSION;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.SCHEMA_VERSION;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.SIGNATURE;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.SOURCE;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.*;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import io.mosip.registration.packetmanager.dto.PacketWriter.PacketInfo;
import org.apache.commons.io.FileUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

/**
 * @Author Anshul Vanawat
 */
@Singleton
public class PacketManagerHelper {

    private static final String TAG = PacketManagerHelper.class.getSimpleName();

    private String configServerFileStorageURL;
    private String schemaName;

    @Inject
    public PacketManagerHelper(Context context){
        configServerFileStorageURL = ConfigService.getProperty("mosip.kernel.xsdstorage-uri", context);
        schemaName = ConfigService.getProperty("mosip.kernel.xsdfile", context);
    }


    public byte[] getXMLData(BiometricRecord biometricRecord, boolean offlineMode) throws Exception {
        //TODO validation of xml with XSD skipped
        BIR bir = new BIR();
        BIRInfo.BIRInfoBuilder infoBuilder = new BIRInfo.BIRInfoBuilder().withIntegrity(false);
        BIRInfo birInfo = new BIRInfo(infoBuilder);
        bir.setBirInfo(birInfo);
        bir.setBirs(biometricRecord.getSegments());

        RegistryMatcher matcher = new RegistryMatcher();
        matcher.bind(byte[].class, new ByteArrayTransformer());
        matcher.bind(LocalDateTime.class, new LocalDateTimeTransformer());
        matcher.bind(BiometricType.class, new BiometricTypeTransformer());
        matcher.bind(ProcessedLevelType.class, new ProcessedLevelTypeTransformer());
        matcher.bind(PurposeType.class, new PurposeTypeTransformer());
        Serializer serializer = new Persister(matcher);
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            serializer.write(bir, baos);
            return baos.toByteArray();
        }
    }

    public static byte[] generateHash(List<String> order, Map<String, byte[]> data) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (order != null && !order.isEmpty()) {
            for (String name : order) {
                outputStream.write(data.get(name));
            }
            return HMACUtils2.digestAsPlainText(outputStream.toByteArray()).getBytes();
        }
        return null;
    }

    public static Map<String, Object> getMetaMap(PacketInfo packetInfo) {
        Map<String, Object> metaMap = new HashMap<>();
        metaMap.put(ID, packetInfo.getId());
        metaMap.put(PACKET_NAME, packetInfo.getPacketName());
        metaMap.put(SOURCE, packetInfo.getSource());
        metaMap.put(PROCESS, packetInfo.getProcess());
        metaMap.put(SCHEMA_VERSION, packetInfo.getSchemaVersion());
        metaMap.put(SIGNATURE, packetInfo.getSignature());
        metaMap.put(ENCRYPTED_HASH, packetInfo.getEncryptedHash());
        metaMap.put(PROVIDER_NAME, packetInfo.getProviderName());
        metaMap.put(PROVIDER_VERSION, packetInfo.getProviderVersion());
        metaMap.put(CREATION_DATE, packetInfo.getCreationDate());
        return metaMap;
    }

    public static PacketInfo getPacketInfo(Map<String, Object> metaMap) {
        PacketInfo packetInfo = new PacketInfo();
        packetInfo.setId((String) metaMap.get(ID));
        packetInfo.setPacketName((String) metaMap.get(PACKET_NAME));
        packetInfo.setSource((String) metaMap.get(SOURCE));
        packetInfo.setProcess((String) metaMap.get(PROCESS));
        packetInfo.setSchemaVersion((String) metaMap.get(SCHEMA_VERSION));
        packetInfo.setSignature((String) metaMap.get(SIGNATURE));
        packetInfo.setEncryptedHash((String) metaMap.get(ENCRYPTED_HASH));
        packetInfo.setProviderName((String) metaMap.get(PROVIDER_NAME));
        packetInfo.setProviderVersion((String) metaMap.get(PROVIDER_VERSION));
        packetInfo.setCreationDate((String) metaMap.get(CREATION_DATE));
        return packetInfo;
    }
}
