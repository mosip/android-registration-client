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
public class LocalClientCryptoServiceImpl extends Service implements ClientCryptoManagerService {
    private static final String TAG = LocalClientCryptoServiceImpl.class.getSimpleName();
    private static Context context;

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

    private static String KEYS_DIR = "mosipkeys";

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
    public LocalClientCryptoServiceImpl() {
        Log.d(TAG, "LocalClientCryptoServiceImpl: Constructor call successful");
    }

    public void initLocalClientCryptoService() {
        context = getApplicationContext();
        initializeClientSecurity();

        try {
            // keys do not exist
            if(!doesKeysExists()) {
                Log.e(TAG, "LocalClientCryptoServiceImpl: Keys do not exist. Generating keys ");
                genSecretKey();
                genPrivPubKey();


            }
            else {
                Log.e(TAG, "LocalClientCryptoServiceImpl: Keys exist ");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        Log.e(TAG, "LocalClientCryptoServiceImpl: Initialization call successful");
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
        Log.d(TAG, "LocalClientCryptoServiceImpl: Initializating");
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
        KEYGEN_ASYMMETRIC_ALGO_BLOCK=KeyProperties.BLOCK_MODE_ECB;
        KEYGEN_ASYMMETRIC_ALGO_PAD=KeyProperties.ENCRYPTION_PADDING_RSA_OAEP;

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                    KEYGEN_ASYMMETRIC_ALGORITHM, ANDROID_KEY_STORE);

            final KeyGenParameterSpec keyPairGenParameterSpec = new KeyGenParameterSpec.Builder(
                    SECRET_ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KEYGEN_ASYMMETRIC_ALGO_BLOCK)
                    .setKeySize(KEYGEN_ASYMMETRIC_KEY_LENGTH)
                    .setEncryptionPaddings(KEYGEN_ASYMMETRIC_ALGO_PAD)
//                    .setUserAuthenticationRequired(true)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .build();

            kpg.initialize(keyPairGenParameterSpec);
            KeyPair kp = kpg.generateKeyPair();

            PublicKey publicKey = kp.getPublic();

            Log.e(TAG, "genSecretKey: Generated private key successfully " + kp.getPublic());

            KeyGenerator keyGen = KeyGenerator.getInstance(KEYGEN_SYMMETRIC_ALGORITHM);
            keyGen.init(KEYGEN_SYMMETRIC_KEY_LENGTH);
            SecretKey mosipSecretKey = keyGen.generateKey();

            Log.e(TAG, "genSecretKey: Generated mosip key successfully " + mosipSecretKey.getEncoded());

            final Cipher cipher_asymmetric = Cipher.getInstance(CRYPTO_ASYMMETRIC_ALGORITHM);
            OAEPParameterSpec spec = new OAEPParameterSpec(
                    "SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
            cipher_asymmetric.init(Cipher.ENCRYPT_MODE, publicKey, spec);

            byte[] secretKeyEncryption = cipher_asymmetric.doFinal(mosipSecretKey.getEncoded());
            Log.e(TAG, "genSecretKey: Generated secret key encryption");

            Log.e(TAG, "getSecretKey: Key length " + (secretKeyEncryption.length));
            createKeyFile(getKeysDirPath(), secretKeyEncryption);

            Log.e(TAG, "genSecretKey: Generated secret key successfully ");

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void genPrivPubKey() {

        KEYGEN_ASYMMETRIC_ALGO_BLOCK=KeyProperties.BLOCK_MODE_ECB;
        KEYGEN_ASYMMETRIC_ALGO_PAD=KeyProperties.ENCRYPTION_PADDING_RSA_OAEP;

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
//                    .setUserAuthenticationRequired(true)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .build();

            kpg.initialize(keyPairGenParameterSpec);
            KeyPair kp = kpg.generateKeyPair();
            Log.e(TAG, "genPrivateKey: Generated private key successfully " + base64encoder.encodeToString(kp.getPublic().getEncoded()));

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
            Log.e(TAG, "encrypt: Read public key obj");
            SecretKey mosipSecretKey = getSecretKey();
            Log.e(TAG, "encrypt: Read mosip key successfully " + mosipSecretKey.getEncoded());
            // symmetric encryption of data-----------------------------------------------------
            final Cipher cipher_symmetric = Cipher.getInstance(CRYPTO_SYMMETRIC_ALGORITHM);

            cipher_symmetric.init(Cipher.ENCRYPT_MODE, mosipSecretKey);
            byte[] iv = cipher_symmetric.getIV();
            byte[] aad = generateRandomBytes(AAD_LENGTH);
            cipher_symmetric.updateAAD(aad);
            byte[] data_encryption = cipher_symmetric.doFinal(dataToEncrypt);
            Log.e(TAG, "encrypt: Generated message encryption" + data_encryption);

            // asymmetric encryption of secret key----------------------------------------------------
            final Cipher cipher_asymmetric = Cipher.getInstance(CRYPTO_ASYMMETRIC_ALGORITHM);
            OAEPParameterSpec spec = new OAEPParameterSpec(
                    "SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
            cipher_asymmetric.init(Cipher.ENCRYPT_MODE, publicKey,spec);
            byte[] key_encryption = cipher_asymmetric.doFinal(mosipSecretKey.getEncoded());
            Log.e(TAG, "encrypt: Generated secret key encryption");

            // storing iv and encryption in encrypted_data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(key_encryption);
            outputStream.write(iv);
            outputStream.write(aad);
            outputStream.write(data_encryption);
            // need to add aad
            byte[] encrypted_key_iv_data = outputStream.toByteArray();

            Log.e(TAG, "encrypt: Generated encrypted data");
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
            Log.e(TAG, "getPublicKey: Public key accessed " + public_key);
            publicKeyResponseDto.setPublicKey(public_key);
            return publicKeyResponseDto;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private boolean doesKeysExists() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);

        // check if secret key exists
        KeyStore.Entry entry = ks.getEntry(SECRET_ALIAS, null);
        if (!(entry instanceof KeyStore.SecretKeyEntry)) {
            return false;
        }
        Log.e(TAG, "doesKeysExists: Secret key exists");

        // check if private key exists
        entry = ks.getEntry(PRIVATE_ALIAS, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            return false;
        }
        Log.e(TAG, "doesKeysExists: Private key exists");

        File file = new File(context.getFilesDir(),getKeysDirPath());
        if(!file.exists()) {
            file.createNewFile();
            Log.e(TAG, "doesKeysExists: File created...");
            return false;
        }
        Log.e(TAG, "doesKeysExists: Mosip key exists");
        return true;
    }

    private String getKeysDirPath() {
//        return KEYS_DIR + File.separator + SECRET_ALIAS;
        return SECRET_ALIAS;
    }

    // create a key file to store the encrypted secret key
    private void createKeyFile(String keyDir, byte[] key) throws IOException {

        String keyEncoded = Base64.getEncoder().encodeToString(key);
        try {
            File file = new File(context.getFilesDir(), getKeysDirPath());
            if(!file.exists()) {
                try {
                    file.createNewFile();
                    Log.e(TAG, "createKeyFile: File");
                } catch (AccessDeniedException ex) {
                    Log.e(TAG, "createKeyFile: Access Denied", ex);
                }
            }
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(keyEncoded.getBytes());
            } finally {
                stream.close();
            }

            Log.e(TAG, "createKeyFile: key " + keyEncoded);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // get secret key from keystore
    private SecretKey getSecretKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        PrivateKey privateKey = (PrivateKey) ks.getKey(SECRET_ALIAS, null);

        String keyEncoded = "";
        File file = new File(context.getFilesDir(), getKeysDirPath());
        if(file.exists()) {
            int length = (int) file.length();
            byte[] bytes = new byte[length];
            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }
            keyEncoded += new String(bytes);
            Log.e(TAG, "createKeyFile: key " + keyEncoded);
        }
        else {
            Log.e(TAG, "getSecretKey: File not exists");
        }

        byte[] secretKeyEncryption = base64decoder.decode(keyEncoded);
        Log.e(TAG, "getSecretKey: Key length " + (secretKeyEncryption.length));


        final Cipher cipher_asymmetric = Cipher.getInstance(CRYPTO_ASYMMETRIC_ALGORITHM);
        cipher_asymmetric.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] secretKeyBytes = cipher_asymmetric.doFinal(secretKeyEncryption);
        Log.e(TAG, "getSecretKey: secretKeyBytes " + secretKeyBytes);
        return new SecretKeySpec(secretKeyBytes, KEYGEN_SYMMETRIC_ALGORITHM);
    }

    // get private key from keystore
    private PrivateKey getPrivateKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        return  (PrivateKey) ks.getKey(PRIVATE_ALIAS, null);
    }

    // get public key from keystore
    private PublicKey getPublicKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
        ks.load(null);
        return (PublicKey) ks.getCertificate(PRIVATE_ALIAS).getPublicKey();
    }

    //  random byte generation for AAD
    public static byte[] generateRandomBytes(int length) {

        SecureRandom secureRandom = new SecureRandom();

        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }



}