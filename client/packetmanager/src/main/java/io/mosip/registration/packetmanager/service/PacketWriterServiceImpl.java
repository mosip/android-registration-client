package io.mosip.registration.packetmanager.service;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.packetmanager.util.ConfigService;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricsType;
import io.mosip.registration.packetmanager.dto.PacketWriter.Document;
import io.mosip.registration.packetmanager.dto.PacketWriter.DocumentType;
import io.mosip.registration.packetmanager.dto.PacketWriter.HashSequenceMetaInfo;
import io.mosip.registration.packetmanager.dto.PacketWriter.Packet;
import io.mosip.registration.packetmanager.dto.PacketWriter.PacketInfo;
import io.mosip.registration.packetmanager.dto.PacketWriter.RegistrationPacket;
import io.mosip.registration.packetmanager.spi.PacketWriterService;
import io.mosip.registration.packetmanager.util.JsonUtils;
import io.mosip.registration.packetmanager.util.PacketKeeper;
import io.mosip.registration.packetmanager.util.PacketManagerConstant;
import io.mosip.registration.packetmanager.util.PacketManagerHelper;

/**
 * @Author Anshul Vanawat
 */
@Singleton
public class PacketWriterServiceImpl implements PacketWriterService {

    private static final String TAG = PacketWriterServiceImpl.class.getSimpleName();
    private static final String UNDERSCORE = "_";
    private static final String HASHSEQUENCE1 = "hashSequence1";
    private static final String HASHSEQUENCE2 = "hashSequence2";

    private RegistrationPacket registrationPacket = null;
    private static Map<String, String> categorySubpacketMapping = new HashMap<>();

    static {
        categorySubpacketMapping.put("pvt", "id");
        categorySubpacketMapping.put("kyc", "id");
        categorySubpacketMapping.put("none", "id,evidence,optional");
        categorySubpacketMapping.put("evidence", "evidence");
        categorySubpacketMapping.put("optional", "optional");
    }

    private PacketManagerHelper packetManagerHelper;
    private PacketKeeper packetKeeper;

    private String defaultSubpacketName;
    private String defaultProviderVersion;
    private Context context;
    private String timeFormat;

    @Inject
    public PacketWriterServiceImpl(Context appContext, PacketManagerHelper packetManagerHelper,
                                   PacketKeeper packetKeeper){
        this.context = appContext;
        this.packetKeeper = packetKeeper;
        this.packetManagerHelper = packetManagerHelper;
    }

    public RegistrationPacket initialize(String id) {

        if (this.registrationPacket == null || !registrationPacket.getRegistrationId().equalsIgnoreCase(id)) {
            this.registrationPacket = new RegistrationPacket();
            this.registrationPacket.setRegistrationId(id);
        }

        defaultSubpacketName = ConfigService.getProperty("mosip.kernel.packet.default_subpacket_name", context);
        defaultProviderVersion = ConfigService.getProperty("default.provider.version", context);
        timeFormat = ConfigService.getProperty("mosip.utc-datetime-pattern", context);

        return registrationPacket;
    }

    @Override
    public void setField(String id, String fieldId, Object value) {
        this.initialize(id).setField(fieldId, value);
    }

    @Override
    public void setBiometric(String id, String fieldId, BiometricRecord biometricRecord) {
        biometricRecord.getSegments().removeIf(Objects::isNull);
        this.initialize(id).setBiometricField(fieldId, biometricRecord);
    }

    @Override
    public void setDocument(String id, String fieldId, Document document) {
        this.initialize(id).setDocumentField(fieldId, document);
    }

    @Override
    public void addMetaInfo(String id, String key, Object value) {
        this.initialize(id).setMetaData(key, value);
    }

    @Override
    public void addAudits(String id, List<Map<String, String>> audits) {
        this.initialize(id).setAudits(audits);
    }

    @Override
    public void addAudit(String id, Map<String, String> audit) {
        this.initialize(id).setAudit(audit);
    }

    @Override
    public String persistPacket(String id, String version, String schemaJson, String source, String process, boolean offlineMode, String refId) {
        try {
            return createPacket(id, version, schemaJson, source, process, offlineMode, refId);
        } catch (Exception e) {
            Log.e(TAG, "Persist packet failed : ", e);
        }
        return null;
    }

