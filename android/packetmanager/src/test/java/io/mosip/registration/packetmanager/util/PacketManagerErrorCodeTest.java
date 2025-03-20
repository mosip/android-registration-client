package io.mosip.registration.packetmanager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class PacketManagerErrorCodeTest {

    @Test
    public void testEnumValues() {
        for (PacketManagerErrorCode errorCode : PacketManagerErrorCode.values()) {
            assertNotNull(errorCode.getErrorCode(), "Error code should not be null");
            assertNotNull(errorCode.getErrorMessage(), "Error message should not be null");
        }
    }

    @Test
    public void testSpecificErrorCodes() {
        assertEquals("KER-PUT-001", PacketManagerErrorCode.UNKNOWN_RESOURCE_EXCEPTION.getErrorCode());
        assertEquals("Unknown resource provided", PacketManagerErrorCode.UNKNOWN_RESOURCE_EXCEPTION.getErrorMessage());

        assertEquals("KER-PUT-002", PacketManagerErrorCode.FILE_NOT_FOUND_IN_DESTINATION.getErrorCode());
        assertEquals("Unable to Find File in Destination Folder", PacketManagerErrorCode.FILE_NOT_FOUND_IN_DESTINATION.getErrorMessage());
    }
}
