package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.constant.SBIError;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.sbi.CaptureRequest;
import io.mosip.registration.clientmanager.dto.sbi.CaptureRespDetail;
import io.mosip.registration.clientmanager.dto.sbi.CaptureResponse;
import io.mosip.registration.clientmanager.dto.sbi.DeviceDto;
import io.mosip.registration.clientmanager.dto.sbi.InfoResponse;
import io.mosip.registration.clientmanager.exception.BiometricsServiceException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Biometrics095ServiceTest {

    @Mock
    private Context mockContext;

    @Mock
    private ObjectMapper mockObjectMapper;

    @Mock
    private AuditManagerService mockAuditManagerService;

    @Mock
    private GlobalParamRepository mockGlobalParamRepository;

    @Mock
    private ClientCryptoManagerService mockCryptoManagerService;

    @Mock
    private UserBiometricRepository mockUserBiometricRepository;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @InjectMocks
    private Biometrics095Service biometrics095Service;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
    }

    @Test
    public void testGetRCaptureRequest() {
        Modality modality = Modality.FACE;
        String deviceId = "testDevice";
        List<String> exceptionAttributes = new ArrayList<>();

        CaptureRequest request = biometrics095Service.getRCaptureRequest(modality, deviceId, exceptionAttributes);

        assertNotNull(request);
        assertEquals("Developer", request.getEnv());
        assertEquals("Registration", request.getPurpose());
        assertFalse("Bio list should not be empty", request.getBio().isEmpty());
        assertEquals(deviceId, request.getBio().get(0).getDeviceId());
    }

//    @Test
//    public void testHandleRCaptureResponse_validResponse() throws Exception {
//        Modality modality = Modality.FACE;
//        List<String> exceptionAttributes = new ArrayList<>();
//
//        // Simulated valid JSON response
//        String encodedPayload = Base64.getEncoder().encodeToString("payload".getBytes());
//        String jsonResponse = "{\"biometrics\":[{\"data\":\"" + encodedPayload + "\",\"specVersion\":\"0.9.5\"}]}";
//        InputStream responseStream = new ByteArrayInputStream(jsonResponse.getBytes());
//
//        // Ensure CaptureResponse is properly mocked
//        CaptureResponse captureResponse = new CaptureResponse();
//        List<CaptureRespDetail> biometricsList = new ArrayList<>();
//        CaptureRespDetail captureRespDetail = new CaptureRespDetail();
//
//        captureRespDetail.setData(encodedPayload);
//        captureRespDetail.setSpecVersion("0.9.5");
//        biometricsList.add(captureRespDetail);
//
//        captureResponse.setBiometrics(biometricsList);
//
//        //  Fix Mockito issue: Match any InputStream explicitly
//        when(mockObjectMapper.readValue(eq(responseStream), eq(CaptureResponse.class)))
//                .thenReturn(captureResponse);
//
//        //  Call the service method
//        List<BiometricsDto> result = biometrics095Service.handleRCaptureResponse(modality, responseStream, exceptionAttributes);
//
//        //  Assertions
//        assertNotNull("Result should not be null", result);
//        assertFalse("The result list should not be empty", result.isEmpty());
//        assertEquals("BioValue should match expected data", encodedPayload, result.get(0).getBioValue());
//    }

    @Test
    public void testHandleRCaptureResponse_errorResponse() throws Exception {
        Modality modality = Modality.FACE;
        List<String> exceptionAttributes = new ArrayList<>();
        InputStream responseStream = new ByteArrayInputStream("{}".getBytes());

        when(mockObjectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<CaptureResponse>>any()))
                .thenThrow(new RuntimeException("Parsing error"));

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(modality, responseStream, exceptionAttributes));

        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), exception.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.R_CAPTURE_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void testHandleDeviceInfoResponse_invalidResponse() throws Exception {
        Modality modality = Modality.FACE;
        byte[] response = "{}".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), ArgumentMatchers.<TypeReference<List<InfoResponse>>>any()))
                .thenThrow(new RuntimeException("Invalid data"));

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleDeviceInfoResponse(modality, response));

        assertEquals(SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    public void testHandleDiscoveryResponse_success() throws Exception {
        Modality modality = Modality.FACE;
        byte[] response = "[{\"callbackId\":\"device_123\"}]".getBytes();

        List<DeviceDto> devices = new ArrayList<>();
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setCallbackId("device_123");
        devices.add(deviceDto);

        when(mockObjectMapper.readValue(any(byte[].class), ArgumentMatchers.<TypeReference<List<DeviceDto>>>any()))
                .thenReturn(devices);

        String callbackId = biometrics095Service.handleDiscoveryResponse(modality, response);

        assertEquals("device_123", callbackId);
    }

    @Test
    public void testHandleDiscoveryResponse_error() throws Exception {
        Modality modality = Modality.FACE;
        byte[] response = "{}".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), ArgumentMatchers.<TypeReference<List<DeviceDto>>>any()))
                .thenThrow(new RuntimeException("Parsing error"));

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleDiscoveryResponse(modality, response));

        assertEquals(SBIError.SBI_DISC_INVALID_REPSONSE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    public void testGetModalityThreshold() {
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(anyString())).thenReturn(50);

        int threshold = biometrics095Service.getModalityThreshold(Modality.FACE);

        assertEquals(50, threshold);
    }

    @Test
    public void testValidateJWTResponse_invalidSignature() throws Exception {
        when(mockCryptoManagerService.jwtVerify(any())).thenThrow(new BiometricsServiceException(SBIError.SBI_INVALID_SIGNATURE.getErrorCode(), "Invalid signature"));

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.validateJWTResponse("signedData", "DEVICE"));

        assertEquals(SBIError.SBI_INVALID_SIGNATURE.getErrorCode(), exception.getErrorCode());
    }
}
