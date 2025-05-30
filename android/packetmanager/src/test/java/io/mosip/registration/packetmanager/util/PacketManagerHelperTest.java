package io.mosip.registration.packetmanager.util;

import android.content.Context;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord;
import io.mosip.registration.packetmanager.dto.PacketWriter.PacketInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PacketManagerHelperTest {

    private PacketManagerHelper helper;
    private MockedStatic<ConfigService> configServiceMockedStatic;

    @Before
    public void setUp() {
        configServiceMockedStatic = mockStatic(ConfigService.class);
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("mosip.kernel.xsdstorage-uri"), any(Context.class)))
                .thenReturn("dummy-uri");
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("mosip.kernel.xsdfile"), any(Context.class)))
                .thenReturn("dummy-schema");
        Context mockContext = mock(Context.class);
        helper = new PacketManagerHelper(mockContext);
    }

    @After
    public void tearDown() {
        if (configServiceMockedStatic != null) configServiceMockedStatic.close();
    }

    @Test
    public void testGetXMLData() throws Exception {
        BiometricRecord record = mock(BiometricRecord.class);
        List<BIR> segments = new ArrayList<>();
        segments.add(new BIR()); // Use correct type: List<BIR>
        when(record.getSegments()).thenReturn(segments);

        byte[] xmlData = helper.getXMLData(record, false);
        assertNotNull(xmlData);
        assertTrue(xmlData.length > 0);
    }

    @Test
    public void testGenerateHashWithOrder() throws IOException, NoSuchAlgorithmException {
        List<String> order = Arrays.asList("a", "b");
        Map<String, byte[]> data = new HashMap<>();
        data.put("a", "foo".getBytes());
        data.put("b", "bar".getBytes());

        // Mock HMACUtils2.digestAsPlainText
        try (MockedStatic<HMACUtils2> hmacMock = mockStatic(HMACUtils2.class)) {
            hmacMock.when(() -> HMACUtils2.digestAsPlainText(any(byte[].class))).thenReturn("digest");
            byte[] result = PacketManagerHelper.generateHash(order, data);
            assertNotNull(result);
            assertEquals("digest", new String(result));
        }
    }

    @Test
    public void testGenerateHashWithNullOrder() throws IOException, NoSuchAlgorithmException {
        byte[] result = PacketManagerHelper.generateHash(null, null);
        assertNull(result);
    }

    @Test
    public void testGenerateHashWithEmptyOrder() throws IOException, NoSuchAlgorithmException {
        byte[] result = PacketManagerHelper.generateHash(Collections.emptyList(), new HashMap<>());
        assertNull(result);
    }

    @Test
    public void testGetMetaMapAndGetPacketInfo() {
        PacketInfo info = new PacketInfo();
        info.setId("id");
        info.setPacketName("packetName");
        info.setSource("source");
        info.setProcess("process");
        info.setSchemaVersion("schemaVersion");
        info.setSignature("signature");
        info.setEncryptedHash("encryptedHash");
        info.setProviderName("providerName");
        info.setProviderVersion("providerVersion");
        info.setCreationDate("creationDate");

        Map<String, Object> metaMap = PacketManagerHelper.getMetaMap(info);
        assertEquals("id", metaMap.get(PacketManagerConstant.ID));
        assertEquals("packetName", metaMap.get(PacketManagerConstant.PACKET_NAME));
        assertEquals("source", metaMap.get(PacketManagerConstant.SOURCE));
        assertEquals("process", metaMap.get(PacketManagerConstant.PROCESS));
        assertEquals("schemaVersion", metaMap.get(PacketManagerConstant.SCHEMA_VERSION));
        assertEquals("signature", metaMap.get(PacketManagerConstant.SIGNATURE));
        assertEquals("encryptedHash", metaMap.get(PacketManagerConstant.ENCRYPTED_HASH));
        assertEquals("providerName", metaMap.get(PacketManagerConstant.PROVIDER_NAME));
        assertEquals("providerVersion", metaMap.get(PacketManagerConstant.PROVIDER_VERSION));
        assertEquals("creationDate", metaMap.get(PacketManagerConstant.CREATION_DATE));

        PacketInfo info2 = PacketManagerHelper.getPacketInfo(metaMap);
        assertEquals("id", info2.getId());
        assertEquals("packetName", info2.getPacketName());
        assertEquals("source", info2.getSource());
        assertEquals("process", info2.getProcess());
        assertEquals("schemaVersion", info2.getSchemaVersion());
        assertEquals("signature", info2.getSignature());
        assertEquals("encryptedHash", info2.getEncryptedHash());
        assertEquals("providerName", info2.getProviderName());
        assertEquals("providerVersion", info2.getProviderVersion());
        assertEquals("creationDate", info2.getCreationDate());
    }
}
