package io.mosip.registration.packetmanager.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;

import io.mosip.registration.keymanager.dto.CryptoManagerRequestDto;
import io.mosip.registration.keymanager.dto.CryptoManagerResponseDto;
import io.mosip.registration.keymanager.dto.SignResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;


public class PacketCryptoServiceImplTest {

    @Mock
    private ClientCryptoManagerService clientCryptoManagerService;

    @Mock
    private CryptoManagerService cryptoManagerService;

    @InjectMocks
    private PacketCryptoServiceImpl packetCryptoService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void signTest() {
        String packetSignature = "signature";
        SignResponseDto signatureResponse = new SignResponseDto();
        signatureResponse.setData(CryptoUtil.encodeToURLSafeBase64(packetSignature.getBytes(StandardCharsets.UTF_8)));

        Mockito.when(clientCryptoManagerService.sign(any())).thenReturn(signatureResponse);

        byte[] result = packetCryptoService.sign(packetSignature.getBytes());
        assertTrue(ArrayUtils.isEquals(packetSignature.getBytes(), result));

    }

    @Test
    public void encryptTest() throws Exception {
        String id = "10001100770000320200720092256";
        String response = "packet";
        byte[] packet = "packet".getBytes();
        CryptoManagerResponseDto cryptomanagerResponseDto = new CryptoManagerResponseDto ();
        cryptomanagerResponseDto.setData(response);
        Mockito.when(cryptoManagerService.encrypt(any(CryptoManagerRequestDto.class)))
                .thenReturn(cryptomanagerResponseDto);

        byte[] result = packetCryptoService.encrypt(id, packet);
        assertNotNull(result);
    }
}
