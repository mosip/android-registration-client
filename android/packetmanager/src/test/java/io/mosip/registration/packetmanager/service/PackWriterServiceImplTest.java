package io.mosip.registration.packetmanager.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.util.ArrayList;
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

import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

@RunWith(RobolectricTestRunner.class) //
public class PackWriterServiceImplTest {

    @Mock
    private PacketManagerHelper packetManagerHelper;
    @Mock
    private PacketKeeper packetKeeper;
    @Mock
    private Context context;
    @Mock
    private AssetManager assetManager;

    private PacketWriterServiceImpl packetWriterService;
    private MockedStatic<ConfigService> configServiceMock;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(context.getAssets()).thenReturn(assetManager);
        packetWriterService = spy(new PacketWriterServiceImpl(context, packetManagerHelper, packetKeeper));

        configServiceMock = mockStatic(ConfigService.class);

        // Correct static mocking for ConfigService
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
    public void testInitializeWithExistingId() {
        String id = "110111101120191111121111";
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
        auditData.put("audit", "audit1");
        packetWriterService.addAudit(id, auditData);
        assertNotNull(packetWriterService.initialize(id));
    }

//    @Test
//    public void testPersistPacket_success() {
//        String id = "110111101120191111121111";
//        String version = "0.2";
//        String schemaJson = "schema";
//        String source = "reg-client";
//        String process = "NEW";
//        boolean offlineMode = false;
//
//        List<PacketInfo> packetInfos = new ArrayList<>();
//        when(packetKeeper.pack(anyString(), anyString(), anyString(), anyString()))
//                .thenReturn(packetInfos.toString());
//
//        String result = packetWriterService.persistPacket(id, version, schemaJson, source, process, offlineMode, null);
//        assertEquals(packetInfos.toString(), result);
//    }

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

//    @Test
//    public void testCreatePacket() {
//        String id = "110111101120191111121111";
//        String version = "0.2";
//        String schemaJson = "schema";
//        String source = "reg-client";
//        String process = "NEW";
//        boolean offlineMode = false;
//
//        packetWriterService.initialize(id);
//
//        when(packetKeeper.pack(anyString(), anyString(), anyString(), anyString()))
//                .thenReturn("mockContainerPath");
//
//        String result = packetWriterService.persistPacket(id, version, schemaJson, source, process, offlineMode, null);
//        assertEquals("mockContainerPath", result);
//    }
}
