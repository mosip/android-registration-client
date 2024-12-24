package io.mosip.registration.packetmanager.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class CryptoUtilTest {

    @Test
    public void testEncodeToURLSafeBase64() {
        byte[] data = "test".getBytes(StandardCharsets.UTF_8);
        String encoded = CryptoUtil.encodeToURLSafeBase64(data);
        assertNotNull(encoded);
        assertEquals("dGVzdA", encoded);
    }

    @Test
    public void testEncodeToURLSafeBase64WithNull() {
        assertNull(CryptoUtil.encodeToURLSafeBase64(null));
    }

    @Test
    public void testDecodeURLSafeBase64() {
        String encoded = "dGVzdA";
        byte[] decoded = CryptoUtil.decodeURLSafeBase64(encoded);
        assertNotNull(decoded);
        assertArrayEquals("test".getBytes(StandardCharsets.UTF_8), decoded);
    }

    @Test
    public void testDecodeURLSafeBase64WithNull() {
        assertNull(CryptoUtil.decodeURLSafeBase64(null));
    }

    @Test
    public void testDecodeBase64() {
        String encoded = Base64.getEncoder().encodeToString("example".getBytes(StandardCharsets.UTF_8));
        byte[] decoded = CryptoUtil.decodeBase64(encoded);
        assertNotNull(decoded);
        assertArrayEquals("example".getBytes(StandardCharsets.UTF_8), decoded);
    }

    @Test
    public void testEncodeBase64String() {
        byte[] data = "example".getBytes(StandardCharsets.UTF_8);
        String encoded = CryptoUtil.encodeBase64String(data);
        assertNotNull(encoded);
        assertEquals(Base64.getEncoder().encodeToString(data), encoded); // Ensure match with standard encoder
    }
}
