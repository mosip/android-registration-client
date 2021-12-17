package io.mosip.registration.packetmanager.util;

import java.util.Base64;

public class CryptoUtil {

    public static String encodeBase64(byte[] data) {
        return Base64.getUrlEncoder().encodeToString(data); //encodeBase64URLSafeString(data);
    }
}
