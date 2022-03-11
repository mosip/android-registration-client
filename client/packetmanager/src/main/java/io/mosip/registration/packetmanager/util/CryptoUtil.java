package io.mosip.registration.packetmanager.util;

import java.util.Base64;
import java.util.Base64.Encoder;

public class CryptoUtil {

    private static Encoder urlSafeEncoder;


    static {
        urlSafeEncoder = Base64.getUrlEncoder().withoutPadding();
    }

    public static String encodeToURLSafeBase64(byte[] data) {
        if (data == null) {
            return null;
        }
        return urlSafeEncoder.encodeToString(data);
    }

    public static byte[] decodeURLSafeBase64(String data) {
        if (data == null) {
            return null;
        }
        return Base64.getUrlDecoder().decode(data);
    }

    public static byte[] decodeBase64(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static String encodeBase64String(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

}
