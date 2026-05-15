package io.mosip.registration.clientmanager.util;

import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.mosip.registration.clientmanager.dto.http.*;
import io.mosip.registration.keymanager.dto.*;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.keymanager.util.KeyManagerConstant;
import io.mosip.registration.packetmanager.util.DateUtils;
import io.mosip.registration.packetmanager.util.JsonUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;
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
    public void getServiceError_withEmptyErrors_returnsNull() {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        responseWrapper.setErrors(Collections.emptyList());
        responseWrapper.setResponse(new Object());
        assertNull(SyncRestUtil.getServiceError(responseWrapper));
    }

    @Test
    public void getServiceError_withErrorsList_returnsFirstError() {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        ServiceError error = new ServiceError();
        responseWrapper.setErrors(Collections.singletonList(error));
        assertEquals(error, SyncRestUtil.getServiceError(responseWrapper));
    }

    @Test
    public void getServiceError_onboardWrapperWithEmptyErrors_returnsNull() {
        OnboardResponseWrapper wrapper = new OnboardResponseWrapper();
        wrapper.setErrors(Collections.emptyList());
        wrapper.setResponse(new Object());
        assertNull(SyncRestUtil.getServiceError(wrapper));
    }

    @Test
    public void getServiceError_onboardWrapperWithErrors_returnsFirstError() {
        OnboardResponseWrapper wrapper = new OnboardResponseWrapper();
        OnboardError error = new OnboardError();
        wrapper.setErrors(Collections.singletonList(error));
        assertEquals(error, SyncRestUtil.getServiceError(wrapper));
    }

    @Test
    public void getAuthRequest_withValidCredentials_returnsSignedRequestWrapper() {
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

    @Test
    public void getServiceError_withNullErrorsAndNonNullResponse_returnsNull() {
        RegProcResponseWrapper wrapper = new RegProcResponseWrapper();
        wrapper.setErrors(null);
        wrapper.setResponse("response");
        assertNull(SyncRestUtil.getServiceError(wrapper));
    }

    @Test
    public void getServiceError_withEmptyErrorsAndNonNullResponse_returnsNull() {
        RegProcResponseWrapper wrapper = new RegProcResponseWrapper();
        wrapper.setErrors(new ArrayList<>());
        wrapper.setResponse("response");
        assertNull(SyncRestUtil.getServiceError(wrapper));
    }

    @Test
    public void getServiceError_withMultipleErrors_returnsFirstError() {
        RegProcResponseWrapper wrapper = new RegProcResponseWrapper();
        ServiceError error1 = new ServiceError("E001", "First error");
        ServiceError error2 = new ServiceError("E002", "Second error");
        wrapper.setErrors(new ArrayList<>());
        wrapper.getErrors().add(error1);
        wrapper.getErrors().add(error2);

        try (MockedStatic<Log> logMock = mockStatic(Log.class);
             MockedStatic<JsonUtils> jsonMock = mockStatic(JsonUtils.class)) {
            jsonMock.when(() -> JsonUtils.javaObjectToJsonString(any())).thenReturn("json");
            ServiceError result = SyncRestUtil.getServiceError(wrapper);
            assertEquals(error1, result);
        }
    }

    @Test
    public void getServiceError_withJsonProcessingException_returnsFirstError() throws Exception {
        RegProcResponseWrapper wrapper = new RegProcResponseWrapper();
        ServiceError error = new ServiceError("E001", "Error");
        wrapper.setErrors(Collections.singletonList(error));
        wrapper.setResponse(null);

        try (MockedStatic<Log> logMock = mockStatic(Log.class);
             MockedStatic<JsonUtils> jsonMock = mockStatic(JsonUtils.class)) {
            jsonMock.when(() -> JsonUtils.javaObjectToJsonString(any()))
                    .thenThrow(new JsonProcessingException("fail") {});
            ServiceError result = SyncRestUtil.getServiceError(wrapper);
            assertEquals(error, result);
        }
    }

    @Test(expected = NullPointerException.class)
    public void getServiceError_withNullRegProcWrapper_throwsNullPointerException() {
        SyncRestUtil.getServiceError((RegProcResponseWrapper) null);
    }

    @Test
    public void getServiceError_responseWrapperWithNullErrorsAndNullResponse_throwsNullPointerException() {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.setErrors(null);
        wrapper.setResponse(null);
        try {
            SyncRestUtil.getServiceError(wrapper);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // expected — errors is null and response is null, falls through to getErrors().get(0)
        }
    }

    @Test
    public void getServiceError_responseWrapperWithJsonProcessingException_returnsFirstError() throws Exception {
        ResponseWrapper wrapper = new ResponseWrapper();
        ServiceError error = new ServiceError("E001", "Error");
        wrapper.setErrors(Collections.singletonList(error));
        wrapper.setResponse(null);

        try (MockedStatic<Log> logMock = mockStatic(Log.class);
             MockedStatic<JsonUtils> jsonMock = mockStatic(JsonUtils.class)) {
            jsonMock.when(() -> JsonUtils.javaObjectToJsonString(any()))
                    .thenThrow(new JsonProcessingException("fail") {});
            ServiceError result = SyncRestUtil.getServiceError(wrapper);
            assertEquals(error, result);
        }
    }

    @Test
    public void getServiceError_onboardWrapperWithNullErrorsAndNullResponse_throwsNullPointerException() {
        OnboardResponseWrapper wrapper = new OnboardResponseWrapper();
        wrapper.setErrors(null);
        wrapper.setResponse(null);
        try {
            SyncRestUtil.getServiceError(wrapper);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // expected — errors is null and response is null, falls through to getErrors().get(0)
        }
    }

    @Test
    public void getServiceError_onboardWrapperWithJsonProcessingException_returnsFirstError() throws Exception {
        OnboardResponseWrapper wrapper = new OnboardResponseWrapper();
        OnboardError error = new OnboardError();
        wrapper.setErrors(Collections.singletonList(error));
        wrapper.setResponse(null);

        try (MockedStatic<Log> logMock = mockStatic(Log.class);
             MockedStatic<JsonUtils> jsonMock = mockStatic(JsonUtils.class)) {
            jsonMock.when(() -> JsonUtils.javaObjectToJsonString(any()))
                    .thenThrow(new JsonProcessingException("fail") {});
            OnboardError result = SyncRestUtil.getServiceError(wrapper);
            assertEquals(error, result);
        }
    }

    @Test
    public void getServiceError_responseWrapperWithNullErrors_returnsNull() {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.setErrors(null);
        wrapper.setResponse(new Object());
        assertNull(SyncRestUtil.getServiceError(wrapper));
    }

    @Test
    public void getServiceError_onboardWrapperWithNullErrors_returnsNull() {
        OnboardResponseWrapper wrapper = new OnboardResponseWrapper();
        wrapper.setErrors(null);
        wrapper.setResponse(new Object());
        assertNull(SyncRestUtil.getServiceError(wrapper));
    }
}