package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PacketClientStatusTest {

    @Test
    public void testCreatedExists() {
        PacketClientStatus status = PacketClientStatus.CREATED;
        assertEquals("CREATED", status.name());
    }

    @Test
    public void testApprovedExists() {
        PacketClientStatus status = PacketClientStatus.APPROVED;
        assertEquals("APPROVED", status.name());
    }

    @Test
    public void testRejectedExists() {
        PacketClientStatus status = PacketClientStatus.REJECTED;
        assertEquals("REJECTED", status.name());
    }

    @Test
    public void testSyncedExists() {
        PacketClientStatus status = PacketClientStatus.SYNCED;
        assertEquals("SYNCED", status.name());
    }

    @Test
    public void testExportedExists() {
        PacketClientStatus status = PacketClientStatus.EXPORTED;
        assertEquals("EXPORTED", status.name());
    }

    @Test
    public void testUploadedExists() {
        PacketClientStatus status = PacketClientStatus.UPLOADED;
        assertEquals("UPLOADED", status.name());
    }

    @Test
    public void testEnumValuesCount() {
        // Verify the total number of enum constants
        assertEquals(6, PacketClientStatus.values().length);
    }

    @Test
    public void testValueOf_Created() {
        // Verify that valueOf retrieves the correct enum constant for CREATED
        PacketClientStatus status = PacketClientStatus.valueOf("CREATED");
        assertEquals("CREATED", status.name());
    }

    @Test
    public void testValueOf_Approved() {
        // Verify that valueOf retrieves the correct enum constant for APPROVED
        PacketClientStatus status = PacketClientStatus.valueOf("APPROVED");
        assertEquals("APPROVED", status.name());
    }

    @Test
    public void testValueOf_Rejected() {
        // Verify that valueOf retrieves the correct enum constant for REJECTED
        PacketClientStatus status = PacketClientStatus.valueOf("REJECTED");
        assertEquals("REJECTED", status.name());
    }

    @Test
    public void testValueOf_Synced() {
        // Verify that valueOf retrieves the correct enum constant for SYNCED
        PacketClientStatus status = PacketClientStatus.valueOf("SYNCED");
        assertEquals("SYNCED", status.name());
    }

    @Test
    public void testValueOf_Exported() {
        // Verify that valueOf retrieves the correct enum constant for EXPORTED
        PacketClientStatus status = PacketClientStatus.valueOf("EXPORTED");
        assertEquals("EXPORTED", status.name());
    }

    @Test
    public void testValueOf_Uploaded() {
        // Verify that valueOf retrieves the correct enum constant for UPLOADED
        PacketClientStatus status = PacketClientStatus.valueOf("UPLOADED");
        assertEquals("UPLOADED", status.name());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOf_InvalidName() {
        // Verify that valueOf throws IllegalArgumentException for an invalid name
        PacketClientStatus.valueOf("INVALID_STATUS");
    }
}