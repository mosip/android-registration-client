package io.mosip.registration.clientmanager.service.crypto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.crypto.*;
import io.mosip.registration.clientmanager.spi.crypto.ClientCryptoManagerService;
import io.mosip.registration.clientmanager.util.ConfigService;

@Singleton
public class LocalClientCryptoServiceImpl extends Service implements ClientCryptoManagerService {

    private static Context context;
    private static String ALGORITHM;
    private static int KEY_LENGTH;
    private static String SIGN_ALGORITHM;
    private static String PRIVATE_KEY = "reg.key";
    private static String PUBLIC_KEY = "reg.pub";
    private static Encoder base64encoder;
    private static Decoder base64decoder;



    @Inject
    LocalClientCryptoServiceImpl() {
        try {
            if (!doesKeysExists()) {
                setupKeysDir();
                KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ALGORITHM);
                keyGenerator.initialize(KEY_LENGTH, new SecureRandom());
                KeyPair keypair = keyGenerator.generateKeyPair();
                createKeyFile(PRIVATE_KEY, keypair.getPrivate().getEncoded());
                createKeyFile(PUBLIC_KEY, keypair.getPublic().getEncoded());
            }
        } catch (Exception ex) {
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
        Context context = getApplicationContext();
        ALGORITHM = ConfigService.getProperty("mosip.kernel.keygenerator.asymmetric-algorithm-name",context);
        KEY_LENGTH = Integer.parseInt(ConfigService.getProperty("mosip.kernel.keygenerator.asymmetric-key-length",context));
        SIGN_ALGORITHM = ConfigService.getProperty("mosip.kernel.certificate.sign.algorithm", context);
        base64encoder = Base64.getEncoder();
        base64decoder = Base64.getDecoder();



    }
    @Override
    public SignResponseDto sign(SignRequestDto signRequestDto) {
        byte[] dataToSign = base64decoder.decode(signRequestDto.getData());
        try {
            Signature sign = Signature.getInstance(SIGN_ALGORITHM);
            sign.initSign(getPrivateKey());

            try(ByteArrayInputStream in = new ByteArrayInputStream(dataToSign)) {
                byte[] buffer = new byte[2048];
                int len = 0;
                while((len = in.read(buffer)) != -1) {
                    sign.update(buffer, 0, len);
                }
                byte[] signedData = sign.sign();

                SignResponseDto signResponseDto = new SignResponseDto();
                signResponseDto.setData(base64encoder.encodeToString(signedData));
                return signResponseDto;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public SignVerifyResponseDto verifySign(SignVerifyRequestDto signVerifyRequestDto) {
//        byte[] message;
//        byte[] signature;
//        PublicKey key;
//        Signature s = null;
//        try {
//            s = Signature.getInstance("SHA256withRSA");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        s.initVerify(key);
//        s.update(message);
//        boolean valid = s.verify(signature);
        return null;
    }

    @Override
    public CryptoResponseDto encrypt(CryptoRequestDto cryptoRequestDto) {
//        byte[] message;
//        byte[] signature;
//        Signature sign = Signature.getInstance("SHA256withRSA");
//
//        //Creating KeyPair generator object
//        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
//
//        //Initializing the key pair generator
//        keyPairGen.initialize(2048);
//
//        //Generate the pair of keys
//        KeyPair pair = keyPairGen.generateKeyPair();
//
//        //Getting the public key from the key pair
//        PublicKey publicKey = pair.getPublic();
//
//        //Creating a Cipher object
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//
//        //Initializing a Cipher object
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//
//        //Add data to the cipher
//        byte[] input = "Welcome to Tutorialspoint".getBytes();
//        cipher.update(input);
//
//        //encrypting the data
//        byte[] cipherText = cipher.doFinal();
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

    private void setupKeysDir() {
        File keysDir = new File(getKeysDirPath());
        keysDir.mkdirs();
    }

    private boolean doesKeysExists() {
        File keysDir = new File(getKeysDirPath());
        return (keysDir.exists() && Objects.requireNonNull(keysDir.list()).length >= 2);
    }

    private String getKeysDirPath() {
        return System.getProperty("user.dir") + File.separator + ".mosipkeys";
    }

    private void createKeyFile(String fileName, byte[] key) throws IOException {
        try(FileOutputStream os =
                    new FileOutputStream(getKeysDirPath() + File.separator + fileName)) {
            os.write(key);
        }
    }

    private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] key = Files.readAllBytes(Paths.get(getKeysDirPath() + File.separator + PRIVATE_KEY));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        return kf.generatePrivate(keySpec);
    }

    private PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] key = Files.readAllBytes(Paths.get(getKeysDirPath() + File.separator + PUBLIC_KEY));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        return kf.generatePublic(keySpec);
    }

}
