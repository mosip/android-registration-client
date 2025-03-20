package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientManagerErrorTest {

    @Test
    public void testDateParseError() {
        ClientManagerError error = ClientManagerError.DATE_PARSE_ERROR;

        assertEquals("NEW-CODE", error.getErrorCode());
        assertEquals("Date parsing error", error.getErrorMessage());
    }

    @Test
    public void testRegBiometricDtoNull() {
        ClientManagerError error = ClientManagerError.REG_BIOMETRIC_DTO_NULL;

        assertEquals("REG-UOS-001", error.getErrorCode());
        assertEquals("Biometric Dto is mandatory field and it is missing.", error.getErrorMessage());
    }

    @Test
    public void testSbiDiscoverError() {
        ClientManagerError error = ClientManagerError.SBI_DISCOVER_ERROR;

        assertEquals("MOS-REG-002", error.getErrorCode());
        assertEquals("Failed to discover SBI", error.getErrorMessage());
    }

    @Test
    public void testSetErrorCode() {
        ClientManagerError error = ClientManagerError.DATE_PARSE_ERROR;

        // Set a new error code
        error.setErrorCode("NEW-CODE");
        assertEquals("NEW-CODE", error.getErrorCode());
        // Verify error message remains unchanged
        assertEquals("Date parsing error", error.getErrorMessage());
    }

    @Test
    public void testSetErrorMessage() {
        ClientManagerError error = ClientManagerError.REG_BIOMETRIC_DTO_NULL;

        // Set a new error message
        error.setErrorMessage("New error message");
        assertEquals("New error message", error.getErrorMessage());
        // Verify error code remains unchanged
        assertEquals("REG-UOS-001", error.getErrorCode());
    }

    @Test
    public void testEnumValuesCount() {
        // Verify the total number of enum constants
        assertEquals(3, ClientManagerError.values().length);
    }

    @Test
    public void testValueOf_DateParseError() {
        // Verify that valueOf retrieves the correct enum constant for DATE_PARSE_ERROR
        ClientManagerError error = ClientManagerError.valueOf("DATE_PARSE_ERROR");
        assertEquals("NEW-CODE", error.getErrorCode());
        assertEquals("Date parsing error", error.getErrorMessage());
    }

    @Test
    public void testValueOf_RegBiometricDtoNull() {
        // Verify that valueOf retrieves the correct enum constant for REG_BIOMETRIC_DTO_NULL
        ClientManagerError error = ClientManagerError.valueOf("REG_BIOMETRIC_DTO_NULL");
        assertEquals("REG-UOS-001", error.getErrorCode());
        assertEquals("Biometric Dto is mandatory field and it is missing.", error.getErrorMessage());
    }

    @Test
    public void testValueOf_SbiDiscoverError() {
        // Verify that valueOf retrieves the correct enum constant for SBI_DISCOVER_ERROR
        ClientManagerError error = ClientManagerError.valueOf("SBI_DISCOVER_ERROR");
        assertEquals("MOS-REG-002", error.getErrorCode());
        assertEquals("Failed to discover SBI", error.getErrorMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOf_InvalidName() {
        // Verify that valueOf throws IllegalArgumentException for an invalid name
        ClientManagerError.valueOf("INVALID_ERROR");
    }
}