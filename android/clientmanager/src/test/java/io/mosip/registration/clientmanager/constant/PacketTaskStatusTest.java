package io.mosip.registration.clientmanager.constant;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PacketTaskStatusTest {

    @Test
    public void testSyncStartedExists() {
        PacketTaskStatus status = PacketTaskStatus.SYNC_STARTED;
        assertEquals("SYNC_STARTED", status.name());
    }

    @Test
    public void testSyncCompletedExists() {
        PacketTaskStatus status = PacketTaskStatus.SYNC_COMPLETED;
        assertEquals("SYNC_COMPLETED", status.name());
    }

    @Test
    public void testSyncFailedExists() {
        PacketTaskStatus status = PacketTaskStatus.SYNC_FAILED;
        assertEquals("SYNC_FAILED", status.name());
    }

    @Test
    public void testSyncAlreadyCompletedExists() {
        PacketTaskStatus status = PacketTaskStatus.SYNC_ALREADY_COMPLETED;
        assertEquals("SYNC_ALREADY_COMPLETED", status.name());
    }

    @Test
    public void testUploadStartedExists() {
        PacketTaskStatus status = PacketTaskStatus.UPLOAD_STARTED;
        assertEquals("UPLOAD_STARTED", status.name());
    }

    @Test
    public void testUploadCompletedExists() {
        PacketTaskStatus status = PacketTaskStatus.UPLOAD_COMPLETED;
        assertEquals("UPLOAD_COMPLETED", status.name());
    }

    @Test
    public void testUploadFailedExists() {
        PacketTaskStatus status = PacketTaskStatus.UPLOAD_FAILED;
        assertEquals("UPLOAD_FAILED", status.name());
    }

    @Test
    public void testUploadAlreadyCompletedExists() {
        PacketTaskStatus status = PacketTaskStatus.UPLOAD_ALREADY_COMPLETED;
        assertEquals("UPLOAD_ALREADY_COMPLETED", status.name());
    }

    @Test
    public void testEnumValuesCount() {
        // Verify the total number of enum constants
        assertEquals(8, PacketTaskStatus.values().length);
    }

    @Test
    public void testValueOf_SyncStarted() {
        // Verify that valueOf retrieves the correct enum constant for SYNC_STARTED
        PacketTaskStatus status = PacketTaskStatus.valueOf("SYNC_STARTED");
        assertEquals("SYNC_STARTED", status.name());
    }

    @Test
    public void testValueOf_SyncCompleted() {
        // Verify that valueOf retrieves the correct enum constant for SYNC_COMPLETED
        PacketTaskStatus status = PacketTaskStatus.valueOf("SYNC_COMPLETED");
        assertEquals("SYNC_COMPLETED", status.name());
    }

    @Test
    public void testValueOf_SyncFailed() {
        // Verify that valueOf retrieves the correct enum constant for SYNC_FAILED
        PacketTaskStatus status = PacketTaskStatus.valueOf("SYNC_FAILED");
        assertEquals("SYNC_FAILED", status.name());
    }

    @Test
    public void testValueOf_SyncAlreadyCompleted() {
        // Verify that valueOf retrieves the correct enum constant for SYNC_ALREADY_COMPLETED
        PacketTaskStatus status = PacketTaskStatus.valueOf("SYNC_ALREADY_COMPLETED");
        assertEquals("SYNC_ALREADY_COMPLETED", status.name());
    }

    @Test
    public void testValueOf_UploadStarted() {
        // Verify that valueOf retrieves the correct enum constant for UPLOAD_STARTED
        PacketTaskStatus status = PacketTaskStatus.valueOf("UPLOAD_STARTED");
        assertEquals("UPLOAD_STARTED", status.name());
    }

    @Test
    public void testValueOf_UploadCompleted() {
        // Verify that valueOf retrieves the correct enum constant for UPLOAD_COMPLETED
        PacketTaskStatus status = PacketTaskStatus.valueOf("UPLOAD_COMPLETED");
        assertEquals("UPLOAD_COMPLETED", status.name());
    }

    @Test
    public void testValueOf_UploadFailed() {
        // Verify that valueOf retrieves the correct enum constant for UPLOAD_FAILED
        PacketTaskStatus status = PacketTaskStatus.valueOf("UPLOAD_FAILED");
        assertEquals("UPLOAD_FAILED", status.name());
    }

    @Test
    public void testValueOf_UploadAlreadyCompleted() {
        // Verify that valueOf retrieves the correct enum constant for UPLOAD_ALREADY_COMPLETED
        PacketTaskStatus status = PacketTaskStatus.valueOf("UPLOAD_ALREADY_COMPLETED");
        assertEquals("UPLOAD_ALREADY_COMPLETED", status.name());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOf_InvalidName() {
        // Verify that valueOf throws IllegalArgumentException for an invalid name
        PacketTaskStatus.valueOf("INVALID_STATUS");
    }
}