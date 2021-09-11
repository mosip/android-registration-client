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
import java.io.IOException;
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
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
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
public class LocalClientCryptoServiceImpl extends Service implements ClientCryptoManagerService {

    private Context context;

    // encoder and decoder from java itself
    private static Encoder base64encoder;
    private static Decoder base64decoder;


    // asymmetric encryption details together-----------------------------
    private static String CRYPTO_ASYMMETRIC_ALGORITHM;
    private static String KEYGEN_ASYMMETRIC_ALGORITHM;
    private static String KEYGEN_ASYMMETRIC_ALGO_BLOCK;
    private static String KEYGEN_ASYMMETRIC_ALGO_PAD;
    private static int KEYGEN_ASYMMETRIC_KEY_LENGTH;

    // symmetric encryption details together-----------------------------
    private static String CRYPTO_SYMMETRIC_ALGORITHM;
    private static String KEYGEN_SYMMETRIC_ALGORITHM;
    private static String KEYGEN_SYMMETRIC_ALGO_BLOCK;
    private static String KEYGEN_SYMMETRIC_ALGO_PAD;
    private static int KEYGEN_SYMMETRIC_KEY_LENGTH;

    private static int CRYPTO_GCM_TAG_LENGTH;


    // need to read aad and iv length from config files-----
    private static int IV_LENGTH = 12;
    private static int AAD_LENGTH = 32;

    private static String CRYPTO_HASH_ALGORITHM;
    private static int CRYPTO_HASH_SYMMETRIC_KEY_LENGTH;
    private static int CRYPTO_HASH_ITERATION;
    private static String CRYPTO_SIGN_ALGORITHM;
    private static String CERTIFICATE_SIGN_ALGORITHM;

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String PRIVATE_ALIAS = "PRIVATE_ALIAS";
    private static final String SECRET_ALIAS = "SECRET_ALIAS";

    private static SecureRandom secureRandom = null;



    //    implementing service...extending binder
    public class ClientCryptoServiceBinder extends Binder {
        public  LocalClientCryptoServiceImpl getServiceInstance(){
            return LocalClientCryptoServiceImpl.this;
        }
    }

    //creating instance of binder to be passed to MainActivity
    private IBinder mBinder=new ClientCryptoServiceBinder();


    @Inject
    LocalClientCryptoServiceImpl() {
        Context context = getApplicationContext();
        this.context = context;
        initializeClientSecurity();

        genSecretKey();
        genPrivPubKey();
    }

    //    ON BIND BINDER FOR MAIN CLASS HERE
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    //    on start of service
//    @Override
//    public void onStart(Intent intent, int startId) {
//        super.onStart(intent, startId);
//    }

    //    on start command
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        initializeClientSecurity();
//
//        stopSelf();
//        return START_STICKY;
//    }


    //    onDestroy service
    @Override
    public void onDestroy() {
        super.onDestroy();
//        any object to be destroyed should be destroyed here
    }


