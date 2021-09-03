package io.mosip.registration.clientmanager.service.crypto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
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
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
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
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.crypto.*;
import io.mosip.registration.clientmanager.spi.crypto.ClientCryptoManagerService;
import io.mosip.registration.clientmanager.util.ConfigService;

@Singleton
public class LocalClientCryptoServiceImpl extends Service implements ClientCryptoManagerService {

    private Context context;

    private static Encoder base64encoder;
    private static Decoder base64decoder;

    private static String CRYPTO_ASYMMETRIC_ALGORITHM;
    private static String CRYPTO_SYMMETRIC_ALGORITHM;
    private static String KEYGEN_ASYMMETRIC_ALGORITHM;
    private static String KEYGEN_SYMMETRIC_ALGORITHM;
    private static int KEYGEN_ASYMMETRIC_KEY_LENGTH;
    private static int KEYGEN_SYMMETRIC_KEY_LENGTH;
    private static int CRYPTO_GCM_TAG_LENGTH;
    private static final int IV_LENGTH = 12;
    private static final int AAD_LENGTH = 32;
    private static String CRYPTO_HASH_ALGORITHM;
    private static int CRYPTO_HASH_SYMMETRIC_KEY_LENGTH;
    private static int CRYPTO_HASH_ITERATION;
    private static String CRYPTO_SIGN_ALGORITHM;
    private static String CERTIFICATE_SIGN_ALGORITHM;

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String PRIVATE_ALIAS = "PRIVATE_ALIAS";
    private static final String SECRET_ALIAS = "SECRET_ALIAS";

    private static SecureRandom secureRandom = null;



