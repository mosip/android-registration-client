package io.mosip.registration.keymanager.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KeyManagerErrorCodeTest {

    @Test
    public void testEnumValues() {
        // Test if enum constants have expected error codes and messages
        assertEquals("KER-CRY-001", KeyManagerErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode());
        assertEquals("No Such algorithm is supported", KeyManagerErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());

        assertEquals("KER-CRY-002", KeyManagerErrorCode.INVALID_SPEC_PUBLIC_KEY.getErrorCode());
        assertEquals("public key is invalid", KeyManagerErrorCode.INVALID_SPEC_PUBLIC_KEY.getErrorMessage());

        assertEquals("KER-CRY-003", KeyManagerErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorCode());
        assertEquals("data sent to decrypt is without key splitter or invalid", KeyManagerErrorCode.INVALID_DATA_WITHOUT_KEY_BREAKER.getErrorMessage());

        assertEquals("KER-CRY-004", KeyManagerErrorCode.INVALID_REQUEST.getErrorCode());
        assertEquals("should not be null or empty", KeyManagerErrorCode.INVALID_REQUEST.getErrorMessage());

        assertEquals("KER-CRY-005", KeyManagerErrorCode.CANNOT_CONNECT_TO_KEYMANAGER_SERVICE.getErrorCode());
        assertEquals("cannot connect to keymanager service or response is null", KeyManagerErrorCode.CANNOT_CONNECT_TO_KEYMANAGER_SERVICE.getErrorMessage());

        assertEquals("KER-CRY-006", KeyManagerErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorCode());
        assertEquals("Keymanager Service has replied with following error", KeyManagerErrorCode.KEYMANAGER_SERVICE_ERROR.getErrorMessage());

        assertEquals("KER-CRY-007", KeyManagerErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorCode());
        assertEquals("timestamp should be in ISO 8601 format yyyy-MM-ddTHH::mm:ss.SZ", KeyManagerErrorCode.DATE_TIME_PARSE_EXCEPTION.getErrorMessage());

        assertEquals("KER-CRY-009", KeyManagerErrorCode.HEX_DATA_PARSE_EXCEPTION.getErrorCode());
        assertEquals("Invalid Hex Data", KeyManagerErrorCode.HEX_DATA_PARSE_EXCEPTION.getErrorMessage());

        assertEquals("KER-CRY-010", KeyManagerErrorCode.CERTIFICATE_THUMBPRINT_ERROR.getErrorCode());
        assertEquals("Error in generating Certificate Thumbprint.", KeyManagerErrorCode.CERTIFICATE_THUMBPRINT_ERROR.getErrorMessage());

        assertEquals("KER-CRY-500", KeyManagerErrorCode.INTERNAL_SERVER_ERROR.getErrorCode());
        assertEquals("Internal server error", KeyManagerErrorCode.INTERNAL_SERVER_ERROR.getErrorMessage());
    }

    @Test
    public void testEnumValuesAreNotNull() {
        for (KeyManagerErrorCode errorCode : KeyManagerErrorCode.values()) {
            assertNotNull("Error code should not be null", errorCode.getErrorCode());
            assertNotNull("Error message should not be null", errorCode.getErrorMessage());
        }
    }
}
