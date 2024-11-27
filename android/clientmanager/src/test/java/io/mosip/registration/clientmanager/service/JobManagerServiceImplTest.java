package io.mosip.registration.clientmanager.service;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.util.DateUtil;

public class JobManagerServiceImplTest {

    @Mock
    private Context mockContext;

    @Mock
    private JobScheduler mockJobScheduler;

    @Mock
    private SyncJobDefRepository mockSyncJobDefRepository;

    @Mock
    private JobTransactionService mockJobTransactionService;

    @Mock
    private DateUtil mockDateUtil;

    private JobManagerServiceImpl jobManagerService;

    private static final String JOB_ID = "mosip.syncJobId";
    @Mock
    private JobInfo.Builder mockJobInfoBuilder;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);
        when(mockContext.getSystemService(Context.JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        mockJobInfoBuilder = mock(JobInfo.Builder.class);
        when(mockJobInfoBuilder.setRequiresCharging(anyBoolean())).thenReturn(mockJobInfoBuilder);
    }


    @Test(expected = NotImplementedException.class)
    public void scheduleJob_NotImplementedJob_Test() {
        int jobId = 1;
        String apiName = "nonExistentJob";

        jobManagerService.scheduleJob(jobId, apiName, null);
    }

    @Test
    public void getLastSyncTime_Test() {
        int jobId = 1;
        long lastSyncTime = 1609459200L;  // Example timestamp

        when(mockJobTransactionService.getLastSyncTime(jobId)).thenReturn(lastSyncTime);
        when(mockDateUtil.getDateTime(lastSyncTime)).thenReturn("2021-01-01 00:00:00");

        String lastSync = jobManagerService.getLastSyncTime(jobId);

        assertEquals("2021-01-01 00:00:00", lastSync);
    }

    @Test
    public void getNextSyncTime_Test() {
        int jobId = 1;
        long lastSyncTime = 1609459200L;  // Example timestamp
        long nextSyncTime = lastSyncTime + 15 * 60;

        when(mockJobTransactionService.getLastSyncTime(jobId)).thenReturn(lastSyncTime);
        when(mockDateUtil.getDateTime(nextSyncTime)).thenReturn("2021-01-01 00:15:00");

        String nextSync = jobManagerService.getNextSyncTime(jobId);

        assertEquals("2021-01-01 00:15:00", nextSync);
    }
}
