package io.mosip.registration.keymanager.service;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import io.mosip.registration.keymanager.dto.*;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.ConfigService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.keymanager.util.KeyManagerErrorCode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.mosip.registration.keymanager.util.KeyManagerConstant.*;


/**
 * @author George T Abraham
 * @author Eric John
 * @author Anusha
 */
@Singleton
public class LocalClientCryptoServiceImpl implements ClientCryptoManagerService {

    private static final String TAG = LocalClientCryptoServiceImpl.class.getSimpleName();
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private Context context;

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

    private static String CERTIFICATE_SIGN_ALGORITHM;

    private static SecureRandom secureRandom = new SecureRandom();
    private KeyStore keyStore = null;


    @Inject
    public LocalClientCryptoServiceImpl(Context appContext) {
        Log.i(TAG, "LocalClientCryptoServiceImpl: Constructor call successful");
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
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
        KEYGEN_ASYMMETRIC_ALGO_SIGN_PAD = ConfigService.getProperty("mosip.kernel.crypto.sign-algorithm-padding-scheme",context);
        CERTIFICATE_SIGN_ALGORITHM = ConfigService.getProperty("mosip.kernel.certificate.sign.algorithm",context);
    }

    private void genSignKey() {
        try {
            final KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(SIGNV_ALIAS, null);

            if(keyEntry == null) {
                final KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                        KEYGEN_ASYMMETRIC_ALGORITHM, ANDROID_KEY_STORE);
                final KeyGenParameterSpec keyPairGenParameterSpec = new KeyGenParameterSpec.Builder(
                         SIGNV_ALIAS, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                        .setKeySize(KEYGEN_ASYMMETRIC_KEY_LENGTH)
                        .setSignaturePaddings(KEYGEN_ASYMMETRIC_ALGO_SIGN_PAD)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .build();
                kpg.initialize(keyPairGenParameterSpec);
                kpg.generateKeyPair();
                Log.i(TAG, "genSignKey: Initialized the machine signing key");
            }
        } catch(Exception e){
            Log.e(TAG, "genSignKey: Sign key generation failed ", e);
            if(e  instanceof  UnrecoverableKeyException) {
                try {
                    keyStore.deleteEntry(SIGNV_ALIAS);
                } catch (KeyStoreException ex) {
                    Log.e(TAG, "genSignKey: Entry deletion also failed", e);
                }
            }
        }
    }

