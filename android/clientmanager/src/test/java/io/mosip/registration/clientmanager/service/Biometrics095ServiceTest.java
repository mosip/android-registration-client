package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.clientmanager.constant.*;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.sbi.*;
import io.mosip.registration.clientmanager.exception.BiometricsServiceException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.keymanager.dto.JWTSignatureVerifyRequestDto;
import io.mosip.registration.keymanager.dto.JWTSignatureVerifyResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;

import io.mosip.registration.keymanager.util.KeyManagerConstant;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Mock
    private InputStream mockInputStream;

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

    @Test
    public void test_validates_jwt_signature_successfully_when_valid() throws Exception {
        String signedData = "valid.jwt.token";
        String domain = "test-domain";

        JWTSignatureVerifyResponseDto mockResponse = new JWTSignatureVerifyResponseDto();
        mockResponse.setSignatureValid(true);
        mockResponse.setTrustValid(KeyManagerConstant.TRUST_VALID);

        when(mockCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class)))
                .thenReturn(mockResponse);

        assertDoesNotThrow(() -> biometrics095Service.validateJWTResponse(signedData, domain));

        verify(mockCryptoManagerService).jwtVerify(argThat(request ->
                request.getJwtSignatureData().equals(signedData) &&
                        request.getDomain().equals(domain) &&
                        request.getValidateTrust().equals(true)
        ));
    }

    @Test
    public void test_throws_exception_when_signature_invalid() throws Exception {
        String signedData = "invalid.jwt.token";
        String domain = "test-domain";

        JWTSignatureVerifyResponseDto mockResponse = new JWTSignatureVerifyResponseDto();
        mockResponse.setSignatureValid(false);
        mockResponse.setTrustValid(KeyManagerConstant.TRUST_VALID);

        when(mockCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class)))
                .thenReturn(mockResponse);

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class,
                () -> biometrics095Service.validateJWTResponse(signedData, domain));

        assertEquals(SBIError.SBI_INVALID_SIGNATURE.getErrorCode(), exception.getErrorCode());

        verify(mockCryptoManagerService).jwtVerify(any(JWTSignatureVerifyRequestDto.class));
    }

    @Test
    public void test_successfully_parses_valid_json_response_and_returns_callback_id() throws Exception {
        ReflectionTestUtils.setField(biometrics095Service, "objectMapper", mockObjectMapper);
        ReflectionTestUtils.setField(biometrics095Service, "auditManagerService", mockAuditManagerService);

        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setCallbackId("test-callback-123");
        deviceDto.setDeviceStatus("Ready");
        deviceDto.setError(null);

        List<DeviceDto> deviceList = Arrays.asList(deviceDto);
        byte[] responseBytes = "test-response".getBytes();

        when(mockObjectMapper.readValue(eq(responseBytes), any(TypeReference.class))).thenReturn(deviceList);

        String result = biometrics095Service.handleDiscoveryResponse(Modality.FACE, responseBytes);

        assertEquals("test-callback-123", result);
        verify(mockObjectMapper).readValue(eq(responseBytes), any(TypeReference.class));
        verifyNoInteractions(mockAuditManagerService);
    }

    @Test
    public void test_throws_exception_when_device_list_is_empty() throws Exception {
        ReflectionTestUtils.setField(biometrics095Service, "objectMapper", mockObjectMapper);
        ReflectionTestUtils.setField(biometrics095Service, "auditManagerService", mockAuditManagerService);

        List<DeviceDto> emptyDeviceList = new ArrayList<>();
        byte[] responseBytes = "empty-response".getBytes();

        when(mockObjectMapper.readValue(eq(responseBytes), any(TypeReference.class))).thenReturn(emptyDeviceList);

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class, () -> {
            biometrics095Service.handleDiscoveryResponse(Modality.FACE, responseBytes);
        });

        assertEquals(SBIError.SBI_DISC_INVALID_REPSONSE.getErrorCode(), exception.getErrorCode());
        assertEquals(SBIError.SBI_DISC_INVALID_REPSONSE.getErrorMessage(), exception.getErrorText());
        verify(mockAuditManagerService).audit(eq(AuditEvent.DEVICE_INFO_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void test_getRCaptureRequest_exceptionPhoto() {
        List<String> exceptionAttrs = Arrays.asList("attr1");
        CaptureRequest req = biometrics095Service.getRCaptureRequest(Modality.EXCEPTION_PHOTO, "dev1", exceptionAttrs);
        assertEquals("Registration", req.getPurpose());
        assertEquals(1, req.getBio().get(0).getCount());
        assertEquals("Face", req.getBio().get(0).getType());
    }

    @Test
    public void test_getRCaptureRequest_normalModality() {
        List<String> exceptionAttrs = Arrays.asList("LEFT_INDEX");
        CaptureRequest req = biometrics095Service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "dev2", exceptionAttrs);
        assertEquals("Finger", req.getBio().get(0).getType());
        assertTrue(req.getBio().get(0).getCount() >= 0);
    }

    @Test (expected = BiometricsServiceException.class)
    public void test_handleRCaptureResponse_success() throws Exception {
        CaptureRespDetail respDetail = new CaptureRespDetail();
        respDetail.setError(null);
        respDetail.setData(Base64.getUrlEncoder().encodeToString("{\"bioType\":\"FINGERPRINT\",\"bioSubType\":\"LEFT_INDEX\",\"bioValue\":\"val\",\"timestamp\":\"2023-01-01T00:00:00Z\",\"qualityScore\":90}".getBytes()));
        respDetail.setSpecVersion("0.9.5");
        CaptureResponse captureResponse = new CaptureResponse();
        captureResponse.setBiometrics(Collections.singletonList(respDetail));
        InputStream is = new ByteArrayInputStream("dummy".getBytes());

        when(mockObjectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(captureResponse);
        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(
                new CaptureDto()
        );
        when(mockSharedPreferences.getString(anyString(), anyString())).thenReturn("DISABLE");

        biometrics095Service.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, is, Collections.emptyList());
    }

    @Test
    public void test_handleRCaptureResponse_bioError() throws Exception {
        CaptureRespDetail respDetail = new CaptureRespDetail();
        ErrorDto error = new ErrorDto();
        error.setErrorCode("123");
        error.setErrorInfo("fail");
        respDetail.setError(error);
        CaptureResponse captureResponse = new CaptureResponse();
        captureResponse.setBiometrics(Collections.singletonList(respDetail));
        InputStream is = new ByteArrayInputStream("dummy".getBytes());

        when(mockObjectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(captureResponse);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, is, Collections.emptyList()));
        assertEquals("123", ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.R_CAPTURE_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void test_handleRCaptureResponse_bioDataNull() throws Exception {
        CaptureRespDetail respDetail = new CaptureRespDetail();
        respDetail.setError(null);
        respDetail.setData(null);
        CaptureResponse captureResponse = new CaptureResponse();
        captureResponse.setBiometrics(Collections.singletonList(respDetail));
        InputStream is = new ByteArrayInputStream("dummy".getBytes());

        when(mockObjectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(captureResponse);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, is, Collections.emptyList()));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.R_CAPTURE_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void test_handleRCaptureResponse_generalException() throws Exception {
        InputStream is = new ByteArrayInputStream("dummy".getBytes());
        when(mockObjectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenThrow(new RuntimeException("fail"));

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, is, Collections.emptyList()));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.R_CAPTURE_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test (expected = BiometricsServiceException.class)
    public void test_handleDeviceInfoResponse_success() throws Exception {
        InfoResponse info = new InfoResponse();
        info.setError(null);
        info.setDeviceInfo(Base64.getUrlEncoder().encodeToString("{\"callbackId\":\"cb.info\",\"digitalId\":\"eyJzZXJpYWxObyI6InMyMTAifQ==\"}".getBytes()));
        List<InfoResponse> list = Collections.singletonList(info);
        byte[] respBytes = "dummy".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(list);
        when(mockObjectMapper.readValue(any(byte[].class), eq(DeviceDto.class))).thenReturn(new DeviceDto());
        when(mockObjectMapper.readValue(any(byte[].class), eq(DigitalId.class))).thenReturn(new DigitalId());

        biometrics095Service.handleDeviceInfoResponse(Modality.FACE, respBytes);
    }

    @Test
    public void test_handleDeviceInfoResponse_emptyList() throws Exception {
        List<InfoResponse> list = Collections.emptyList();
        byte[] respBytes = "dummy".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(list);

        assertThrows(RuntimeException.class, () ->
                biometrics095Service.handleDeviceInfoResponse(Modality.FACE, respBytes));
        verify(mockAuditManagerService).audit(eq(AuditEvent.DEVICE_INFO_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void test_handleDeviceInfoResponse_generalException() throws Exception {
        byte[] respBytes = "dummy".getBytes();
        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenThrow(new RuntimeException("fail"));

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleDeviceInfoResponse(Modality.FACE, respBytes));
        assertEquals(SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorCode(), ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.DEVICE_INFO_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void test_handleDiscoveryResponse_success() throws Exception {
        DeviceDto device = new DeviceDto();
        device.setCallbackId("cb-123");
        device.setDeviceStatus("Ready");
        device.setError(null);
        List<DeviceDto> list = Collections.singletonList(device);
        byte[] respBytes = "dummy".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(list);

        String result = biometrics095Service.handleDiscoveryResponse(Modality.FACE, respBytes);
        assertEquals("cb-123", result);
    }

    @Test
    public void test_handleDiscoveryResponse_deviceError() throws Exception {
        DeviceDto device = new DeviceDto();
        ErrorDto error = new ErrorDto();
        error.setErrorCode("123");
        error.setErrorInfo("fail");
        device.setError(error);
        List<DeviceDto> list = Collections.singletonList(device);
        byte[] respBytes = "dummy".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(list);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleDiscoveryResponse(Modality.FACE, respBytes));
        assertEquals("123", ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.DEVICE_INFO_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void test_handleDiscoveryResponse_emptyList() throws Exception {
        List<DeviceDto> list = Collections.emptyList();
        byte[] respBytes = "dummy".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(list);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleDiscoveryResponse(Modality.FACE, respBytes));
        assertEquals(SBIError.SBI_DISC_INVALID_REPSONSE.getErrorCode(), ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.DEVICE_INFO_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void test_handleDiscoveryResponse_generalException() throws Exception {
        byte[] respBytes = "dummy".getBytes();
        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenThrow(new RuntimeException("fail"));

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleDiscoveryResponse(Modality.FACE, respBytes));
        assertEquals(SBIError.SBI_DISC_INVALID_REPSONSE.getErrorCode(), ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.DISCOVER_SBI_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void test_getModalityThreshold_allCases() {
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(anyString())).thenReturn(10);
        assertEquals(10, biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_LEFT));
        assertEquals(10, biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_RIGHT));
        assertEquals(10, biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_THUMBS));
        assertEquals(10, biometrics095Service.getModalityThreshold(Modality.IRIS_DOUBLE));
        assertEquals(10, biometrics095Service.getModalityThreshold(Modality.FACE));
        assertEquals(0, biometrics095Service.getModalityThreshold(Modality.EXCEPTION_PHOTO));
    }

    @Test
    public void test_getAttemptsCount_allCases() {
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(anyString())).thenReturn(5);
        assertEquals(5, biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_LEFT));
        assertEquals(5, biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_RIGHT));
        assertEquals(5, biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_THUMBS));
        assertEquals(5, biometrics095Service.getAttemptsCount(Modality.IRIS_DOUBLE));
        assertEquals(5, biometrics095Service.getAttemptsCount(Modality.FACE));
        assertEquals(0, biometrics095Service.getAttemptsCount(Modality.EXCEPTION_PHOTO));
    }

    @Test
    public void test_validateJWTResponse_signatureValidAndTrustValid() throws Exception {
        JWTSignatureVerifyResponseDto resp = new JWTSignatureVerifyResponseDto();
        resp.setSignatureValid(true);
        resp.setTrustValid(KeyManagerConstant.TRUST_VALID);
        when(mockCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class))).thenReturn(resp);

        assertDoesNotThrow(() -> biometrics095Service.validateJWTResponse("jwt", "domain"));
    }

    @Test
    public void test_validateJWTResponse_signatureInvalid() throws Exception {
        JWTSignatureVerifyResponseDto resp = new JWTSignatureVerifyResponseDto();
        resp.setSignatureValid(false);
        resp.setTrustValid(KeyManagerConstant.TRUST_VALID);
        when(mockCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class))).thenReturn(resp);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.validateJWTResponse("jwt", "domain"));
        assertEquals(SBIError.SBI_INVALID_SIGNATURE.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void test_validateJWTResponse_trustInvalid() throws Exception {
        JWTSignatureVerifyResponseDto resp = new JWTSignatureVerifyResponseDto();
        resp.setSignatureValid(true);
        resp.setTrustValid("INVALID");
        when(mockCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class))).thenReturn(resp);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.validateJWTResponse("jwt", "domain"));
        assertEquals(SBIError.SBI_CERT_PATH_TRUST_FAILED.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void test_handleRCaptureResponse_nullBiometricsList() throws Exception {
        CaptureResponse captureResponse = new CaptureResponse();
        captureResponse.setBiometrics(null);
        InputStream is = new ByteArrayInputStream("dummy".getBytes());

        when(mockObjectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(captureResponse);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, is, Collections.emptyList()));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void test_handleDiscoveryResponse_nullCallbackId() throws Exception {
        DeviceDto device = new DeviceDto();
        device.setCallbackId(null);
        device.setDeviceStatus("Ready");
        device.setError(null);
        List<DeviceDto> list = Collections.singletonList(device);
        byte[] respBytes = "dummy".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(list);

        biometrics095Service.handleDiscoveryResponse(Modality.FACE, respBytes);
    }

    @Test
    public void test_getRCaptureRequest_nullDeviceId() {
        List<String> exceptionAttrs = Arrays.asList("attr1");
        CaptureRequest req = biometrics095Service.getRCaptureRequest(Modality.FACE, null, exceptionAttrs);
        assertNull(req.getBio().get(0).getDeviceId());
    }

    @Test (expected = NullPointerException.class)
    public void test_getRCaptureRequest_nullExceptionAttributes() {
        biometrics095Service.getRCaptureRequest(Modality.FACE, "dev1", null);
    }

    @Test
    public void test_validateJWTResponse_nullResponse() throws Exception {
        when(mockCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class))).thenReturn(null);
        assertThrows(NullPointerException.class, () ->
                biometrics095Service.validateJWTResponse("jwt", "domain"));
    }

    @Test
    public void test_handleDeviceInfoResponse_successful() throws Exception {
        InfoResponse info = new InfoResponse();
        info.setError(null);

        String deviceInfoJWT = "header.payload.signature";
        info.setDeviceInfo(deviceInfoJWT);

        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setCallbackId("cb.info");
        deviceDto.setDigitalId("header2.payload2.signature2");

        DigitalId digitalId = new DigitalId();
        digitalId.setSerialNo("serial123");

        List<InfoResponse> list = Collections.singletonList(info);
        byte[] respBytes = "dummy".getBytes();

        Biometrics095Service spyService = Mockito.spy(biometrics095Service);
        doNothing().when(spyService).validateJWTResponse(anyString(), anyString());
        doReturn("cGF5bG9hZA==").when(spyService).getJWTPayLoad(deviceInfoJWT);
        doReturn("cGF5bG9hZDI=").when(spyService).getJWTPayLoad(deviceDto.getDigitalId());

        when(mockObjectMapper.readValue(any(byte[].class), ArgumentMatchers.<TypeReference<List<InfoResponse>>>any()))
                .thenReturn(list);
        when(mockObjectMapper.readValue(any(byte[].class), eq(DeviceDto.class))).thenReturn(deviceDto);
        when(mockObjectMapper.readValue(any(byte[].class), eq(DigitalId.class))).thenReturn(digitalId);

        String[] result = spyService.handleDeviceInfoResponse(Modality.FACE, respBytes);

        assertEquals("cb", result[0]);
        assertEquals("serial123", result[1]);
    }

}
