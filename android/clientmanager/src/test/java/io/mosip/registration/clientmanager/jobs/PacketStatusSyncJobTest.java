package io.mosip.registration.clientmanager.jobs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import io.mosip.registration.clientmanager.spi.PacketService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PacketStatusSyncJobTest {

    @Spy
    private PacketStatusSyncJob packetStatusSyncJob = new PacketStatusSyncJob();

    @Mock
    private PacketService packetService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        packetStatusSyncJob.packetService = packetService;
    }

    @Test
    public void testTriggerJob_SuccessfulSync_ReturnsTrue() {

        int jobId = 1;
        long currentTimeMillis = System.currentTimeMillis();

        boolean result = packetStatusSyncJob.triggerJob(jobId);

        assertTrue(result);
        verify(packetService).syncAllPacketStatus();
        verify(packetStatusSyncJob).logJobTransaction(jobId, TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis));
    }

    @Test
    public void testTriggerJob_SyncThrowsException_ReturnsFalse() {

        int jobId = 1;
        doThrow(new RuntimeException("Sync failed"))
                .when(packetService).syncAllPacketStatus();

        boolean result = packetStatusSyncJob.triggerJob(jobId);

        assertFalse(result);
        verify(packetService).syncAllPacketStatus();
    }

    @Test
    public void testTriggerJob_LogsStartAndCompletion() {

        int jobId = 1;

        packetStatusSyncJob.triggerJob(jobId);

        verify(packetService).syncAllPacketStatus();
    }
}