    private void genEnDecKey() {
        try {
            final KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(ENCDEC_ALIAS, null);

            if(keyEntry == null) {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                        KEYGEN_ASYMMETRIC_ALGORITHM, ANDROID_KEY_STORE);

                final KeyGenParameterSpec keyPairGenParameterSpec = new KeyGenParameterSpec.Builder(
                         ENCDEC_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KEYGEN_ASYMMETRIC_ALGO_BLOCK)
                        .setKeySize(KEYGEN_ASYMMETRIC_KEY_LENGTH)
                        .setEncryptionPaddings(KEYGEN_ASYMMETRIC_ALGO_PAD)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .build();

                kpg.initialize(keyPairGenParameterSpec);
                kpg.generateKeyPair();
                Log.i(TAG, "genEncDecKey: Initialized the machine crypto key");
           }
        } catch(Exception e){
            Log.e(TAG, "genEncDecKey: Crypto key generation failed ", e);
            if(e  instanceof  UnrecoverableKeyException) {
                try {
                    keyStore.deleteEntry(ENCDEC_ALIAS);
                } catch (KeyStoreException ex) {
                    Log.e(TAG, "genEncDecKey: Entry deletion also failed", e);
                }
            }
        }
    }


    @Override
    public SignResponseDto sign(SignRequestDto signRequestDto) {
        SignResponseDto signResponseDto = new SignResponseDto();

        byte[] dataToSign = CryptoUtil.base64decoder.decode(signRequestDto.getData());
        try {
            PrivateKey privateKey = getSignPrivateKey();
            Signature sign = Signature.getInstance(CERTIFICATE_SIGN_ALGORITHM);
            sign.initSign(privateKey);
            sign.update(dataToSign);
            byte[] signedData = sign.sign();

            signResponseDto.setData(CryptoUtil.base64encoder.encodeToString(signedData));
            return signResponseDto;

        } catch (Exception e) {
            Log.e(TAG, KeyManagerErrorCode.SIGNATURE_EXCEPTION.getErrorMessage(), e);
        }
        return null;
    }

    @Override
    public SignVerifyResponseDto verifySign(SignVerifyRequestDto signVerifyRequestDto) {
        SignVerifyResponseDto signVerifyResponseDto = new SignVerifyResponseDto();
        try {
            byte[] public_key = CryptoUtil.base64decoder.decode(signVerifyRequestDto.getPublicKey());
            byte[] signature = CryptoUtil.base64decoder.decode(signVerifyRequestDto.getSignature());
            byte[] actualData = CryptoUtil.base64decoder.decode(signVerifyRequestDto.getData().getBytes());

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(public_key);
            KeyFactory kf = KeyFactory.getInstance(KEYGEN_ASYMMETRIC_ALGORITHM);
            PublicKey publicKey = kf.generatePublic(keySpec);

            Signature sign = Signature.getInstance(CERTIFICATE_SIGN_ALGORITHM);
            sign.initVerify(publicKey);
            sign.update(actualData);
            signVerifyResponseDto.setVerified(sign.verify(signature));
            return signVerifyResponseDto;
        } catch (Exception e) {
            Log.e(TAG, KeyManagerErrorCode.SIGNATURE_EXCEPTION.getErrorMessage(), e);
        }
        return null;
    }

    @Override
    public CryptoResponseDto encrypt(CryptoRequestDto cryptoRequestDto) {
        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();

        try {
            byte[] dataToEncrypt = CryptoUtil.base64decoder.decode(cryptoRequestDto.getValue().getBytes());

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
            byte[] key_encryption = asymmetricEncrypt(mosipSecretKey.getEncoded());

            // constructing key, iv, add and encryption stream--------------------------------------
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(key_encryption);
            outputStream.write(iv);
            outputStream.write(aad);
            outputStream.write(data_encryption);

            byte[] encrypted_key_iv_data = outputStream.toByteArray();

            cryptoResponseDto.setValue(CryptoUtil.base64encoder.encodeToString(encrypted_key_iv_data));
            return cryptoResponseDto;
        } catch (Exception e) {
            Log.e(TAG, KeyManagerErrorCode.CRYPTO_EXCEPTION.getErrorMessage(), e);
        }
        return null;
    }


    @Override
    public CryptoResponseDto decrypt(CryptoRequestDto cryptoRequestDto) {
        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();

        try {
            byte[] dataToDecrypt = CryptoUtil.base64decoder.decode(cryptoRequestDto.getValue());
            byte[] encryptedSecretKey = Arrays.copyOfRange(dataToDecrypt, 0, KEYGEN_SYMMETRIC_KEY_LENGTH);
            byte[] iv = Arrays.copyOfRange(dataToDecrypt, KEYGEN_SYMMETRIC_KEY_LENGTH, KEYGEN_SYMMETRIC_KEY_LENGTH+CRYPTO_SYMMETRIC_IV_LENGTH);
            byte[] aad = Arrays.copyOfRange(dataToDecrypt,KEYGEN_SYMMETRIC_KEY_LENGTH+CRYPTO_SYMMETRIC_IV_LENGTH, KEYGEN_SYMMETRIC_KEY_LENGTH+CRYPTO_SYMMETRIC_IV_LENGTH+CRYPTO_SYMMETRIC_AAD_LENGTH);
            byte[] encrypted_data = Arrays.copyOfRange(dataToDecrypt, KEYGEN_SYMMETRIC_KEY_LENGTH+CRYPTO_SYMMETRIC_IV_LENGTH+CRYPTO_SYMMETRIC_AAD_LENGTH, dataToDecrypt.length);

            // asymmetric decryption of secret key----------------------------------------------------
            byte[] secretKeyBytes = asymmetricDecrypt(encryptedSecretKey);

            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, KEYGEN_SYMMETRIC_ALGORITHM);
            // symmetric decryption of data-----------------------------------------------------
            final Cipher cipher_symmetric = Cipher.getInstance(CRYPTO_SYMMETRIC_ALGORITHM);
            final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(CRYPTO_GCM_TAG_LENGTH, iv);
            cipher_symmetric.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            cipher_symmetric.updateAAD(aad);
            byte[] plainBytes = cipher_symmetric.doFinal(encrypted_data);
            cryptoResponseDto.setValue(CryptoUtil.base64encoder.encodeToString(plainBytes));
            return cryptoResponseDto;
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public PublicKeyResponseDto getPublicKey(PublicKeyRequestDto publicKeyRequestDto) {
        PublicKeyResponseDto publicKeyResponseDto = new PublicKeyResponseDto();
        try {
            String requestedAlias = publicKeyRequestDto.getAlias();
            if(SIGNV_ALIAS.equals(requestedAlias)) {
                PublicKey publicKey = getSignPublicKey();
                publicKeyResponseDto.setPublicKey(CryptoUtil.base64encoder.encodeToString(publicKey.getEncoded()));
                return publicKeyResponseDto;
            }

            if(ENCDEC_ALIAS.equals(requestedAlias)) {
                PublicKey publicKey = getEnDecPublicKey();
                publicKeyResponseDto.setPublicKey(CryptoUtil.base64encoder.encodeToString(publicKey.getEncoded()));
                return publicKeyResponseDto;
            }
        } catch (Exception e) {
            Log.e(TAG, KeyManagerErrorCode.KEY_STORE_EXCEPTION.getErrorMessage(), e);
        }
        return null;
    }

    @Override
    public String getMachineName() {
        String name = getAppConf(APP_CONF_KEY_MACHINE_NAME);
        if(name == null) {
            saveAppConf(APP_CONF_KEY_MACHINE_NAME, RandomStringUtils.random(12, true, true));
        }
        return getAppConf(APP_CONF_KEY_MACHINE_NAME);
    }

    private byte[] asymmetricEncrypt(byte[] data) throws Exception {
        final Cipher cipher = Cipher.getInstance(CRYPTO_ASYMMETRIC_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getEnDecPublicKey(), new OAEPParameterSpec(
                CRYPTO_ASYMMETRIC_ALGO_MD, CRYPTO_ASYMMETRIC_ALGO_MGF, MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT));
        return cipher.doFinal(data);
    }

    private byte[] asymmetricDecrypt(byte[] dataToDecrypt) throws Exception {
        final Cipher cipher = Cipher.getInstance(CRYPTO_ASYMMETRIC_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getEnDecPrivateKey(), new OAEPParameterSpec(
                CRYPTO_ASYMMETRIC_ALGO_MD, CRYPTO_ASYMMETRIC_ALGO_MGF, MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT));
        return cipher.doFinal(dataToDecrypt);
    }


    private void saveAppConf(String entryName, String entryValue) {
        try {
            File dir = new File(context.getDataDir(), APP_CONF_DIR);
            if (!dir.exists())
                dir.mkdir();

            File file = new File(dir, APP_CONF);
            JSONObject jsonObject = new JSONObject();
            if(file.exists()) {
                try (FileReader fileReader = new FileReader(file)) {
                    String content = IOUtils.toString(fileReader);
                    byte[] appConfBytes = asymmetricDecrypt(CryptoUtil.base64decoder.decode(content));
                    jsonObject = new JSONObject(new String(appConfBytes));
                } catch (IllegalBlockSizeException ex) {
                    Log.e(TAG, "Failed to decrypt the app name, deleting the file", ex);
                    file.delete();
                }
            }

            jsonObject.put(entryName, entryValue);
            String appConf = jsonObject.toString();
            byte[] encrypted = asymmetricEncrypt(appConf.getBytes(StandardCharsets.UTF_8));
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(CryptoUtil.base64encoder.encodeToString(encrypted));
            }
        } catch (Exception e) {
            Log.e(TAG, KeyManagerErrorCode.KEY_STORE_EXCEPTION.getErrorMessage(), e);
        }
    }

    private String getAppConf(String entryName) {
        try {
            File dir = new File(context.getDataDir(), APP_CONF_DIR);
            if(!dir.exists())
                return null;

            File file = new File(dir, APP_CONF);
            if(!file.exists())
                return null;

            try(FileReader fileReader = new FileReader(file)) {
                String content = IOUtils.toString(fileReader);
                byte[] appConfBytes = asymmetricDecrypt(CryptoUtil.base64decoder.decode(content));
                JSONObject jsonObject = new JSONObject(new String(appConfBytes));
                return jsonObject.has(entryName) ? jsonObject.getString(entryName) : null;
            }
        } catch (Exception e) {
            Log.e(TAG, KeyManagerErrorCode.KEY_STORE_EXCEPTION.getErrorMessage(), e);
        }
       return null;
    }

    @Override
    public String getClientKeyIndex() throws Exception {
        return CryptoUtil.computeFingerPrint(getEnDecPublicKey().getEncoded(), null);
    }

    @Override
    public Map<String, String> getMachineDetails() {
        Map<String, String> map = new HashMap<>();
        try {
            map.put("name", getMachineName());
            map.put("publicKey", CryptoUtil.base64encoder.encodeToString(getEnDecPublicKey().getEncoded()));
            map.put("signPublicKey", CryptoUtil.base64encoder.encodeToString(getSignPublicKey().getEncoded()));
        } catch (Exception e) {
            Log.e(TAG, KeyManagerErrorCode.KEY_STORE_EXCEPTION.getErrorMessage(), e);
        }
        return map;
    }

    private PrivateKey getEnDecPrivateKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
        return  (PrivateKey) keyStore.getKey(ENCDEC_ALIAS, null);
    }

    private PublicKey getEnDecPublicKey() throws KeyStoreException {
        return (PublicKey) keyStore.getCertificate(ENCDEC_ALIAS).getPublicKey();
    }

    private PrivateKey getSignPrivateKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
        return  (PrivateKey) keyStore.getKey(SIGNV_ALIAS, null);
    }

    private PublicKey getSignPublicKey() throws KeyStoreException {
        return (PublicKey) keyStore.getCertificate(SIGNV_ALIAS).getPublicKey();
    }

    //  random byte generation
    public static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }
}
