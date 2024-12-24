package io.mosip.registration.packetmanager.service;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord;
import io.mosip.registration.packetmanager.dto.PacketWriter.Document;
import io.mosip.registration.packetmanager.dto.PacketWriter.PacketInfo;
import io.mosip.registration.packetmanager.dto.PacketWriter.RegistrationPacket;
import io.mosip.registration.packetmanager.util.ConfigService;
import io.mosip.registration.packetmanager.util.PacketKeeper;
import io.mosip.registration.packetmanager.util.PacketManagerHelper;
import java.lang.reflect.Method;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigService.class, PacketManagerHelper.class})
public class PackWriterServiceImplTest {

    @Mock
    private PacketWriterServiceImpl packetWriterService;
    @Mock
    private PacketManagerHelper packetManagerHelper;
    @Mock
    private PacketKeeper packetKeeper;
    @Mock
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        packetWriterService = new PacketWriterServiceImpl(context, packetManagerHelper, packetKeeper);
        PowerMockito.mockStatic(ConfigService.class);
    }

    @Test
    public void testInitializeWithExistingId() {

       String id = "110111101120191111121111";
        when(ConfigService.getProperty("mosip.kernel.packet.default_subpacket_name", context))
                .thenReturn("defaultSubpacket");
        when(ConfigService.getProperty("default.provider.version", context))
                .thenReturn("1.0");
        when(ConfigService.getProperty("mosip.utc-datetime-pattern", context))
                .thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        RegistrationPacket result = packetWriterService.initialize(id);
        assertNotNull(result);
        assertEquals(id, result.getRegistrationId());
    }

    @Test
    public void testSetField() {
        packetWriterService.setField("110111101120191111121111", "firstName", "sachin");

        RegistrationPacket packet = packetWriterService.initialize("110111101120191111121111");
        assertEquals("sachin", packet.getDemographics().get("firstName"));
    }

    @Test
    public void testSetBiometric() {
        String applicationId = "110111101120191111121111";
        String fieldId = "biometric1";
        BiometricRecord biometricRecord = mock(BiometricRecord.class);

        packetWriterService.setBiometric(applicationId, fieldId, biometricRecord);
        verify(biometricRecord, times(1)).getSegments();
    }

    @Test
    public void testSetDocument() {
        String id = "110111101120191111121111";
        Document document = mock(Document.class);

        packetWriterService.setDocument(id, "poa", document);
        assertNotNull(packetWriterService.initialize(id));
    }

    @Test
    public void testAddMetaInfo() {
        String id = "110111101120191111121111";

        packetWriterService.addMetaInfo(id, "rid", "regid");
        assertNotNull(packetWriterService.initialize(id));
    }

    @Test
    public void testAddAudit() {
        String id = "110111101120191111121111";
        Map<String, String> auditData = new HashMap<>();
        auditData.put("audit","audit1");

        packetWriterService.addAudit(id, auditData);
        assertNotNull(packetWriterService.initialize(id));
    }

    @Test
    public void testPersistPacket_success() {
        String id = "110111101120191111121111";
        String version = "0.2";
        String schemaJson = "schema";
        String source = "reg-client";
        String process = "NEW";
        boolean offlineMode = false;

        List<PacketInfo> packetInfos = new ArrayList<>();

        when(packetKeeper.pack(anyString(), anyString(), anyString(), anyString())).thenReturn(packetInfos.toString());

        String result = packetWriterService.persistPacket(id, version, schemaJson, source, process, offlineMode, null);

        assertEquals(packetInfos, result);
    }

    @Test
    public void testPersistPacket_Failure() {
        String id = "110111101120191111121111";
        String version = "0.2";
        String schemaJson = "schema";
        String source = "reg-client";
        String process = "NEW";
        boolean offlineMode = false;

        when(packetKeeper.pack(anyString(), anyString(), anyString(), anyString())).thenReturn(null);

        String result = packetWriterService.persistPacket(id, version, schemaJson, source, process, offlineMode, null);
        assertNull(result);
    }

    @Test
    public void testCreatePacket() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        when(packetKeeper.pack(anyString(), anyString(), anyString(), anyString())).thenReturn("mockContainerPath");

        Map<String, List<Object>> schemaFields = new HashMap<>();
        schemaFields.put("subpacket", Collections.singletonList(new HashMap<>()));

        Method loadSchemaFieldsMethod = PacketWriterServiceImpl.class.getDeclaredMethod("loadSchemaFields", String.class);
        loadSchemaFieldsMethod.setAccessible(true);

        // Mocking loadSchemaFields via reflection
        when(loadSchemaFieldsMethod.invoke(packetWriterService, anyString())).thenReturn(schemaFields);

        String id = "110111101120191111121111";
        String version = "0.2";
        String schemaJson = "schema";
        String source = "reg-client";
        String process = "NEW";
        boolean offlineMode = false;

        Method createPacketMethod = PacketWriterServiceImpl.class.getDeclaredMethod(
                "createPacket",
                String.class, String.class, String.class, String.class, String.class, boolean.class, String.class
        );
        createPacketMethod.setAccessible(true);

        String result = (String) createPacketMethod.invoke(packetWriterService, id, version, schemaJson, source, process, offlineMode, null);

        // Verify outcomes
        verify(packetKeeper, times(1)).pack(id, source, process, null);
        assertEquals("mockContainerPath", result);
    }
}
