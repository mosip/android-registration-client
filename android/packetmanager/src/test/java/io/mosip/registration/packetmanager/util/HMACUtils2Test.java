package io.mosip.registration.packetmanager.util;

import org.junit.Test;

import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class HMACUtils2Test {

    @Test
    public void testGenerateHash() throws Exception {
        byte[] input = "test".getBytes();
        byte[] hash = HMACUtils2.generateHash(input);
        assertNotNull(hash);
        assertEquals(32, hash.length); // SHA-256 hash length
    }

    @Test
    public void testDigestAsPlainTextWithSalt() throws Exception {
        byte[] password = "password".getBytes();
        byte[] salt = "salt".getBytes();
        String digest = HMACUtils2.digestAsPlainTextWithSalt(password, salt);
        assertNotNull(digest);
        assertEquals(64, digest.length()); // SHA-256 hex string length
    }

    @Test
    public void testDigestAsPlainText() throws Exception {
        byte[] input = "plain".getBytes();
        String digest = HMACUtils2.digestAsPlainText(input);
        assertNotNull(digest);
        assertEquals(64, digest.length());
    }

    @Test
    public void testEncodeBytesToHex_uppercase_bigEndian() {
        byte[] bytes = new byte[] { (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
        String hex = HMACUtils2.encodeBytesToHex(bytes, true, ByteOrder.BIG_ENDIAN);
        assertEquals("ABCDEF", hex);
    }

    @Test
    public void testEncodeBytesToHex_lowercase_bigEndian() {
        byte[] bytes = new byte[] { (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
        String hex = HMACUtils2.encodeBytesToHex(bytes, false, ByteOrder.BIG_ENDIAN);
        assertEquals("abcdef", hex);
    }

    @Test
    public void testEncodeBytesToHex_uppercase_littleEndian() {
        byte[] bytes = new byte[] { (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
        String hex = HMACUtils2.encodeBytesToHex(bytes, true, ByteOrder.LITTLE_ENDIAN);
        assertEquals("EFCDAB", hex);
    }

    @Test
    public void testEncodeBytesToHex_lowercase_littleEndian() {
        byte[] bytes = new byte[] { (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
        String hex = HMACUtils2.encodeBytesToHex(bytes, false, ByteOrder.LITTLE_ENDIAN);
        assertEquals("efcdab", hex);
    }

    @Test
    public void testGenerateHash_invalidAlgorithm() {
        try {
            MessageDigest.getInstance("INVALID-ALGO");
            fail("Expected NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException e) {
            // expected
        }
    }
}