    private String createPacket(String id, String version, String schemaJson, String source, String process, boolean offlineMode, String refId) throws Exception {
        Log.i(TAG, "Started packet creation");

        if (this.registrationPacket == null || !registrationPacket.getRegistrationId().equalsIgnoreCase(id))
            throw new Exception("Registration packet is null or registration id does not exists");

        String containerPath = null;
        Map<String, List<Object>> identityProperties = loadSchemaFields(schemaJson);

        try {
            int counter = 1;
            for (String subPacketName : identityProperties.keySet()) {
                Log.i(TAG, "Started Subpacket: " + subPacketName);

                List<Object> schemaFields = identityProperties.get(subPacketName);
                byte[] subpacketBytes = createSubpacket(Double.valueOf(version), schemaFields, defaultSubpacketName.equalsIgnoreCase(subPacketName),
                        id, offlineMode);

                PacketInfo packetInfo = new PacketInfo();
                packetInfo.setProviderName(this.getClass().getSimpleName());
                packetInfo.setSchemaVersion(new Double(version).toString());
                packetInfo.setId(id);
                packetInfo.setRefId(refId);
                packetInfo.setSource(source);
                packetInfo.setProcess(process);
                packetInfo.setPacketName(id + UNDERSCORE + subPacketName);
                packetInfo.setCreationDate(OffsetDateTime.now().toInstant().toString());
                packetInfo.setProviderVersion(defaultProviderVersion);

                Packet packet = new Packet();
                packet.setPacketInfo(packetInfo);
                packet.setPacket(subpacketBytes);
                packetKeeper.putPacket(packet);
                Log.i(TAG, "Completed SubPacket Creation");

                if (counter == identityProperties.keySet().size()) {
                    containerPath = packetKeeper.pack(packetInfo.getId(), packetInfo.getSource(), packetInfo.getProcess(), refId);
                    if (containerPath == null) {
                        packetKeeper.deletePacket(id, source, process);
                        throw new Exception("Failed to pack the created zip");
                    }
                }
                counter++;
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception occurred. Deleting the packet.", e);
            packetKeeper.deletePacket(id, source, process);
            throw new Exception("Exception occurred in createPacket " + e.getStackTrace());
        } finally {
            this.registrationPacket = null;
        }
        Log.i(TAG, "Exiting packet creation");
        return containerPath;
    }

    private byte[] createSubpacket(double version, List<Object> schemaFields, boolean isDefault, String id, boolean offlineMode)
            throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream subpacketZip = new ZipOutputStream(new BufferedOutputStream(out))) {

            Map<String, Object> identity = new HashMap<String, Object>();
            Map<String, HashSequenceMetaInfo> hashSequences = new HashMap<>();

            identity.put(PacketManagerConstant.IDSCHEMA_VERSION, version);
            this.registrationPacket.getMetaData().put(PacketManagerConstant.REGISTRATIONID, id);
            this.registrationPacket.getMetaData().put(PacketManagerConstant.META_CREATION_DATE, this.registrationPacket.getCreationDate());

            for (Object obj : schemaFields) {
                Map<String, Object> field = (Map<String, Object>) obj;
                String fieldName = (String) field.get(PacketManagerConstant.SCHEMA_ID);

                switch ((String) field.get(PacketManagerConstant.SCHEMA_TYPE)) {
                    case PacketManagerConstant.BIOMETRICS_TYPE:
                        //if (this.registrationPacket.getBiometrics().get(fieldName) != null)
                            addBiometricDetailsToZip(fieldName, identity, subpacketZip, hashSequences, offlineMode);
                        break;
                    case PacketManagerConstant.DOCUMENTS_TYPE:
                        if (this.registrationPacket.getDocuments().get(fieldName) != null)
                            addDocumentDetailsToZip(fieldName, identity, subpacketZip, hashSequences, offlineMode);
                        break;
                    default:
                        if (this.registrationPacket.getDemographics().get(fieldName) != null) {
                            identity.put(fieldName, this.registrationPacket.getDemographics().get(fieldName));
                        }
                        break;
                }
            }

            byte[] identityBytes = getIdentity(identity).getBytes();
            addEntryToZip(PacketManagerConstant.IDENTITY_FILENAME_WITH_EXT, identityBytes, subpacketZip);
            addHashSequenceWithSource(PacketManagerConstant.DEMOGRAPHIC_SEQ, PacketManagerConstant.IDENTITY_FILENAME, identityBytes,
                    hashSequences);
            addOtherFilesToZip(isDefault, subpacketZip, hashSequences, offlineMode);

        } catch (Exception e) {
            Log.e(TAG, "Error while createSubPacket : ", e);
            throw e;
        }

