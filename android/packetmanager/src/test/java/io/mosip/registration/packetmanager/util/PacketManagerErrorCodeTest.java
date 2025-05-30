package io.mosip.registration.packetmanager.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class PacketManagerErrorCodeTest {

    @Test
    public void testAllEnumValues() {
        for (PacketManagerErrorCode code : PacketManagerErrorCode.values()) {
            assertNotNull(code.getErrorCode());
            assertNotNull(code.getErrorMessage());
            assertEquals(code, PacketManagerErrorCode.valueOf(code.name()));
        }
    }

    @Test
    public void testSpecificEnumValues() {
        assertEquals("KER-PUT-001", PacketManagerErrorCode.UNKNOWN_RESOURCE_EXCEPTION.getErrorCode());
        assertEquals("Unknown resource provided", PacketManagerErrorCode.UNKNOWN_RESOURCE_EXCEPTION.getErrorMessage());

        assertEquals("KER-PUT-014", PacketManagerErrorCode.PACKET_KEEPER_GET_ERROR.getErrorCode());
        assertEquals(PacketManagerConstant.PACKET_KEEPER_EXCEPTION_MSG, PacketManagerErrorCode.PACKET_KEEPER_GET_ERROR.getErrorMessage());

        assertEquals("KER-PUT-025", PacketManagerErrorCode.SOURCE_NOT_PRESENT.getErrorCode());
        assertEquals("Source not present in request.", PacketManagerErrorCode.SOURCE_NOT_PRESENT.getErrorMessage());
    }
}
