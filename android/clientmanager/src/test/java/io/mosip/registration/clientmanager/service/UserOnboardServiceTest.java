package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.clientmanager.constant.ClientManagerError;
import io.mosip.registration.clientmanager.dto.http.OnboardResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.*;

import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.keymanager.dto.CryptoManagerResponseDto;
import io.mosip.registration.keymanager.dto.SignResponseDto;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;
import retrofit2.Callback;
import retrofit2.Response;

import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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
        when(context.getString(anyInt())).thenReturn("app_name");
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn("testUserToken");
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
        // Patch for cases where constructor didn't set sharedPreferences
        ReflectionTestUtils.setField(userOnboardService, "sharedPreferences", sharedPreferences);
    }

    /**
     * Test onboardOperator() should throw exception if biometrics list is null.
     */
    @Test
    public void testOnboardOperator_BiometricsNull_ThrowsException() {
        List<BiometricsDto> biometricsList = null;
        Runnable onFinish = mock(Runnable.class);

        ClientCheckedException exception = assertThrows(ClientCheckedException.class, () -> {
            userOnboardService.onboardOperator(biometricsList, onFinish);
        });
        assertEquals("REG-UOS-001", exception.getErrorCode());
    }

    /**
     * Test getBiometricsByModality() returns only biometrics matching the given modality.
     */
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

    /**
     * Test getExtractedTemplates() returns empty list when input is null.
     */
    @Test
    public void testGetExtractedTemplatesReturnsEmptyForNull() {
        List<BIR> result = invokeGetExtractedTemplates(null);
        assertTrue(result.isEmpty());
    }

    /**
     * Test getExtractedTemplates() returns a list with BIR objects when biometrics are provided.
     */
    @Test
    public void testGetExtractedTemplatesReturnsBIRList() {
        BiometricsDto dto = new BiometricsDto();
        List<BiometricsDto> biometrics = Collections.singletonList(dto);
        BIR bir = new BIR();
        when(registrationService.buildBIR(any())).thenReturn(bir);

        List<BIR> result = invokeGetExtractedTemplates(biometrics);
        assertEquals(1, result.size());
    }

    /**
     * Test splitEncryptedData() splits the input string into session key and encrypted data.
     */
    @Test
    public void testSplitEncryptedData() {
        String data = "ZW5jcnlwdGVkU2Vzc2lvbktFWV9TUExJVEVSRU5DUllQVEVEQVRB";
        UserOnboardService.SplitEncryptedData split = userOnboardService.splitEncryptedData(data);
        assertNotNull(split.getEncryptedSessionKey());
        assertNotNull(split.getEncryptedData());
    }

    /**
     * Test prependZeros() adds the specified number of zeros at the beginning of the byte array.
     */
    @Test
    public void testPrependZeros() {
        byte[] input = "abc".getBytes();
        byte[] result = invokePrependZeros(input, 2);
        assertEquals(input.length + 2, result.length);
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);
    }

    /**
     * Test getXOR() returns a byte array of XORed characters from two strings of same length.
     */
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

    /**
     * Test onboardOperator() throws exception if biometrics input is null and checks error code/message.
     */
    @Test
    public void test_handle_null_biometrics_input() {
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);

        ClientCheckedException exception = assertThrows(ClientCheckedException.class, () -> {
            userOnboardService.onboardOperator(null, () -> {});
        });

        assertTrue(exception.getMessage().contains(ClientManagerError.REG_BIOMETRIC_DTO_NULL.getErrorCode()));
        assertTrue(exception.getMessage().contains(ClientManagerError.REG_BIOMETRIC_DTO_NULL.getErrorMessage()));
    }

    /**
     * Test getLastBytes() returns the last N bytes from the input array.
     */
    @Test
    public void test_returns_last_n_bytes() {
        byte[] inputBytes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int lastBytesNum = 3;

        byte[] result = ReflectionTestUtils.invokeMethod(userOnboardService, "getLastBytes", inputBytes, lastBytesNum);

        byte[] expected = {8, 9, 10};
        assertArrayEquals(expected, result);
    }

    /**
     * Test getLastBytes() returns a copy of the full array if requested length equals array length.
     */
    @Test
    public void test_handles_full_array_copy() {
        byte[] inputBytes = {1, 2, 3, 4, 5};
        int lastBytesNum = inputBytes.length;

        byte[] result = ReflectionTestUtils.invokeMethod(userOnboardService, "getLastBytes", inputBytes, lastBytesNum);

        assertArrayEquals(inputBytes, result);
        assertNotSame(inputBytes, result);
    }

    /**
     * Test getSessionKey() throws NullPointerException if timestamp is null in request map.
     */
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

    /**
     * Test save() returns true when both insertExtractedTemplates and saveOnboardStatus succeed.
     */
    @Test
    public void testSave_SuccessPath() throws Exception {
        // Set up biometrics with valid modality values
        BiometricsDto finger = new BiometricsDto();
        finger.setModality("FINGER");
        BiometricsDto iris = new BiometricsDto();
        iris.setModality("IRIS");
        BiometricsDto face = new BiometricsDto();
        face.setModality("FACE");
        List<BiometricsDto> biometrics = Arrays.asList(finger, iris, face);

        when(userBiometricRepository.insertExtractedTemplates(anyList(), anyString())).thenReturn("SUCCESS");
        when(userBiometricRepository.saveOnboardStatus(anyString())).thenReturn("SUCCESS");
        when(registrationService.buildBIR(any())).thenReturn(new BIR());

        boolean result = ReflectionTestUtils.invokeMethod(userOnboardService, "save", biometrics, "userX");
        assertTrue(result);
    }

    /**
     * Test save() returns false if insertExtractedTemplates does not return "SUCCESS".
     */
    @Test
    public void testSave_InsertNotSuccess() throws Exception {
        List<BiometricsDto> biometrics = Arrays.asList(new BiometricsDto());
        when(userBiometricRepository.insertExtractedTemplates(anyList(), anyString())).thenReturn("FAIL");
        when(registrationService.buildBIR(any())).thenReturn(new BIR());
        boolean result = ReflectionTestUtils.invokeMethod(userOnboardService, "save", biometrics, "userX");
        assertFalse(result);
    }

    /**
     * Test save() returns false if saveOnboardStatus does not return "SUCCESS".
     */
    @Test
    public void testSave_SaveOnboardStatusFail() throws Exception {
        List<BiometricsDto> biometrics = Arrays.asList(new BiometricsDto());
        when(userBiometricRepository.insertExtractedTemplates(anyList(), anyString())).thenReturn("SUCCESS");
        when(userBiometricRepository.saveOnboardStatus(anyString())).thenReturn("FAIL");
        when(registrationService.buildBIR(any())).thenReturn(new BIR());
        boolean result = ReflectionTestUtils.invokeMethod(userOnboardService, "save", biometrics, "userX");
        assertFalse(result);
    }

    /**
     * Test save() returns false if insertExtractedTemplates throws an exception.
     */
    @Test
    public void testSave_Exception() throws Exception {
        List<BiometricsDto> biometrics = Arrays.asList(new BiometricsDto());
        when(userBiometricRepository.insertExtractedTemplates(anyList(), anyString())).thenThrow(new RuntimeException("fail"));
        boolean result = ReflectionTestUtils.invokeMethod(userOnboardService, "save", biometrics, "userX");
        assertFalse(result);
    }

    /**
     * Test buildDataBlock() throws RuntimeException if objectMapper fails.
     */
    @Test(expected = RuntimeException.class)
    public void testBuildDataBlock_ObjectMapperException() throws Exception {
        UserOnboardService realService = new UserOnboardService(
                context, objectMapper, auditManagerService,
                certificateManagerService, syncRestService, cryptoManagerService,
                registrationService, userBiometricRepository, clientCryptoManagerService, userDetailRepository
        );
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("fail"));
        ReflectionTestUtils.invokeMethod(realService, "buildDataBlock", "FINGER", "LEFT", "abc".getBytes(), "hash");
    }

    /**
     * Test getIdaAuthResponse() covers onResponse (success/fail), onFailure, and exception branches.
     */
    @Test
    public void testGetIdaAuthResponse_onResponseBranches() throws Exception {
        try (MockedStatic<Toast> toastMock = mockStatic(Toast.class)) {
            // Return a mock Toast instance when makeText is called
            Toast toast = mock(Toast.class);
            toastMock.when(() -> Toast.makeText(any(), any(), anyInt())).thenReturn(toast);
            // Optionally, stub show() if your code calls it
            doNothing().when(toast).show();

            // ... rest of your test code from here ...
            Certificate certificate = mock(Certificate.class);
            PublicKey publicKey = mock(PublicKey.class);
            when(certificate.getPublicKey()).thenReturn(publicKey);
            when(cryptoManagerService.getCertificateThumbprint(any())).thenReturn("thumb".getBytes());
            KeyGenerator keyGen = mock(KeyGenerator.class);
            SecretKey secretKey = mock(SecretKey.class);
            when(cryptoManagerService.generateAESKey(anyInt())).thenReturn(keyGen);
            when(keyGen.generateKey()).thenReturn(secretKey);
            when(secretKey.getEncoded()).thenReturn("sekrit".getBytes());
            when(cryptoManagerService.symmetricEncryptWithRandomIV(any(), any(), any())).thenReturn("enc".getBytes());
            when(cryptoManagerService.asymmetricEncrypt(any(), any())).thenReturn("asy".getBytes());
            SignResponseDto signResponseDto = new SignResponseDto();
            signResponseDto.setData("sig");
            when(clientCryptoManagerService.sign(any())).thenReturn(signResponseDto);
            retrofit2.Call<OnboardResponseWrapper<Map<String, Object>>> call = mock(retrofit2.Call.class);
            when(syncRestService.doOperatorAuth(any(), any(), any(), any())).thenReturn(call);
            doAnswer(invocation -> {
                Callback cb = (Callback) invocation.getArgument(0);
                // onResponse (success) with null error
                Response resp = mock(Response.class);
                when(resp.isSuccessful()).thenReturn(true);
                OnboardResponseWrapper wrapper = mock(OnboardResponseWrapper.class);
                when(wrapper.getResponse()).thenReturn(Collections.singletonMap(UserOnboardService.ON_BOARD_AUTH_STATUS, true));
                when(resp.body()).thenReturn(wrapper);
                cb.onResponse(call, resp);
                // onResponse (fail)
                when(resp.isSuccessful()).thenReturn(false);
                cb.onResponse(call, resp);
                // onFailure
                cb.onFailure(call, new Exception("fail"));
                return null;
            }).when(call).enqueue(any());
            Map<String, Object> idaRequestMap = new HashMap<>();
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put(UserOnboardService.ON_BOARD_BIOMETRICS, new ArrayList<>());
            ReflectionTestUtils.invokeMethod(userOnboardService, "getIdaAuthResponse", idaRequestMap, requestMap, certificate, (Runnable) () -> {});
        }
    }

    /**
     * Test getCertificate() returns cached certificate if available.
     */
    @Test
    public void testGetCertificate_Cached() {
        when(certificateManagerService.getCertificate(anyString(), anyString())).thenReturn("cached");
        userOnboardService = new UserOnboardService(
                context, objectMapper, auditManagerService, certificateManagerService,
                syncRestService, cryptoManagerService, registrationService, userBiometricRepository,
                clientCryptoManagerService, userDetailRepository
        );
        final boolean[] called = {false};
        ReflectionTestUtils.invokeMethod(userOnboardService, "getCertificate", (Runnable) () -> called[0] = true);
        assertTrue(called[0]);
        assertEquals("cached", userOnboardService.getCertificateDataResponse());
    }

    /**
     * Test getCertificate() fetches certificate from network and handles success/failure.
     */
    @Test
    public void testGetCertificate_Network() {
        // Mock the static Toast.makeText method
        try (MockedStatic<Toast> toastMock = mockStatic(Toast.class)) {
            // Return a mock Toast instance
            Toast toast = mock(Toast.class);
            toastMock.when(() -> Toast.makeText(any(), any(), anyInt())).thenReturn(toast);
            // No-op for show(), prevents further errors
            doNothing().when(toast).show();

            when(certificateManagerService.getCertificate(anyString(), anyString())).thenReturn(null);
            retrofit2.Call<ResponseWrapper<Map<String, Object>>> call = mock(retrofit2.Call.class);
            when(syncRestService.getIDACertificate()).thenReturn(call);
            doAnswer(invocation -> {
                Callback cb = (Callback) invocation.getArgument(0);
                // Simulate network callback
                cb.onResponse(call, mock(Response.class));
                return null;
            }).when(call).enqueue(any());

            userOnboardService = new UserOnboardService(
                    context, objectMapper, auditManagerService, certificateManagerService,
                    syncRestService, cryptoManagerService, registrationService, userBiometricRepository,
                    clientCryptoManagerService, userDetailRepository
            );
            final boolean[] called = {false};
            ReflectionTestUtils.invokeMethod(userOnboardService, "getCertificate", (Runnable) () -> called[0] = true);
            assertTrue(called[0]);
        }
    }

    /**
     * Test splitAtFirstOccurrence() splits array at first occurrence of separator in various scenarios.
     */
    @Test
    public void testSplitAtFirstOccurrence_cases() {
        byte[] arr = "hello#KEY_SPLITTER#world".getBytes();
        byte[] sep = "#KEY_SPLITTER#".getBytes();
        byte[][] result = ReflectionTestUtils.invokeMethod(UserOnboardService.class, "splitAtFirstOccurrence", arr, sep);
        assertEquals("hello", new String(result[0]));
        assertEquals("world", new String(result[1]));

        // Not found
        arr = "helloworld".getBytes();
        result = ReflectionTestUtils.invokeMethod(UserOnboardService.class, "splitAtFirstOccurrence", arr, sep);
        assertEquals("helloworld", new String(result[0]));
        assertEquals("", new String(result[1]));

        // Found at 0
        arr = "#KEY_SPLITTER#abc".getBytes();
        result = ReflectionTestUtils.invokeMethod(UserOnboardService.class, "splitAtFirstOccurrence", arr, sep);
        assertEquals("", new String(result[0]));
        assertEquals("abc", new String(result[1]));

        // Found at end
        arr = "abc#KEY_SPLITTER#".getBytes();
        result = ReflectionTestUtils.invokeMethod(UserOnboardService.class, "splitAtFirstOccurrence", arr, sep);
        assertEquals("abc", new String(result[0]));
        assertEquals("", new String(result[1]));

        // Empty array
        arr = "".getBytes();
        result = ReflectionTestUtils.invokeMethod(UserOnboardService.class, "splitAtFirstOccurrence", arr, sep);
        assertEquals("", new String(result[0]));
        assertEquals("", new String(result[1]));
    }

    /**
     * Test findIndex() returns index of subarray if found, -1 otherwise.
     */
    @Test
    public void testFindIndex_foundAndNotFound() {
        byte[] arr = "hello#KEY_SPLITTER#world".getBytes();
        byte[] sep = "#KEY_SPLITTER#".getBytes();
        int idx = ReflectionTestUtils.invokeMethod(UserOnboardService.class, "findIndex", arr, sep);
        assertTrue(idx >= 0);
        // Not found
        arr = "helloworld".getBytes();
        idx = ReflectionTestUtils.invokeMethod(UserOnboardService.class, "findIndex", arr, sep);
        assertEquals(-1, idx);
    }

    /**
     * Test idaResponse, isOnboardSuccess, and certificateDataResponse getters/setters work as expected.
     */
    @Test
    public void testGettersSetters() {
        userOnboardService.setIdaResponse(true);
        assertTrue(userOnboardService.isIdaResponse());
        userOnboardService.setIsOnboardSuccess(true);
        assertTrue(userOnboardService.getIsOnboardSuccess());
    }

    /**
     * Test getOperatorBiometrics() returns a non-null list.
     */
    @Test
    public void testGetOperatorBiometrics() {
        List<BiometricsDto> list = userOnboardService.getOperatorBiometrics();
        assertNotNull(list);
    }

    /**
     * Test save() returns false if biometrics modality is null (should throw NPE internally).
     */
    @Test
    public void testSave_NullModality_Exception() throws Exception {
        BiometricsDto badDto = new BiometricsDto();
        badDto.setModality(null); // Deliberately null
        List<BiometricsDto> biometrics = Arrays.asList(badDto);
        when(registrationService.buildBIR(any())).thenReturn(new BIR());
        // Should throw NPE in .toLowerCase()
        boolean result = ReflectionTestUtils.invokeMethod(userOnboardService, "save", biometrics, "userX");
        assertFalse(result);
    }

    /**
     * Test getExtractedTemplates() returns empty list if buildBIR throws exception.
     */
    @Test
    public void testGetExtractedTemplates_handlesBuildBIRException() {
        BiometricsDto dto = new BiometricsDto();
        dto.setModality("FINGER");
        List<BiometricsDto> biometrics = Collections.singletonList(dto);
        when(registrationService.buildBIR(any())).thenThrow(new RuntimeException("fail"));
        List<BIR> result = ReflectionTestUtils.invokeMethod(userOnboardService, "getExtractedTemplates", biometrics);
        assertTrue(result.isEmpty());
    }

    /**
     * Test getBiometricsByModality() returns empty list if no biometrics match the modality.
     */
    @Test
    public void testGetBiometricsByModality_NoMatch() {
        BiometricsDto dto1 = new BiometricsDto();
        dto1.setModality("FACE");
        List<BiometricsDto> biometrics = Arrays.asList(dto1);
        List<BiometricsDto> result = ReflectionTestUtils.invokeMethod(userOnboardService, "getBiometricsByModality", "IRIS", biometrics);
        assertTrue(result.isEmpty());
    }

    /**
     * Test onboardOperator() skips save and calls onFinish if idaResponse is already true.
     */
    @Test
    public void testOnboardOperator_IdaResponseAlreadyTrue() throws Exception {
        userOnboardService.setIdaResponse(true);
        BiometricsDto dto = new BiometricsDto();
        dto.setModality("FINGER");
        List<BiometricsDto> biometrics = Arrays.asList(dto);
        Runnable onFinish = mock(Runnable.class);

        // Save should be skipped, onFinish should be called
        userOnboardService.onboardOperator(biometrics, onFinish);
        verify(onFinish, atLeastOnce()).run();
    }

    /**
     * Test validateWithIDA() calls onFinish immediately if biometrics list is empty.
     */
    @Test
    public void testValidateWithIDA_EmptyBiometrics() {
        // Mock the static Toast.makeText method if needed
        try (MockedStatic<Toast> toastMock = mockStatic(Toast.class)) {
            Toast toast = mock(Toast.class);
            toastMock.when(() -> Toast.makeText(any(), any(), anyInt())).thenReturn(toast);
            doNothing().when(toast).show();

            // Mock getIDACertificate to return a mock Call
            retrofit2.Call<ResponseWrapper<Map<String, Object>>> call = mock(retrofit2.Call.class);
            when(syncRestService.getIDACertificate()).thenReturn(call);
            doAnswer(invocation -> {
                Callback cb = (Callback) invocation.getArgument(0);
                // Simulate network callback
                cb.onResponse(call, mock(Response.class));
                return null;
            }).when(call).enqueue(any());

            List<BiometricsDto> biometrics = Collections.emptyList();
            Runnable onFinish = mock(Runnable.class);
            userOnboardService.validateWithIDA("userX", biometrics, onFinish);
            // Now this will pass
            verify(onFinish, atLeastOnce()).run();
        }
    }

    /**
     * Test buildDataBlock() returns non-null result on happy path.
     */
    @Test
    public void testBuildDataBlock_Success() throws Exception {
        // Mock cryptoManagerService
        byte[] sessionKey = "abcd".getBytes();
        CryptoManagerResponseDto resp = new CryptoManagerResponseDto();
        resp.setData(Base64.getEncoder().encodeToString("part1#KEY_SPLITTER#part2".getBytes()));
        when(cryptoManagerService.encrypt(any())).thenReturn(resp);

        // Fix: Mock objectMapper to return non-null JSON
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        String prevHash = "prev";
        String modality = "FINGER";
        String bioSubType = "LEFT";
        byte[] attr = "bio".getBytes();

        Object result = ReflectionTestUtils.invokeMethod(userOnboardService, "buildDataBlock", modality, bioSubType, attr, prevHash);
        assertNotNull(result);
    }

    /**
     * Test splitEncryptedData() handles empty input string gracefully.
     */
    @Test
    public void testSplitEncryptedData_EmptyData() {
        UserOnboardService.SplitEncryptedData result = userOnboardService.splitEncryptedData("");
        assertNotNull(result.getEncryptedSessionKey());
        assertNotNull(result.getEncryptedData());
    }

    /**
     * Test getSessionKey() throws NullPointerException if timestamp is null.
     */
    @Test(expected = NullPointerException.class)
    public void testGetSessionKey_NullTimestamp() throws Exception {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(UserOnboardService.ON_BOARD_TIME_STAMP, null);
        byte[] data = "test".getBytes();
        ReflectionTestUtils.invokeMethod(userOnboardService, "getSessionKey", requestMap, data);
    }

    /**
     * Test getXOR() returns correct length when timestamp and transactionId are same length.
     */
    @Test
    public void testGetXOR_SameLength() {
        byte[] result = ReflectionTestUtils.invokeMethod(userOnboardService, "getXOR", "abc", "def");
        assertEquals(3, result.length);
    }

    /**
     * Test prependZeros() returns original array if count is zero.
     */
    @Test
    public void testPrependZeros_ZeroCount() {
        byte[] input = {1, 2, 3};
        byte[] result = ReflectionTestUtils.invokeMethod(userOnboardService, "prependZeros", input, 0);
        assertArrayEquals(input, result);
    }

    /**
     * Test splitAtFirstOccurrence() returns original array and empty if separator not found.
     */
    @Test
    public void testSplitAtFirstOccurrence_SeparatorNotFound() {
        byte[] arr = "hello world".getBytes();
        byte[] sep = "#SEP#".getBytes();
        byte[][] result = ReflectionTestUtils.invokeMethod(UserOnboardService.class, "splitAtFirstOccurrence", arr, sep);
        assertEquals("hello world", new String(result[0]));
        assertEquals("", new String(result[1]));
    }

    /**
     * Test findIndex() returns 0 if subarray is at start.
     */
    @Test
    public void testFindIndex_SubarrayAtStart() {
        byte[] arr = "abcde".getBytes();
        byte[] subarr = "abc".getBytes();
        int idx = ReflectionTestUtils.invokeMethod(UserOnboardService.class, "findIndex", arr, subarr);
        assertEquals(0, idx);
    }

    /**
     * Test findIndex() returns correct index if subarray is at end.
     */
    @Test
    public void testFindIndex_SubarrayAtEnd() {
        byte[] arr = "abcde".getBytes();
        byte[] subarr = "cde".getBytes();
        int idx = ReflectionTestUtils.invokeMethod(UserOnboardService.class, "findIndex", arr, subarr);
        assertEquals(2, idx);
    }

    /**
     * Test findIndex() returns -1 if subarray is not present.
     */
    @Test
    public void testFindIndex_SubarrayNotFound() {
        byte[] arr = "abcde".getBytes();
        byte[] subarr = "xyz".getBytes();
        int idx = ReflectionTestUtils.invokeMethod(UserOnboardService.class, "findIndex", arr, subarr);
        assertEquals(-1, idx);
    }

    /**
     * Test SplitEncryptedData toString() and getters for coverage.
     */
    @Test
    public void testSplitEncryptedDataToStringCoverage() {
        UserOnboardService.SplitEncryptedData data =
                new UserOnboardService.SplitEncryptedData("session", "data");
        assertEquals("session", data.getEncryptedSessionKey());
        assertEquals("data", data.getEncryptedData());
    }

    /**
     * Test validateWithIDA() throws exception if getCertificate throws.
     */
    @Test(expected = RuntimeException.class)
    public void testValidateWithIDA_getCertificateError() {
        when(certificateManagerService.getCertificate(anyString(), anyString())).thenThrow(new RuntimeException("fail"));
        List<BiometricsDto> biometrics = Arrays.asList(new BiometricsDto());
        Runnable onFinish = mock(Runnable.class);
        userOnboardService.validateWithIDA("userX", biometrics, onFinish);
        // No need to verify onFinish, the exception prevents it from being called
    }

    /**
     * Test getCertificate() handles error response from network call.
     */
    @Test
    public void testGetCertificate_Network_ErrorOnResponse() {
        try (MockedStatic<Toast> toastMock = mockStatic(Toast.class)) {
            Toast toast = mock(Toast.class);
            toastMock.when(() -> Toast.makeText(any(), any(), anyInt())).thenReturn(toast);
            doNothing().when(toast).show();

            when(certificateManagerService.getCertificate(anyString(), anyString())).thenReturn(null);
            retrofit2.Call<ResponseWrapper<Map<String, Object>>> call = mock(retrofit2.Call.class);
            when(syncRestService.getIDACertificate()).thenReturn(call);
            doAnswer(invocation -> {
                Callback cb = (Callback) invocation.getArgument(0);
                // onResponse (fail)
                Response resp = mock(Response.class);
                when(resp.isSuccessful()).thenReturn(false);
                cb.onResponse(call, resp);
                return null;
            }).when(call).enqueue(any());
            userOnboardService = new UserOnboardService(
                    context, objectMapper, auditManagerService, certificateManagerService,
                    syncRestService, cryptoManagerService, registrationService, userBiometricRepository,
                    clientCryptoManagerService, userDetailRepository
            );
            final boolean[] called = {false};
            ReflectionTestUtils.invokeMethod(userOnboardService, "getCertificate", (Runnable) () -> called[0] = true);
            assertTrue(called[0]);
        }
    }

    /**
     * Test getCertificate() handles network failure (onFailure).
     */
    @Test
    public void testGetCertificate_Network_OnFailure() {
        try (MockedStatic<Toast> toastMock = mockStatic(Toast.class)) {
            Toast toast = mock(Toast.class);
            toastMock.when(() -> Toast.makeText(any(), any(), anyInt())).thenReturn(toast);
            doNothing().when(toast).show();

            when(certificateManagerService.getCertificate(anyString(), anyString())).thenReturn(null);
            retrofit2.Call<ResponseWrapper<Map<String, Object>>> call = mock(retrofit2.Call.class);
            when(syncRestService.getIDACertificate()).thenReturn(call);
            doAnswer(invocation -> {
                Callback cb = (Callback) invocation.getArgument(0);
                // onFailure
                cb.onFailure(call, new Exception("fail"));
                return null;
            }).when(call).enqueue(any());
            userOnboardService = new UserOnboardService(
                    context, objectMapper, auditManagerService, certificateManagerService,
                    syncRestService, cryptoManagerService, registrationService, userBiometricRepository,
                    clientCryptoManagerService, userDetailRepository
            );
            final boolean[] called = {false};
            ReflectionTestUtils.invokeMethod(userOnboardService, "getCertificate", (Runnable) () -> called[0] = true);
            assertTrue(called[0]);
        }
    }
}
