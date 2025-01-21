package io.mosip.registration.clientmanager.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.registration.clientmanager.dto.sbi.CaptureBioDetail;
import io.mosip.registration.clientmanager.exception.BiometricsServiceException;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.keymanager.dto.JWTSignatureVerifyRequestDto;
import io.mosip.registration.keymanager.dto.JWTSignatureVerifyResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.clientmanager.dto.sbi.CaptureRequest;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.keymanager.util.KeyManagerConstant;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

public class Biometrics095ServiceTest {

    @Mock private Context context;
    @Mock private ObjectMapper objectMapper;
    @Mock private AuditManagerService auditManagerService;
    @Mock private GlobalParamRepository globalParamRepository;
    @Mock private ClientCryptoManagerService clientCryptoManagerService;
    @Mock private UserBiometricRepository userBiometricRepository;

    private Biometrics095Service biometrics095Service;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        biometrics095Service = new Biometrics095Service(
                context, objectMapper, auditManagerService, globalParamRepository,
                clientCryptoManagerService, userBiometricRepository);
    }

    @Test
    public void getRCaptureRequest_Test() {
        String deviceId = "device123";
        Modality modality = Modality.FACE;
        List<String> exceptionAttributes = new ArrayList<>();

        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(modality, deviceId, exceptionAttributes);

        assertNotNull(captureRequest);
        assertEquals("Registration", captureRequest.getPurpose());
        assertEquals(10000, captureRequest.getTimeout());
        assertNotNull(captureRequest.getBio());
        assertEquals(1, captureRequest.getBio().size());
        CaptureBioDetail bioDetail = captureRequest.getBio().get(0);
        assertEquals(deviceId, bioDetail.getDeviceId());
        assertEquals(modality.getSingleType().value(), bioDetail.getType());
    }

    @Test(expected = BiometricsServiceException.class)
    public void handleRCaptureResponse_Error_Test() throws Exception {
        String responseJson = "{\"biometrics\": [{\"error\": {\"errorCode\": \"SOME_ERROR\", \"errorInfo\": \"Some error\"}}]}"; // Error response
        InputStream responseStream = new ByteArrayInputStream(responseJson.getBytes());
        Modality modality = Modality.FACE;
        List<String> exceptionAttributes = new ArrayList<>();
        biometrics095Service.handleRCaptureResponse(modality, responseStream, exceptionAttributes);
    }

    @Test
    public void validateJWTResponse_Success_Test() throws Exception {
        JWTSignatureVerifyResponseDto mockResponse = mock(JWTSignatureVerifyResponseDto.class);
        when(mockResponse.isSignatureValid()).thenReturn(true);
        when(mockResponse.getTrustValid()).thenReturn(KeyManagerConstant.TRUST_VALID);
        when(clientCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class))).thenReturn(mockResponse);

        biometrics095Service.validateJWTResponse("some-signed-data", "DEVICE");
    }

    @Test(expected = BiometricsServiceException.class)
    public void validateJWTResponse_Failure_Test() throws Exception {
        JWTSignatureVerifyResponseDto mockResponse = mock(JWTSignatureVerifyResponseDto.class);
        when(mockResponse.isSignatureValid()).thenReturn(false);
        when(clientCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class))).thenReturn(mockResponse);

        biometrics095Service.validateJWTResponse("some-invalid-signed-data", "DEVICE");
    }
}
