package io.mosip.registration.clientmanager.jobs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import dagger.android.AndroidInjection;
import io.mosip.registration.clientmanager.spi.PacketService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class RegistrationDeletionJobTest {

    @Mock
    private PacketService packetService;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    private RegistrationDeletionJob createJobSpy() {
        RegistrationDeletionJob job = spy(new RegistrationDeletionJob());
        job.packetService = packetService;
        doNothing().when(job).logJobTransaction(anyInt(), anyLong());
        return job;
    }

    @Test
    public void onCreate_invokesAndroidInjection() {
        try (MockedStatic<AndroidInjection> injectionMock = org.mockito.Mockito.mockStatic(AndroidInjection.class)) {
            Robolectric.buildService(RegistrationDeletionJob.class).create().get();
            injectionMock.verify(() -> AndroidInjection.inject(org.mockito.ArgumentMatchers.any(RegistrationDeletionJob.class)),
                    org.mockito.Mockito.atLeastOnce());
        }
    }

    @Test
    public void triggerJob_whenDeletionSucceeds_logsTransactionAndReturnsTrue() {
        RegistrationDeletionJob job = createJobSpy();
        doNothing().when(packetService).deleteRegistrationPackets();

        boolean result = job.triggerJob(101);

        assertTrue(result);
        verify(packetService).deleteRegistrationPackets();
        verify(job).logJobTransaction(eq(101), anyLong());
    }

    @Test
    public void triggerJob_whenDeletionThrows_returnsFalseAndDoesNotLogTransaction() {
        RegistrationDeletionJob job = createJobSpy();
        doThrow(new RuntimeException("Deletion failed")).when(packetService).deleteRegistrationPackets();

        boolean result = job.triggerJob(202);

        assertFalse(result);
        verify(packetService).deleteRegistrationPackets();
        verify(job, never()).logJobTransaction(anyInt(), anyLong());
    }

    @Test
    public void triggerJob_withDifferentJobIds_logsCorrectJobId() {
        RegistrationDeletionJob job = createJobSpy();
        doNothing().when(packetService).deleteRegistrationPackets();

        job.triggerJob(501);
        verify(job).logJobTransaction(eq(501), anyLong());

        job.triggerJob(502);
        verify(job).logJobTransaction(eq(502), anyLong());
    }
}
