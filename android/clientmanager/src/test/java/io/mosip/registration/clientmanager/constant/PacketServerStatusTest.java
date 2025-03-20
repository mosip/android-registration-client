package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PacketServerStatusTest {

    @Test
    public void testProcessedExists() {
        PacketServerStatus status = PacketServerStatus.PROCESSED;
        assertEquals("PROCESSED", status.name());
    }

    @Test
    public void testAcceptedExists() {
        PacketServerStatus status = PacketServerStatus.ACCEPTED;
        assertEquals("ACCEPTED", status.name());
    }

    @Test
    public void testResendExists() {
        PacketServerStatus status = PacketServerStatus.RESEND;
        assertEquals("RESEND", status.name());
    }

    @Test
    public void testRejectedExists() {
        PacketServerStatus status = PacketServerStatus.REJECTED;
        assertEquals("REJECTED", status.name());
    }

    @Test
    public void testReregisterExists() {
        PacketServerStatus status = PacketServerStatus.REREGISTER;
        assertEquals("REREGISTER", status.name());
    }

    @Test
    public void testUploadPendingExists() {
        PacketServerStatus status = PacketServerStatus.UPLOAD_PENDING;
        assertEquals("UPLOAD_PENDING", status.name());
    }

    @Test
    public void testEnumValuesCount() {
        // Verify the total number of enum constants
        assertEquals(6, PacketServerStatus.values().length);
    }

    @Test
    public void testValueOf_Processed() {
        // Verify that valueOf retrieves the correct enum constant for PROCESSED
        PacketServerStatus status = PacketServerStatus.valueOf("PROCESSED");
        assertEquals("PROCESSED", status.name());
    }

    @Test
    public void testValueOf_Accepted() {
        // Verify that valueOf retrieves the correct enum constant for ACCEPTED
        PacketServerStatus status = PacketServerStatus.valueOf("ACCEPTED");
        assertEquals("ACCEPTED", status.name());
    }

    @Test
    public void testValueOf_Resend() {
        // Verify that valueOf retrieves the correct enum constant for RESEND
        PacketServerStatus status = PacketServerStatus.valueOf("RESEND");
        assertEquals("RESEND", status.name());
    }

    @Test
    public void testValueOf_Rejected() {
        // Verify that valueOf retrieves the correct enum constant for REJECTED
        PacketServerStatus status = PacketServerStatus.valueOf("REJECTED");
        assertEquals("REJECTED", status.name());
    }

    @Test
    public void testValueOf_Reregister() {
        // Verify that valueOf retrieves the correct enum constant for REREGISTER
        PacketServerStatus status = PacketServerStatus.valueOf("REREGISTER");
        assertEquals("REREGISTER", status.name());
    }

    @Test
    public void testValueOf_UploadPending() {
        // Verify that valueOf retrieves the correct enum constant for UPLOAD_PENDING
        PacketServerStatus status = PacketServerStatus.valueOf("UPLOAD_PENDING");
        assertEquals("UPLOAD_PENDING", status.name());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOf_InvalidName() {
        // Verify that valueOf throws IllegalArgumentException for an invalid name
        PacketServerStatus.valueOf("INVALID_STATUS");
    }
}