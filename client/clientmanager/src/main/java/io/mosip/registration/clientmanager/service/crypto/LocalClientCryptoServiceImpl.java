package io.mosip.registration.clientmanager.service.crypto;

import android.content.Context;

import java.security.*;
import javax.crypto.Cipher;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.crypto.*;
import io.mosip.registration.clientmanager.spi.crypto.ClientCryptoManagerService;
import io.mosip.registration.clientmanager.util.ConfigService;

@Singleton
public class LocalClientCryptoServiceImpl implements ClientCryptoManagerService {
    private static String ALGORITHM ;
    private static int KEY_LENGTH;
    private static String SIGN_ALGORITHM;
    private static String PRIVATE_KEY;
    private static String PUBLIC_KEY;


    LocalClientCryptoServiceImpl() {
        // get context from main activity

    }

    private void initializeClientSecurity() {
    }
    @Override
    public SignResponseDto sign(SignRequestDto signRequestDto) {
//        byte[] message;
//        PrivateKey key;
//        Signature s = null;
//        try {
//            s = Signature.getInstance("SHA256withRSA");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        s.initSign(key);
//        s.update(message);
//        byte[] signature = s.sign();
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
}
