package io.mosip.registration.clientmanager.service.crypto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.crypto.CryptoRequestDto;
import io.mosip.registration.clientmanager.dto.crypto.CryptoResponseDto;
import io.mosip.registration.clientmanager.dto.crypto.PublicKeyRequestDto;
import io.mosip.registration.clientmanager.dto.crypto.PublicKeyResponseDto;
import io.mosip.registration.clientmanager.dto.crypto.SignRequestDto;
import io.mosip.registration.clientmanager.dto.crypto.SignResponseDto;
import io.mosip.registration.clientmanager.dto.crypto.SignVerifyRequestDto;
import io.mosip.registration.clientmanager.dto.crypto.SignVerifyResponseDto;
import io.mosip.registration.clientmanager.spi.crypto.ClientCryptoManagerService;
import io.mosip.registration.clientmanager.util.ConfigService;
import android.util.Log;
import android.R;

@Singleton
public class LocalClientCryptoServiceImpl implements ClientCryptoManagerService {
    private static final String TAG = LocalClientCryptoServiceImpl.class.getSimpleName();
    private Context context;

    // encoder and decoder from java itself
    private static Encoder base64encoder;
    private static Decoder base64decoder;


    // asymmetric encryption details together-----------------------------
    private static String CRYPTO_ASYMMETRIC_ALGORITHM;
    private static String KEYGEN_ASYMMETRIC_ALGO_BLOCK;
    private static String KEYGEN_ASYMMETRIC_ALGO_PAD;
    private static String KEYGEN_ASYMMETRIC_ALGORITHM;
    private static String CRYPTO_ASYMMETRIC_ALGO_MD;
    private static String CRYPTO_ASYMMETRIC_ALGO_MGF;

    private static String KEYGEN_ASYMMETRIC_ALGO_SIGN_PAD;
    private static int KEYGEN_ASYMMETRIC_KEY_LENGTH;

    // symmetric encryption details together-----------------------------
    private static String CRYPTO_SYMMETRIC_ALGORITHM;
    private static String KEYGEN_SYMMETRIC_ALGORITHM;
    private static int KEYGEN_SYMMETRIC_KEY_LENGTH;

    private static int CRYPTO_GCM_TAG_LENGTH;


    // need to read aad and iv length from config files-----
    private static int CRYPTO_SYMMETRIC_IV_LENGTH;
    private static int CRYPTO_SYMMETRIC_AAD_LENGTH;


    private static String CRYPTO_HASH_ALGORITHM;
    private static int CRYPTO_HASH_SYMMETRIC_KEY_LENGTH;
    private static int CRYPTO_HASH_ITERATION;
    private static String CRYPTO_SIGN_ALGORITHM;
    private static String CERTIFICATE_SIGN_ALGORITHM;

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String ENDEC_ALIAS = "ENDEC";
    private static final String SIGN_ALIAS = "SIGN";

    private static SecureRandom secureRandom = null;


    @Inject
    public LocalClientCryptoServiceImpl(Context appContext) {
        Log.i(TAG, "LocalClientCryptoServiceImpl: Constructor call successful");
        try {
            initLocalClientCryptoService(appContext);
        } catch (Exception e) {
            Log.e(TAG, "LocalClientCryptoServiceImpl: Failed Initialization", e);
        }

    }

    public void initLocalClientCryptoService(Context appContext) {
        this.context = appContext;
        initializeClientSecurity();


        genSignKey();
        genEnDecKey();

        Log.i(TAG, "initLocalClientCryptoService: Initialization call successful");
    }

