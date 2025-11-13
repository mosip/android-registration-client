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
import io.mosip.registration.clientmanager.spi.AuditManagerService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class DeleteAuditLogsJobTest {

    @Mock
    private AuditManagerService auditManagerService;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    private DeleteAuditLogsJob createJobSpy() {
        DeleteAuditLogsJob job = spy(new DeleteAuditLogsJob());
        job.auditManagerService = auditManagerService;
        doNothing().when(job).logJobTransaction(anyInt(), anyLong());
        return job;
    }

    @Test
    public void onCreate_invokesAndroidInjection() {
        try (MockedStatic<AndroidInjection> injectionMock = org.mockito.Mockito.mockStatic(AndroidInjection.class)) {
            DeleteAuditLogsJob job = Robolectric.buildService(DeleteAuditLogsJob.class).create().get();
            injectionMock.verify(() -> AndroidInjection.inject(job), org.mockito.Mockito.atLeastOnce());
        }
    }

    @Test
    public void triggerJob_whenDeletionSucceeds_logsTransactionAndReturnsTrue() {
        DeleteAuditLogsJob job = createJobSpy();
        when(auditManagerService.deleteAuditLogs()).thenReturn(true);

        boolean result = job.triggerJob(101);

        assertTrue(result);
        verify(auditManagerService).deleteAuditLogs();
        verify(job).logJobTransaction(eq(101), anyLong());
    }

    @Test
    public void triggerJob_whenDeletionFails_logsTransactionAndReturnsFalse() {
        DeleteAuditLogsJob job = createJobSpy();
        when(auditManagerService.deleteAuditLogs()).thenReturn(false);

        boolean result = job.triggerJob(202);

        assertFalse(result);
        verify(auditManagerService).deleteAuditLogs();
        verify(job).logJobTransaction(eq(202), anyLong());
    }

    @Test
    public void triggerJob_whenDeletionThrows_logsErrorAndReturnsFalse() {
        DeleteAuditLogsJob job = createJobSpy();
        when(auditManagerService.deleteAuditLogs()).thenThrow(new RuntimeException("Deletion failed"));

        boolean result = job.triggerJob(303);

        assertFalse(result);
        verify(auditManagerService).deleteAuditLogs();
        verify(job, never()).logJobTransaction(anyInt(), anyLong());
    }
}