        return out.toByteArray();
    }

    private Map<String, List<Object>> loadSchemaFields(String schemaJson) throws Exception {
        Map<String, List<Object>> packetBasedMap = new HashMap<String, List<Object>>();

        try {
            JSONObject schema = new JSONObject(schemaJson);
            schema = schema.getJSONObject(PacketManagerConstant.PROPERTIES);
            schema = schema.getJSONObject(PacketManagerConstant.IDENTITY);
            schema = schema.getJSONObject(PacketManagerConstant.PROPERTIES);

            JSONArray fieldNames = schema.names();
            for (int i = 0; i < fieldNames.length(); i++) {
                String fieldName = fieldNames.getString(i);
                JSONObject fieldDetail = schema.getJSONObject(fieldName);
                String fieldCategory = fieldDetail.has(PacketManagerConstant.SCHEMA_CATEGORY) ?
                        fieldDetail.getString(PacketManagerConstant.SCHEMA_CATEGORY) : "none";
                String packets = categorySubpacketMapping.get(fieldCategory.toLowerCase());

                String[] packetNames = packets.split(",");
                for (String packetName : packetNames) {
                    if (!packetBasedMap.containsKey(packetName)) {
                        packetBasedMap.put(packetName, new ArrayList<Object>());
                    }

                    Map<String, String> attributes = new HashMap<>();
                    attributes.put(PacketManagerConstant.SCHEMA_ID, fieldName);
                    attributes.put(PacketManagerConstant.SCHEMA_TYPE, fieldDetail.has(PacketManagerConstant.SCHEMA_REF) ?
                            fieldDetail.getString(PacketManagerConstant.SCHEMA_REF) : fieldDetail.getString(PacketManagerConstant.SCHEMA_TYPE));
                    packetBasedMap.get(packetName).add(attributes);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Load Schema Fields failed" + e.getStackTrace());
            throw new Exception("Load Schema Fields failed" +
                    e.getStackTrace());
        }
        return packetBasedMap;
    }

    private void addDocumentDetailsToZip(String fieldName, Map<String, Object> identity,
                                         ZipOutputStream zipOutputStream, Map<String, HashSequenceMetaInfo> hashSequences, boolean offlineMode) throws Exception {
        Log.d(TAG, "Adding field : " + fieldName);
        Document document = this.registrationPacket.getDocuments().get(fieldName);
        //filename without extension must be set as value in ID.json
        identity.put(fieldName, new DocumentType(fieldName, document.getType(), document.getFormat()));
        String fileName = String.format("%s.%s", fieldName, document.getFormat());
        addEntryToZip(fileName, document.getDocument(), zipOutputStream);
        this.registrationPacket.getMetaData().put(fieldName, document.getType());

        addHashSequenceWithSource(PacketManagerConstant.DEMOGRAPHIC_SEQ, fieldName, document.getDocument(),
                hashSequences);
    }

    private void addBiometricDetailsToZip(String fieldName, Map<String, Object> identity,
                                          ZipOutputStream zipOutputStream, Map<String, HashSequenceMetaInfo> hashSequences, boolean offlineMode) throws Exception {
        BiometricRecord birType = this.registrationPacket.getBiometrics().get(fieldName);
        if (birType != null && birType.getSegments() != null && !birType.getSegments().isEmpty()) {
            Log.d(TAG, "Adding field : " + fieldName);
            byte[] xmlBytes = null;
            try {
                xmlBytes = packetManagerHelper.getXMLData(birType, offlineMode);
            } catch (Exception e) {
                Log.e(TAG, "Get XML Data failed", e);
            }

            addEntryToZip(String.format(PacketManagerConstant.CBEFF_FILENAME_WITH_EXT, fieldName), xmlBytes, zipOutputStream);
            identity.put(fieldName, new BiometricsType(PacketManagerConstant.CBEFF_FILE_FORMAT,
                    PacketManagerConstant.CBEFF_VERSION, String.format(PacketManagerConstant.CBEFF_FILENAME, fieldName)));
            addHashSequenceWithSource(PacketManagerConstant.BIOMETRIC_SEQ, String.format(PacketManagerConstant.CBEFF_FILENAME,
                    fieldName), xmlBytes, hashSequences);
        }
    }

    private void addEntryToZip(String fileName, byte[] data, ZipOutputStream zipOutputStream)
            throws Exception {
        Log.i(TAG, "Adding file : " + fileName);

        try {
            if (data != null) {
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(data);
            }
        } catch (IOException e) {
            Log.e(TAG, "Adding file failed : " + fileName);
            throw e;
        }
    }

    private void addHashSequenceWithSource(String sequenceType, String name, byte[] bytes,
                                           Map<String, HashSequenceMetaInfo> hashSequences) {
        if (!hashSequences.containsKey(sequenceType))
            hashSequences.put(sequenceType, new HashSequenceMetaInfo(sequenceType));

        hashSequences.get(sequenceType).addHashSource(name, bytes);
    }


    private void addOtherFilesToZip(boolean isDefault, ZipOutputStream zipOutputStream,
                                    Map<String, HashSequenceMetaInfo> hashSequences, boolean offlineMode) throws JsonProcessingException, Exception, IOException, NoSuchAlgorithmException {

        if (isDefault) {
            addOperationsBiometricsToZip(PacketManagerConstant.OFFICER,
                    zipOutputStream, hashSequences, offlineMode);
            addOperationsBiometricsToZip(PacketManagerConstant.SUPERVISOR,
                    zipOutputStream, hashSequences, offlineMode);

            if (this.registrationPacket.getAudits() == null || this.registrationPacket.getAudits().isEmpty()) {
                Log.e(TAG, "AUDITS_REQUIRED");
                throw new Exception("addOtherFilesToZip failed : AUDITS_REQUIRED");
            }

            byte[] auditBytes = JsonUtils.javaObjectToJsonString(this.registrationPacket.getAudits()).getBytes();
            addEntryToZip(PacketManagerConstant.AUDIT_FILENAME_WITH_EXT, auditBytes, zipOutputStream);
            addHashSequenceWithSource(PacketManagerConstant.OPERATIONS_SEQ, PacketManagerConstant.AUDIT_FILENAME, auditBytes,
                    hashSequences);

            HashSequenceMetaInfo hashSequenceMetaInfo = hashSequences.get(PacketManagerConstant.OPERATIONS_SEQ);
            addEntryToZip(PacketManagerConstant.PACKET_OPER_HASH_FILENAME,
                    PacketManagerHelper.generateHash(hashSequenceMetaInfo.getValue(), hashSequenceMetaInfo.getHashSource()),
                    zipOutputStream);

            List list = new ArrayList<HashSequenceMetaInfo>();
            list.add(hashSequenceMetaInfo);
            this.registrationPacket.getMetaData().put(HASHSEQUENCE2, list);
        }

        addPacketDataHash(hashSequences, zipOutputStream);
        addEntryToZip(PacketManagerConstant.PACKET_META_FILENAME, getIdentity(this.registrationPacket.getMetaData()).getBytes(), zipOutputStream);
    }

    private void addOperationsBiometricsToZip(String operationType,
                                              ZipOutputStream zipOutputStream, Map<String, HashSequenceMetaInfo> hashSequences, boolean offlineMode) throws Exception {

        BiometricRecord biometrics = this.registrationPacket.getBiometrics().get(operationType);

        if (biometrics != null && biometrics.getSegments() != null && !biometrics.getSegments().isEmpty()) {
            byte[] xmlBytes;
            try {
                xmlBytes = packetManagerHelper.getXMLData(biometrics, offlineMode);
            } catch (Exception e) {
                Log.e(TAG, "BIR_TO_XML_ERROR");
                throw e;
            }

            if (xmlBytes != null) {
                String fileName = operationType + PacketManagerConstant.CBEFF_EXT;
                addEntryToZip(fileName, xmlBytes, zipOutputStream);
                this.registrationPacket.getMetaData().put(String.format("%sBiometricFileName", operationType), fileName);
                addHashSequenceWithSource(PacketManagerConstant.OPERATIONS_SEQ, operationType, xmlBytes, hashSequences);
            }
        }
    }

    private void addPacketDataHash(Map<String, HashSequenceMetaInfo> hashSequences,
                                   ZipOutputStream zipOutputStream) throws Exception, IOException, NoSuchAlgorithmException {

        LinkedList<String> sequence = new LinkedList<String>();
        List<HashSequenceMetaInfo> hashSequenceMetaInfos = new ArrayList<>();
        Map<String, byte[]> data = new HashMap<>();
        if (hashSequences.containsKey(PacketManagerConstant.BIOMETRIC_SEQ)) {
            sequence.addAll(hashSequences.get(PacketManagerConstant.BIOMETRIC_SEQ).getValue());
            data.putAll(hashSequences.get(PacketManagerConstant.BIOMETRIC_SEQ).getHashSource());
            hashSequenceMetaInfos.add(hashSequences.get(PacketManagerConstant.BIOMETRIC_SEQ));
        }
        if (hashSequences.containsKey(PacketManagerConstant.DEMOGRAPHIC_SEQ)) {
            sequence.addAll(hashSequences.get(PacketManagerConstant.DEMOGRAPHIC_SEQ).getValue());
            data.putAll(hashSequences.get(PacketManagerConstant.DEMOGRAPHIC_SEQ).getHashSource());
            hashSequenceMetaInfos.add(hashSequences.get(PacketManagerConstant.DEMOGRAPHIC_SEQ));
        }
        if (hashSequenceMetaInfos.size() > 0)
            this.registrationPacket.getMetaData().put(HASHSEQUENCE1, hashSequenceMetaInfos);

        addEntryToZip(PacketManagerConstant.PACKET_DATA_HASH_FILENAME, PacketManagerHelper.generateHash(sequence, data),
                zipOutputStream);
    }


    private String getIdentity(Object object) throws JsonProcessingException {
        return "{ \"identity\" : " + JsonUtils.javaObjectToJsonString(object) + " } ";
    }
}
