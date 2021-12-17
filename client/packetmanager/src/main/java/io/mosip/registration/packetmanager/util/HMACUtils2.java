package io.mosip.registration.packetmanager.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.datatype.DatatypeFactory;

public class HMACUtils2 {

    private static final String HASH_ALGORITHM_NAME = "SHA-256";

    public static byte[] generateHash(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        return messageDigest.digest(bytes);
    }

    public static String digestAsPlainText(byte[] bytes) throws NoSuchAlgorithmException {
        //TODO find alternative for datatype converter
        //return DatatypeConverter.printHexBinary(generateHash(bytes)).toUpperCase();
        return "";
    }

}
