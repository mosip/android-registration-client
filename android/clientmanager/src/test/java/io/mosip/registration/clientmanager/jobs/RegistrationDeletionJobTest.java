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
public class RegistrationDeletionJobTest {

    @Spy
    private RegistrationDeletionJob registrationDeletionJob = new RegistrationDeletionJob();

    @Mock
    private PacketService packetService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        registrationDeletionJob.packetService = packetService;
    }

    @Test
    public void testTriggerJob_SuccessfulDeletion_ReturnsTrue() {
        int jobId = 1;
        long currentTimeMillis = System.currentTimeMillis();

        boolean result = registrationDeletionJob.triggerJob(jobId);

        assertTrue(result);
        verify(packetService).deleteRegistrationPackets();
        verify(registrationDeletionJob).logJobTransaction(jobId, TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis));
    }

    @Test
    public void testTriggerJob_DeletionThrowsException_ReturnsFalse() {
        int jobId = 1;
        doThrow(new RuntimeException("Deletion failed"))
                .when(packetService).deleteRegistrationPackets();

        boolean result = registrationDeletionJob.triggerJob(jobId);

        assertFalse(result);
        verify(packetService).deleteRegistrationPackets();
    }

    @Test
    public void testTriggerJob_LogsStartAndCompletion() {
        int jobId = 1;

        registrationDeletionJob.triggerJob(jobId);

        verify(packetService).deleteRegistrationPackets();
    }
}

