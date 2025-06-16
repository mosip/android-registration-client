package io.mosip.registration.keymanager.util;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Base64;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CryptoUtilTest {

    private static byte[] sampleData;
    private static String sampleMetaData;

    @BeforeClass
    public static void setUp() {
        sampleData = "testdata".getBytes();
        sampleMetaData = "meta";
    }

    @Test
    public void testComputeFingerPrint_withMetaData() {
        String result = CryptoUtil.computeFingerPrint(sampleData, sampleMetaData);
        assertNotNull(result);
        assertTrue(result.contains(":"));
    }

    @Test
    public void testComputeFingerPrint_withoutMetaData() {
        String result = CryptoUtil.computeFingerPrint(sampleData, null);
        assertNotNull(result);
        assertTrue(result.contains(":"));
    }

    @Test
    public void testComputeFingerPrint_withEmptyMetaData() {
        String result = CryptoUtil.computeFingerPrint(sampleData, "");
        assertNotNull(result);
        assertTrue(result.contains(":"));
    }

    @Test
    public void testGenerateMD5Hash() {
        String hash = CryptoUtil.generateMD5Hash(sampleData);
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    public void testDecodeBase64_valid() {
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(sampleData);
        byte[] decoded = CryptoUtil.decodeBase64(encoded);
        assertArrayEquals(sampleData, decoded);
    }

    @Test
    public void testDecodeBase64_blank() {
        assertNull(CryptoUtil.decodeBase64(""));
        assertNull(CryptoUtil.decodeBase64("   "));
        assertNull(CryptoUtil.decodeBase64(null));
    }

    @Test
    public void testDecodeBase64_invalidFallback() {
        // purposely use standard Base64 (not URL-safe) to trigger fallback
        String encoded = Base64.getEncoder().encodeToString(sampleData);
        byte[] decoded = CryptoUtil.decodeBase64(encoded);
        assertArrayEquals(sampleData, decoded);
    }

    @Test
    public void testEncodeToURLSafeBase64_valid() {
        String encoded = CryptoUtil.encodeToURLSafeBase64(sampleData);
        String expectedUnpadded = Base64.getUrlEncoder().withoutPadding().encodeToString(sampleData);
        String expectedPadded = Base64.getUrlEncoder().encodeToString(sampleData);
        assertNotNull(encoded);
        assertTrue(
                encoded.equals(expectedUnpadded) ||
                        encoded.equals(expectedPadded)
        );
    }

    @Test
    public void testEncodeToURLSafeBase64_null() {
        assertNull(CryptoUtil.encodeToURLSafeBase64(null));
    }
}