    @Inject
    LocalClientCryptoServiceImpl() {
        Context context = getApplicationContext();
        this.context = context;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                    KEYGEN_ASYMMETRIC_ALGORITHM, ANDROID_KEY_STORE);
            kpg.initialize(new KeyGenParameterSpec.Builder(
                    PRIVATE_ALIAS,
                    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY |
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(KEYGEN_ASYMMETRIC_KEY_LENGTH)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .build());

            KeyPair kp = kpg.generateKeyPair();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        try {
            KeyGenerator kg = KeyGenerator
                    .getInstance(KEYGEN_SYMMETRIC_ALGORITHM, ANDROID_KEY_STORE);

            final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(SECRET_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setKeySize(KEYGEN_SYMMETRIC_KEY_LENGTH)
                    .build();

            kg.init(keyGenParameterSpec);
           SecretKey secretKey = kg.generateKey();

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initializeClientSecurity() {
        // get context from main activity

        CRYPTO_ASYMMETRIC_ALGORITHM = ConfigService.getProperty("mosip.kernel.crypto.asymmetric-algorithm-name",context);
        CRYPTO_SYMMETRIC_ALGORITHM = ConfigService.getProperty("mosip.kernel.crypto.symmetric-algorithm-name",context);
        KEYGEN_ASYMMETRIC_ALGORITHM = ConfigService.getProperty("mosip.kernel.keygenerator.asymmetric-algorithm-name",context);
        KEYGEN_SYMMETRIC_ALGORITHM = ConfigService.getProperty("mosip.kernel.keygenerator.symmetric-algorithm-name",context);
        KEYGEN_ASYMMETRIC_KEY_LENGTH = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.keygenerator.asymmetric-key-length",context));
        KEYGEN_SYMMETRIC_KEY_LENGTH = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.keygenerator.symmetric-key-length",context));
        CRYPTO_GCM_TAG_LENGTH = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.crypto.gcm-tag-length",context));
        CRYPTO_HASH_ALGORITHM = ConfigService.getProperty("mosip.kernel.crypto.hash-algorithm-name",context);
        CRYPTO_HASH_SYMMETRIC_KEY_LENGTH = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.crypto.hash-symmetric-key-length",context));
        CRYPTO_HASH_ITERATION = Integer.parseInt(
                ConfigService.getProperty("mosip.kernel.crypto.hash-iteration",context));
        CRYPTO_SIGN_ALGORITHM = ConfigService.getProperty("mosip.kernel.crypto.sign-algorithm-name",context);
        CERTIFICATE_SIGN_ALGORITHM = ConfigService.getProperty("mosip.kernel.certificate.sign.algorithm",context);

        base64encoder = Base64.getEncoder();
        base64decoder = Base64.getDecoder();



    }
    @Override
    public SignResponseDto sign(SignRequestDto signRequestDto) {
        SignResponseDto signResponseDto = new SignResponseDto();

        byte[] dataToSign = base64decoder.decode(signRequestDto.getData());
        try {
            PrivateKey privateKey = getPrivateKey();

            Signature sign = Signature.getInstance(CRYPTO_SIGN_ALGORITHM);
            sign.initSign(privateKey);
            sign.update(dataToSign);
            byte[] signedData = sign.sign();

            signResponseDto.setData(base64encoder.encodeToString(signedData));
            return signResponseDto;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public SignVerifyResponseDto verifySign(SignVerifyRequestDto signVerifyRequestDto) {
        SignVerifyResponseDto signVerifyResponseDto = new SignVerifyResponseDto();

        byte[] public_key = base64decoder.decode(signVerifyRequestDto.getPublicKey());
        byte[] signature = base64decoder.decode(signVerifyRequestDto.getSignature());
        byte[] actualData = base64decoder.decode(signVerifyRequestDto.getData());
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(public_key);
            KeyFactory kf = KeyFactory.getInstance(KEYGEN_ASYMMETRIC_ALGORITHM);
            PublicKey publicKey = kf.generatePublic(keySpec);

            Signature sign = Signature.getInstance(CRYPTO_SIGN_ALGORITHM);
            sign.initVerify(publicKey);
            sign.update(actualData);
            boolean result = sign.verify(signature);

            signVerifyResponseDto.setVerified(result);
            return signVerifyResponseDto;
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public CryptoResponseDto encrypt(CryptoRequestDto cryptoRequestDto) {
        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();

        byte[] public_key = base64decoder.decode(cryptoRequestDto.getPublicKey());
        byte[] dataToEncrypt = base64decoder.decode(cryptoRequestDto.getValue());
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(public_key);
            KeyFactory kf = KeyFactory.getInstance(KEYGEN_ASYMMETRIC_ALGORITHM);
            PublicKey publicKey = kf.generatePublic(keySpec);

            SecretKey secretKey = getSecretKey();

            final Cipher cipher_symmetric = Cipher.getInstance(CRYPTO_SYMMETRIC_ALGORITHM);
            cipher_symmetric.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher_symmetric.getIV();
            byte[] data_encryption = cipher_symmetric.doFinal(dataToEncrypt);

            final Cipher cipher_asymmetric = Cipher.getInstance(CRYPTO_ASYMMETRIC_ALGORITHM);
            cipher_asymmetric.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] key_encryption = cipher_asymmetric.doFinal(secretKey.getEncoded());

            // store iv and encryption in encrypted_data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(key_encryption);
            outputStream.write(iv);
            outputStream.write(data_encryption);
            byte encrypted_key_iv_data[] = outputStream.toByteArray();


            cryptoResponseDto.setValue(base64encoder.encodeToString(encrypted_key_iv_data));
            return cryptoResponseDto;
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public CryptoResponseDto decrypt(CryptoRequestDto cryptoRequestDto) {

        return null;
    }

    @Override
    public PublicKeyResponseDto getPublicKey(PublicKeyRequestDto publicKeyRequestDto) {
        return null;
    }

    private SecretKey getSecretKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(SECRET_ALIAS, null);
        if (!(entry instanceof KeyStore.SecretKeyEntry)) {
            return null;
        }
        return ((KeyStore.SecretKeyEntry) entry).getSecretKey();
    }

    private PrivateKey getPrivateKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(PRIVATE_ALIAS, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            return null;
        }
        return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
    }




}