    private void initializeClientSecurity() {
        Log.i(TAG, "LocalClientCryptoServiceImpl: Initializing");
        CRYPTO_ASYMMETRIC_ALGORITHM = ConfigService.getProperty("mosip.kernel.crypto.asymmetric-algorithm-name",context);
        KEYGEN_ASYMMETRIC_ALGO_BLOCK = ConfigService.getProperty("mosip.kernel.crypto.asymmetric-algorithm-block-mode",context);
        KEYGEN_ASYMMETRIC_ALGO_PAD = ConfigService.getProperty("mosip.kernel.crypto.asymmetric-algorithm-padding-scheme",context);
        CRYPTO_ASYMMETRIC_ALGO_MD = ConfigService.getProperty("mosip.kernel.crypto.asymmetric-algorithm-message-digest-function",context);
        CRYPTO_ASYMMETRIC_ALGO_MGF = ConfigService.getProperty("mosip.kernel.crypto.asymmetric-algorithm-mask-generation-function",context);
        CRYPTO_SYMMETRIC_ALGORITHM = ConfigService.getProperty("mosip.kernel.crypto.symmetric-algorithm-name",context);
        KEYGEN_ASYMMETRIC_ALGORITHM = ConfigService.getProperty("mosip.kernel.keygenerator.asymmetric-algorithm-name",context);
        KEYGEN_SYMMETRIC_ALGORITHM = ConfigService.getProperty("mosip.kernel.keygenerator.symmetric-algorithm-name",context);
        KEYGEN_ASYMMETRIC_KEY_LENGTH = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.keygenerator.asymmetric-key-length",context));
        KEYGEN_SYMMETRIC_KEY_LENGTH = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.keygenerator.symmetric-key-length",context));
        CRYPTO_SYMMETRIC_IV_LENGTH = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.crypto.symmetric-algorithm-iv-length",context));
        CRYPTO_SYMMETRIC_AAD_LENGTH = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.crypto.symmetric-algorithm-aad-length",context));
        CRYPTO_GCM_TAG_LENGTH = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.crypto.gcm-tag-length",context));
        CRYPTO_HASH_ALGORITHM = ConfigService.getProperty("mosip.kernel.crypto.hash-algorithm-name",context);
        CRYPTO_HASH_SYMMETRIC_KEY_LENGTH = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.crypto.hash-symmetric-key-length",context));
        CRYPTO_HASH_ITERATION = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.crypto.hash-iteration",context));
        CRYPTO_SIGN_ALGORITHM = ConfigService.getProperty("mosip.kernel.crypto.sign-algorithm-name",context);
        KEYGEN_ASYMMETRIC_ALGO_SIGN_PAD = ConfigService.getProperty("mosip.kernel.crypto.sign-algorithm-padding-scheme",context);
        CERTIFICATE_SIGN_ALGORITHM = ConfigService.getProperty("mosip.kernel.certificate.sign.algorithm",context);

        base64encoder = Base64.getEncoder();
        base64decoder = Base64.getDecoder();
    }

