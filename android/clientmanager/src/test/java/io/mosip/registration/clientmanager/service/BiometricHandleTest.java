package io.mosip.registration.clientmanager.service;

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
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BiometricHandleTest {

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
        lenient().when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
    }


    @Test
    public void handleRCaptureResponse_validCaptureResponse_returnsBiometricsList() throws Exception {
        Biometrics095Service service = new Biometrics095Service(mockContext, mockObjectMapper,
                mockAuditManagerService, mockGlobalParamRepository, mockCryptoManagerService, mockUserBiometricRepository, null);
        service.sharedPreferences = mockSharedPreferences;

        CaptureResponse captureResponse = new CaptureResponse();
        CaptureRespDetail captureRespDetail = new CaptureRespDetail();
        captureRespDetail.setData("eyJhbGciOiJIUzI1NiJ9.eyJiaW9UeXBlIjoiRmluZ2VyIiwiYmlvU3ViVHlwZSI6IkxlZnRJbmRleCIsImJpb1ZhbHVlIjoidGVzdFZhbHVlIiwidGltZXN0YW1wIjoiMjAyNC0wMS0wMVQxMDowMDowMFoiLCJxdWFsaXR5U2NvcmUiOjg1LjB9.signature");
        captureRespDetail.setSpecVersion("0.9.5");
        captureResponse.setBiometrics(Arrays.asList(captureRespDetail));

        CaptureDto captureDto = new CaptureDto();
        captureDto.setBioType("Finger");
        captureDto.setBioSubType("LeftIndex");
        captureDto.setBioValue("testValue");
        captureDto.setTimestamp("2024-01-01T10:00:00Z");
        captureDto.setQualityScore(85.0f);

        when(mockObjectMapper.readValue(eq(mockInputStream), any(TypeReference.class))).thenReturn(captureResponse);
        when(mockObjectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(captureDto);
        when(mockSharedPreferences.getString(eq(RegistrationConstants.DEDUPLICATION_ENABLE_FLAG), eq(""))).thenReturn("N");

        Biometrics095Service spyService = spy(service);
        doNothing().when(spyService).validateJWTResponse(anyString(), anyString());
        doReturn("eyJiaW9UeXBlIjoiRmluZ2VyIiwiYmlvU3ViVHlwZSI6IkxlZnRJbmRleCIsImJpb1ZhbHVlIjoidGVzdFZhbHVlIiwidGltZXN0YW1wIjoiMjAyNC0wMS0wMVQxMDowMDowMFoiLCJxdWFsaXR5U2NvcmUiOjg1LjB9").when(spyService).getJWTPayLoad(anyString());
        doReturn("eyJhbGciOiJIUzI1NiJ9..signature").when(spyService).getJWTSignatureWithHeader(anyString());
        doNothing().when(spyService).validateResponseTimestamp(anyString());

        List<String> exceptionAttributes = Arrays.asList("leftThumb");
        CaptureRequest captureRequest = service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "dev", exceptionAttributes);
        captureDto.setTransactionId(captureRequest.getTransactionId());
        captureDto.setPurpose(captureRequest.getPurpose());

        List<BiometricsDto> result = spyService.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, mockInputStream, exceptionAttributes, captureRequest.getTransactionId());

        assertNotNull(result);
        assertEquals(1, result.size());
        BiometricsDto biometricsDto = result.get(0);
        assertEquals("Finger", biometricsDto.getModality());
        assertEquals("LeftIndex", biometricsDto.getBioSubType());
        assertEquals("testValue", biometricsDto.getBioValue());
        assertEquals("0.9.5", biometricsDto.getSpecVersion());
    }

    @Test
    public void handleRCaptureResponse_captureResponseWithError_throwsBiometricsServiceException() throws Exception {
        Biometrics095Service service = new Biometrics095Service(mockContext, mockObjectMapper,
                mockAuditManagerService, mockGlobalParamRepository, mockCryptoManagerService, mockUserBiometricRepository, null);

        CaptureResponse captureResponse = new CaptureResponse();
        CaptureRespDetail captureRespDetail = new CaptureRespDetail();
        ErrorDto errorDto = new ErrorDto();
        errorDto.setErrorCode("101");
        errorDto.setErrorInfo("Biometric capture failed");
        captureRespDetail.setError(errorDto);
        captureResponse.setBiometrics(Arrays.asList(captureRespDetail));

        when(mockObjectMapper.readValue(eq(mockInputStream), any(TypeReference.class))).thenReturn(captureResponse);

        List<String> exceptionAttributes = Arrays.asList();

        CaptureRequest captureRequest = service.getRCaptureRequest(Modality.FINGERPRINT_SLAB_LEFT, "dev", exceptionAttributes);

        BiometricsServiceException exception = assertThrows(BiometricsServiceException.class, () -> {
            service.handleRCaptureResponse(Modality.FINGERPRINT_SLAB_LEFT, mockInputStream, exceptionAttributes, captureRequest.getTransactionId());
        });

        assertEquals("101", exception.getErrorCode());
        assertEquals("Biometric capture failed", exception.getErrorText());
        verify(mockAuditManagerService).audit(eq(AuditEvent.R_CAPTURE_PARSE_FAILED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void handleRCaptureResponse_exceptionPhotoModality_throwsBiometricsServiceException() throws Exception {
        String jsonResponse = "{\"biometrics\":[{\"specVersion\":\"0.9.5\",\"data\":\"eyJhbGciOiJIUzI1NiJ9.eyJiaW9UeXBlIjoiRmFjZSIsImJpb1N1YlR5cGUiOiJ1bmtub3duIiwiYmlvVmFsdWUiOiJkYXRhIn0=.signature\",\"error\":null}]}";
        InputStream responseStream = new ByteArrayInputStream(jsonResponse.getBytes());
        List<String> exceptionAttributes = Arrays.asList("unknown");
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.EXCEPTION_PHOTO, "dev", exceptionAttributes);

        assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.EXCEPTION_PHOTO, responseStream, exceptionAttributes, captureRequest.getTransactionId()));
    }

    @Test
    public void handleRCaptureResponse_multipleBiometrics_throwsBiometricsServiceException() throws Exception {
        String jsonResponse = "{\"biometrics\":[{\"specVersion\":\"0.9.5\",\"data\":\"eyJhbGciOiJIUzI1NiJ9.eyJiaW9UeXBlIjoiRmFjZSIsImJpb1N1YlR5cGUiOiJmYWNlIiwiYmlvVmFsdWUiOiJkYXRhIn0=.signature\",\"error\":null},{\"specVersion\":\"0.9.5\",\"data\":\"eyJhbGciOiJIUzI1NiJ9.eyJiaW9UeXBlIjoiRmFjZSIsImJpb1N1YlR5cGUiOiJmYWNlIiwiYmlvVmFsdWUiOiJkYXRhIn0=.signature\",\"error\":null}]}";
        InputStream responseStream = new ByteArrayInputStream(jsonResponse.getBytes());
        List<String> exceptionAttributes = new ArrayList<>();
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(Modality.FACE, "dev", exceptionAttributes);

        assertThrows(BiometricsServiceException.class, () ->
                biometrics095Service.handleRCaptureResponse(Modality.FACE, responseStream, exceptionAttributes, captureRequest.getTransactionId()));
    }

    @Test
    public void handleRCaptureResponse_nullOrEmptyBiometricData_throwsBiometricsServiceException() {
        InputStream mockInputStream = new ByteArrayInputStream("{\"biometrics\":[{\"specVersion\":\"0.9.5\",\"data\":null,\"hash\":\"mockHash\",\"sessionKey\":\"mockSessionKey\",\"thumbprint\":\"mockThumbprint\"}]}".getBytes());
        List<String> exceptionAttributes = new ArrayList<>();
        Modality modality = Modality.FACE;
        CaptureRequest captureRequest = biometrics095Service.getRCaptureRequest(modality, "dev", exceptionAttributes);

        assertThrows(BiometricsServiceException.class, () -> {
            biometrics095Service.handleRCaptureResponse(modality, mockInputStream, exceptionAttributes, captureRequest.getTransactionId());
        });
    }

}
