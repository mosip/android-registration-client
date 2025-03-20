package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SBIErrorTest {

    @Test
    public void testParseError() {
        SBIError error = SBIError.PARSE_ERROR;

        assertEquals("REG-SBI-101", error.getErrorCode());
        assertEquals("JSON parsing error", error.getErrorMessage());
    }

    @Test
    public void testDeviceNotRegistered() {
        SBIError error = SBIError.DEVICE_NOT_REGISTERED;

        assertEquals("REG-SBI-102", error.getErrorCode());
        assertEquals("Device not registered", error.getErrorMessage());
    }

    @Test
    public void testUnsupportedSpec() {
        SBIError error = SBIError.UNSUPPORTED_SPEC;

        assertEquals("REG-SBI-103", error.getErrorCode());
        assertEquals("Unsupported SpecVersion", error.getErrorMessage());
    }

    @Test
    public void testDeviceNotFound() {
        SBIError error = SBIError.DEVICE_NOT_FOUND;

        assertEquals("REG-SBI-104", error.getErrorCode());
        assertEquals("Device not found", error.getErrorMessage());
    }

    @Test
    public void testSbiRequestFailed() {
        SBIError error = SBIError.SBI_REQUEST_FAILED;

        assertEquals("REG-SBI-105", error.getErrorCode());
        assertEquals("SBI request Failed : ", error.getErrorMessage());
    }

    @Test
    public void testSbiInvalidSignature() {
        SBIError error = SBIError.SBI_INVALID_SIGNATURE;

        assertEquals("REG-SBI-106", error.getErrorCode());
        assertEquals("Device response with invalid signature", error.getErrorMessage());
    }

    @Test
    public void testSbiJwtInvalid() {
        SBIError error = SBIError.SBI_JWT_INVALID;

        assertEquals("REG-SBI-107", error.getErrorCode());
        assertEquals("Invalid JWT value (Header.Payload.Signature)", error.getErrorMessage());
    }

    @Test
    public void testSbiCertPathTrustFailed() {
        SBIError error = SBIError.SBI_CERT_PATH_TRUST_FAILED;

        assertEquals("REG-SBI-108", error.getErrorCode());
        assertEquals("Certificate path trust validation failed", error.getErrorMessage());
    }

    @Test
    public void testSbiPayloadEmpty() {
        SBIError error = SBIError.SBI_PAYLOAD_EMPTY;

        assertEquals("REG-SBI-109", error.getErrorCode());
        assertEquals("Payload is Empty", error.getErrorMessage());
    }

    @Test
    public void testSbiSignatureEmpty() {
        SBIError error = SBIError.SBI_SIGNATURE_EMPTY;

        assertEquals("REG-SBI-110", error.getErrorCode());
        assertEquals("Signature is Empty", error.getErrorMessage());
    }

    @Test
    public void testSbiCaptureInvalidTime() {
        SBIError error = SBIError.SBI_CAPTURE_INVALID_TIME;

        assertEquals("REG-SBI-111", error.getErrorCode());
        assertEquals("RCapture Time was Invalid", error.getErrorMessage());
    }

    @Test
    public void testSbiRCaptureInvalidScore() {
        SBIError error = SBIError.SBI_RCAPTURE_INVALID_SCORE;

        assertEquals("REG-SBI-112", error.getErrorCode());
        assertEquals("RCapture Failed! Invalid quality score", error.getErrorMessage());
    }

    @Test
    public void testSbiDinfoInvalidResponse() {
        SBIError error = SBIError.SBI_DINFO_INVALID_REPSONSE;

        assertEquals("REG-SBI-113", error.getErrorCode());
        assertEquals("Device Info Failed! Invalid response", error.getErrorMessage());
    }

    @Test
    public void testSbiDiscInvalidResponse() {
        SBIError error = SBIError.SBI_DISC_INVALID_REPSONSE;

        assertEquals("REG-SBI-114", error.getErrorCode());
        assertEquals("Discovery Failed! Invalid response", error.getErrorMessage());
    }

    @Test
    public void testSbiRCaptureError() {
        SBIError error = SBIError.SBI_RCAPTURE_ERROR;

        assertEquals("REG-SBI-115", error.getErrorCode());
        assertEquals("RCapture Failed! ", error.getErrorMessage());
    }

    @Test
    public void testSetErrorCode() {
        SBIError error = SBIError.PARSE_ERROR;

        // Set a new error code
        error.setErrorCode("NEW-CODE");
        assertEquals("NEW-CODE", error.getErrorCode());
        // Verify error message remains unchanged
        assertEquals("JSON parsing error", error.getErrorMessage());
    }

    @Test
    public void testSetErrorMessage() {
        SBIError error = SBIError.DEVICE_NOT_REGISTERED;

        // Set a new error message
        error.setErrorMessage("New error message");
        assertEquals("New error message", error.getErrorMessage());
        // Verify error code remains unchanged
        assertEquals("REG-SBI-102", error.getErrorCode());
    }

    @Test
    public void testEnumValuesCount() {
        // Verify the total number of enum constants
        assertEquals(15, SBIError.values().length);
    }

    @Test
    public void testValueOf_ParseError() {
        // Verify that valueOf retrieves the correct enum constant for PARSE_ERROR
        SBIError error = SBIError.valueOf("PARSE_ERROR");
        assertEquals("NEW-CODE", error.getErrorCode());
        assertEquals("JSON parsing error", error.getErrorMessage());
    }

    @Test
    public void testValueOf_DeviceNotFound() {
        // Verify that valueOf retrieves the correct enum constant for DEVICE_NOT_FOUND
        SBIError error = SBIError.valueOf("DEVICE_NOT_FOUND");
        assertEquals("REG-SBI-104", error.getErrorCode());
        assertEquals("Device not found", error.getErrorMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOf_InvalidName() {
        // Verify that valueOf throws IllegalArgumentException for an invalid name
        SBIError.valueOf("INVALID_ERROR");
    }
}