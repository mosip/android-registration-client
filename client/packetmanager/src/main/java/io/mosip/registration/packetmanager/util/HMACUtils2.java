package io.mosip.registration.packetmanager.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HMACUtils2 {

    private static final String HASH_ALGORITHM_NAME = "SHA-256";
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static byte[] generateHash(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM_NAME);
        return messageDigest.digest(bytes);
    }

    public static String digestAsPlainText(byte[] bytes) throws NoSuchAlgorithmException {
        return printHexBinary(generateHash(bytes)).toUpperCase();
    }

    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

}
