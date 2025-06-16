package io.mosip.registration.keymanager.service;

import android.content.Context;
import io.mosip.registration.keymanager.dto.CryptoManagerRequestDto;
import io.mosip.registration.keymanager.dto.CryptoManagerResponseDto;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.util.ConfigService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.lang.reflect.Method;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Base64;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CryptoManagerServiceImplTest {

    @Mock
    Context mockContext;
    @Mock
    CertificateManagerService mockCertManagerService;

    AutoCloseable closeable;
    MockedStatic<ConfigService> configServiceMockedStatic;

    CryptoManagerServiceImpl cryptoManagerService;

    static final String SYM_ALGO = "AES";
    static final String ASYM_ALGO = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    static final String ASYM_ALGO_MD = "SHA-256";
    static final String ASYM_ALGO_MGF = "MGF1";
    static final String KEY_SPLITTER = "::";
    static final int SYM_KEY_LENGTH = 128;
    static final int GCM_TAG_LENGTH = 128;

    @BeforeClass
    public static void setUpCryptoUtil() {
        // Ensure CryptoUtil base64decoder/encoder are set.
        try {
            CryptoUtil.base64encoder = Base64.getEncoder();
            CryptoUtil.base64decoder = Base64.getDecoder();
        } catch (Exception e) {}
    }

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        configServiceMockedStatic = mockStatic(ConfigService.class);
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("mosip.kernel.keygenerator.symmetric-algorithm-name"), any()))
                .thenReturn(SYM_ALGO);
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("mosip.kernel.keygenerator.symmetric-key-length"), any()))
                .thenReturn(String.valueOf(SYM_KEY_LENGTH));
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.symmetric-algorithm-name"), any()))
                .thenReturn(SYM_ALGO + "/GCM/NoPadding");
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.gcm-tag-length"), any()))
                .thenReturn(String.valueOf(GCM_TAG_LENGTH));
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.asymmetric-algorithm-name"), any()))
                .thenReturn(ASYM_ALGO);
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.asymmetric-algorithm-message-digest-function"), any()))
                .thenReturn(ASYM_ALGO_MD);
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.asymmetric-algorithm-mask-generation-function"), any()))
                .thenReturn(ASYM_ALGO_MGF);
        configServiceMockedStatic.when(() -> ConfigService.getProperty(eq("mosip.kernel.data-key-splitter"), any()))
                .thenReturn(KEY_SPLITTER);

        cryptoManagerService = new CryptoManagerServiceImpl(mockContext, mockCertManagerService);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
        if (configServiceMockedStatic != null) {
            configServiceMockedStatic.close();
        }
    }

    /**
     * Tests that generateAESKey() generates a valid AES key with the specified length and algorithm.
     */
    @Test
    public void testGenerateAESKey() throws Exception {
        KeyGenerator keyGen = cryptoManagerService.generateAESKey(SYM_KEY_LENGTH);
        assertNotNull(keyGen);
        assertEquals(SYM_ALGO, keyGen.getAlgorithm());
    }

    /**
     * Tests that isDataValid() correctly validates input data, returning true for valid data and false for null, empty, or whitespace strings.
     */
    @Test
    public void testIsDataValid() {

        try {
            Method m = CryptoManagerServiceImpl.class.getDeclaredMethod("isDataValid", String.class);
            m.setAccessible(true);
            assertTrue((Boolean) m.invoke(cryptoManagerService, "data"));
            assertFalse((Boolean) m.invoke(cryptoManagerService, ""));
            assertFalse((Boolean) m.invoke(cryptoManagerService, "   "));
            assertFalse((Boolean) m.invoke(cryptoManagerService, (Object) null));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests that nullOrTrim() correctly trims valid strings and returns null for null input.
     */
    @Test
    public void testNullOrTrim() {
        try {
            Method m = CryptoManagerServiceImpl.class.getDeclaredMethod("nullOrTrim", String.class);
            m.setAccessible(true);
            assertEquals("data", m.invoke(cryptoManagerService, "  data  "));
            assertNull(m.invoke(cryptoManagerService, (Object) null));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests that nullOrTrim() returns an empty string when input is whitespace.
     */
    @Test
    public void testNullOrTrimWhitespace() {
        try {
            Method m = CryptoManagerServiceImpl.class.getDeclaredMethod("nullOrTrim", String.class);
            m.setAccessible(true);
            assertEquals("", m.invoke(cryptoManagerService, "   "));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests that concatByteArrays() correctly concatenates byte arrays, including cases with empty arrays.
     */
    @Test
    public void testConcatByteArraysWithEmpty() {
        byte[] arr1 = {};
        byte[] arr2 = {1,2,3};
        byte[] result = cryptoManagerService.concatByteArrays(arr1, arr2);
        assertArrayEquals(arr2, result);

        arr1 = new byte[]{4,5};
        arr2 = new byte[]{};
        result = cryptoManagerService.concatByteArrays(arr1, arr2);
        assertArrayEquals(new byte[]{4,5}, result);

        arr1 = new byte[]{};
        arr2 = new byte[]{};
        result = cryptoManagerService.concatByteArrays(arr1, arr2);
        assertArrayEquals(new byte[]{}, result);
    }

    /**
     * Tests that convertToCertificate() throws an exception when provided with invalid PEM certificate data.
     */
    @Test
    public void testConvertToCertificateFailure() {
        String invalidPem = "-----BEGIN CERTIFICATE-----\nINVALID\n-----END CERTIFICATE-----\n";
        Exception ex = assertThrows(Exception.class, () -> cryptoManagerService.convertToCertificate(invalidPem));
        assertEquals("CERTIFICATE_PARSING_ERROR", ex.getMessage());
    }

    /**
     * Tests that getCertificateThumbprint() throws an exception when certificate encoding fails.
     */
    @Test
    public void testGetCertificateThumbprintFailure() throws Exception {
        Certificate mockCert = mock(Certificate.class);
        when(mockCert.getEncoded()).thenThrow(new java.security.cert.CertificateEncodingException());
        Exception ex = assertThrows(Exception.class, () -> cryptoManagerService.getCertificateThumbprint(mockCert));
        assertEquals("CERTIFICATE_THUMBPRINT_ERROR", ex.getMessage());
    }

    /**
     * Tests that symmetricDecrypt() correctly decrypts data encrypted with symmetricEncryptWithRandomIV() using the same key and AAD.
     */
    @Test
    public void testSymmetricDecrypt() throws Exception {
        SecretKey key = KeyGenerator.getInstance(SYM_ALGO).generateKey();
        byte[] data = "test-data".getBytes();
        byte[] aad = "aad-value-1234567890123456".getBytes();
        byte[] encrypted = cryptoManagerService.symmetricEncryptWithRandomIV(key, data, aad);
        byte[] decrypted = cryptoManagerService.symmetricDecrypt(key, encrypted, aad);
        assertArrayEquals(data, decrypted);
    }

    /**
     * Tests that asymmetricEncrypt() throws an exception when an invalid key type (DSA) is used.
     */
    @Test
    public void testAsymmetricEncryptWithInvalidKey() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.generateKeyPair();
        Exception ex = assertThrows(Exception.class, () -> cryptoManagerService.asymmetricEncrypt(kp.getPublic(), "data".getBytes()));
        assertTrue(ex.getMessage().contains("No installed provider supports this key")
                || ex.getMessage().contains("IllegalBlockSizeException")
                || ex.getMessage().contains("javax.crypto.BadPaddingException"));
    }

    /**
     * Tests that generateRandomBytes() returns an empty array for zero length and throws NegativeArraySizeException for negative length.
     */
    @Test
    public void testGenerateRandomBytesZeroAndNegative() {
        byte[] zero = cryptoManagerService.generateRandomBytes(0);
        assertEquals(0, zero.length);
        try {
            cryptoManagerService.generateRandomBytes(-1);
            fail("Should throw NegativeArraySizeException");
        } catch (NegativeArraySizeException ignored) {}
    }

    /**
     * Tests that combineByteArray() correctly combines empty data and key arrays with a splitter.
     */
    @Test
    public void testCombineByteArrayWithEmpty() {
        byte[] data = {};
        byte[] key = {};
        String splitter = "--";
        byte[] expected = {'-','-'};
        assertArrayEquals(expected, CryptoManagerServiceImpl.combineByteArray(data, key, splitter));
    }

    /**
     * Tests that isDataValid() private method correctly validates input data, returning true for valid data and false for null, empty, or whitespace strings.
     */
    @Test
    public void testPrivateIsDataValid() throws Exception {
        Method m = CryptoManagerServiceImpl.class.getDeclaredMethod("isDataValid", String.class);
        m.setAccessible(true);
        assertFalse((Boolean) m.invoke(cryptoManagerService, (Object) null));
        assertFalse((Boolean) m.invoke(cryptoManagerService, ""));
        assertFalse((Boolean) m.invoke(cryptoManagerService, "   "));
        assertTrue((Boolean) m.invoke(cryptoManagerService, "abc"));
    }

    /**
     * Tests that symmetricEncrypt() private method successfully encrypts data with a null AAD.
     */
    @Test
    public void testPrivateSymmetricEncryptWithNullAAD() throws Exception {
        SecretKey key = KeyGenerator.getInstance(SYM_ALGO).generateKey();
        byte[] data = "test".getBytes();
        Method m = CryptoManagerServiceImpl.class.getDeclaredMethod("symmetricEncrypt", SecretKey.class, byte[].class, byte[].class, byte[].class);
        m.setAccessible(true);
        byte[] iv = cryptoManagerService.generateRandomBytes(12);
        byte[] result = (byte[]) m.invoke(cryptoManagerService, key, data, iv, null);
        assertNotNull(result);
    }

    /**
     * Tests that encrypt() handles all branches, including successful encryption, invalid application ID, empty reference ID, and valid salt.
     */
    @Test
    public void testEncryptAndAllBranches() throws Exception {
        CryptoManagerRequestDto reqDto = new CryptoManagerRequestDto();
        reqDto.setApplicationId("REG");
        reqDto.setReferenceId("ref-id");
        reqDto.setData(new String(CryptoUtil.base64encoder.encode("encryptdata".getBytes())));
        reqDto.setAad(new String(CryptoUtil.base64encoder.encode(new byte[32])));
        reqDto.setSalt(null);

        // Use the provided valid RSA certificate PEM for encryption
        String certPem = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDOTCCAiGgAwIBAgIUQFYJFLdNomEJe4+29bABk7/FEvIwDQYJKoZIhvcNAQEL\n" +
                "BQAwVjELMAkGA1UEBhMCSU4xCzAJBgNVBAgMAktBMQ0wCwYDVQQHDAR0ZXN0MQ0w\n" +
                "CwYDVQQKDAR0ZXN0MQ0wCwYDVQQLDAR0ZXN0MQ0wCwYDVQQDDARyb290MB4XDTI0\n" +
                "MTIxMzExMjYyM1oXDTI2MTIxMzExMjYyM1owVzELMAkGA1UEBhMCSU4xCzAJBgNV\n" +
                "BAgMAktBMQ0wCwYDVQQHDAR0ZXN0MQ0wCwYDVQQKDAR0ZXN0MQ0wCwYDVQQLDAR0\n" +
                "ZXN0MQ4wDAYDVQQDDAVpbnRlcjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n" +
                "ggEBAJoWZ5B+sQLXo/ra5S4lgnG7vRjFvN+2jscdb0ZIcfmfIKVM0rQWlESVTCJW\n" +
                "NQp6g6orPrKebiUFmqvhyqYZpn5AdAor50QHQHLk2tcm7RvglW5mpw2/kHDW6HIE\n" +
                "gbKD6vqKFAX9w1PZ64wkJMPvREggsCmHUYt3PU4hTFfA+wJsFGwNXVTNZyTGPOZz\n" +
                "7C+ywqOjDIBp1Fq7VeT6XmlLTu3yJBVXLStW3BRg3zxpDZdZ2uxCln+OU5vvGZjp\n" +
                "66xft5qPHN138j5ATutF9DfOXl3Zk05ttdfphOTjlYiwrqUAZSq40ROkVB7dhW91\n" +
                "KlnwkH+Ma4noUClOd14/pCJi4H0CAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAT0qo\n" +
                "g6jBLVX7EvE6bpykiwMWeqtb3eUo/Lt3dmJqgItpXA0cZWc28B0OfplPf+PdpbuT\n" +
                "gh9fh4xrCfYm4uZjw/KHfDLoLyMu3g36P/dovWtHJWFFNceGtwSvSnD+Shu2NsdS\n" +
                "MNEQLD2tTBLCfx3smjEk5JjMJQmHxgRsLEEFRITgekfRL+kU8iXk0a4gkKVW2gL+\n" +
                "6pAsRsNVuM2ABLoxudE2WBfiZ22Tyq+dbjhf0UFlZpI+wip/vV+5lom6YmRlOsBE\n" +
                "2SOBZcHGC9NwZq3Bv+VALne87jdSvRQRNFSE8LHAI2TNFZwU4kLfcTGlvay7uojl\n" +
                "qDCoPAErFbLnXZG+HQ==\n" +
                "-----END CERTIFICATE-----\n";
        when(mockCertManagerService.getCertificate(anyString(), anyString())).thenReturn(certPem);
        CryptoManagerResponseDto resp = cryptoManagerService.encrypt(reqDto);
        assertNotNull(resp);
        assertNotNull(resp.getData());

        reqDto.setApplicationId("KERNEL");
        reqDto.setReferenceId("SIGN");
        Exception ex = assertThrows(Exception.class, () -> cryptoManagerService.encrypt(reqDto));
        assertTrue(ex.getMessage().contains("ENCRYPT_NOT_ALLOWED_ERROR"));

        reqDto.setApplicationId("REG");
        reqDto.setReferenceId("");
        ex = assertThrows(Exception.class, () -> cryptoManagerService.encrypt(reqDto));
        assertTrue(ex.getMessage().contains("ENCRYPT_NOT_ALLOWED_ERROR"));

        reqDto.setReferenceId("ref-id");
        reqDto.setSalt(new String(CryptoUtil.base64encoder.encode("salt-12345678".getBytes())));
        resp = cryptoManagerService.encrypt(reqDto);
        assertNotNull(resp.getData());
    }

    /**
     * Tests that combineByteArray() correctly combines non-empty data and key arrays with a splitter.
     */
    @Test
    public void testCombineByteArrayWithNonEmpty() {
        byte[] data = {1, 2};
        byte[] key = {3, 4};
        String splitter = "::";
        byte[] expected = {3, 4, ':', ':', 1, 2};
        assertArrayEquals(expected, CryptoManagerServiceImpl.combineByteArray(data, key, splitter));
    }

    /**
     * Tests that concatCertThumbprint() correctly concatenates a short thumbprint with a key, padding to the expected length.
     */
    @Test
    public void testConcatCertThumbprintShortThumbprint() {
        byte[] thumb = {1, 2, 3};
        byte[] key = {4, 5, 6};
        byte[] result = cryptoManagerService.concatCertThumbprint(thumb, key);
        assertEquals(32 + 3, result.length); // THUMBPRINT_LENGTH + key.length
    }

    /**
     * Tests that symmetricEncrypt() successfully encrypts data with a null AAD.
     */
    @Test
    public void testSymmetricEncryptNullAAD() throws Exception {
        SecretKey key = KeyGenerator.getInstance(SYM_ALGO).generateKey();
        byte[] data = "abc".getBytes();
        byte[] enc = cryptoManagerService.symmetricEncrypt(key, data, null);
        assertNotNull(enc);
    }

    /**
     * Tests that symmetricEncrypt() successfully encrypts data with an empty AAD.
     */
    @Test
    public void testSymmetricEncryptEmptyAAD() throws Exception {
        SecretKey key = KeyGenerator.getInstance(SYM_ALGO).generateKey();
        byte[] data = "abc".getBytes();
        byte[] enc = cryptoManagerService.symmetricEncrypt(key, data, new byte[0]);
        assertNotNull(enc);
    }

    /**
     * Tests that symmetricEncryptWithRandomIV() successfully encrypts data with a null AAD.
     */
    @Test
    public void testSymmetricEncryptWithRandomIVNullAAD() throws Exception {
        SecretKey key = KeyGenerator.getInstance(SYM_ALGO).generateKey();
        byte[] data = "abc".getBytes();
        byte[] enc = cryptoManagerService.symmetricEncryptWithRandomIV(key, data, null);
        assertNotNull(enc);
    }

    /**
     * Tests that symmetricEncryptWithRandomIV() successfully encrypts data with an empty AAD.
     */
    @Test
    public void testSymmetricEncryptWithRandomIVEmptyAAD() throws Exception {
        SecretKey key = KeyGenerator.getInstance(SYM_ALGO).generateKey();
        byte[] data = "abc".getBytes();
        byte[] enc = cryptoManagerService.symmetricEncryptWithRandomIV(key, data, new byte[0]);
        assertNotNull(enc);
    }

    /**
     * Tests that generateAadAndEncryptData() successfully generates AAD and encrypts data with a given key.
     */
    @Test
    public void testGenerateAadAndEncryptData() throws Exception {
        SecretKey key = KeyGenerator.getInstance(SYM_ALGO).generateKey();
        String data = CryptoUtil.base64encoder.encodeToString("abc".getBytes());
        byte[] enc = cryptoManagerService.generateAadAndEncryptData(key, data);
        assertNotNull(enc);
    }

    /**
     * Tests that symmetricDecrypt() correctly decrypts data encrypted with symmetricEncryptWithRandomIV() using an empty AAD.
     */
    @Test
    public void testSymmetricDecryptEmptyAAD() throws Exception {
        SecretKey key = KeyGenerator.getInstance(SYM_ALGO).generateKey();
        byte[] data = "test-data".getBytes();
        byte[] aad = new byte[0];
        byte[] encrypted = cryptoManagerService.symmetricEncryptWithRandomIV(key, data, aad);
        byte[] decrypted = cryptoManagerService.symmetricDecrypt(key, encrypted, aad);
        assertArrayEquals(data, decrypted);
    }

    /**
     * Tests that symmetricDecrypt() correctly decrypts data encrypted with symmetricEncryptWithRandomIV() using a null AAD.
     */
    @Test
    public void testSymmetricDecryptNullAAD() throws Exception {
        SecretKey key = KeyGenerator.getInstance(SYM_ALGO).generateKey();
        byte[] data = "test-data".getBytes();
        byte[] encrypted = cryptoManagerService.symmetricEncryptWithRandomIV(key, data, null);
        byte[] decrypted = cryptoManagerService.symmetricDecrypt(key, encrypted, null);
        assertArrayEquals(data, decrypted);
    }

    /**
     * Tests that generateRandomBytes() generates a byte array of the specified positive length.
     */
    @Test
    public void testGenerateRandomBytesPositive() {
        byte[] random = cryptoManagerService.generateRandomBytes(16);
        assertEquals(16, random.length);
    }
}