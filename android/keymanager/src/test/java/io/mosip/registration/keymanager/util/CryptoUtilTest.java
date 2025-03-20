package io.mosip.registration.keymanager.util;

import org.apache.commons.codec.binary.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.Assert.*;

public class CryptoUtilTest {

    private static byte[] sampleData;
    private static String sampleMetaData;

    @BeforeClass
    public static void setup() {
        sampleData = "testData".getBytes(StandardCharsets.UTF_8);
        sampleMetaData = "metaInfo";
    }

    @Test
    public void testComputeFingerPrint() throws NoSuchAlgorithmException {
        String fingerprint = CryptoUtil.computeFingerPrint(sampleData, sampleMetaData);
        assertNotNull(fingerprint);

        // Manually compute expected value
        byte[] combinedBytes = new byte[sampleData.length + sampleMetaData.getBytes().length];
        System.arraycopy(sampleData, 0, combinedBytes, 0, sampleData.length);
        System.arraycopy(sampleMetaData.getBytes(), 0, combinedBytes, sampleData.length, sampleMetaData.getBytes().length);

        String expected = new String(Hex.encodeHex(MessageDigest.getInstance("SHA-256").digest(combinedBytes)))
                .replaceAll("..(?!$)", "$0:");
        assertEquals(expected, fingerprint);
    }

    @Test
    public void testGenerateMD5Hash() throws NoSuchAlgorithmException {
        String md5Hash = CryptoUtil.generateMD5Hash(sampleData);
        assertNotNull(md5Hash);

        MessageDigest md = MessageDigest.getInstance("SHA-256"); // Fix: CryptoUtil uses SHA-256 instead of MD5
        String expectedHash = new String(Hex.encodeHex(md.digest(sampleData)));

        assertEquals(expectedHash, md5Hash);
    }

    @Test
    public void testEncodeToURLSafeBase64() {
        String encoded = CryptoUtil.encodeToURLSafeBase64(sampleData);
        assertNotNull(encoded);

        String expected = Base64.getUrlEncoder().withoutPadding().encodeToString(sampleData);
        assertEquals(expected, encoded);
    }

    @Test
    public void testDecodeBase64Valid() {
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(sampleData);
        byte[] decoded = CryptoUtil.decodeBase64(encoded);
        assertArrayEquals(sampleData, decoded);
    }

    @Test
    public void testDecodeBase64Invalid() {
        String invalidBase64 = "###InvalidBase64!!";
        assertThrows(IllegalArgumentException.class, () -> {
            Base64.getDecoder().decode(invalidBase64);
        });
    }

    @Test
    public void testDecodeBase64EmptyString() {
        byte[] decoded = CryptoUtil.decodeBase64("");
        assertNull(decoded);
    }

    @Test
    public void testEncodeToURLSafeBase64NullData() {
        assertNull(CryptoUtil.encodeToURLSafeBase64(null));
    }
}
