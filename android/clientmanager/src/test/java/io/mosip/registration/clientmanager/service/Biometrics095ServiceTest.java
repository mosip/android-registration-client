package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.clientmanager.constant.*;
import io.mosip.registration.clientmanager.dto.sbi.*;
import io.mosip.registration.clientmanager.exception.BiometricsServiceException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

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

    @Test
    public void test_creates_capture_request_with_standard_environment_settings() {
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockContext.getString(anyInt())).thenReturn("app_name");

        Biometrics095Service biometrics095Service = new Biometrics095Service(
                mockContext, mockObjectMapper, mockAuditManagerService,
                mockGlobalParamRepository, mockCryptoManagerService, mockUserBiometricRepository);

        Modality modality = Modality.FACE;
        String deviceId = "test-device-id";
        List<String> exceptionAttributes = new ArrayList<>();

        CaptureRequest result = biometrics095Service.getRCaptureRequest(modality, deviceId, exceptionAttributes);

        assertEquals("Developer", result.getEnv());
        assertEquals("Registration", result.getPurpose());
        assertEquals(10000, result.getTimeout());
        assertEquals("0.9.5", result.getSpecVersion());
    }

    @Test
    public void test_handles_null_modality_parameter() {
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockContext.getString(anyInt())).thenReturn("app_name");

        Biometrics095Service biometrics095Service = new Biometrics095Service(
                mockContext, mockObjectMapper, mockAuditManagerService,
                mockGlobalParamRepository, mockCryptoManagerService, mockUserBiometricRepository);

        String deviceId = "test-device-id";
        List<String> exceptionAttributes = new ArrayList<>();

        assertThrows(NullPointerException.class, () -> {
            biometrics095Service.getRCaptureRequest(null, deviceId, exceptionAttributes);
        });
    }

    @Test
    public void test_biometric_type_based_on_modality() {
        Biometrics095Service service = new Biometrics095Service(
                mockContext, new ObjectMapper(), mockAuditManagerService, mockGlobalParamRepository,
                mockCryptoManagerService, mockUserBiometricRepository
        );

        CaptureRequest request = service.getRCaptureRequest(Modality.EXCEPTION_PHOTO, "device123", new ArrayList<>());
        assertEquals("Face", request.getBio().get(0).getType());

        request = service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "device123", new ArrayList<>());
        assertEquals("Finger", request.getBio().get(0).getType());
    }

    @Test
    public void test_exception_attributes_conversion() {
        Biometrics095Service service = new Biometrics095Service(
                mockContext, new ObjectMapper(), mockAuditManagerService, mockGlobalParamRepository,
                mockCryptoManagerService, mockUserBiometricRepository
        );

        List<String> exceptionAttributes = Arrays.asList("leftthumb", "rightthumb");
        CaptureRequest request = service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "device123", exceptionAttributes);

        CaptureBioDetail detail = request.getBio().get(0);
        assertArrayEquals(new String[]{"Left Thumb", "Right Thumb"}, detail.getException());
    }

    @Test
    public void test_count_setting_based_on_modality() {
        Biometrics095Service service = new Biometrics095Service(
                mockContext, new ObjectMapper(), mockAuditManagerService, mockGlobalParamRepository,
                mockCryptoManagerService, mockUserBiometricRepository
        );

        List<String> exceptionAttributes = Arrays.asList("leftthumb");
        CaptureRequest request = service.getRCaptureRequest(Modality.EXCEPTION_PHOTO, "device123", exceptionAttributes);

        CaptureBioDetail detail = request.getBio().get(0);
        assertEquals(1, detail.getCount());

        request = service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "device123", exceptionAttributes);
        detail = request.getBio().get(0);
        assertEquals(Modality.FINGERPRINT_SLAB_LEFT.getAttributes().size() - exceptionAttributes.size(), detail.getCount());
    }

    @Test
    public void test_threshold_score_retrieval() {
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.LEFT_SLAP_THRESHOLD_KEY)).thenReturn(50);

        Biometrics095Service service = new Biometrics095Service(
                mockContext,
                new ObjectMapper(),
                mock(AuditManagerService.class),
                mockGlobalParamRepository,
                mock(ClientCryptoManagerService.class),
                mock(UserBiometricRepository.class)
        );

        CaptureRequest request = service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "device123", new ArrayList<>());

        CaptureBioDetail detail = request.getBio().get(0);
        assertEquals(50, detail.getRequestedScore());
    }

    @Test(expected = BiometricsServiceException.class)
    public void test_validate_jwt_response_with_trust_domain() throws Exception {
        Biometrics095Service serviceSpy = Mockito.spy(
                new Biometrics095Service(mockContext, mockObjectMapper, mockAuditManagerService,
                        mockGlobalParamRepository, mockCryptoManagerService, mockUserBiometricRepository)
        );

        InputStream mockResponse = new ByteArrayInputStream("{\"biometrics\":[{\"specVersion\":\"0.9.5\",\"data\":\"mockData\",\"error\":null}]}".getBytes());
        List<String> exceptionAttributes = new ArrayList<>();
        Modality modality = Modality.FACE;

        doNothing().when(serviceSpy).validateJWTResponse(anyString(), eq("DEVICE"));

        serviceSpy.handleRCaptureResponse(modality, mockResponse, exceptionAttributes);

        verify(serviceSpy).validateJWTResponse(anyString(), eq("DEVICE"));
    }

    @Test
    public void test_handle_device_info_response_with_invalid_response_throws_exception() throws Exception {
        Biometrics095Service service = new Biometrics095Service(
                mockContext, mockObjectMapper, mockAuditManagerService, mockGlobalParamRepository,
                mockCryptoManagerService, mockUserBiometricRepository);

        InfoResponse infoResponse = new InfoResponse();
        infoResponse.setDeviceInfo("invalidDeviceInfoJWT");

        List<InfoResponse> infoResponses = new ArrayList<>();
        infoResponses.add(infoResponse);

        byte[] responseBytes = new ObjectMapper().writeValueAsBytes(infoResponses);

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class, () -> {
            service.handleDeviceInfoResponse(Modality.FACE, responseBytes);
        });

        assertTrue(exception.getMessage().contains("REG-SBI-113"));
        assertTrue(exception.getMessage().contains("Device Info Failed! Invalid response"));
    }

    @Test(expected = BiometricsServiceException.class)
    public void test_unsuccessful_device_info_response_parsing_invalid_jwt_payload() throws Exception {
        Biometrics095Service biometrics095Service = new Biometrics095Service(
                mockContext, mockObjectMapper, mockAuditManagerService, mockGlobalParamRepository,
                mockCryptoManagerService, mockUserBiometricRepository);

        InfoResponse infoResponse = new InfoResponse();
        String invalidDeviceInfo = "header.invalidPayload.signature";
        infoResponse.setDeviceInfo(invalidDeviceInfo);
        List<InfoResponse> responseList = Collections.singletonList(infoResponse);

        Biometrics095Service spyService = spy(biometrics095Service);

        doNothing().when(spyService).validateJWTResponse(anyString(), anyString());
        doReturn("{ \"malformedJson\": \"value\"").when(spyService).getJWTPayLoad(invalidDeviceInfo);

        when(mockObjectMapper.writeValueAsBytes(responseList)).thenReturn(new byte[]{});

        byte[] mockResponse = mockObjectMapper.writeValueAsBytes(responseList);

        spyService.handleDeviceInfoResponse(Modality.FACE, mockResponse);
    }

    @Test
    public void test_getModalityThreshold_fingerprintSlabLeft() {
        int expectedThreshold = 100;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.LEFT_SLAP_THRESHOLD_KEY)))
                .thenReturn(expectedThreshold);

        int actualThreshold = biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_LEFT);

        assertEquals(expectedThreshold, actualThreshold);
    }

    @Test
    public void test_getModalityThreshold_fingerprintSlabRight() {
        int expectedThreshold = 150;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.RIGHT_SLAP_THRESHOLD_KEY)))
                .thenReturn(expectedThreshold);

        int actualThreshold = biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_RIGHT);

        assertEquals(expectedThreshold, actualThreshold);
    }

    @Test
    public void test_getModalityThreshold_fingerprintSlabThumbs() {
        int expectedThreshold = 200;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.THUMBS_THRESHOLD_KEY)))
                .thenReturn(expectedThreshold);

        int actualThreshold = biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_THUMBS);

        assertEquals(expectedThreshold, actualThreshold);
    }

    @Test
    public void test_getModalityThreshold_irisDouble() {
        int expectedThreshold = 250;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.IRIS_THRESHOLD_KEY)))
                .thenReturn(expectedThreshold);

        int actualThreshold = biometrics095Service.getModalityThreshold(Modality.IRIS_DOUBLE);

        assertEquals(expectedThreshold, actualThreshold);
    }

    @Test
    public void test_getModalityThreshold_face() {
        int expectedThreshold = 300;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.FACE_THRESHOLD_KEY)))
                .thenReturn(expectedThreshold);

        int actualThreshold = biometrics095Service.getModalityThreshold(Modality.FACE);

        assertEquals(expectedThreshold, actualThreshold);
    }

    @Test
    public void test_getModalityThreshold_unhandledModality() {
        Modality unhandledModality = Modality.EXCEPTION_PHOTO;

        int actualAttempts = biometrics095Service.getModalityThreshold(unhandledModality);

        assertEquals(0, actualAttempts);
    }

    @Test
    public void test_getAttemptsCount_fingerprintSlabLeft() {
        int expectedAttempts = 3;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.LEFT_SLAP_ATTEMPTS_KEY)))
                .thenReturn(expectedAttempts);

        int actualAttempts = biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_LEFT);

        assertEquals(expectedAttempts, actualAttempts);
    }

    @Test
    public void test_getAttemptsCount_fingerprintSlabRight() {
        int expectedAttempts = 4;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.RIGHT_SLAP_ATTEMPTS_KEY)))
                .thenReturn(expectedAttempts);

        int actualAttempts = biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_RIGHT);

        assertEquals(expectedAttempts, actualAttempts);
    }

    @Test
    public void test_getAttemptsCount_fingerprintSlabThumbs() {
        int expectedAttempts = 5;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.THUMBS_ATTEMPTS_KEY)))
                .thenReturn(expectedAttempts);

        int actualAttempts = biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_THUMBS);

        assertEquals(expectedAttempts, actualAttempts);
    }

    @Test
    public void test_getAttemptsCount_irisDouble() {
        int expectedAttempts = 2;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.IRIS_ATTEMPTS_KEY)))
                .thenReturn(expectedAttempts);

        int actualAttempts = biometrics095Service.getAttemptsCount(Modality.IRIS_DOUBLE);

        assertEquals(expectedAttempts, actualAttempts);
    }

    @Test
    public void test_getAttemptsCount_face() {
        int expectedAttempts = 1;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.FACE_ATTEMPTS_KEY)))
                .thenReturn(expectedAttempts);

        int actualAttempts = biometrics095Service.getAttemptsCount(Modality.FACE);

        assertEquals(expectedAttempts, actualAttempts);
    }

    @Test
    public void test_getAttemptsCount_unhandledModality_returnsZero() {
        Modality unhandledModality = Modality.EXCEPTION_PHOTO;

        int actualAttempts = biometrics095Service.getAttemptsCount(unhandledModality);

        assertEquals(0, actualAttempts);
    }

}
