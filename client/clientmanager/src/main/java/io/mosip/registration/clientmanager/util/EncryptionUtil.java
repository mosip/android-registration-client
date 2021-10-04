package io.mosip.registration.clientmanager.util;

//import io.mosip.commons.khazana.constant.KhazanaConstant;

public class EncryptionUtil {

    public static byte[] mergeEncryptedData(byte[] encryptedData, byte[] nonce, byte[] aad) {
        byte[] finalEncData = new byte[encryptedData.length + 32 + 12];
        System.arraycopy(nonce, 0, finalEncData, 0, nonce.length);
        System.arraycopy(aad, 0, finalEncData, nonce.length, aad.length);
        System.arraycopy(encryptedData, 0, finalEncData, nonce.length + aad.length,	encryptedData.length);
        return finalEncData;
    }
}
