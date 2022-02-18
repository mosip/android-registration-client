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
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mosip.registration.packetmanager.cbeffutil.CbeffContainerImpl;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIRVersion;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.QualityType;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.RegistryIDType;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.SingleType;
import io.mosip.registration.packetmanager.dto.PacketWriter.BIR;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import io.mosip.registration.packetmanager.dto.PacketWriter.PacketInfo;
import io.mosip.registration.packetmanager.dto.PacketWriter.ProcessedLevelType;
import io.mosip.registration.packetmanager.dto.PacketWriter.PurposeType;

public class PacketManagerHelper {

    private static final String TAG = PacketManagerHelper.class.getSimpleName();

    private String configServerFileStorageURL;
    private String schemaName;

    public PacketManagerHelper(Context context){
        configServerFileStorageURL = ConfigService.getProperty("mosip.kernel.xsdstorage-uri", context);
        schemaName = ConfigService.getProperty("mosip.kernel.xsdfile", context);
    }


    public byte[] getXMLData(BiometricRecord biometricRecord, boolean offlineMode) throws Exception {
        //Reading of xsd file from CBEFF_SCHEMA_FILE_PATH and validation of xml skipped
        CbeffContainerImpl cbeffContainer = new CbeffContainerImpl();
        List<io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR> birList = new ArrayList<>();
        biometricRecord.getSegments().forEach(s -> birList.add(convertToBIR(s)));
        io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIRType bir = cbeffContainer.createBIRType(birList);
        byte[] xmlBytes = cbeffContainer.createXMLBytes(bir);
        return xmlBytes;
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

    public static io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR convertToBIR(BIR bir) {
        List<SingleType> bioTypes = new ArrayList<>();
        for(BiometricType type : bir.getBdbInfo().getType()) {
            bioTypes.add(SingleType.fromValue(type.value()));
        }

        RegistryIDType format = null;
        if (bir.getBdbInfo() != null && bir.getBdbInfo().getFormat() != null) {
            format = new RegistryIDType();
            format.setOrganization(bir.getBdbInfo().getFormat().getOrganization());
            format.setType(bir.getBdbInfo().getFormat().getType());
        }

        RegistryIDType birAlgorithm = null;
        if (bir.getBdbInfo() != null
                && bir.getBdbInfo().getQuality() != null && bir.getBdbInfo().getQuality().getAlgorithm() != null) {
            birAlgorithm = new RegistryIDType();
            birAlgorithm.setOrganization(bir.getBdbInfo().getQuality().getAlgorithm().getOrganization());
            birAlgorithm.setType(bir.getBdbInfo().getQuality().getAlgorithm().getType());
        }


        QualityType qualityType = null;
        if (bir.getBdbInfo() != null && bir.getBdbInfo().getQuality() != null) {
            qualityType = new QualityType();
            qualityType.setAlgorithm(birAlgorithm);
            qualityType.setQualityCalculationFailed(bir.getBdbInfo().getQuality().getQualityCalculationFailed());
            qualityType.setScore(bir.getBdbInfo().getQuality().getScore());
        }

        return new io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR.BIRBuilder()
                .withBdb(bir.getBdb())
                .withVersion(bir.getVersion() == null ? null : new BIRVersion.BIRVersionBuilder()
                        .withMinor(bir.getVersion().getMinor())
                        .withMajor(bir.getVersion().getMajor()).build())
                .withCbeffversion(bir.getCbeffversion() == null ? null : new BIRVersion.BIRVersionBuilder()
                        .withMinor(bir.getCbeffversion().getMinor())
                        .withMajor(bir.getCbeffversion().getMajor()).build())
                .withBirInfo(new io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIRInfo.BIRInfoBuilder().withIntegrity(true).build())
                .withBdbInfo(bir.getBdbInfo() == null ? null : new io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BDBInfo.BDBInfoBuilder()
                        .withFormat(format)
                        .withType(bioTypes)
                        .withQuality(qualityType)
                        .withCreationDate(bir.getBdbInfo().getCreationDate())
                        .withIndex(bir.getBdbInfo().getIndex())
                        .withPurpose(bir.getBdbInfo().getPurpose() == null ? null :
                                io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.PurposeType.fromValue(PurposeType.fromValue(bir.getBdbInfo().getPurpose().name()).value()))
                        .withLevel(bir.getBdbInfo().getLevel() == null ? null :
                                io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.ProcessedLevelType.fromValue(ProcessedLevelType.fromValue(bir.getBdbInfo().getLevel().name()).value()))
                        .withSubtype(bir.getBdbInfo().getSubtype()).build()).build();
    }
}