    //    UNBIND METHOD
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    //    REBIND
    public void onRebind(Intent intent) {
        super.onRebind(intent);
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

    private void genSecretKey() {
        // hardcoding some initializations for now
        // for symmetric encryption----------------------------------------------------------

        // first set the encryption algorithm variables like blocks,padding,key length etc.....should be originally from the config file
        // NOTE:CURRENTLY HARDCODED
        // [
        KEYGEN_SYMMETRIC_ALGO_BLOCK=KeyProperties.BLOCK_MODE_GCM;
        KEYGEN_SYMMETRIC_ALGO_PAD=KeyProperties.ENCRYPTION_PADDING_PKCS7;
        // ]

        try {
            KeyGenerator kg = KeyGenerator
                    .getInstance(KEYGEN_SYMMETRIC_ALGORITHM, ANDROID_KEY_STORE);

            final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(SECRET_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KEYGEN_SYMMETRIC_ALGO_BLOCK)
                    .setEncryptionPaddings(KEYGEN_SYMMETRIC_ALGO_PAD)
                    .setKeySize(KEYGEN_SYMMETRIC_KEY_LENGTH)
                    .build();

            kg.init(keyGenParameterSpec);
            SecretKey secretKey = kg.generateKey();

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void genPrivPubKey() {
        // for asymmetric encryption storing keypair---------------------------
        //set the asymmetric encryption algorithm variables like blocks,padding,key length etc.....should be originally from the config file
        // NOTE:CURRENTLY HARDCODED
        // [
        KEYGEN_ASYMMETRIC_ALGO_BLOCK=KeyProperties.BLOCK_MODE_ECB;
        KEYGEN_ASYMMETRIC_ALGO_PAD=KeyProperties.ENCRYPTION_PADDING_RSA_OAEP;
        // ]

        try {
            // lot of errors in asymmetric part
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                    KEYGEN_ASYMMETRIC_ALGORITHM, ANDROID_KEY_STORE);

            final KeyGenParameterSpec keyPairGenParameterSpec = new KeyGenParameterSpec.Builder(
                    PRIVATE_ALIAS,
                    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY |
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KEYGEN_ASYMMETRIC_ALGO_BLOCK)
                    .setKeySize(KEYGEN_ASYMMETRIC_KEY_LENGTH)
                    .setEncryptionPaddings(KEYGEN_ASYMMETRIC_ALGO_PAD)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .build();

            kpg.initialize(keyPairGenParameterSpec);

            KeyPair kp = kpg.generateKeyPair();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
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

            // symmetric encryption of data-----------------------------------------------------
            final Cipher cipher_symmetric = Cipher.getInstance(CRYPTO_SYMMETRIC_ALGORITHM);
            cipher_symmetric.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher_symmetric.getIV();
            byte[] aad = generateRandomBytes(AAD_LENGTH);
            cipher_symmetric.updateAAD(aad);
            byte[] data_encryption = cipher_symmetric.doFinal(dataToEncrypt);


            // asymmetric encryption of secret key----------------------------------------------------
            final Cipher cipher_asymmetric = Cipher.getInstance(CRYPTO_ASYMMETRIC_ALGORITHM);
            cipher_asymmetric.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] key_encryption = cipher_asymmetric.doFinal(secretKey.getEncoded());

            // storing iv and encryption in encrypted_data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(key_encryption);
            outputStream.write(iv);
            outputStream.write(aad);
            outputStream.write(data_encryption);
            // need to add aad
            byte[] encrypted_key_iv_data = outputStream.toByteArray();


            cryptoResponseDto.setValue(base64encoder.encodeToString(encrypted_key_iv_data));
            return cryptoResponseDto;
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public CryptoResponseDto decrypt(CryptoRequestDto cryptoRequestDto) {
        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();
        byte[] dataToDecrypt =  base64decoder.decode(cryptoRequestDto.getValue());
        byte[] public_key =  base64decoder.decode(cryptoRequestDto.getPublicKey());

        byte[] encryptedSecretKey = Arrays.copyOfRange(dataToDecrypt, 0, KEYGEN_SYMMETRIC_KEY_LENGTH);
        byte[] iv = Arrays.copyOfRange(dataToDecrypt, KEYGEN_SYMMETRIC_KEY_LENGTH, KEYGEN_SYMMETRIC_KEY_LENGTH+IV_LENGTH);
        byte[] aad = Arrays.copyOfRange(dataToDecrypt,KEYGEN_SYMMETRIC_KEY_LENGTH+IV_LENGTH, KEYGEN_SYMMETRIC_KEY_LENGTH+IV_LENGTH+AAD_LENGTH);
        byte[] encrypted_data = Arrays.copyOfRange(dataToDecrypt, KEYGEN_SYMMETRIC_KEY_LENGTH+IV_LENGTH+AAD_LENGTH, dataToDecrypt.length);

        try {
            PrivateKey privateKey = getPrivateKey();

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
            String decrypted_data = base64encoder.encodeToString(cipher_symmetric.doFinal(encrypted_data));

            cryptoResponseDto.setValue(decrypted_data);
            return cryptoResponseDto;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public PublicKeyResponseDto getPublicKey(PublicKeyRequestDto publicKeyRequestDto) {
        PublicKeyResponseDto publicKeyResponseDto = new PublicKeyResponseDto();

        try {
            PublicKey publicKey = getPublicKey();
            String public_key = base64encoder.encodeToString(publicKey.getEncoded());
            publicKeyResponseDto.setPublicKey(public_key);
            return publicKeyResponseDto;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // get secret key from keystore
    private SecretKey getSecretKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(SECRET_ALIAS, null);
        if (!(entry instanceof KeyStore.SecretKeyEntry)) {
            return null;
        }
        return ((KeyStore.SecretKeyEntry) entry).getSecretKey();
    }

    // get private key from keystore
    private PrivateKey getPrivateKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(PRIVATE_ALIAS, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            return null;
        }
        return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
    }

    // get public key from keystore
    private PublicKey getPublicKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(PRIVATE_ALIAS, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            return null;
        }

        return ((KeyStore.PrivateKeyEntry) entry).getCertificate().getPublicKey();
    }

    //  random byte generation for AAD
    public static byte[] generateRandomBytes(int length) {

        SecureRandom secureRandom = new SecureRandom();

        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }



}