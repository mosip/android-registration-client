package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.clientmanager.constant.ClientManagerError;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserOnboardServiceTest {

    @Mock
    private Context context;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private AuditManagerService auditManagerService;
    @Mock
    private CertificateManagerService certificateManagerService;
    @Mock
    private SyncRestService syncRestService;
    @Mock
    private CryptoManagerService cryptoManagerService;
    @Mock
    private RegistrationService registrationService;
    @Mock
    private UserBiometricRepository userBiometricRepository;
    @Mock
    private ClientCryptoManagerService clientCryptoManagerService;
    @Mock
    private UserDetailRepository userDetailRepository;
    @Mock
    private SharedPreferences sharedPreferences;
    @Mock
    private SharedPreferences.Editor sharedPreferencesEditor;

    private UserOnboardService userOnboardService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn("testUser");
        userOnboardService = new UserOnboardService(
                context,
                objectMapper,
                auditManagerService,
                certificateManagerService,
                syncRestService,
                cryptoManagerService,
                registrationService,
                userBiometricRepository,
                clientCryptoManagerService,
                userDetailRepository
        );
    }

    @Test
    public void testOnboardOperator_BiometricsNull_ThrowsException() {
        List<BiometricsDto> biometricsList = null;
        Runnable onFinish = mock(Runnable.class);

        ClientCheckedException exception = assertThrows(ClientCheckedException.class, () -> {
            userOnboardService.onboardOperator(biometricsList, onFinish);
        });
        assertEquals("REG-UOS-001", exception.getErrorCode());
    }

    @Test
    public void testGetBiometricsByModality() {
        BiometricsDto dto1 = new BiometricsDto();
        dto1.setModality("FINGER");
        BiometricsDto dto2 = new BiometricsDto();
        dto2.setModality("IRIS");
        List<BiometricsDto> biometrics = Arrays.asList(dto1, dto2);

        List<BiometricsDto> result = invokeGetBiometricsByModality("FINGER", biometrics);
        assertEquals(1, result.size());
        assertEquals("FINGER", result.get(0).getModality());
    }

    @Test
    public void testGetExtractedTemplatesReturnsEmptyForNull() {
        List<BIR> result = invokeGetExtractedTemplates(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetExtractedTemplatesReturnsBIRList() {
        BiometricsDto dto = new BiometricsDto();
        List<BiometricsDto> biometrics = Collections.singletonList(dto);
        BIR bir = new BIR();
        when(registrationService.buildBIR(any())).thenReturn(bir);

        List<BIR> result = invokeGetExtractedTemplates(biometrics);
        assertEquals(1, result.size());
    }

    @Test
    public void testSplitEncryptedData() {
        String data = "ZW5jcnlwdGVkU2Vzc2lvbktFWV9TUExJVEVSRU5DUllQVEVEQVRB";
        UserOnboardService.SplitEncryptedData split = userOnboardService.splitEncryptedData(data);
        assertNotNull(split.getEncryptedSessionKey());
        assertNotNull(split.getEncryptedData());
    }

    @Test
    public void testPrependZeros() {
        byte[] input = "abc".getBytes();
        byte[] result = invokePrependZeros(input, 2);
        assertEquals(input.length + 2, result.length);
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);
    }

    @Test
    public void testGetXOR() {
        String ts = "12345";
        String tid = "54321";
        byte[] xor = invokeGetXOR(ts, tid);
        assertEquals(ts.length(), xor.length);
    }

    private List<BiometricsDto> invokeGetBiometricsByModality(String modality, List<BiometricsDto> biometrics) {
        try {
            java.lang.reflect.Method m = UserOnboardService.class.getDeclaredMethod("getBiometricsByModality", String.class, List.class);
            m.setAccessible(true);
            return (List<BiometricsDto>) m.invoke(userOnboardService, modality, biometrics);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<BIR> invokeGetExtractedTemplates(List<BiometricsDto> biometrics) {
        try {
            java.lang.reflect.Method m = UserOnboardService.class.getDeclaredMethod("getExtractedTemplates", List.class);
            m.setAccessible(true);
            return (List<BIR>) m.invoke(userOnboardService, biometrics);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] invokePrependZeros(byte[] input, int count) {
        try {
            java.lang.reflect.Method m = UserOnboardService.class.getDeclaredMethod("prependZeros", byte[].class, int.class);
            m.setAccessible(true);
            return (byte[]) m.invoke(userOnboardService, input, count);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] invokeGetXOR(String ts, String tid) {
        try {
            java.lang.reflect.Method m = UserOnboardService.class.getDeclaredMethod("getXOR", String.class, String.class);
            m.setAccessible(true);
            return (byte[]) m.invoke(userOnboardService, ts, tid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_handle_null_biometrics_input() {
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);

        ClientCheckedException exception = assertThrows(ClientCheckedException.class, () -> {
            userOnboardService.onboardOperator(null, () -> {});
        });

        assertTrue(exception.getMessage().contains(ClientManagerError.REG_BIOMETRIC_DTO_NULL.getErrorCode()));
        assertTrue(exception.getMessage().contains(ClientManagerError.REG_BIOMETRIC_DTO_NULL.getErrorMessage()));
    }

    @Test
    public void test_returns_last_n_bytes() {
        byte[] inputBytes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int lastBytesNum = 3;

        byte[] result = ReflectionTestUtils.invokeMethod(userOnboardService, "getLastBytes", inputBytes, lastBytesNum);

        byte[] expected = {8, 9, 10};
        assertArrayEquals(expected, result);
    }

    @Test
    public void test_handles_full_array_copy() {
        byte[] inputBytes = {1, 2, 3, 4, 5};
        int lastBytesNum = inputBytes.length;

        byte[] result = ReflectionTestUtils.invokeMethod(userOnboardService, "getLastBytes", inputBytes, lastBytesNum);

        assertArrayEquals(inputBytes, result);
        assertNotSame(inputBytes, result);
    }

    @Test
    public void test_null_timestamp_throws_exception() {
        ReflectionTestUtils.setField(userOnboardService, "cryptoManagerService", cryptoManagerService);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(UserOnboardService.ON_BOARD_TIME_STAMP, null);
        byte[] testData = "testData".getBytes();

        assertThrows(NullPointerException.class, () -> {
            ReflectionTestUtils.invokeMethod(
                userOnboardService,
                "getSessionKey",
                requestMap,
                testData
            );
        });

        Mockito.verifyNoInteractions(cryptoManagerService);
    }
}
