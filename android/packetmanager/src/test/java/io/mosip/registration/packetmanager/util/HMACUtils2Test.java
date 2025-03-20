package io.mosip.registration.packetmanager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;

public class HMACUtils2Test {

    @Test
    public void testGenerateHash() throws NoSuchAlgorithmException {
        byte[] input = "test".getBytes();
        byte[] hash = HMACUtils2.generateHash(input);
        assertNotNull(hash);
        assertEquals(32, hash.length); // SHA-256 produces 32-byte hash
    }

    @Test
    public void testDigestAsPlainTextWithSalt() throws NoSuchAlgorithmException {
        byte[] password = "mosip".getBytes();
        byte[] salt = "salt".getBytes();
        String hash = HMACUtils2.digestAsPlainTextWithSalt(password, salt);
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    public void testDigestAsPlainText() throws NoSuchAlgorithmException {
        byte[] input = "test".getBytes();
        String hash = HMACUtils2.digestAsPlainText(input);
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    public void testEncodeBytesToHex_BigEndianUpperCase() {
        byte[] input = {0x12, 0x34, 0x56, 0x78};
        String hex = HMACUtils2.encodeBytesToHex(input, true, ByteOrder.BIG_ENDIAN);
        assertEquals("12345678", hex);
    }

    @Test
    public void testEncodeBytesToHex_BigEndianLowerCase() {
        byte[] input = {0x12, 0x34, 0x56, 0x78};
        String hex = HMACUtils2.encodeBytesToHex(input, false, ByteOrder.BIG_ENDIAN);
        assertEquals("12345678", hex);
    }

    @Test
    public void testEncodeBytesToHex_LittleEndianUpperCase() {
        byte[] input = {0x12, 0x34, 0x56, 0x78};
        String hex = HMACUtils2.encodeBytesToHex(input, true, ByteOrder.LITTLE_ENDIAN);
        assertEquals("78563412", hex);
    }

    @Test
    public void testEncodeBytesToHex_LittleEndianLowerCase() {
        byte[] input = {0x12, 0x34, 0x56, 0x78};
        String hex = HMACUtils2.encodeBytesToHex(input, false, ByteOrder.LITTLE_ENDIAN);
        assertEquals("78563412", hex);
    }
}
