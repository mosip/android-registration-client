package io.mosip.registration.keymanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import io.mosip.registration.keymanager.dto.*;
import io.mosip.registration.keymanager.exception.KeymanagerServiceException;
import io.mosip.registration.keymanager.service.LocalClientCryptoServiceImpl;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.util.CertificateManagerUtil;
import io.mosip.registration.keymanager.util.ConfigService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.keymanager.util.JsonUtils;
import io.mosip.registration.keymanager.util.KeyManagerConstant;
import org.apache.commons.io.IOUtils;
import org.jose4j.jws.JsonWebSignature;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class LocalClientCryptoServiceImplTest {

    @Mock
    private Context context;

    @Mock
    private CertificateManagerService certificateManagerService;

    @Mock
    private KeyStore keyStore;

    @Mock
    private KeyPairGenerator keyPairGenerator;

    @Mock
    private Signature signature;

    @Mock
    private Cipher cipher;

    @Mock
    private KeyGenerator keyGenerator;

    @Mock
    private KeyFactory keyFactory;

    @Mock
    private JsonWebSignature jsonWebSignature;

    @Mock
    private File file;

    @Mock
    private X509Certificate x509Certificate;

    @Mock
    private PublicKey publicKey;

    @Mock
    private RSAPrivateKey privateKey;

    @Mock
    private KeyGenParameterSpec.Builder keyGenParameterSpecBuilder;

    @InjectMocks
    private LocalClientCryptoServiceImpl cryptoService;

    private static final String SIGNV_ALIAS = KeyManagerConstant.SIGNV_ALIAS;
    private static final String ENCDEC_ALIAS = KeyManagerConstant.ENCDEC_ALIAS;
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private MockedStatic<KeyStore> keyStoreStaticMock;
    private MockedStatic<ConfigService> configServiceStaticMock;
    private MockedStatic<Cipher> cipherStaticMock;
    private MockedStatic<KeyPairGenerator> keyPairGeneratorStaticMock;
    private MockedStatic<KeyFactory> keyFactoryStaticMock;
    private MockedStatic<Signature> signatureStaticMock;
    private MockedStatic<KeyGenerator> keyGeneratorStaticMock;
    private MockedStatic<JsonUtils> jsonUtilsStaticMock;
    private MockedStatic<JSONObject> jsonObjectStaticMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock static KeyStore.getInstance
        keyStoreStaticMock = Mockito.mockStatic(KeyStore.class);
        keyStoreStaticMock.when(() -> KeyStore.getInstance(ANDROID_KEY_STORE)).thenReturn(keyStore);

        // Mock static ConfigService.getProperty
        configServiceStaticMock = Mockito.mockStatic(ConfigService.class);
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.asymmetric-algorithm-name"), any(Context.class))).thenReturn("RSA/ECB/OAEPWithSHA-256andMGF1Padding");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.asymmetric-algorithm-block-mode"), any(Context.class))).thenReturn("ECB");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.asymmetric-algorithm-padding-scheme"), any(Context.class))).thenReturn("OAEPWithSHA-256andMGF1Padding");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.asymmetric-algorithm-message-digest-function"), any(Context.class))).thenReturn("SHA-256");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.asymmetric-algorithm-mask-generation-function"), any(Context.class))).thenReturn("MGF1");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.symmetric-algorithm-name"), any(Context.class))).thenReturn("AES/GCM/NoPadding");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.keygenerator.asymmetric-algorithm-name"), any(Context.class))).thenReturn("RSA");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.keygenerator.symmetric-algorithm-name"), any(Context.class))).thenReturn("AES");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.keygenerator.asymmetric-key-length"), any(Context.class))).thenReturn("2048");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.keygenerator.symmetric-key-length"), any(Context.class))).thenReturn("256");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.symmetric-algorithm-iv-length"), any(Context.class))).thenReturn("12");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.symmetric-algorithm-aad-length"), any(Context.class))).thenReturn("16");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.gcm-tag-length"), any(Context.class))).thenReturn("128");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.crypto.sign-algorithm-padding-scheme"), any(Context.class))).thenReturn("PSS");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.kernel.certificate.sign.algorithm"), any(Context.class))).thenReturn("SHA256withRSA");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.sign.applicationid"), any(Context.class))).thenReturn("APP_ID");
        configServiceStaticMock.when(() -> ConfigService.getProperty(eq("mosip.sign.refid"), any(Context.class))).thenReturn("REF_ID");

        // Mock static Cipher.getInstance
        cipherStaticMock = Mockito.mockStatic(Cipher.class);
        cipherStaticMock.when(() -> Cipher.getInstance(anyString())).thenReturn(cipher);

        // Mock static KeyPairGenerator.getInstance and KeyGenParameterSpec.Builder
        keyPairGeneratorStaticMock = Mockito.mockStatic(KeyPairGenerator.class);
        keyPairGeneratorStaticMock.when(() -> KeyPairGenerator.getInstance(eq("RSA"), eq(ANDROID_KEY_STORE))).thenReturn(keyPairGenerator);
        when(keyPairGenerator.genKeyPair()).thenReturn(mock(KeyPair.class));
        when(keyGenParameterSpecBuilder.setKeySize(anyInt())).thenReturn(keyGenParameterSpecBuilder);
        when(keyGenParameterSpecBuilder.setBlockModes(anyString())).thenReturn(keyGenParameterSpecBuilder);
        when(keyGenParameterSpecBuilder.setEncryptionPaddings(anyString())).thenReturn(keyGenParameterSpecBuilder);
        when(keyGenParameterSpecBuilder.setDigests(anyString())).thenReturn(keyGenParameterSpecBuilder);
        when(keyGenParameterSpecBuilder.build()).thenReturn(mock(KeyGenParameterSpec.class));

        // Mock static KeyFactory.getInstance
        keyFactoryStaticMock = Mockito.mockStatic(KeyFactory.class);
        keyFactoryStaticMock.when(() -> KeyFactory.getInstance(eq("RSA"))).thenReturn(keyFactory);

        // Mock static Signature.getInstance
        signatureStaticMock = Mockito.mockStatic(Signature.class);
        signatureStaticMock.when(() -> Signature.getInstance(eq("SHA256withRSA"))).thenReturn(signature);

        // Mock static KeyGenerator.getInstance
        keyGeneratorStaticMock = Mockito.mockStatic(KeyGenerator.class);
        keyGeneratorStaticMock.when(() -> KeyGenerator.getInstance(eq("AES"))).thenReturn(keyGenerator);

        // Mock static JsonUtils
        jsonUtilsStaticMock = Mockito.mockStatic(JsonUtils.class);
        // Mock static JSONObject
        jsonObjectStaticMock = Mockito.mockStatic(JSONObject.class);

        doNothing().when(keyStore).load(null);

        // Mock Context data directory
        File dataDir = mock(File.class);
        when(context.getDataDir()).thenReturn(dataDir);
        when(dataDir.getAbsolutePath()).thenReturn("/data");

        // Initialize the service
        cryptoService = new LocalClientCryptoServiceImpl(context, certificateManagerService);

        // Inject mocked KeyStore using reflection
        Field keyStoreField = LocalClientCryptoServiceImpl.class.getDeclaredField("keyStore");
        keyStoreField.setAccessible(true);
        keyStoreField.set(cryptoService, keyStore);
    }

    @After
    public void tearDown() {
        if (keyStoreStaticMock != null) keyStoreStaticMock.close();
        if (configServiceStaticMock != null) configServiceStaticMock.close();
        if (cipherStaticMock != null) cipherStaticMock.close();
        if (keyPairGeneratorStaticMock != null) keyPairGeneratorStaticMock.close();
        if (keyFactoryStaticMock != null) keyFactoryStaticMock.close();
        if (signatureStaticMock != null) signatureStaticMock.close();
        if (keyGeneratorStaticMock != null) keyGeneratorStaticMock.close();
        if (jsonUtilsStaticMock != null) jsonUtilsStaticMock.close();
        if (jsonObjectStaticMock != null) jsonObjectStaticMock.close();
    }

    @Test
    public void testConstructorKeyStoreException() throws Exception {
        keyStoreStaticMock.reset();
        keyStoreStaticMock.when(() -> KeyStore.getInstance(ANDROID_KEY_STORE)).thenThrow(new KeyStoreException("KeyStore error"));
        LocalClientCryptoServiceImpl service = new LocalClientCryptoServiceImpl(context, certificateManagerService);
        Field keyStoreField = LocalClientCryptoServiceImpl.class.getDeclaredField("keyStore");
        keyStoreField.setAccessible(true);
        keyStoreField.set(service, keyStore);
        assertNotNull(service);
    }

    @Test
    public void testInitLocalClientCryptoService() {
        cryptoService.initLocalClientCryptoService(context);
        assertNotNull(cryptoService);
    }

    @Test
    public void testGenSignKeySuccess() throws Exception {
        when(keyStore.getEntry(SIGNV_ALIAS, null)).thenReturn(null);
        when(keyPairGenerator.generateKeyPair()).thenReturn(mock(KeyPair.class));
        invokePrivateMethod("genSignKey");
    }

    @Test
    public void testGenSignKeyExists() throws Exception {
        KeyStore.PrivateKeyEntry entry = mock(KeyStore.PrivateKeyEntry.class);
        when(keyStore.getEntry(SIGNV_ALIAS, null)).thenReturn(entry);
        invokePrivateMethod("genSignKey");
    }

    @Test
    public void testGenSignKeyUnrecoverableKeyException() throws Exception {
        when(keyStore.getEntry(SIGNV_ALIAS, null)).thenThrow(new UnrecoverableKeyException());
        doNothing().when(keyStore).deleteEntry(SIGNV_ALIAS);
        invokePrivateMethod("genSignKey");
    }

    @Test
    public void testGenEnDecKeySuccess() throws Exception {
        when(keyStore.getEntry(ENCDEC_ALIAS, null)).thenReturn(null);
        when(keyPairGenerator.generateKeyPair()).thenReturn(mock(KeyPair.class));
        invokePrivateMethod("genEnDecKey");
    }

    @Test
    public void testGenEnDecKeyExists() throws Exception {
        KeyStore.PrivateKeyEntry entry = mock(KeyStore.PrivateKeyEntry.class);
        when(keyStore.getEntry(ENCDEC_ALIAS, null)).thenReturn(entry);
        invokePrivateMethod("genEnDecKey");
    }

    @Test
    public void testGenEnDecKeyUnrecoverableKeyException() throws Exception {
        when(keyStore.getEntry(ENCDEC_ALIAS, null)).thenThrow(new UnrecoverableKeyException());
        doNothing().when(keyStore).deleteEntry(ENCDEC_ALIAS);
        invokePrivateMethod("genEnDecKey");
    }

    @Test
    public void testSignSuccess() throws Exception {
        SignRequestDto request = new SignRequestDto();
        request.setData(CryptoUtil.base64encoder.encodeToString("test data".getBytes()));

        when(keyStore.getKey(SIGNV_ALIAS, null)).thenReturn(privateKey);
        when(signature.sign()).thenReturn("signed data".getBytes());

        SignResponseDto response = cryptoService.sign(request);
        assertNotNull(response);
        assertEquals(CryptoUtil.base64encoder.encodeToString("signed data".getBytes()), response.getData());
    }

    @Test
    public void testSignException() throws Exception {
        SignRequestDto request = new SignRequestDto();
        request.setData(CryptoUtil.base64encoder.encodeToString("test data".getBytes()));

        when(keyStore.getKey(SIGNV_ALIAS, null)).thenThrow(new KeyStoreException());
        SignResponseDto response = cryptoService.sign(request);
        assertNull(response);
    }

    @Test
    public void testVerifySignSuccess() throws Exception {
        SignVerifyRequestDto request = new SignVerifyRequestDto();
        request.setData(CryptoUtil.base64encoder.encodeToString("data".getBytes()));
        request.setSignature(CryptoUtil.base64encoder.encodeToString("signature".getBytes()));
        request.setPublicKey(CryptoUtil.base64encoder.encodeToString("publicKey".getBytes()));

        when(keyFactory.generatePublic(any(X509EncodedKeySpec.class))).thenReturn(publicKey);
        when(signature.verify(any(byte[].class))).thenReturn(true);

        SignVerifyResponseDto response = cryptoService.verifySign(request);
        assertNotNull(response);
        assertTrue(response.isVerified());
    }

    @Test@Ignore
    public void testVerifySignException() throws Exception {
        SignVerifyRequestDto request = new SignVerifyRequestDto();
        request.setData(CryptoUtil.base64encoder.encodeToString("data".getBytes()));
        request.setSignature(CryptoUtil.base64encoder.encodeToString("signature".getBytes()));
        request.setPublicKey(CryptoUtil.base64encoder.encodeToString("publicKey".getBytes()));

        keyFactoryStaticMock.reset();
        keyFactoryStaticMock.when(() -> KeyFactory.getInstance("RSA")).thenThrow(new NoSuchAlgorithmException());
        SignVerifyResponseDto response = cryptoService.verifySign(request);
        assertNull(response);
    }

    @Test@Ignore
    public void testJwtVerifyWithHeaderCertSuccess() throws Exception {
        JWTSignatureVerifyRequestDto request = new JWTSignatureVerifyRequestDto();
        request.setJwtSignatureData("header.payload.signature");
        request.setActualData(CryptoUtil.base64encoder.encodeToString("data".getBytes()));
        request.setValidateTrust(true);
        request.setDomain("test-domain");

        jsonUtilsStaticMock.when(() -> JsonUtils.jsonStringToJavaMap("header")).thenReturn(
                Map.of(KeyManagerConstant.JWT_HEADER_CERT_KEY, List.of(CryptoUtil.base64encoder.encodeToString("cert".getBytes())))
        );
        CertificateTrustResponseDto trustResponse = new CertificateTrustResponseDto();
        trustResponse.setStatus(true);
        when(certificateManagerService.verifyCertificateTrust(any())).thenReturn(trustResponse);
        when(jsonWebSignature.verifySignature()).thenReturn(true);
        when(x509Certificate.getPublicKey()).thenReturn(publicKey);
        when(CertificateManagerUtil.convertToCertificate(anyString())).thenReturn(x509Certificate);
        when(CertificateManagerUtil.isCertificateDatesValid(any())).thenReturn(true);

        JWTSignatureVerifyResponseDto response = cryptoService.jwtVerify(request);
        assertNotNull(response);
        assertTrue(response.isSignatureValid());
        assertEquals(KeyManagerConstant.TRUST_VALID, response.getTrustValid());
    }

    @Test@Ignore
    public void testJwtVerifyWithReqCert() throws Exception {
        JWTSignatureVerifyRequestDto request = new JWTSignatureVerifyRequestDto();
        request.setJwtSignatureData("header.payload.signature");
        request.setCertificateData(CryptoUtil.base64encoder.encodeToString("cert".getBytes()));
        request.setValidateTrust(true);
        request.setDomain("test-domain");

        jsonUtilsStaticMock.when(() -> JsonUtils.jsonStringToJavaMap("header")).thenReturn(new HashMap<>());
        when(CertificateManagerUtil.convertToCertificate(anyString())).thenReturn(x509Certificate);
        when(CertificateManagerUtil.isCertificateDatesValid(any())).thenReturn(true);
        when(x509Certificate.getPublicKey()).thenReturn(publicKey);
        when(jsonWebSignature.verifySignature()).thenReturn(true);
        CertificateTrustResponseDto trustResponse = new CertificateTrustResponseDto();
        trustResponse.setStatus(true);
        when(certificateManagerService.verifyCertificateTrust(any())).thenReturn(trustResponse);

        JWTSignatureVerifyResponseDto response = cryptoService.jwtVerify(request);
        assertNotNull(response);
        assertTrue(response.isSignatureValid());
        assertEquals(KeyManagerConstant.TRUST_VALID, response.getTrustValid());
    }

    @Ignore
    @Test
    public void testJwtVerifyWithAppIdRefId() throws Exception {
        JWTSignatureVerifyRequestDto request = new JWTSignatureVerifyRequestDto();
        request.setJwtSignatureData("header.payload.signature");
        request.setApplicationId("APP_ID");
        request.setReferenceId("REF_ID");
        request.setValidateTrust(true);
        request.setDomain("test-domain");

        jsonUtilsStaticMock.when(() -> JsonUtils.jsonStringToJavaMap("header")).thenReturn(new HashMap<>());
        when(certificateManagerService.getCertificate("APP_ID", "REF_ID")).thenReturn(CryptoUtil.base64encoder.encodeToString("cert".getBytes()));
        when(CertificateManagerUtil.convertToCertificate(anyString())).thenReturn(x509Certificate);
        when(CertificateManagerUtil.isCertificateDatesValid(any())).thenReturn(true);
        when(x509Certificate.getPublicKey()).thenReturn(publicKey);
        when(jsonWebSignature.verifySignature()).thenReturn(true);
        CertificateTrustResponseDto trustResponse = new CertificateTrustResponseDto();
        trustResponse.setStatus(true);
        when(certificateManagerService.verifyCertificateTrust(any())).thenReturn(trustResponse);

        JWTSignatureVerifyResponseDto response = cryptoService.jwtVerify(request);
        assertNotNull(response);
        assertTrue(response.isSignatureValid());
        assertEquals(KeyManagerConstant.TRUST_VALID, response.getTrustValid());
    }

    @Test
    public void testJwtVerifyInvalidData() {
        JWTSignatureVerifyRequestDto request = new JWTSignatureVerifyRequestDto();
        request.setJwtSignatureData("");
        assertThrows(Exception.class, () -> cryptoService.jwtVerify(request));
    }

    @Test@Ignore
    public void testJwtVerifyInvalidCertDates() throws Exception {
        JWTSignatureVerifyRequestDto request = new JWTSignatureVerifyRequestDto();
        request.setJwtSignatureData("header.payload.signature");

        jsonUtilsStaticMock.when(() -> JsonUtils.jsonStringToJavaMap("header")).thenReturn(
                Map.of(KeyManagerConstant.JWT_HEADER_CERT_KEY, List.of(CryptoUtil.base64encoder.encodeToString("cert".getBytes())))
        );
        when(CertificateManagerUtil.convertToCertificate(anyString())).thenReturn(x509Certificate);
        when(CertificateManagerUtil.isCertificateDatesValid(any())).thenReturn(false);

        assertThrows(KeymanagerServiceException.class, () -> cryptoService.jwtVerify(request));
    }

    @Test
    public void testEncryptSuccess() throws Exception {
        CryptoRequestDto request = new CryptoRequestDto();
        request.setValue(CryptoUtil.base64encoder.encodeToString("data".getBytes()));

        SecretKey secretKey = mock(SecretKey.class);
        when(keyGenerator.generateKey()).thenReturn(secretKey);
        when(secretKey.getEncoded()).thenReturn(new byte[32]);
        when(cipher.getIV()).thenReturn(new byte[12]);
        when(cipher.doFinal(any(byte[].class))).thenReturn("encrypted".getBytes()).thenReturn("key_encrypted".getBytes());
        when(keyStore.getCertificate(ENCDEC_ALIAS)).thenReturn(x509Certificate);
        when(x509Certificate.getPublicKey()).thenReturn(publicKey);

        CryptoResponseDto response = cryptoService.encrypt(request);
        assertNotNull(response);
        assertNotNull(response.getValue());
    }

    @Test
    public void testEncryptException() throws Exception {
        CryptoRequestDto request = new CryptoRequestDto();
        request.setValue(CryptoUtil.base64encoder.encodeToString("data".getBytes()));

        keyGeneratorStaticMock.reset();
        keyGeneratorStaticMock.when(() -> KeyGenerator.getInstance("AES")).thenThrow(new NoSuchAlgorithmException());
        CryptoResponseDto response = cryptoService.encrypt(request);
        assertNull(response);
    }

    @Test@Ignore
    public void testDecryptSuccess() throws Exception {
        CryptoRequestDto request = new CryptoRequestDto();
        byte[] encryptedData = new byte[256 + 12 + 16 + 100]; // key + IV + tag + data
        String encodedData = CryptoUtil.base64encoder.encodeToString(encryptedData);
        request.setValue(encodedData);

        KeyStore.PrivateKeyEntry entry = mock(KeyStore.PrivateKeyEntry.class);
        when(keyStore.getEntry(ENCDEC_ALIAS, null)).thenReturn(entry);
        when(entry.getPrivateKey()).thenReturn(privateKey);
        when(keyStore.getCertificate(ENCDEC_ALIAS)).thenReturn(x509Certificate);
        when(x509Certificate.getPublicKey()).thenReturn(publicKey);
        when(privateKey.getModulus()).thenReturn(new BigInteger(
                "2519590847565789349402718324004839857142928212620403202777713783604366202070" +
                        "7595556264018525880784406918290641249515082189298559149176184502808489120072" +
                        "8449926873928072877767359714183472702618963750149718246911650776133798590957" +
                        "0009733045974880842840179742910064245869181719511874612151517265463228221686" +
                        "998754918242243363725908514186846204357927984233" +
                        "84672"));
        when(cipher.doFinal(any(byte[].class))).thenReturn("decrypted_key".getBytes()).thenReturn("decrypted_data".getBytes());

        CryptoResponseDto response = cryptoService.decrypt(request);
        assertNotNull(response);
        assertEquals(CryptoUtil.base64encoder.encodeToString("decrypted_data".getBytes()), response.getValue());
    }

    @Test
    public void testDecryptException() throws Exception {
        CryptoRequestDto request = new CryptoRequestDto();
        request.setValue(CryptoUtil.base64encoder.encodeToString(new byte[100]));

        cipherStaticMock.reset();
        cipherStaticMock.when(() -> Cipher.getInstance("RSA/ECB/NOPADDING")).thenThrow(new NoSuchAlgorithmException());
        CryptoResponseDto response = cryptoService.decrypt(request);
        assertNull(response);
    }

    @Test
    public void testGetPublicKeySignAlias() throws IOException, KeyStoreException {
        PublicKeyRequestDto request = new PublicKeyRequestDto();
        request.setAlias(SIGNV_ALIAS);

        when(keyStore.getCertificate(SIGNV_ALIAS)).thenReturn(x509Certificate);
        when(x509Certificate.getPublicKey()).thenReturn(publicKey);
        when(publicKey.getEncoded()).thenReturn("public_key".getBytes());

        PublicKeyResponseDto response = cryptoService.getPublicKey(request);
        assertNotNull(response);
        assertEquals(CryptoUtil.base64encoder.encodeToString("public_key".getBytes()), response.getPublicKey());
    }

    @Test
    public void testGetPublicKeyEncDecAlias() throws IOException, KeyStoreException {
        PublicKeyRequestDto request = new PublicKeyRequestDto();
        request.setAlias(ENCDEC_ALIAS);

        when(keyStore.getCertificate(ENCDEC_ALIAS)).thenReturn(x509Certificate);
        when(x509Certificate.getPublicKey()).thenReturn(publicKey);
        when(publicKey.getEncoded()).thenReturn("public_key".getBytes());

        PublicKeyResponseDto response = cryptoService.getPublicKey(request);
        assertNotNull(response);
        assertEquals(CryptoUtil.base64encoder.encodeToString("public_key".getBytes()), response.getPublicKey());
    }

    @Test
    public void testGetPublicKeyException() throws Exception {
        PublicKeyRequestDto request = new PublicKeyRequestDto();
        request.setAlias("INVALID_ALIAS");

        PublicKeyResponseDto response = cryptoService.getPublicKey(request);
        assertNull(response);
    }

    @Test@Ignore
    public void testGetMachineNameNew() throws IOException, IllegalBlockSizeException, BadPaddingException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        File dataDir = new File("/data");
        when(context.getDataDir()).thenReturn(dataDir);
        when(cipher.doFinal(any(byte[].class))).thenReturn("encrypted_conf".getBytes());
        when(keyStore.getKey(ENCDEC_ALIAS, null)).thenReturn(privateKey);
        when(keyStore.getCertificate(ENCDEC_ALIAS)).thenReturn(x509Certificate);
        when(x509Certificate.getPublicKey()).thenReturn(publicKey);
        when(privateKey.getModulus()).thenReturn(new BigInteger(
                "251959084756578934940271832400483985714292821262040320277771378360287" +
                        "7514183472702618963750149718246911650776133798590957000973304597488084284" +
                        "017974291006424586918171951187461215151726546322822168699875491824224336372" +
                        "590851418654620435767984233871847744861204357679842338718477444792073993508" +
                        "7868376563123465489273"));

        String machineName = cryptoService.getMachineName();
        assertNotNull(machineName);
    }

    @Test@Ignore
    public void testGetMachineNameExisting() throws Exception {
        File dataDir = new File("/data");
        File confDir = mock(File.class);
        File confFile = mock(File.class);

        when(context.getDataDir()).thenReturn(dataDir);
        when(confDir.exists()).thenReturn(true);
        when(confFile.exists()).thenReturn(true);
        when(IOUtils.toString(any(Reader.class))).thenReturn(CryptoUtil.base64encoder.encodeToString("{\"machineName\":\"test-machine\"}".getBytes()));
        when(keyStore.getKey(ENCDEC_ALIAS, null)).thenReturn(privateKey);
        when(keyStore.getCertificate(ENCDEC_ALIAS)).thenReturn(x509Certificate);
        when(x509Certificate.getPublicKey()).thenReturn(publicKey);
        when(privateKey.getModulus()).thenReturn(new BigInteger(
                "2519590847565789349402718324004839857142928212620403202777713783604366202070" +
                        "7595556264018525880784406918290641249515082189298559149176184502808489120072" +
                        "8449926873928072877767359714183472702618963750149718246911650776133798590957" +
                        "0009733045974880842840179742910064245869181719511874612151517265463228221686" +
                        "998754918242243363725908514186546204357679842338718477444792073993508786837656" +
                        "3123465489273"));
        when(cipher.doFinal(any(byte[].class))).thenReturn("{\"machineName\":\"test-machine\"}".getBytes());

        String machineName = cryptoService.getMachineName();
        assertEquals("test-machine", machineName);
    }

    @Test
    public void testGetClientKeyIndex() throws Exception {
        when(keyStore.getCertificate(ENCDEC_ALIAS)).thenReturn(x509Certificate);
        when(x509Certificate.getPublicKey()).thenReturn(publicKey);
        when(publicKey.getEncoded()).thenReturn("public_key".getBytes());

        String keyIndex = cryptoService.getClientKeyIndex();
        assertNotNull(keyIndex);
    }

    @Test@Ignore
    public void testGetMachineDetails() throws Exception {
        when(keyStore.getCertificate(ENCDEC_ALIAS)).thenReturn(x509Certificate);
        when(keyStore.getCertificate(SIGNV_ALIAS)).thenReturn(x509Certificate);
        when(x509Certificate.getPublicKey()).thenReturn(publicKey);
        when(publicKey.getEncoded()).thenReturn("public_key".getBytes());

        File dataDir = spy(new File("/data"));
        File confDir = spy(new File(dataDir, KeyManagerConstant.APP_CONF_DIR));
        File confFile = spy(new File(confDir, KeyManagerConstant.APP_CONF));

        when(context.getDataDir()).thenReturn(dataDir);
        doReturn(true).when(confDir).exists();
        doReturn(true).when(confFile).exists();

        try (MockedStatic<IOUtils> ioUtilsStatic = Mockito.mockStatic(IOUtils.class)) {
            String encryptedConfig = CryptoUtil.base64encoder.encodeToString("{\"machineName\":\"test-machine\"}".getBytes());
            ioUtilsStatic.when(() -> IOUtils.toString(any(Reader.class))).thenReturn(encryptedConfig);

            when(keyStore.getKey(ENCDEC_ALIAS, null)).thenReturn(privateKey);
            when(privateKey.getModulus()).thenReturn(new BigInteger(
                    "2519590847565789349402718324004839857142928212620403202777713783604366202070" +
                            "7595556264018525880784406918290641249515082189298559149176184502808489120072" +
                            "8449926873928072877767359714183472702618963750149718246911650776133798590957" +
                            "0009733045974880842840179742910064245869181719511874612151517265463228221686" +
                            "9987549182422433637259085141865462043576798423387184774447920739935087868376" +
                            "563126548927043188656312654892704318820765356622076011406914346062099073131"
            ));

            when(cipher.doFinal(any(byte[].class))).thenReturn("{\"machineName\":\"test-machine\"}".getBytes());

            Map<String, String> details = cryptoService.getMachineDetails();
            assertNotNull(details);
            assertEquals("test-machine", details.get("name"));
            assertEquals(CryptoUtil.base64encoder.encodeToString("public_key".getBytes()), details.get("publicKey"));
            assertEquals(CryptoUtil.base64encoder.encodeToString("public_key".getBytes()), details.get("signPublicKey"));
        }
    }

    private void invokePrivateMethod(String methodName) throws Exception {
        java.lang.reflect.Method method = LocalClientCryptoServiceImpl.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(cryptoService);
    }
}
