package io.mosip.registration.packetmanager.service;

import io.mosip.registration.keymanager.dto.*;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import android.content.Context;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class PacketCryptoServiceImplTest {

    private ClientCryptoManagerService mockClientCryptoService;
    private CryptoManagerService mockCryptoManagerService;
    private Context mockContext;
    private PacketCryptoServiceImpl packetCryptoService;

    @Before
    public void setUp() {
        mockClientCryptoService = mock(ClientCryptoManagerService.class);
        mockCryptoManagerService = mock(CryptoManagerService.class);
        mockContext = mock(Context.class);

        packetCryptoService = new PacketCryptoServiceImpl(mockContext, mockClientCryptoService, mockCryptoManagerService);
    }

    @Test
    public void testSign() {
        byte[] inputPacket = "TestPacket".getBytes(StandardCharsets.UTF_8);
        String base64Encoded = Base64.getEncoder().encodeToString(inputPacket);
        String signedBase64 = Base64.getEncoder().encodeToString("SignedPacket".getBytes(StandardCharsets.UTF_8));

        SignResponseDto responseDto = new SignResponseDto();
        responseDto.setData(signedBase64);
        when(mockClientCryptoService.sign(any(SignRequestDto.class))).thenReturn(responseDto);

        byte[] result = packetCryptoService.sign(inputPacket);
        assertNotNull(result);
        assertEquals("SignedPacket", new String(result, StandardCharsets.UTF_8));

        verify(mockClientCryptoService, times(1)).sign(any(SignRequestDto.class));
    }

    @Test
    public void testEncrypt() throws Exception {
        byte[] inputPacket = "TestPacket".getBytes(StandardCharsets.UTF_8);
        String base64EncodedPacket = Base64.getEncoder().encodeToString(inputPacket);
        byte[] encryptedBytes = "EncryptedData".getBytes(StandardCharsets.UTF_8);
        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

        CryptoManagerResponseDto responseDto = new CryptoManagerResponseDto();
        responseDto.setData(encryptedBase64);

        when(mockCryptoManagerService.encrypt(any(CryptoManagerRequestDto.class))).thenReturn(responseDto);

        byte[] result = packetCryptoService.encrypt("REF123", inputPacket);

        assertNotNull(result);
        assertTrue(result.length >= encryptedBytes.length + 28); // GCM_AAD_LENGTH + GCM_NONCE_LENGTH

        verify(mockCryptoManagerService, times(1)).encrypt(any(CryptoManagerRequestDto.class));
    }

    @Test
    public void testMergeEncryptedData() {
        byte[] encrypted = new byte[]{1, 2, 3};
        byte[] nonce = new byte[12];
        byte[] aad = new byte[16];
        for (int i = 0; i < 12; i++) nonce[i] = (byte) i;
        for (int i = 0; i < 16; i++) aad[i] = (byte) (i + 10);

        byte[] merged = PacketCryptoServiceImpl.mergeEncryptedData(encrypted, nonce, aad);

        assertEquals(47, merged.length);
        assertArrayEquals(nonce, java.util.Arrays.copyOfRange(merged, 0, 12));
        assertArrayEquals(aad, java.util.Arrays.copyOfRange(merged, 12, 28));
        assertArrayEquals(encrypted, java.util.Arrays.copyOfRange(merged, 28, 31));
    }
}
