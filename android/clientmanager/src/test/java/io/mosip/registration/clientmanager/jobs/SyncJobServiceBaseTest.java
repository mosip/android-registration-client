package io.mosip.registration.clientmanager.jobs;

import android.app.job.JobParameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.registration.clientmanager.spi.JobTransactionService;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SyncJobServiceBaseTest {

    // Testable subclass to instantiate SyncJobServiceBase
    private static class TestSyncJobService extends SyncJobServiceBase {
        private final boolean triggerResult;

        TestSyncJobService(boolean triggerResult) {
            this.triggerResult = triggerResult;
        }

        @Override
        public boolean triggerJob(int jobId) {
            return triggerResult;
        }
    }

    @Spy
    @InjectMocks
    private TestSyncJobService syncJobService = new TestSyncJobService(true);

    @Mock
    private JobTransactionService jobTransactionService;

    @Mock
    private JobParameters jobParameters;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        syncJobService.jobTransactionService = jobTransactionService;
        when(jobParameters.getJobId()).thenReturn(1);
    }

    @Test
    public void testOnStartJob_TriggerSucceeds_JobFinishes() throws InterruptedException {

        syncJobService = spy(new TestSyncJobService(true));
        syncJobService.jobTransactionService = jobTransactionService;
        doNothing().when(syncJobService).jobFinished(any(), anyBoolean());

        boolean result = syncJobService.onStartJob(jobParameters);

        assertTrue(result); // Indicates job is running asynchronously
        Thread.sleep(100); // Wait briefly for thread to execute
        verify(syncJobService).triggerJob(1);
        verify(syncJobService).jobFinished(jobParameters, false);
    }

    @Test
    public void testOnStartJob_TriggerFails_JobFinishes() throws InterruptedException {

        syncJobService = spy(new TestSyncJobService(false));
        syncJobService.jobTransactionService = jobTransactionService;
        doNothing().when(syncJobService).jobFinished(any(), anyBoolean());

        boolean result = syncJobService.onStartJob(jobParameters);

        assertTrue(result); // Indicates job is running asynchronously
        Thread.sleep(100); // Wait briefly for thread to execute
        verify(syncJobService).triggerJob(1);
        verify(syncJobService).jobFinished(jobParameters, false);
    }

    @Test
    public void testOnStopJob_ThreadRunning_InterruptsThread() throws InterruptedException {

        syncJobService = spy(new TestSyncJobService(true));
        syncJobService.jobTransactionService = jobTransactionService;
        doNothing().when(syncJobService).jobFinished(any(), anyBoolean());

        syncJobService.onStartJob(jobParameters);
        Thread.sleep(50);

        boolean result = syncJobService.onStopJob(jobParameters);

        assertTrue(result);
    }

    @Test
    public void testOnStopJob_NoThreadRunning_NoInterrupt() {

        syncJobService = spy(new TestSyncJobService(true));
        syncJobService.jobTransactionService = jobTransactionService;

        boolean result = syncJobService.onStopJob(jobParameters);

        assertTrue(result);
    }

    @Test
    public void testLogJobTransaction_Success() {

        int jobId = 1;
        long timeStamp = 123456789L;

        syncJobService.logJobTransaction(jobId, timeStamp);

        verify(jobTransactionService).LogJobTransaction(jobId, timeStamp);
    }

    @Test
    public void testLogJobTransaction_Failure_DoesNotThrow() {

        int jobId = 1;
        long timeStamp = 123456789L;
        doThrow(new RuntimeException("Logging failed"))
                .when(jobTransactionService).LogJobTransaction(anyInt(), anyLong());

        syncJobService.logJobTransaction(jobId, timeStamp);

        verify(jobTransactionService).LogJobTransaction(jobId, timeStamp);
    }
}