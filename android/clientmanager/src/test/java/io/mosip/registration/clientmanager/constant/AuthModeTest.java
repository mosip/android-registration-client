package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AuthModeTest {

    @Test
    public void testPwdExists() {
        AuthMode mode = AuthMode.PWD;
        assertEquals("PWD", mode.name());
    }

    @Test
    public void testOtpExists() {
        AuthMode mode = AuthMode.OTP;
        assertEquals("OTP", mode.name());
    }

    @Test
    public void testFingerExists() {
        AuthMode mode = AuthMode.FINGER;
        assertEquals("FINGER", mode.name());
    }

    @Test
    public void testIrisExists() {
        AuthMode mode = AuthMode.IRIS;
        assertEquals("IRIS", mode.name());
    }

    @Test
    public void testFaceExists() {
        AuthMode mode = AuthMode.FACE;
        assertEquals("FACE", mode.name());
    }

    @Test
    public void testEnumValuesCount() {
        // Verify the total number of enum constants
        assertEquals(5, AuthMode.values().length);
    }

    @Test
    public void testValueOf_Pwd() {
        // Verify that valueOf retrieves the correct enum constant for PWD
        AuthMode mode = AuthMode.valueOf("PWD");
        assertEquals("PWD", mode.name());
    }

    @Test
    public void testValueOf_Otp() {
        // Verify that valueOf retrieves the correct enum constant for OTP
        AuthMode mode = AuthMode.valueOf("OTP");
        assertEquals("OTP", mode.name());
    }

    @Test
    public void testValueOf_Finger() {
        // Verify that valueOf retrieves the correct enum constant for FINGER
        AuthMode mode = AuthMode.valueOf("FINGER");
        assertEquals("FINGER", mode.name());
    }

    @Test
    public void testValueOf_Iris() {
        // Verify that valueOf retrieves the correct enum constant for IRIS
        AuthMode mode = AuthMode.valueOf("IRIS");
        assertEquals("IRIS", mode.name());
    }

    @Test
    public void testValueOf_Face() {
        // Verify that valueOf retrieves the correct enum constant for FACE
        AuthMode mode = AuthMode.valueOf("FACE");
        assertEquals("FACE", mode.name());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOf_InvalidName() {
        // Verify that valueOf throws IllegalArgumentException for an invalid name
        AuthMode.valueOf("INVALID_MODE");
    }
}