package io.mosip.registration.clientmanager.jobs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

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
    public void triggerJob_withValidJobId_returnsTrue() {
        int jobId = 5;

        boolean result = registrationDeletionJob.triggerJob(jobId);

        assertTrue(result);
        verify(packetService).deleteRegistrationPackets();
    }

    @Test
    public void triggerJob_withPacketServiceException_returnsFalse() {
        int jobId = 5;
        doThrow(new RuntimeException("Deletion failed"))
                .when(packetService).deleteRegistrationPackets();

        boolean result = registrationDeletionJob.triggerJob(jobId);

        assertFalse(result);
        verify(packetService).deleteRegistrationPackets();
    }
}
