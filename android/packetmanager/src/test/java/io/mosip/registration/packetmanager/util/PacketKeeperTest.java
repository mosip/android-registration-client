package io.mosip.registration.packetmanager.util;

import android.content.Context;
import io.mosip.registration.packetmanager.dto.PacketWriter.Packet;
import io.mosip.registration.packetmanager.dto.PacketWriter.PacketInfo;
import io.mosip.registration.packetmanager.exception.BaseCheckedException;
import io.mosip.registration.packetmanager.exception.PacketKeeperException;
import io.mosip.registration.packetmanager.service.PacketCryptoServiceImpl;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import io.mosip.registration.packetmanager.spi.ObjectAdapterService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PacketKeeperTest {

    private Context mockContext;
    private IPacketCryptoService mockCryptoService;
    private ObjectAdapterService mockAdapterService;
    private PacketKeeper packetKeeper;
    private MockedStatic<ConfigService> configServiceMockedStatic;
    private MockedStatic<android.util.Log> logMockedStatic;
    private MockedStatic<PacketManagerHelper> packetManagerHelperMockedStatic;

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        mockCryptoService = mock(IPacketCryptoService.class);
        mockAdapterService = mock(ObjectAdapterService.class);

        // Mock ConfigService.getProperty static method
        configServiceMockedStatic = mockStatic(ConfigService.class);
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("objectstore.adapter.name"), any()))
                .thenReturn("PosixAdapter");
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("packet.manager.account.name"), any()))
                .thenReturn("account");

        // Mock android.util.Log.i to avoid "not mocked" error
        logMockedStatic = mockStatic(android.util.Log.class);
        logMockedStatic.when(() -> android.util.Log.i(anyString(), anyString())).thenReturn(0);
        logMockedStatic.when(() -> android.util.Log.i(anyString(), anyString(), any(Throwable.class))).thenReturn(0);

        // Mock PacketManagerHelper.getPacketInfo to return correct PacketInfo for metaMap
        packetManagerHelperMockedStatic = mockStatic(PacketManagerHelper.class);

        packetKeeper = new PacketKeeper(mockContext, mockCryptoService, mockAdapterService);
    }

    @org.junit.After
    public void tearDown() {
        if (configServiceMockedStatic != null) {
            configServiceMockedStatic.close();
        }
        if (logMockedStatic != null) {
            logMockedStatic.close();
        }
        if (packetManagerHelperMockedStatic != null) {
            packetManagerHelperMockedStatic.close();
        }
    }

    @Test
    public void testPutPacket_Success() throws Exception {
        PacketInfo packetInfo = new PacketInfo();
        packetInfo.setId("id");
        packetInfo.setSource("src");
        packetInfo.setProcess("proc");
        packetInfo.setPacketName("packetName");
        packetInfo.setRefId("refId");

        Packet packet = mock(Packet.class);
        when(packet.getPacketInfo()).thenReturn(packetInfo);
        when(packet.getPacket()).thenReturn("data".getBytes());

        when(mockCryptoService.encrypt(anyString(), any())).thenReturn("enc".getBytes());
        when(mockCryptoService.sign(any())).thenReturn("sig".getBytes());

        when(mockAdapterService.putObject(anyString(), anyString(), anyString(), anyString(), anyString(), any(ByteArrayInputStream.class))).thenReturn(true);

        Map<String, Object> metaMap = new HashMap<>();
        metaMap.put("id", "id");
        metaMap.put("source", "src");
        metaMap.put("process", "proc");
        metaMap.put("packetName", "packetName");
        when(mockAdapterService.addObjectMetaData(anyString(), anyString(), anyString(), anyString(), anyString(), anyMap())).thenReturn(metaMap);

        // Mock PacketManagerHelper.getMetaMap to return metaMap (if used)
        packetManagerHelperMockedStatic.when(() -> PacketManagerHelper.getMetaMap(any(PacketInfo.class))).thenReturn(metaMap);

        // Mock PacketManagerHelper.getPacketInfo to return a PacketInfo with correct fields
        PacketInfo expectedPacketInfo = new PacketInfo();
        expectedPacketInfo.setId("id");
        expectedPacketInfo.setSource("src");
        expectedPacketInfo.setProcess("proc");
        expectedPacketInfo.setPacketName("packetName");
        packetManagerHelperMockedStatic.when(() -> PacketManagerHelper.getPacketInfo(metaMap)).thenReturn(expectedPacketInfo);

        PacketInfo result = packetKeeper.putPacket(packet);
        assertNotNull(result);
        assertEquals("id", result.getId());
        assertEquals("src", result.getSource());
        assertEquals("proc", result.getProcess());
        assertEquals("packetName", result.getPacketName());
        // signature and encryptedHash are set on the original packetInfo, not the returned one from getPacketInfo
        // so we don't assert them here
    }

    @Test(expected = PacketKeeperException.class)
    public void testPutPacket_AdapterReturnsFalse() throws Exception {
        PacketInfo packetInfo = new PacketInfo();
        packetInfo.setId("id");
        packetInfo.setSource("src");
        packetInfo.setProcess("proc");
        packetInfo.setPacketName("packetName");
        packetInfo.setRefId("refId");

        Packet packet = mock(Packet.class);
        when(packet.getPacketInfo()).thenReturn(packetInfo);
        when(packet.getPacket()).thenReturn("data".getBytes());

        when(mockCryptoService.encrypt(anyString(), any())).thenReturn("enc".getBytes());
        when(mockAdapterService.putObject(anyString(), anyString(), anyString(), anyString(), anyString(), any(ByteArrayInputStream.class))).thenReturn(false);

        packetKeeper.putPacket(packet);
    }

    @Test(expected = PacketKeeperException.class)
    public void testPutPacket_ThrowsBaseCheckedException() throws Exception {
        PacketInfo packetInfo = new PacketInfo();
        packetInfo.setId("id");
        packetInfo.setSource("src");
        packetInfo.setProcess("proc");
        packetInfo.setPacketName("packetName");
        packetInfo.setRefId("refId");

        Packet packet = mock(Packet.class);
        when(packet.getPacketInfo()).thenReturn(packetInfo);
        when(packet.getPacket()).thenReturn("data".getBytes());

        when(mockCryptoService.encrypt(anyString(), any())).thenThrow(new BaseCheckedException("ERR", "fail"));

        packetKeeper.putPacket(packet);
    }

    @Test(expected = PacketKeeperException.class)
    public void testPutPacket_ThrowsOtherException() throws Exception {
        PacketInfo packetInfo = new PacketInfo();
        packetInfo.setId("id");
        packetInfo.setSource("src");
        packetInfo.setProcess("proc");
        packetInfo.setPacketName("packetName");
        packetInfo.setRefId("refId");

        Packet packet = mock(Packet.class);
        when(packet.getPacketInfo()).thenReturn(packetInfo);
        when(packet.getPacket()).thenReturn("data".getBytes());

        when(mockCryptoService.encrypt(anyString(), any())).thenThrow(new RuntimeException("fail"));

        packetKeeper.putPacket(packet);
    }

    @Test
    public void testDeletePacket() {
        when(mockAdapterService.removeContainer(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        boolean result = packetKeeper.deletePacket("id", "src", "proc");
        assertTrue(result);

        when(mockAdapterService.removeContainer(anyString(), anyString(), anyString(), anyString())).thenReturn(false);
        result = packetKeeper.deletePacket("id", "src", "proc");
        assertFalse(result);
    }

    @Test
    public void testPack() {
        when(mockAdapterService.pack(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn("packed");
        String result = packetKeeper.pack("id", "src", "proc", "refId");
        assertEquals("packed", result);
    }

    @Test(expected = IllegalStateException.class)
    public void testPack_NonPosixAdapter_throwsIllegalStateException() {
        nonPosixPacketKeeper().pack("id", "src", "proc", "refId");
    }

    @Test(expected = IllegalStateException.class)
    public void testDeletePacket_NonPosixAdapter_throwsIllegalStateException() {
        nonPosixPacketKeeper().deletePacket("id", "src", "proc");
    }

    @Test
    public void testPutPacket_NonPosixAdapter_throwsPutError() throws Exception {
        PacketKeeper keeper = nonPosixPacketKeeper();
        Packet packet = new Packet();
        packet.setPacketInfo(new PacketInfo());
        packet.setPacket(new byte[]{1});
        when(mockCryptoService.encrypt(any(), any())).thenReturn(new byte[]{2});

        try {
            keeper.putPacket(packet);
            fail("Expected PacketKeeperException");
        } catch (PacketKeeperException e) {
            assertEquals(PacketManagerErrorCode.PACKET_KEEPER_PUT_ERROR.getErrorCode(), e.getErrorCode());
            assertTrue(e.getMessage().contains(PacketManagerErrorCode.OS_ADAPTER_EXCEPTION.getErrorCode()));
        }
        verifyNoInteractions(mockAdapterService);
    }

    private PacketKeeper nonPosixPacketKeeper() {
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("objectstore.adapter.name"), any()))
                .thenReturn("OtherAdapter");
        return new PacketKeeper(mockContext, mockCryptoService, mockAdapterService);
    }
}
