package io.mosip.registration.keymanager.service;

import android.content.Context;
import android.util.Log;
import io.mosip.registration.keymanager.dto.CryptoManagerRequestDto;
import io.mosip.registration.keymanager.dto.CryptoManagerResponseDto;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.keymanager.util.ConfigService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.spec.MGF1ParameterSpec;
import java.util.Objects;

import static java.util.Arrays.copyOfRange;

@Singleton
public class CryptoManagerServiceImpl implements CryptoManagerService {

    private static final String TAG = CryptoManagerServiceImpl.class.getSimpleName();
    private static final String signRefId = "SIGN";
    private static final String signApplicationId = "KERNEL";
    private static final String AES = "AES";
    public static final int GCM_NONCE_LENGTH = 12;
    public static final int GCM_AAD_LENGTH = 32;
    private static final byte[] VERSION_RSA_2048 = "VER_R2".getBytes();
    private static final String CERTIFICATE_TYPE = "X.509";
    private static final String MGF1 = "MGF1";
    private static final String HASH_ALGO = "SHA-256";
    public static final int THUMBPRINT_LENGTH = 32;

    private Context context;
    private static String KEYGEN_SYMMETRIC_ALGORITHM;
    private static int KEYGEN_SYMMETRIC_KEY_LENGTH;
    private static String CRYPTO_SYMMETRIC_ALGORITHM;
    private static int CRYPTO_GCM_TAG_LENGTH;
    private static String CRYPTO_ASYMMETRIC_ALGORITHM;
    private static String CRYPTO_ASYMMETRIC_ALGO_MD;
    private static String CRYPTO_ASYMMETRIC_ALGO_MGF;
    private static String KEY_SPLITTER;
    private CertificateManagerService certificateManagerService;
    private SecureRandom secureRandom = new SecureRandom();


    @Inject
    public CryptoManagerServiceImpl(Context context, CertificateManagerService certificateManagerService) {
        this.context = context;
        this.certificateManagerService = certificateManagerService;
        KEYGEN_SYMMETRIC_ALGORITHM = ConfigService.getProperty("mosip.kernel.keygenerator.symmetric-algorithm-name",context);
        KEYGEN_SYMMETRIC_KEY_LENGTH = Integer.parseInt(ConfigService.getProperty("mosip.kernel.keygenerator.symmetric-key-length",context));
        CRYPTO_SYMMETRIC_ALGORITHM = ConfigService.getProperty("mosip.kernel.crypto.symmetric-algorithm-name",context);
        CRYPTO_GCM_TAG_LENGTH = Integer.parseInt(ConfigService.getProperty("mosip.kernel.crypto.gcm-tag-length",context));
        CRYPTO_ASYMMETRIC_ALGORITHM = ConfigService.getProperty("mosip.kernel.crypto.asymmetric-algorithm-name",context);
        CRYPTO_ASYMMETRIC_ALGO_MD = ConfigService.getProperty("mosip.kernel.crypto.asymmetric-algorithm-message-digest-function",context);
        CRYPTO_ASYMMETRIC_ALGO_MGF = ConfigService.getProperty("mosip.kernel.crypto.asymmetric-algorithm-mask-generation-function",context);
        KEY_SPLITTER = ConfigService.getProperty("mosip.kernel.data-key-splitter", context);
    }

