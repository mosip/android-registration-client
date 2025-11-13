package io.mosip.registration.clientmanager.jobs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import io.mosip.registration.clientmanager.spi.MasterDataService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConfigDataSyncJobTest {

    @Spy
    private ConfigDataSyncJob configDataSyncJob = new ConfigDataSyncJob();

    @Mock
    private MasterDataService masterDataService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        configDataSyncJob.masterDataService = masterDataService;
    }

    @Test
    public void testTriggerJob_SuccessfulSync_ReturnsTrue() throws Exception {
        int jobId = 1;
        long currentTimeMillis = System.currentTimeMillis();
        doAnswer(invocation -> {
            Runnable callback = invocation.getArgument(0);
            callback.run(); // Simulate callback execution
            return null;
        }).when(masterDataService).syncGlobalParamsData(any(), anyBoolean(), eq(""));

        boolean result = configDataSyncJob.triggerJob(jobId);

        assertTrue(result);
        verify(masterDataService).syncGlobalParamsData(any(), anyBoolean(), eq(""));
        verify(configDataSyncJob).logJobTransaction(jobId, TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis));
    }

    @Test
    public void testTriggerJob_SyncThrowsException_ReturnsFalse() throws Exception {
        int jobId = 1;
        doThrow(new RuntimeException("Sync failed"))
                .when(masterDataService).syncGlobalParamsData(any(), anyBoolean(), eq(""));

        boolean result = configDataSyncJob.triggerJob(jobId);

        assertFalse(result);
        verify(masterDataService).syncGlobalParamsData(any(), anyBoolean(), eq(""));
    }

    @Test
    public void testTriggerJob_LogsStartAndCompletion() throws Exception {
        int jobId = 1;
        doAnswer(invocation -> {
            Runnable callback = invocation.getArgument(0);
            callback.run();
            return null;
        }).when(masterDataService).syncGlobalParamsData(any(), anyBoolean(), eq(""));

        configDataSyncJob.triggerJob(jobId);

        verify(masterDataService).syncGlobalParamsData(any(), anyBoolean(), eq(""));
    }
}