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
import io.mosip.registration.clientmanager.util.BioSdkProviderFactory;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map;

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
    private BioSdkProviderFactory mockBioSdkProviderFactory;

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
    public void getRCaptureRequest_withFaceModality_returnsConfiguredRequest() {
        Modality modality = Modality.FACE;
        String deviceId = "testDevice";
        List<String> exceptionAttributes = new ArrayList<>();

        CaptureRequest request = biometrics095Service.getRCaptureRequest(modality, deviceId, exceptionAttributes);

        assertNotNull(request);
        assertEquals("Staging", request.getEnv());
        assertEquals("Registration", request.getPurpose());
        assertFalse("Bio list should not be empty", request.getBio().isEmpty());
        assertEquals(deviceId, request.getBio().get(0).getDeviceId());
        assertNotNull("transactionId must not be null", request.getTransactionId());
        assertNotNull("captureTime must not be null", request.getCaptureTime());
    }

    @Test
    public void getRCaptureRequest_multipleConsecutiveCalls_generatesUniqueTransactionIds() {
        CaptureRequest first = biometrics095Service.getRCaptureRequest(Modality.FACE, "dev1", new ArrayList<>());
        CaptureRequest second = biometrics095Service.getRCaptureRequest(Modality.FACE, "dev1", new ArrayList<>());
        assertNotEquals("Each request must have a unique transactionId",
                first.getTransactionId(), second.getTransactionId());
    }

    @Test
    public void handleRCaptureResponse_withParsingError_throwsBiometricsServiceException() throws Exception {
        Modality modality = Modality.FACE;
        List<String> exceptionAttributes = new ArrayList<>();
        InputStream responseStream = new ByteArrayInputStream("{}".getBytes());
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(modality, "dev", exceptionAttributes);

        when(mockObjectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<CaptureResponse>>any()))
                .thenThrow(new RuntimeException("Parsing error"));

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(modality, responseStream, exceptionAttributes, captureRequest.getTransactionId()));

        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), exception.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.R_CAPTURE_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void handleDeviceInfoResponse_withInvalidResponse_throwsBiometricsServiceException() throws Exception {
        Modality modality = Modality.FACE;
        byte[] response = "{}".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), ArgumentMatchers.<TypeReference<List<InfoResponse>>>any()))
                .thenThrow(new RuntimeException("Invalid data"));

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleDeviceInfoResponse(modality, response));

        assertEquals(SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    public void handleDiscoveryResponse_withSingleDevice_returnsCallbackId() throws Exception {
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
    public void handleDiscoveryResponse_withParsingError_throwsBiometricsServiceException() throws Exception {
        Modality modality = Modality.FACE;
        byte[] response = "{}".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), ArgumentMatchers.<TypeReference<List<DeviceDto>>>any()))
                .thenThrow(new RuntimeException("Parsing error"));

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleDiscoveryResponse(modality, response));

        assertEquals(SBIError.SBI_DISC_INVALID_REPSONSE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    public void getModalityThreshold_withFaceModality_returnsGlobalParamValue() {
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(anyString())).thenReturn(50);

        int threshold = biometrics095Service.getModalityThreshold(Modality.FACE);

        assertEquals(50, threshold);
    }

    @Test
    public void validateJWTResponse_withCryptoServiceException_throwsBiometricsServiceException() throws Exception {
        when(mockCryptoManagerService.jwtVerify(any())).thenThrow(new BiometricsServiceException(SBIError.SBI_INVALID_SIGNATURE.getErrorCode(), "Invalid signature"));

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.validateJWTResponse("signedData", "DEVICE"));

        assertEquals(SBIError.SBI_INVALID_SIGNATURE.getErrorCode(), exception.getErrorCode());
    }

    @Test
    public void getRCaptureRequest_withDeveloperProfile_setsEnvironmentFromProfile() {
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockContext.getString(anyInt())).thenReturn("app_name");
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_ACTIVE_PROFILE)).thenReturn("Developer");
        when(mockGlobalParamRepository.getCachedIntCaptureTimeout()).thenReturn(10000);

        Biometrics095Service biometrics095Service = new Biometrics095Service(
                mockContext, mockObjectMapper, mockAuditManagerService,
                mockGlobalParamRepository, mockCryptoManagerService, mockUserBiometricRepository, null);

        Modality modality = Modality.FACE;
        String deviceId = "test-device-id";
        List<String> exceptionAttributes = new ArrayList<>();

        CaptureRequest result = biometrics095Service.getRCaptureRequest(modality, deviceId, exceptionAttributes);

        assertEquals("Developer", result.getEnv());
        assertEquals("Registration", result.getPurpose());
        assertEquals(10000, result.getTimeout());
        assertEquals("0.9.5", result.getSpecVersion());
        assertNotNull("transactionId must not be null", result.getTransactionId());
        assertNotNull("captureTime must not be null", result.getCaptureTime());
    }

    @Test
    public void getRCaptureRequest_withNullModality_throwsNullPointerException() {
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockContext.getString(anyInt())).thenReturn("app_name");

        Biometrics095Service biometrics095Service = new Biometrics095Service(
                mockContext, mockObjectMapper, mockAuditManagerService,
                mockGlobalParamRepository, mockCryptoManagerService, mockUserBiometricRepository, null);

        String deviceId = "test-device-id";
        List<String> exceptionAttributes = new ArrayList<>();

        assertThrows(NullPointerException.class, () -> {
            biometrics095Service.getRCaptureRequest(null, deviceId, exceptionAttributes);
        });
    }

    @Test
    public void getRCaptureRequest_biometricTypeBasedOnModality_returnsBioType() {
        Biometrics095Service service = new Biometrics095Service(
                mockContext, new ObjectMapper(), mockAuditManagerService, mockGlobalParamRepository,
                mockCryptoManagerService, mockUserBiometricRepository, null
        );

        CaptureRequest request = service.getRCaptureRequest(Modality.EXCEPTION_PHOTO, "device123", new ArrayList<>());
        assertEquals("Face", request.getBio().get(0).getType());

        request = service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "device123", new ArrayList<>());
        assertEquals("Finger", request.getBio().get(0).getType());
    }

    @Test
    public void getRCaptureRequest_withExceptionAttributes_convertsToSpecBioSubType() {
        Biometrics095Service service = new Biometrics095Service(
                mockContext, new ObjectMapper(), mockAuditManagerService, mockGlobalParamRepository,
                mockCryptoManagerService, mockUserBiometricRepository, null
        );

        List<String> exceptionAttributes = Arrays.asList("leftthumb", "rightthumb");
        CaptureRequest request = service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "device123", exceptionAttributes);

        CaptureBioDetail detail = request.getBio().get(0);
        assertArrayEquals(new String[]{"Left Thumb", "Right Thumb"}, detail.getException());
    }

    @Test
    public void getRCaptureRequest_countSettingBasedOnModality_returnsCorrectCount() {
        Biometrics095Service service = new Biometrics095Service(
                mockContext, new ObjectMapper(), mockAuditManagerService, mockGlobalParamRepository,
                mockCryptoManagerService, mockUserBiometricRepository, null
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
    public void getModalityThreshold_thresholdScoreRetrieval_returnsRequestedScore() {
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.LEFT_SLAP_THRESHOLD_KEY)).thenReturn(50);

        Biometrics095Service service = new Biometrics095Service(
                mockContext,
                new ObjectMapper(),
                mock(AuditManagerService.class),
                mockGlobalParamRepository,
                mock(ClientCryptoManagerService.class),
                mock(UserBiometricRepository.class),
                null
        );

        CaptureRequest request = service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "device123", new ArrayList<>());

        CaptureBioDetail detail = request.getBio().get(0);
        assertEquals(50, detail.getRequestedScore());
    }

    @Test(expected = BiometricsServiceException.class)
    public void validateJWTResponse_withTrustDomain_throwsBiometricsServiceException() throws Exception {
        Biometrics095Service serviceSpy = Mockito.spy(
                new Biometrics095Service(mockContext, mockObjectMapper, mockAuditManagerService,
                        mockGlobalParamRepository, mockCryptoManagerService, mockUserBiometricRepository, null)
        );

        InputStream mockResponse = new ByteArrayInputStream("{\"biometrics\":[{\"specVersion\":\"0.9.5\",\"data\":\"mockData\",\"error\":null}]}".getBytes());
        List<String> exceptionAttributes = new ArrayList<>();
        Modality modality = Modality.FACE;
        CaptureRequest captureRequest = serviceSpy.getRCaptureRequest(modality, "dev", exceptionAttributes);

        doNothing().when(serviceSpy).validateJWTResponse(anyString(), eq("DEVICE"));

        serviceSpy.handleRCaptureResponse(modality, mockResponse, exceptionAttributes, captureRequest.getTransactionId());

        verify(serviceSpy).validateJWTResponse(anyString(), eq("DEVICE"));
    }

    @Test
    public void handleDeviceInfoResponse_invalidJwtResponse_throwsBiometricsServiceException() throws Exception {
        Biometrics095Service service = new Biometrics095Service(
                mockContext, mockObjectMapper, mockAuditManagerService, mockGlobalParamRepository,
                mockCryptoManagerService, mockUserBiometricRepository, null);

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
    public void handleDeviceInfoResponse_invalidJwtPayload_throwsBiometricsServiceException() throws Exception {
        Biometrics095Service biometrics095Service = new Biometrics095Service(
                mockContext, mockObjectMapper, mockAuditManagerService, mockGlobalParamRepository,
                mockCryptoManagerService, mockUserBiometricRepository, null);

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
    public void getModalityThreshold_fingerprintSlabLeft_returnsThresholdValue() {
        int expectedThreshold = 100;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.LEFT_SLAP_THRESHOLD_KEY)))
                .thenReturn(expectedThreshold);

        int actualThreshold = biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_LEFT);

        assertEquals(expectedThreshold, actualThreshold);
    }

    @Test
    public void getModalityThreshold_fingerprintSlabRight_returnsThresholdValue() {
        int expectedThreshold = 150;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.RIGHT_SLAP_THRESHOLD_KEY)))
                .thenReturn(expectedThreshold);

        int actualThreshold = biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_RIGHT);

        assertEquals(expectedThreshold, actualThreshold);
    }

    @Test
    public void getModalityThreshold_fingerprintSlabThumbs_returnsThresholdValue() {
        int expectedThreshold = 200;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.THUMBS_THRESHOLD_KEY)))
                .thenReturn(expectedThreshold);

        int actualThreshold = biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_THUMBS);

        assertEquals(expectedThreshold, actualThreshold);
    }

    @Test
    public void getModalityThreshold_irisDouble_returnsThresholdValue() {
        int expectedThreshold = 250;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.IRIS_THRESHOLD_KEY)))
                .thenReturn(expectedThreshold);

        int actualThreshold = biometrics095Service.getModalityThreshold(Modality.IRIS_DOUBLE);

        assertEquals(expectedThreshold, actualThreshold);
    }

    @Test
    public void getModalityThreshold_face_returnsThresholdValue() {
        int expectedThreshold = 300;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.FACE_THRESHOLD_KEY)))
                .thenReturn(expectedThreshold);

        int actualThreshold = biometrics095Service.getModalityThreshold(Modality.FACE);

        assertEquals(expectedThreshold, actualThreshold);
    }

    @Test
    public void getModalityThreshold_unhandledModality_returnsZero() {
        Modality unhandledModality = Modality.EXCEPTION_PHOTO;

        int actualAttempts = biometrics095Service.getModalityThreshold(unhandledModality);

        assertEquals(0, actualAttempts);
    }

    @Test
    public void getAttemptsCount_fingerprintSlabLeft_returnsAttemptsCount() {
        int expectedAttempts = 3;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.LEFT_SLAP_ATTEMPTS_KEY)))
                .thenReturn(expectedAttempts);

        int actualAttempts = biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_LEFT);

        assertEquals(expectedAttempts, actualAttempts);
    }

    @Test
    public void getAttemptsCount_fingerprintSlabRight_returnsAttemptsCount() {
        int expectedAttempts = 4;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.RIGHT_SLAP_ATTEMPTS_KEY)))
                .thenReturn(expectedAttempts);

        int actualAttempts = biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_RIGHT);

        assertEquals(expectedAttempts, actualAttempts);
    }

    @Test
    public void getAttemptsCount_fingerprintSlabThumbs_returnsAttemptsCount() {
        int expectedAttempts = 5;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.THUMBS_ATTEMPTS_KEY)))
                .thenReturn(expectedAttempts);

        int actualAttempts = biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_THUMBS);

        assertEquals(expectedAttempts, actualAttempts);
    }

    @Test
    public void getAttemptsCount_irisDouble_returnsAttemptsCount() {
        int expectedAttempts = 2;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.IRIS_ATTEMPTS_KEY)))
                .thenReturn(expectedAttempts);

        int actualAttempts = biometrics095Service.getAttemptsCount(Modality.IRIS_DOUBLE);

        assertEquals(expectedAttempts, actualAttempts);
    }

    @Test
    public void getAttemptsCount_face_returnsAttemptsCount() {
        int expectedAttempts = 1;
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(eq(RegistrationConstants.FACE_ATTEMPTS_KEY)))
                .thenReturn(expectedAttempts);

        int actualAttempts = biometrics095Service.getAttemptsCount(Modality.FACE);

        assertEquals(expectedAttempts, actualAttempts);
    }

    @Test
    public void getAttemptsCount_unhandledModality_returnsZero() {
        Modality unhandledModality = Modality.EXCEPTION_PHOTO;

        int actualAttempts = biometrics095Service.getAttemptsCount(unhandledModality);

        assertEquals(0, actualAttempts);
    }

    @Test
    public void validateJWTResponse_validSignatureAndTrust_completesWithoutException() throws Exception {
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
    public void validateJWTResponse_invalidSignature_throwsBiometricsServiceException() throws Exception {
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
    public void handleDiscoveryResponse_validJsonResponse_returnsCallbackId() throws Exception {
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
    public void handleDiscoveryResponse_emptyDeviceList_throwsBiometricsServiceException() throws Exception {
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
    public void getRCaptureRequest_exceptionPhoto_returnsConfiguredRequest() {
        List<String> exceptionAttrs = Arrays.asList("attr1");
        CaptureRequest req = biometrics095Service.getRCaptureRequest(Modality.EXCEPTION_PHOTO, "dev1", exceptionAttrs);
        assertEquals("Registration", req.getPurpose());
        assertEquals(1, req.getBio().get(0).getCount());
        assertEquals("Face", req.getBio().get(0).getType());
    }

    @Test
    public void getRCaptureRequest_normalModality_returnsBioType() {
        List<String> exceptionAttrs = Arrays.asList("LEFT_INDEX");
        CaptureRequest req = biometrics095Service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "dev2", exceptionAttrs);
        assertEquals("Finger", req.getBio().get(0).getType());
        assertTrue(req.getBio().get(0).getCount() >= 0);
    }

    @Test (expected = BiometricsServiceException.class)
    public void handleRCaptureResponse_validResponse_throwsBiometricsServiceException() throws Exception {
        CaptureRespDetail respDetail = new CaptureRespDetail();
        respDetail.setError(null);
        respDetail.setData(Base64.getUrlEncoder().encodeToString("{\"bioType\":\"FINGERPRINT\",\"bioSubType\":\"LEFT_INDEX\",\"bioValue\":\"val\",\"timestamp\":\"2023-01-01T00:00:00Z\",\"qualityScore\":90}".getBytes()));
        respDetail.setSpecVersion("0.9.5");
        CaptureResponse captureResponse = new CaptureResponse();
        captureResponse.setBiometrics(Collections.singletonList(respDetail));
        InputStream is = new ByteArrayInputStream("dummy".getBytes());
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "dev", Collections.emptyList());

        when(mockObjectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(captureResponse);
        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(new CaptureDto());
        when(mockSharedPreferences.getString(anyString(), anyString())).thenReturn("DISABLE");

        biometrics095Service.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, is, Collections.emptyList(), captureRequest.getTransactionId());
    }

    @Test
    public void handleRCaptureResponse_bioError_throwsBiometricsServiceException() throws Exception {
        CaptureRespDetail respDetail = new CaptureRespDetail();
        ErrorDto error = new ErrorDto();
        error.setErrorCode("123");
        error.setErrorInfo("fail");
        respDetail.setError(error);
        CaptureResponse captureResponse = new CaptureResponse();
        captureResponse.setBiometrics(Collections.singletonList(respDetail));
        InputStream is = new ByteArrayInputStream("dummy".getBytes());
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "dev", Collections.emptyList());

        when(mockObjectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(captureResponse);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, is, Collections.emptyList(), captureRequest.getTransactionId()));
        assertEquals("123", ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.R_CAPTURE_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void handleRCaptureResponse_bioDataNull_throwsBiometricsServiceException() throws Exception {
        CaptureRespDetail respDetail = new CaptureRespDetail();
        respDetail.setError(null);
        respDetail.setData(null);
        CaptureResponse captureResponse = new CaptureResponse();
        captureResponse.setBiometrics(Collections.singletonList(respDetail));
        InputStream is = new ByteArrayInputStream("dummy".getBytes());
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "dev", Collections.emptyList());

        when(mockObjectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(captureResponse);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, is, Collections.emptyList(), captureRequest.getTransactionId()));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.R_CAPTURE_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void handleRCaptureResponse_generalException_throwsBiometricsServiceException() throws Exception {
        InputStream is = new ByteArrayInputStream("dummy".getBytes());
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "dev", Collections.emptyList());
        when(mockObjectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenThrow(new RuntimeException("fail"));

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, is, Collections.emptyList(), captureRequest.getTransactionId()));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.R_CAPTURE_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test (expected = BiometricsServiceException.class)
    public void handleDeviceInfoResponse_validResponse_throwsBiometricsServiceException() throws Exception {
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
    public void handleDeviceInfoResponse_emptyList_throwsException() throws Exception {
        List<InfoResponse> list = Collections.emptyList();
        byte[] respBytes = "dummy".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(list);

        assertThrows(RuntimeException.class, () ->
                biometrics095Service.handleDeviceInfoResponse(Modality.FACE, respBytes));
        verify(mockAuditManagerService).audit(eq(AuditEvent.DEVICE_INFO_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void handleDeviceInfoResponse_generalException_throwsBiometricsServiceException() throws Exception {
        byte[] respBytes = "dummy".getBytes();
        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenThrow(new RuntimeException("fail"));

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleDeviceInfoResponse(Modality.FACE, respBytes));
        assertEquals(SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorCode(), ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.DEVICE_INFO_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void handleDiscoveryResponse_singleDevice_returnsCallbackId() throws Exception {
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
    public void handleDiscoveryResponse_deviceError_throwsBiometricsServiceException() throws Exception {
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
    public void handleDiscoveryResponse_emptyList_throwsBiometricsServiceException() throws Exception {
        List<DeviceDto> list = Collections.emptyList();
        byte[] respBytes = "dummy".getBytes();

        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(list);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleDiscoveryResponse(Modality.FACE, respBytes));
        assertEquals(SBIError.SBI_DISC_INVALID_REPSONSE.getErrorCode(), ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.DEVICE_INFO_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void handleDiscoveryResponse_generalException_throwsBiometricsServiceException() throws Exception {
        byte[] respBytes = "dummy".getBytes();
        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenThrow(new RuntimeException("fail"));

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleDiscoveryResponse(Modality.FACE, respBytes));
        assertEquals(SBIError.SBI_DISC_INVALID_REPSONSE.getErrorCode(), ex.getErrorCode());
        verify(mockAuditManagerService).audit(eq(AuditEvent.DISCOVER_SBI_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void getModalityThreshold_allModalities_returnsConfiguredThresholds() {
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(anyString())).thenReturn(10);
        assertEquals(10, biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_LEFT));
        assertEquals(10, biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_RIGHT));
        assertEquals(10, biometrics095Service.getModalityThreshold(Modality.FINGERPRINT_SLAB_THUMBS));
        assertEquals(10, biometrics095Service.getModalityThreshold(Modality.IRIS_DOUBLE));
        assertEquals(10, biometrics095Service.getModalityThreshold(Modality.FACE));
        assertEquals(0, biometrics095Service.getModalityThreshold(Modality.EXCEPTION_PHOTO));
    }

    @Test
    public void getAttemptsCount_allModalities_returnsConfiguredAttempts() {
        when(mockGlobalParamRepository.getCachedIntegerGlobalParam(anyString())).thenReturn(5);
        assertEquals(5, biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_LEFT));
        assertEquals(5, biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_RIGHT));
        assertEquals(5, biometrics095Service.getAttemptsCount(Modality.FINGERPRINT_SLAB_THUMBS));
        assertEquals(5, biometrics095Service.getAttemptsCount(Modality.IRIS_DOUBLE));
        assertEquals(5, biometrics095Service.getAttemptsCount(Modality.FACE));
        assertEquals(0, biometrics095Service.getAttemptsCount(Modality.EXCEPTION_PHOTO));
    }

    @Test
    public void validateJWTResponse_signatureValidAndTrustValid_completesWithoutException() throws Exception {
        JWTSignatureVerifyResponseDto resp = new JWTSignatureVerifyResponseDto();
        resp.setSignatureValid(true);
        resp.setTrustValid(KeyManagerConstant.TRUST_VALID);
        when(mockCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class))).thenReturn(resp);

        assertDoesNotThrow(() -> biometrics095Service.validateJWTResponse("jwt", "domain"));
    }

    @Test
    public void validateJWTResponse_signatureInvalid_throwsBiometricsServiceException() throws Exception {
        JWTSignatureVerifyResponseDto resp = new JWTSignatureVerifyResponseDto();
        resp.setSignatureValid(false);
        resp.setTrustValid(KeyManagerConstant.TRUST_VALID);
        when(mockCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class))).thenReturn(resp);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.validateJWTResponse("jwt", "domain"));
        assertEquals(SBIError.SBI_INVALID_SIGNATURE.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void validateJWTResponse_trustInvalid_throwsBiometricsServiceException() throws Exception {
        JWTSignatureVerifyResponseDto resp = new JWTSignatureVerifyResponseDto();
        resp.setSignatureValid(true);
        resp.setTrustValid("INVALID");
        when(mockCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class))).thenReturn(resp);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.validateJWTResponse("jwt", "domain"));
        assertEquals(SBIError.SBI_CERT_PATH_TRUST_FAILED.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void handleRCaptureResponse_nullBiometricsList_throwsBiometricsServiceException() throws Exception {
        CaptureResponse captureResponse = new CaptureResponse();
        captureResponse.setBiometrics(null);
        InputStream is = new ByteArrayInputStream("dummy".getBytes());
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "dev", Collections.emptyList());

        when(mockObjectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(captureResponse);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, is, Collections.emptyList(), captureRequest.getTransactionId()));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void handleDiscoveryResponse_nullCallbackId_returnsNull() throws Exception {
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
    public void getRCaptureRequest_nullDeviceId_returnsRequestWithNullDeviceId() {
        List<String> exceptionAttrs = Arrays.asList("attr1");
        CaptureRequest req = biometrics095Service.getRCaptureRequest(Modality.FACE, null, exceptionAttrs);
        assertNull(req.getBio().get(0).getDeviceId());
    }

    @Test (expected = NullPointerException.class)
    public void getRCaptureRequest_nullExceptionAttributes_throwsNullPointerException() {
        biometrics095Service.getRCaptureRequest(Modality.FACE, "dev1", null);
    }

    // --- transactionId / specVersion / purpose validation ---

    @Test
    public void handleRCaptureResponse_nullTransactionId_throwsException() throws Exception {
        CaptureRespDetail respDetail = buildRespDetail(null, "0.9.5", "Registration");
        InputStream is = buildCaptureResponseStream(respDetail);
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.FACE, "dev", Collections.emptyList());
        setupJwtValidationBypass(respDetail);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FACE, is, Collections.emptyList(), captureRequest.getTransactionId()));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
        assertTrue(ex.getErrorText().contains("TransactionId"));
    }

    @Test
    public void handleRCaptureResponse_transactionIdMismatch_throwsException() throws Exception {
        CaptureRespDetail respDetail = buildRespDetail("WRONG-TXN-ID", "0.9.5", "Registration");
        InputStream is = buildCaptureResponseStream(respDetail);
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.FACE, "dev", Collections.emptyList());
        setupJwtValidationBypass(respDetail);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FACE, is, Collections.emptyList(), captureRequest.getTransactionId()));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
        assertTrue(ex.getErrorText().contains("TransactionId"));
    }

    @Test
    public void handleRCaptureResponse_nullSpecVersion_throwsException() throws Exception {
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.FACE, "dev", Collections.emptyList());
        CaptureRespDetail respDetail = buildRespDetail(captureRequest.getTransactionId(), null, "Registration");
        InputStream is = buildCaptureResponseStream(respDetail);
        setupJwtValidationBypass(respDetail);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FACE, is, Collections.emptyList(), captureRequest.getTransactionId()));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
        assertTrue(ex.getErrorText().contains("SpecVersion"));
    }

    @Test
    public void handleRCaptureResponse_specVersionMismatch_throwsException() throws Exception {
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.FACE, "dev", Collections.emptyList());
        CaptureRespDetail respDetail = buildRespDetail(captureRequest.getTransactionId(), "1.0.0", "Registration");
        InputStream is = buildCaptureResponseStream(respDetail);
        setupJwtValidationBypass(respDetail);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FACE, is, Collections.emptyList(), captureRequest.getTransactionId()));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
        assertTrue(ex.getErrorText().contains("SpecVersion"));
    }

    @Test
    public void handleRCaptureResponse_purposeMismatch_throwsException() throws Exception {
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.FACE, "dev", Collections.emptyList());
        CaptureRespDetail respDetail = buildRespDetail(captureRequest.getTransactionId(), captureRequest.getSpecVersion(), "Auth");
        InputStream is = buildCaptureResponseStream(respDetail);
        setupJwtValidationBypass(respDetail);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FACE, is, Collections.emptyList(), captureRequest.getTransactionId()));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
        assertTrue(ex.getErrorText().contains("Purpose"));
    }

    // helpers for MOSIP-44993 validation tests

    private CaptureRespDetail buildRespDetail(String transactionId, String specVersion, String purpose) throws Exception {
        CaptureDto captureDto = new CaptureDto();
        captureDto.setTransactionId(transactionId);
        captureDto.setPurpose(purpose);
        captureDto.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(new ObjectMapper().writeValueAsBytes(captureDto));
        CaptureRespDetail detail = new CaptureRespDetail();
        detail.setData("header." + payload + ".sig");
        detail.setSpecVersion(specVersion);
        detail.setError(null);
        return detail;
    }

    private InputStream buildCaptureResponseStream(CaptureRespDetail detail) throws Exception {
        CaptureResponse resp = new CaptureResponse();
        resp.setBiometrics(Collections.singletonList(detail));
        return new ByteArrayInputStream(new ObjectMapper().writeValueAsBytes(resp));
    }

    private void setupJwtValidationBypass(CaptureRespDetail detail) throws Exception {
        biometrics095Service = Mockito.spy(biometrics095Service);
        ReflectionTestUtils.setField(biometrics095Service, "objectMapper", new ObjectMapper());
        doNothing().when(biometrics095Service).validateJWTResponse(anyString(), anyString());
        doNothing().when(biometrics095Service).validateResponseTimestamp(anyString());
    }

    @Test
    public void validateJWTResponse_nullResponse_throwsNullPointerException() throws Exception {
        when(mockCryptoManagerService.jwtVerify(any(JWTSignatureVerifyRequestDto.class))).thenReturn(null);
        assertThrows(NullPointerException.class, () ->
                biometrics095Service.validateJWTResponse("jwt", "domain"));
    }

    @Test
    public void handleDeviceInfoResponse_successfulParsing_returnsCallbackIdAndSerialNo() throws Exception {
        InfoResponse info = new InfoResponse();
        info.setError(null);

        String deviceInfoJWT = "header.payload.signature";
        info.setDeviceInfo(deviceInfoJWT);

        // Must satisfy isDeviceValid: specVersion contains "0.9.5", status "Ready", cert "L0"
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setCallbackId("cb.info");
        deviceDto.setDigitalId("header2.payload2.signature2");
        deviceDto.setSpecVersion(new String[]{"0.9.5"});
        deviceDto.setDeviceStatus("Ready");
        deviceDto.setCertification("L0");
        deviceDto.setDeviceCode("device-code-001");
        deviceDto.setDeviceId("device-id-001");

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

    // --- isDeviceValid tests via handleDeviceInfoResponse ---

    @Test
    public void handleDeviceInfoResponse_nullSpecVersion_throwsInvalidResponseException() throws Exception {
        DeviceDto deviceDto = buildValidDeviceDto();
        deviceDto.setSpecVersion(null);
        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class,
                () -> runHandleDeviceInfo(deviceDto));
        assertEquals(SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void handleDeviceInfoResponse_wrongSpecVersion_throwsInvalidResponseException() throws Exception {
        DeviceDto deviceDto = buildValidDeviceDto();
        deviceDto.setSpecVersion(new String[]{"1.0.0"});
        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class,
                () -> runHandleDeviceInfo(deviceDto));
        assertEquals(SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void handleDeviceInfoResponse_deviceStatusNotReady_throwsInvalidResponseException() throws Exception {
        DeviceDto deviceDto = buildValidDeviceDto();
        deviceDto.setDeviceStatus("Busy");
        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class,
                () -> runHandleDeviceInfo(deviceDto));
        assertEquals(SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void handleDeviceInfoResponse_wrongCertification_throwsInvalidResponseException() throws Exception {
        DeviceDto deviceDto = buildValidDeviceDto();
        deviceDto.setCertification("L1");
        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class,
                () -> runHandleDeviceInfo(deviceDto));
        assertEquals(SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorCode(), ex.getErrorCode());
    }

    @Test
    public void handleDeviceInfoResponse_nullCertification_throwsInvalidResponseException() throws Exception {
        DeviceDto deviceDto = buildValidDeviceDto();
        deviceDto.setCertification(null);
        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class,
                () -> runHandleDeviceInfo(deviceDto));
        assertEquals(SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorCode(), ex.getErrorCode());
    }

    // --- merged transactionId null check ---

    @Test
    public void handleRCaptureResponse_nullRequestTransactionId_throwsMismatchException() throws Exception {
        CaptureRespDetail respDetail = buildRespDetail("SOME-TXN-ID", "0.9.5", "Registration");
        InputStream is = buildCaptureResponseStream(respDetail);
        setupJwtValidationBypass(respDetail);

        BiometricsServiceException ex = assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FACE, is, Collections.emptyList(), null));
        assertEquals(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(), ex.getErrorCode());
        assertTrue(ex.getErrorText().contains("TransactionId"));
    }

    // --- addBioDevice DEFAULT_PURPOSE fallback ---

    @Test
    public void addBioDevice_nullPurpose_storesDefaultPurpose() {
        DigitalId digitalId = new DigitalId();
        biometrics095Service.addBioDevice(Modality.FACE, "deviceCode1", digitalId, null, "0.9.5");

        Map<String, Object> stored = (Map<String, Object>) biometrics095Service.BIO_DEVICES.get(Modality.FACE);
        assertNotNull(stored);
        assertEquals("Registration", stored.get("purpose"));
    }

    @Test
    public void addBioDevice_validPurpose_storesProvidedPurpose() {
        DigitalId digitalId = new DigitalId();
        biometrics095Service.addBioDevice(Modality.FACE, "deviceCode1", digitalId, "Auth", "0.9.5");

        Map<String, Object> stored = (Map<String, Object>) biometrics095Service.BIO_DEVICES.get(Modality.FACE);
        assertEquals("Auth", stored.get("purpose"));
    }

    @Test
    public void addBioDevice_specVersionStored_matchesPassedValue() {
        DigitalId digitalId = new DigitalId();
        biometrics095Service.addBioDevice(Modality.FACE, "deviceCode1", digitalId, "Registration", "0.9.5");

        Map<String, Object> stored = (Map<String, Object>) biometrics095Service.BIO_DEVICES.get(Modality.FACE);
        assertEquals("0.9.5", stored.get("specVersion"));
    }

    // --- helpers ---

    private DeviceDto buildValidDeviceDto() {
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setCallbackId("cb.info");
        deviceDto.setDigitalId("header.payload.sig");
        deviceDto.setSpecVersion(new String[]{"0.9.5"});
        deviceDto.setDeviceStatus("Ready");
        deviceDto.setCertification("L0");
        deviceDto.setDeviceCode("device-code-001");
        deviceDto.setDeviceId("device-id-001");
        return deviceDto;
    }

    private void runHandleDeviceInfo(DeviceDto deviceDto) throws Exception {
        InfoResponse infoResponse = new InfoResponse();
        infoResponse.setDeviceInfo("header.payload.sig");
        List<InfoResponse> infoList = Collections.singletonList(infoResponse);
        String validBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString("{}".getBytes());

        Biometrics095Service spyService = Mockito.spy(biometrics095Service);
        doNothing().when(spyService).validateJWTResponse(anyString(), anyString());
        doReturn(validBase64).when(spyService).getJWTPayLoad(anyString());

        when(mockObjectMapper.readValue(any(byte[].class), ArgumentMatchers.<TypeReference<List<InfoResponse>>>any()))
                .thenReturn(infoList);
        when(mockObjectMapper.readValue(any(byte[].class), eq(DeviceDto.class))).thenReturn(deviceDto);

        spyService.handleDeviceInfoResponse(Modality.FACE, "dummy".getBytes());
    }

}
