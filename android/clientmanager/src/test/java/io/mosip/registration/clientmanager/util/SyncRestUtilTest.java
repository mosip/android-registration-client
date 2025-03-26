package io.mosip.registration.clientmanager.util;

import io.mosip.registration.clientmanager.dto.http.*;
import io.mosip.registration.keymanager.dto.*;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.keymanager.util.KeyManagerConstant;
import io.mosip.registration.packetmanager.util.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SyncRestUtilTest {

    @Mock
    private ClientCryptoManagerService clientCryptoManagerService;

    @InjectMocks
    private SyncRestUtil syncRestUtil;

    @Before
    public void setUp() {
        syncRestUtil = new SyncRestUtil(clientCryptoManagerService);
    }

    @Test
    public void testGetServiceError_WithNoErrors_ShouldReturnNull() {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        responseWrapper.setErrors(Collections.emptyList());
        responseWrapper.setResponse(new Object());
        assertNull(SyncRestUtil.getServiceError(responseWrapper));
    }

    @Test
    public void testGetServiceError_WithErrors_ShouldReturnFirstError() {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        ServiceError error = new ServiceError();
        responseWrapper.setErrors(Collections.singletonList(error));
        assertEquals(error, SyncRestUtil.getServiceError(responseWrapper));
    }

    @Test
    public void testGetServiceError_OnboardResponseWrapper_WithNoErrors_ShouldReturnNull() {
        OnboardResponseWrapper wrapper = new OnboardResponseWrapper();
        wrapper.setErrors(Collections.emptyList());
        wrapper.setResponse(new Object());
        assertNull(SyncRestUtil.getServiceError(wrapper));
    }

    @Test
    public void testGetServiceError_OnboardResponseWrapper_WithErrors_ShouldReturnFirstError() {
        OnboardResponseWrapper wrapper = new OnboardResponseWrapper();
        OnboardError error = new OnboardError();
        wrapper.setErrors(Collections.singletonList(error));
        assertEquals(error, SyncRestUtil.getServiceError(wrapper));
    }

    @Test
    public void testGetAuthRequest_ShouldReturnRequestWrapper() {
        String username = "9343";
        String password = "admin123";
        String timestamp = DateUtils.formatToISOString(LocalDateTime.now(ZoneOffset.UTC));
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoTZCdjCZfK68bz6PoZVl1FoixEeJQeF1DgVBSiGv-yyhJbA0g4MxgeihFfM2l2vZhlRrIw7yQq5Xa1PmgLsf1aCEUXSrWsFZUh49RfVsdQyhbYJytbnOesKs_UNpIyfD1iiJ5PXBO3AzgZaLHkyIueevB1npxSs-pDiC_EN1vBnsQ2vW8_YtU4qmBWv7-cbkU2W1y4-rNtdbONEj_7DgsLHDEHM9LwG-0nt8mjpiMOZK8Njz3bsNQ-QIMDorxv-f9h6fMjg0lj0-kU5_2eWWBkQREWqmLoUXnbh_pRF8IKXro48J1DdAN_nY2NjHSt-NfoD79FUIyZa_Khf1l1b_mwIDAQAB";
        String signedData = "signedData";

        PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto();
        PublicKeyResponseDto publicKeyResponseDto = new PublicKeyResponseDto();
        publicKeyResponseDto.setPublicKey(publicKey);
        when(clientCryptoManagerService.getPublicKey(any())).thenReturn(publicKeyResponseDto);

        SignRequestDto signRequestDto = new SignRequestDto();
        SignResponseDto signResponseDto = new SignResponseDto();
        signResponseDto.setData(signedData);
        when(clientCryptoManagerService.sign(any())).thenReturn(signResponseDto);

        RequestWrapper<String> result = syncRestUtil.getAuthRequest(username, password);
        assertNotNull(result);
        assertNotNull(result.getRequest());
    }
}