    private void genSignKey() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                    KEYGEN_ASYMMETRIC_ALGORITHM, ANDROID_KEY_STORE);

            final KeyGenParameterSpec keyPairGenParameterSpec = new KeyGenParameterSpec.Builder(
                    SIGN_ALIAS,
                    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                    .setKeySize(KEYGEN_ASYMMETRIC_KEY_LENGTH)
                    .setSignaturePaddings(KEYGEN_ASYMMETRIC_ALGO_SIGN_PAD)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .build();

            kpg.initialize(keyPairGenParameterSpec);
            KeyPair kp = kpg.generateKeyPair();
            Log.i(TAG, "genSignKey: Generated signing private key successfully " +
                    base64encoder.encodeToString(kp.getPublic().getEncoded()));

        } catch(Exception e) {
            Log.e(TAG, "genSignKey: Sign key generation failed ", e);
        }
    }

    private void genEnDecKey() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                    KEYGEN_ASYMMETRIC_ALGORITHM, ANDROID_KEY_STORE);

            final KeyGenParameterSpec keyPairGenParameterSpec = new KeyGenParameterSpec.Builder(
                    ENDEC_ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KEYGEN_ASYMMETRIC_ALGO_BLOCK)
                    .setKeySize(KEYGEN_ASYMMETRIC_KEY_LENGTH)
                    .setEncryptionPaddings(KEYGEN_ASYMMETRIC_ALGO_PAD)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .build();

            kpg.initialize(keyPairGenParameterSpec);
            KeyPair kp = kpg.generateKeyPair();
            Log.i(TAG, "genEnDecKey: Generated encryption private key successfully " +
                    base64encoder.encodeToString(kp.getPublic().getEncoded()));

        } catch(Exception e) {
            Log.e(TAG, "genEnDecKey: Encryption key generation failed ", e);
        }
    }

    @Override
    public SignResponseDto sign(SignRequestDto signRequestDto) {
        SignResponseDto signResponseDto = new SignResponseDto();

        byte[] dataToSign = base64decoder.decode(base64encoder.encodeToString(signRequestDto.getData().getBytes()));
        try {
            PrivateKey privateKey = getSignPrivateKey();
            Signature sign = Signature.getInstance(CERTIFICATE_SIGN_ALGORITHM);
            sign.initSign(privateKey);
            sign.update(dataToSign);
            byte[] signedData = sign.sign();

            signResponseDto.setData(base64encoder.encodeToString(signedData));
            return signResponseDto;

        } catch (Exception e) {
            Log.e(TAG, "sign: Signing Failed ", e);
        }
        return null;
    }

    @Override
    public SignVerifyResponseDto verifySign(SignVerifyRequestDto signVerifyRequestDto) {
        SignVerifyResponseDto signVerifyResponseDto = new SignVerifyResponseDto();

        byte[] public_key = base64decoder.decode(signVerifyRequestDto.getPublicKey());
        byte[] signature = base64decoder.decode(signVerifyRequestDto.getSignature());
        byte[] actualData = base64decoder.decode(base64encoder.encodeToString(signVerifyRequestDto.getData().getBytes()));

        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(public_key);
            KeyFactory kf = KeyFactory.getInstance(KEYGEN_ASYMMETRIC_ALGORITHM);
            PublicKey publicKey = kf.generatePublic(keySpec);

            Signature sign = Signature.getInstance(CERTIFICATE_SIGN_ALGORITHM);
            sign.initVerify(publicKey);
            sign.update(actualData);
            boolean result = sign.verify(signature);

            signVerifyResponseDto.setVerified(result);
            return signVerifyResponseDto;
        }
        catch(Exception e) {
            Log.e(TAG, "verifySign: Sign Verification Failed", e);
        }
        return null;
    }

    @Override
    public CryptoResponseDto encrypt(CryptoRequestDto cryptoRequestDto) {
        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();

        byte[] public_key = base64decoder.decode(cryptoRequestDto.getPublicKey());
        byte[] dataToEncrypt = base64decoder.decode(base64encoder.encodeToString(cryptoRequestDto.getValue().getBytes()));

        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(public_key);
            KeyFactory kf = KeyFactory.getInstance(KEYGEN_ASYMMETRIC_ALGORITHM);
            PublicKey publicKey = kf.generatePublic(keySpec);

            KeyGenerator keyGen = KeyGenerator.getInstance(KEYGEN_SYMMETRIC_ALGORITHM);
            keyGen.init(KEYGEN_SYMMETRIC_KEY_LENGTH);
            SecretKey mosipSecretKey = keyGen.generateKey();

            // symmetric encryption of data---------------------------------------------------------
            final Cipher cipher_symmetric = Cipher.getInstance(CRYPTO_SYMMETRIC_ALGORITHM);
            cipher_symmetric.init(Cipher.ENCRYPT_MODE, mosipSecretKey);
            byte[] iv = cipher_symmetric.getIV();
            byte[] aad = generateRandomBytes(CRYPTO_SYMMETRIC_AAD_LENGTH);
            cipher_symmetric.updateAAD(aad);
            byte[] data_encryption = cipher_symmetric.doFinal(dataToEncrypt);

            // asymmetric encryption of secret key--------------------------------------------------
            final Cipher cipher_asymmetric = Cipher.getInstance(CRYPTO_ASYMMETRIC_ALGORITHM);
            OAEPParameterSpec spec = new OAEPParameterSpec(
                    CRYPTO_ASYMMETRIC_ALGO_MD, CRYPTO_ASYMMETRIC_ALGO_MGF, MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
            cipher_asymmetric.init(Cipher.ENCRYPT_MODE, publicKey, spec);
            byte[] key_encryption = cipher_asymmetric.doFinal(mosipSecretKey.getEncoded());

            // constructing key, iv, add and encryption stream--------------------------------------
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(key_encryption);
            outputStream.write(iv);
            outputStream.write(aad);
            outputStream.write(data_encryption);

            byte[] encrypted_key_iv_data = outputStream.toByteArray();

            cryptoResponseDto.setValue(base64encoder.encodeToString(encrypted_key_iv_data));
            return cryptoResponseDto;
        } catch(Exception e) {
            Log.e(TAG, "encrypt: Encryption failed ", e);
        }

        return null;
    }

    @Override
    public CryptoResponseDto decrypt(CryptoRequestDto cryptoRequestDto) {
        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();
        byte[] dataToDecrypt =  base64decoder.decode(cryptoRequestDto.getValue());
        byte[] public_key =  base64decoder.decode(cryptoRequestDto.getPublicKey());

        byte[] encryptedSecretKey = Arrays.copyOfRange(dataToDecrypt, 0, KEYGEN_SYMMETRIC_KEY_LENGTH);
        byte[] iv = Arrays.copyOfRange(dataToDecrypt, KEYGEN_SYMMETRIC_KEY_LENGTH, KEYGEN_SYMMETRIC_KEY_LENGTH+CRYPTO_SYMMETRIC_IV_LENGTH);
        byte[] aad = Arrays.copyOfRange(dataToDecrypt,KEYGEN_SYMMETRIC_KEY_LENGTH+CRYPTO_SYMMETRIC_IV_LENGTH, KEYGEN_SYMMETRIC_KEY_LENGTH+CRYPTO_SYMMETRIC_IV_LENGTH+CRYPTO_SYMMETRIC_AAD_LENGTH);
        byte[] encrypted_data = Arrays.copyOfRange(dataToDecrypt, KEYGEN_SYMMETRIC_KEY_LENGTH+CRYPTO_SYMMETRIC_IV_LENGTH+CRYPTO_SYMMETRIC_AAD_LENGTH, dataToDecrypt.length);

        try {
            PrivateKey privateKey = getEnDecPrivateKey();

            // asymmetric decryption of secret key----------------------------------------------------
            final Cipher cipher_asymmetric = Cipher.getInstance(CRYPTO_ASYMMETRIC_ALGORITHM);
            cipher_asymmetric.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] secretKeyBytes = cipher_asymmetric.doFinal(encryptedSecretKey);

            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, KEYGEN_SYMMETRIC_ALGORITHM);

            // symmetric decryption of data-----------------------------------------------------
            final Cipher cipher_symmetric = Cipher.getInstance(CRYPTO_SYMMETRIC_ALGORITHM);
            final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(CRYPTO_GCM_TAG_LENGTH, iv);
            cipher_symmetric.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            cipher_symmetric.updateAAD(aad);
            byte[] decodedBytes = cipher_symmetric.doFinal(encrypted_data);
            String decodedString = new String(decodedBytes);
            cryptoResponseDto.setValue(decodedString);
            return cryptoResponseDto;
        } catch(Exception e) {
            Log.e(TAG, "decrypt: Decryption failed", e);
        }
        return null;
    }

    @Override
    public PublicKeyResponseDto getPublicKey(PublicKeyRequestDto publicKeyRequestDto) {
        PublicKeyResponseDto publicKeyResponseDto = new PublicKeyResponseDto();

        try {
            String keyRequest = publicKeyRequestDto.getServerProfile();
            Log.i(TAG, "getPublicKey: Public key accessed for " + keyRequest);
            if(keyRequest == "sign") {
                PublicKey publicKey = getSignPublicKey();
                Log.i(TAG, "getPublicKey: Sign Public key accessed ");
                String public_key = base64encoder.encodeToString(publicKey.getEncoded());
                publicKeyResponseDto.setPublicKey(public_key);
                return publicKeyResponseDto;
            }
            else if(keyRequest == "endec") {
                PublicKey publicKey = getEnDecPublicKey();
                Log.i(TAG, "getPublicKey: EnDec Public key accessed ");
                String public_key = base64encoder.encodeToString(publicKey.getEncoded());
                publicKeyResponseDto.setPublicKey(public_key);
                return publicKeyResponseDto;
            }
        } catch(Exception e) {
            Log.e(TAG, "getPublicKey: Public key access failed ", e);
        }
        return null;
    }

    // get endec private key from keystore
    private PrivateKey getEnDecPrivateKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        return  (PrivateKey) ks.getKey(ENDEC_ALIAS, null);
    }

    // get endec public key from keystore
    private PublicKey getEnDecPublicKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        return (PublicKey) ks.getCertificate(ENDEC_ALIAS).getPublicKey();
    }

    // get sign private key from keystore
    private PrivateKey getSignPrivateKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        return  (PrivateKey) ks.getKey(SIGN_ALIAS, null);
    }

    // get sign public key from keystore
    private PublicKey getSignPublicKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        return (PublicKey) ks.getCertificate(SIGN_ALIAS).getPublicKey();
    }

    //  random byte generation
    public static byte[] generateRandomBytes(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }
}