    @Override
    public KeyGenerator generateAESKey(int keyLength) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(KEYGEN_SYMMETRIC_ALGORITHM);
        keyGen.init(keyLength);
        return keyGen;
    }


    @Override
    public CryptoManagerResponseDto encrypt(CryptoManagerRequestDto cryptoRequestDto) throws Exception {
        Log.i(TAG, "Request for data encryption.");

        if(!isDataValid(cryptoRequestDto.getReferenceId()) ||
                (cryptoRequestDto.getApplicationId().equalsIgnoreCase(signApplicationId) &&
                        cryptoRequestDto.getReferenceId().equalsIgnoreCase(signRefId) )) {
            Log.i(TAG,"Not Allowed to preform encryption with Master Key.");
            throw new Exception("ENCRYPT_NOT_ALLOWED_ERROR WITH SIGN KEY");
        }

        KeyGenerator keyGen = generateAESKey(KEYGEN_SYMMETRIC_KEY_LENGTH);
        SecretKey secretKey = keyGen.generateKey();
        final byte[] encryptedData;
        byte[] headerBytes = new byte[0];
        if (isDataValid(nullOrTrim(cryptoRequestDto.getSalt()))) {
            encryptedData = symmetricEncrypt(secretKey, CryptoUtil.base64decoder.decode(cryptoRequestDto.getData()),
                    CryptoUtil.base64decoder.decode(nullOrTrim(cryptoRequestDto.getSalt())),
                    CryptoUtil.base64decoder.decode(nullOrTrim(cryptoRequestDto.getAad())));
        } else {
            byte[] aad = CryptoUtil.base64decoder.decode(nullOrTrim(cryptoRequestDto.getAad()));
            if (aad == null || aad.length == 0){
                encryptedData = generateAadAndEncryptData(secretKey, cryptoRequestDto.getData());
                headerBytes = VERSION_RSA_2048;
            } else {
                encryptedData = symmetricEncrypt(secretKey, CryptoUtil.base64decoder.decode(cryptoRequestDto.getData()),
                        aad);
            }
        }

        String certificateData = certificateManagerService.getCertificate("REGISTRATION", cryptoRequestDto.getReferenceId());
        Certificate certificate = convertToCertificate(certificateData);
        Log.i(TAG,"Found the cerificate, proceeding with session key encryption.");
        PublicKey publicKey = certificate.getPublicKey();
        final byte[] encryptedSymmetricKey = asymmetricEncrypt(publicKey, secretKey.getEncoded());
        Log.i(TAG,"Session key encryption completed.");
        CryptoManagerResponseDto cryptoResponseDto = new CryptoManagerResponseDto();

        byte[] certThumbprint = getCertificateThumbprint(certificate);
        byte[] concatedData = concatCertThumbprint(certThumbprint, encryptedSymmetricKey);
        byte[] finalEncKeyBytes = concatByteArrays(headerBytes, concatedData);
        cryptoResponseDto.setData(CryptoUtil.base64encoder.encodeToString(combineByteArray(encryptedData,
                finalEncKeyBytes, KEY_SPLITTER)));
        return cryptoResponseDto;
    }


    @Override
    public byte[] symmetricEncryptWithRandomIV(SecretKey secretKey, byte[] data, byte[] aad) throws Exception {
        final Cipher cipher = Cipher.getInstance(CRYPTO_SYMMETRIC_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), AES);
        byte[] randomIV = generateRandomBytes(cipher.getBlockSize());
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(CRYPTO_GCM_TAG_LENGTH, randomIV);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
        byte[] output = new byte[cipher.getOutputSize(data.length) + cipher.getBlockSize()];
        if (aad != null && aad.length != 0) {
            cipher.updateAAD(aad);
        }
        byte[] processData = cipher.doFinal(data);
        System.arraycopy(processData, 0, output, 0, processData.length);
        System.arraycopy(randomIV, 0, output, processData.length, randomIV.length);
        return output;
    }

    public byte[] concatCertThumbprint(byte[] certThumbprint, byte[] encryptedKey){
        byte[] finalData = new byte[THUMBPRINT_LENGTH + encryptedKey.length];
        System.arraycopy(certThumbprint, 0, finalData, 0, certThumbprint.length);
        System.arraycopy(encryptedKey, 0, finalData, certThumbprint.length, encryptedKey.length);
        return finalData;
    }

    private byte[] symmetricEncrypt(SecretKey secretKey, byte[] data, byte[] iv, byte[] aad) throws Exception {
        final Cipher cipher = Cipher.getInstance(CRYPTO_SYMMETRIC_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), AES);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(CRYPTO_GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
        if (aad != null && aad.length != 0) {
            cipher.updateAAD(aad);
        }
        return cipher.doFinal(data);
    }

    private byte[] symmetricEncrypt(SecretKey secretKey, byte[] data, byte[] aad) throws Exception {
        final Cipher cipher = Cipher.getInstance(CRYPTO_SYMMETRIC_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), AES);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        if (aad != null && aad.length != 0) {
            cipher.updateAAD(aad);
        }
        return cipher.doFinal(data);
    }

    @Override
    public byte[] asymmetricEncrypt(PublicKey publicKey, byte[] data) throws Exception {
        final Cipher cipher = Cipher.getInstance(CRYPTO_ASYMMETRIC_ALGORITHM);
        final OAEPParameterSpec oaepParams = new OAEPParameterSpec(HASH_ALGO, MGF1, MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
        return cipher.doFinal(data);
    }

    private byte[] generateAadAndEncryptData(SecretKey secretKey, String data) throws Exception {
        Log.i(TAG,"Provided AAD value is null or empty byte array. So generating random 32 bytes for AAD.");
        byte[] aad = generateRandomBytes(GCM_AAD_LENGTH);
        byte[] nonce = copyOfRange(aad, 0, GCM_NONCE_LENGTH);
        byte[] encData = symmetricEncrypt(secretKey, CryptoUtil.base64decoder.decode(data), nonce, aad);
        return concatByteArrays(aad, encData);
    }

    private boolean isDataValid(String anyData) {
        return anyData != null && !anyData.trim().isEmpty();
    }

    private String nullOrTrim(String parameter) {
        return parameter == null ? null : parameter.trim();
    }

    public byte[] generateRandomBytes(int size) {
        byte[] randomBytes = new byte[size];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    public byte[] concatByteArrays(byte[] array1, byte[] array2){
        byte[] finalData = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, finalData, 0, array1.length);
        System.arraycopy(array2, 0, finalData, array1.length, array2.length);
        return finalData;
    }

    public static byte[] combineByteArray(byte[] data, byte[] key, String keySplitter) {
        byte[] keySplitterBytes = keySplitter.getBytes();
        byte[] combinedArray = new byte[key.length + keySplitterBytes.length + data.length];
        System.arraycopy(key, 0, combinedArray, 0, key.length);
        System.arraycopy(keySplitterBytes, 0, combinedArray, key.length, keySplitterBytes.length);
        System.arraycopy(data, 0, combinedArray, key.length + keySplitterBytes.length, data.length);
        return combinedArray;
    }

    public Certificate convertToCertificate(String certData) throws Exception {
        try {
            StringReader strReader = new StringReader(certData);
            PemReader pemReader = new PemReader(strReader);
            PemObject pemObject = pemReader.readPemObject();
            if (Objects.isNull(pemObject)) {
                Log.i(TAG, "Error Parsing Certificate.");
                throw new Exception("CERTIFICATE_PARSING_ERROR");
            }
            byte[] certBytes = pemObject.getContent();
            CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
            return certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
        } catch(Exception e) {
            Log.e(TAG, "CERTIFICATE_PARSING_ERROR", e);
            throw new Exception("CERTIFICATE_PARSING_ERROR");
        }
    }


    @Override
    public byte[] getCertificateThumbprint(Certificate cert) throws Exception {
        try {
            return DigestUtils.sha256(cert.getEncoded());
        } catch (CertificateEncodingException e) {
            Log.e(TAG, "Error generating certificate thumbprint.", e);
            throw new Exception("CERTIFICATE_THUMBPRINT_ERROR");
        }
    }
}
