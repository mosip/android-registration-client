package io.mosip.registration.packetmanager.service;

import android.content.Context;
import android.content.res.AssetManager;
import io.mosip.registration.packetmanager.dto.PacketWriter.*;
import io.mosip.registration.packetmanager.exception.PacketKeeperException;
import io.mosip.registration.packetmanager.util.ConfigService;
import io.mosip.registration.packetmanager.util.PacketKeeper;
import io.mosip.registration.packetmanager.util.PacketManagerConstant;
import io.mosip.registration.packetmanager.util.PacketManagerHelper;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class PacketWriterServiceImplTest {

    @Mock
    private PacketManagerHelper packetManagerHelper;

    @Mock
    private PacketKeeper packetKeeper;

    @Mock
    private Context context;

    @Mock
    private AssetManager assetManager;

    @InjectMocks
    private PacketWriterServiceImpl packetWriterService;

    private MockedStatic<ConfigService> configServiceMock;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(context.getAssets()).thenReturn(assetManager);

        // Mock ConfigService properties
        configServiceMock = mockStatic(ConfigService.class);
        configServiceMock.when(() -> ConfigService.getProperty("mosip.kernel.packet.default_subpacket_name", context))
                .thenReturn("defaultSubpacket");
        configServiceMock.when(() -> ConfigService.getProperty("default.provider.version", context))
                .thenReturn("1.0");
        configServiceMock.when(() -> ConfigService.getProperty("mosip.utc-datetime-pattern", context))
                .thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        ShadowLog.stream = System.out;
    }

    @After
    public void tearDown() {
        if (configServiceMock != null) {
            configServiceMock.close();
        }
    }

    @Test
    public void testInitializeWithNewId() {
        String id = "110111101120191111121111";
        RegistrationPacket result = packetWriterService.initialize(id);
        assertNotNull(result);
        assertEquals(id, result.getRegistrationId());
    }

    @Test
    public void testInitializeWithSameId() {
        String id = "110111101120191111121111";
        RegistrationPacket firstResult = packetWriterService.initialize(id);
        RegistrationPacket secondResult = packetWriterService.initialize(id);
        assertSame(firstResult, secondResult); // Should return the same instance
        assertEquals(id, secondResult.getRegistrationId());
    }

    @Test
    public void testSetField() {
        String id = "110111101120191111121111";
        String fieldId = "firstName";
        String value = "sachin";

        packetWriterService.setField(id, fieldId, value);
        RegistrationPacket packet = packetWriterService.initialize(id);

        assertEquals(value, packet.getDemographics().get(fieldId));
    }

    @Test
    public void testSetBiometric() {
        String id = "110111101120191111121111";
        String fieldId = "biometric1";
        BiometricRecord biometricRecord = new BiometricRecord();
        biometricRecord.setSegments(new ArrayList<>()); // Empty segments to avoid NullPointerException

        packetWriterService.setBiometric(id, fieldId, biometricRecord);
        RegistrationPacket packet = packetWriterService.initialize(id);

        assertEquals(biometricRecord, packet.getBiometrics().get(fieldId));
    }

    @Test
    public void testSetDocument() {
        String id = "110111101120191111121111";
        String fieldId = "poa";
        Document document = new Document();
        document.setType("docType");
        document.setFormat("pdf");
        document.setDocument(new byte[]{1, 2, 3});

        packetWriterService.setDocument(id, fieldId, document);
        RegistrationPacket packet = packetWriterService.initialize(id);

        assertEquals(document, packet.getDocuments().get(fieldId));
    }

    @Test
    public void testAddMetaInfo() {
        String id = "110111101120191111121111";
        String key = "rid";
        String value = "regid";

        packetWriterService.addMetaInfo(id, key, value);
        RegistrationPacket packet = packetWriterService.initialize(id);

        assertEquals(value, packet.getMetaData().get(key));
    }

    @Test
    public void testAddAudit() {
        String id = "110111101120191111121111";
        Map<String, String> auditData = new HashMap<>();
        auditData.put("event", "audit1");

        packetWriterService.addAudit(id, auditData);
        RegistrationPacket packet = packetWriterService.initialize(id);

        assertTrue(packet.getAudits().contains(auditData));
    }

    @Test
    public void testAddAudits() {
        String id = "110111101120191111121111";
        List<Map<String, String>> audits = new ArrayList<>();
        Map<String, String> auditData = new HashMap<>();
        auditData.put("event", "audit1");
        audits.add(auditData);

        packetWriterService.addAudits(id, audits);
        RegistrationPacket packet = packetWriterService.initialize(id);

        assertEquals(audits, packet.getAudits());
    }

    @Test
    public void testPersistPacketSuccess() throws Exception {
        String id = "110111101120191111121111";
        String version = "1.0";
        String schemaJson = createMockSchemaJson();
        String source = "reg-client";
        String process = "NEW";
        boolean offlineMode = false;
        String refId = "ref123";

        // Initialize packet
        packetWriterService.initialize(id);
        Map<String, String> auditData = new HashMap<>();
        auditData.put("event", "audit1");
        packetWriterService.addAudit(id, auditData);

        // Create a mock PacketInfo to return
        PacketInfo packetInfo = new PacketInfo();
        packetInfo.setId(id);
        packetInfo.setSource(source);
        packetInfo.setProcess(process);
        packetInfo.setPacketName(id + "_defaultSubpacket");

        // Mock PacketKeeper behavior
        when(packetKeeper.putPacket(any(Packet.class))).thenReturn(packetInfo);
        when(packetKeeper.pack(eq(id), eq(source), eq(process), eq(refId))).thenReturn("/path/to/packet.zip");

        String result = packetWriterService.persistPacket(id, version, schemaJson, source, process, offlineMode, refId);

        assertNotNull(result);
        assertEquals("/path/to/packet.zip", result);
        // Expect 3 calls to putPacket because there are 3 subpackets (evidence, optional, id)
        verify(packetKeeper, times(3)).putPacket(any(Packet.class));
        verify(packetKeeper, times(1)).pack(eq(id), eq(source), eq(process), eq(refId));
    }

    @Test
    public void testPersistPacketFailureDueToInvalidRegistration() throws PacketKeeperException {
        String id = "110111101120191111121111";
        String version = "1.0";
        String schemaJson = createMockSchemaJson();
        String source = "reg-client";
        String process = "NEW";
        boolean offlineMode = false;

        // Do not initialize the packet, so it should throw an exception
        String result = packetWriterService.persistPacket(id, version, schemaJson, source, process, offlineMode, null);

        assertNull(result);
        verify(packetKeeper, never()).putPacket(any(Packet.class));
    }

    @Test
    public void testPersistPacketFailureDueToPackFailure() throws Exception {
        String id = "110111101120191111121111";
        String version = "1.0";
        String schemaJson = createMockSchemaJson();
        String source = "reg-client";
        String process = "NEW";
        boolean offlineMode = false;
        String refId = "ref123";

        // Initialize packet
        packetWriterService.initialize(id);
        Map<String, String> auditData = new HashMap<>();
        auditData.put("event", "audit1");
        packetWriterService.addAudit(id, auditData);

        // Create a mock PacketInfo to return
        PacketInfo packetInfo = new PacketInfo();
        packetInfo.setId(id);
        packetInfo.setSource(source);
        packetInfo.setProcess(process);
        packetInfo.setPacketName(id + "_defaultSubpacket");

        // Mock PacketKeeper to return null for pack
        when(packetKeeper.putPacket(any(Packet.class))).thenReturn(packetInfo);
        when(packetKeeper.pack(eq(id), eq(source), eq(process), eq(refId))).thenReturn(null);

        String result = packetWriterService.persistPacket(id, version, schemaJson, source, process, offlineMode, refId);

        assertNull(result);
        // Expect 3 calls to putPacket because there are 3 subpackets (evidence, optional, id)
        verify(packetKeeper, times(3)).putPacket(any(Packet.class));
        // Expect 2 calls to deletePacket: one in the if block and one in the catch block
        verify(packetKeeper, times(2)).deletePacket(eq(id), eq(source), eq(process));
    }

    @Test
    public void testCreateSubpacketSuccess() throws Exception {
        // Use reflection to access the private method createSubpacket
        String id = "110111101120191111121111";
        packetWriterService.initialize(id);
        List<Object> schemaFields = new ArrayList<>();
        Map<String, Object> field = new HashMap<>();
        field.put(PacketManagerConstant.SCHEMA_ID, "firstName");
        field.put(PacketManagerConstant.SCHEMA_TYPE, "string");
        schemaFields.add(field);

        // Set demographic field
        packetWriterService.setField(id, "firstName", "sachin");

        // Add audit data to satisfy the AUDITS_REQUIRED check
        Map<String, String> auditData = new HashMap<>();
        auditData.put("event", "audit1");
        packetWriterService.addAudit(id, auditData);

        // Use reflection to invoke the private method
        java.lang.reflect.Method createSubpacketMethod = PacketWriterServiceImpl.class
                .getDeclaredMethod("createSubpacket", double.class, List.class, boolean.class, String.class, boolean.class);
        createSubpacketMethod.setAccessible(true);

        byte[] result = (byte[]) createSubpacketMethod.invoke(packetWriterService, 1.0, schemaFields, true, id, false);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    private String createMockSchemaJson() {
        try {
            JSONObject schema = new JSONObject();
            JSONObject properties = new JSONObject();
            JSONObject identity = new JSONObject();
            JSONObject identityProperties = new JSONObject();

            // Add a simple field
            JSONObject firstName = new JSONObject();
            firstName.put("type", "string");
            firstName.put("category", "pvt");
            identityProperties.put("firstName", firstName);

            identity.put("properties", identityProperties);
            properties.put("identity", identity);
            schema.put("properties", properties);

            return schema.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock schema JSON", e);
        }
    }
}