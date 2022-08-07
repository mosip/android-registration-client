package io.mosip.registration.keymanager.util;

import android.util.Log;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Base64;

public class CryptoUtil {

    private static final String TAG = CryptoUtil.class.getSimpleName();
    private static final String SHA2_ALGORITHM_NAME = "SHA-256";
    private static final String MD5_ALGORITHM_NAME = "SHA-256";
    private static MessageDigest messageDigest;
    private static MessageDigest messageDigestMD5;

    public static Base64.Encoder base64encoder = Base64.getUrlEncoder().withoutPadding();
    public static Base64.Decoder base64decoder = Base64.getUrlDecoder();

    static {
        try {
            messageDigest = messageDigest != null ? messageDigest : MessageDigest.getInstance(SHA2_ALGORITHM_NAME);
            messageDigestMD5 = messageDigestMD5 != null ? messageDigestMD5 : MessageDigest.getInstance(MD5_ALGORITHM_NAME);
        } catch (java.security.NoSuchAlgorithmException exception) {
            Log.e(TAG, "Failed to create messageDigest instance", exception);
        }
    }

    public static String computeFingerPrint(byte[] data, String metaData) {
        byte[] combinedPlainTextBytes = null;
        if (metaData == null || metaData.trim().isEmpty()) {
            combinedPlainTextBytes = ArrayUtils.addAll(data);
        } else {
            combinedPlainTextBytes = ArrayUtils.addAll(data, metaData.getBytes());
        }
        String hexEncodedString = new String(Hex.encodeHex(messageDigest.digest(combinedPlainTextBytes)));
        return hexEncodedString.replaceAll("..(?!$)", "$0:");
    }

    public static String generateMD5Hash(byte[] data) {
        return new String(Hex.encodeHex(messageDigestMD5.digest(data)));
    }

}
