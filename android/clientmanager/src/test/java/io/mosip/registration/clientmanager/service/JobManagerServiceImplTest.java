package io.mosip.registration.clientmanager.service;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.spi.LocalConfigService;
import io.mosip.registration.clientmanager.util.CronExpressionParser;
import io.mosip.registration.clientmanager.util.DateUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Mock
    private LocalConfigService mockLocalConfigService;

    private JobManagerServiceImpl jobManagerService;

    private static final String JOB_ID = "mosip.syncJobId";

    @Mock
    private JobInfo.Builder mockJobInfoBuilder;

    @Mock
    private JobInfo mockJobInfo;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        mockJobInfoBuilder = mock(JobInfo.Builder.class);
        when(mockJobInfoBuilder.setRequiresCharging(anyBoolean())).thenReturn(mockJobInfoBuilder);
    }


    @Test(expected = NotImplementedException.class)
    public void scheduleJob_withNonExistentApiName_throwsNotImplementedException() {
        int jobId = 1;
        String apiName = "nonExistentJob";

        jobManagerService.scheduleJob(jobId, apiName, null);
    }

    @Test
    public void getLastSyncTime_withValidJobId_returnsFormattedDateTime() {
        int jobId = 1;
        long lastSyncTime = 1609459200L;  // Example timestamp

        when(mockJobTransactionService.getLastSyncTime(jobId)).thenReturn(lastSyncTime);
        when(mockDateUtil.getDateTime(lastSyncTime)).thenReturn("2024-11-27 00:00:00");

        String lastSync = jobManagerService.getLastSyncTime(jobId);

        assertEquals("2024-11-27 00:00:00", lastSync);
    }

    @Test
    public void getNextSyncTime_withInvalidCronFrequency_usesPeriodicFallback() {
        int jobId = 1;
        // Note: Based on the implementation, getLastSyncTime returns milliseconds (despite variable name)
        // JOB_PERIODIC_SECONDS = (15 * 60) * 1000 = 900000 milliseconds
        // So nextSyncTime = lastSyncTime + JOB_PERIODIC_SECONDS
        long lastSyncTime = 1732530600000L;
        long nextSyncTime = lastSyncTime + (15 * 60 * 1000L);

        // Mock repository to return a job def with jobId 1 that has an invalid cron expression
        // This will make getNextSyncTime use the fallback calculation
        SyncJobDef jobDef = new SyncJobDef("00001"); // Last 5 chars = "00001" = jobId 1
        jobDef.setId("00001");
        jobDef.setSyncFreq("15"); // Invalid cron expression, will use fallback
        List<SyncJobDef> jobDefList = new ArrayList<>();
        jobDefList.add(jobDef);
        when(mockSyncJobDefRepository.getAllSyncJobDefList()).thenReturn(jobDefList);

        when(mockJobTransactionService.getLastSyncTime(jobId)).thenReturn(lastSyncTime);
        when(mockDateUtil.getDateTime(nextSyncTime)).thenReturn("2024-11-27 00:15:00");

        String nextSync = jobManagerService.getNextSyncTime(jobId);

        assertEquals("2024-11-27 00:15:00", nextSync);
    }

    @Test
    public void constructor_withNullContext_throwsNullPointerException() {
        Context nullContext = null;

        assertThrows(NullPointerException.class, () -> {
            new JobManagerServiceImpl(
                    nullContext,
                    mockSyncJobDefRepository,
                    mockJobTransactionService,
                    mockDateUtil,
                    mockLocalConfigService
            );
        });
    }

    @Test
    public void refreshAllJobs_withValidRepository_fetchesAllJobDefs() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        List<SyncJobDef> mockJobDefList = new ArrayList<>();
        SyncJobDef jobDef1 = new SyncJobDef("job12345");
        SyncJobDef jobDef2 = new SyncJobDef("job67890");
        mockJobDefList.add(jobDef1);
        mockJobDefList.add(jobDef2);

        when(mockSyncJobDefRepository.getAllSyncJobDefList()).thenReturn(mockJobDefList);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        jobManagerService.refreshAllJobs();

        verify(mockSyncJobDefRepository).getAllSyncJobDefList();
    }

    @Test
    public void refreshAllJobs_withEmptyJobList_doesNotScheduleOrCancel() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        List<SyncJobDef> emptyJobDefList = new ArrayList<>();
        when(mockSyncJobDefRepository.getAllSyncJobDefList()).thenReturn(emptyJobDefList);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        jobManagerService.refreshAllJobs();

        verify(mockSyncJobDefRepository).getAllSyncJobDefList();
        verify(mockJobScheduler, never()).getPendingJob(anyInt());
        verify(mockJobScheduler, never()).schedule(any(JobInfo.class));
    }


    @Test
    public void refreshJobStatus_whenActiveImplementedAndNotScheduled_schedulesJob() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext,
                mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        SyncJobDef jobDef = new SyncJobDef("12345");
        jobDef.setId("12345");
        jobDef.setApiName("packetSyncStatusJob");
        jobDef.setIsActive(true);
        jobDef.setSyncFreq("daily");

        int jobId = 12345;

        JobManagerServiceImpl spyJobManagerService = spy(jobManagerService);
        doReturn(jobId).when(spyJobManagerService).generateJobServiceId(jobDef.getId());
        doReturn(true).when(spyJobManagerService).isJobImplementedOnRegClient(jobDef.getApiName());
        doReturn(false).when(spyJobManagerService).isJobScheduled(jobId);
        doReturn(1).when(spyJobManagerService).scheduleJob(jobId, jobDef.getApiName(), jobDef.getSyncFreq());

        spyJobManagerService.refreshJobStatus(jobDef);

        verify(spyJobManagerService).generateJobServiceId(jobDef.getId());
        verify(spyJobManagerService).isJobImplementedOnRegClient(jobDef.getApiName());
        verify(spyJobManagerService).isJobScheduled(jobId);
        verify(spyJobManagerService).scheduleJob(jobId, jobDef.getApiName(), jobDef.getSyncFreq());
        verify(spyJobManagerService, never()).cancelJob(jobId);
    }

    @Test
    public void refreshJobStatus_withNullIsActive_cancelsJob() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext,
                mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        SyncJobDef jobDef = new SyncJobDef("12345");
        jobDef.setId("12345");
        jobDef.setApiName("packetSyncStatusJob");
        jobDef.setIsActive(null);

        int jobId = 12345;

        JobManagerServiceImpl spyJobManagerService = spy(jobManagerService);
        doReturn(jobId).when(spyJobManagerService).generateJobServiceId(jobDef.getId());
        doNothing().when(spyJobManagerService).cancelJob(jobId);

        spyJobManagerService.refreshJobStatus(jobDef);

        verify(spyJobManagerService).generateJobServiceId(jobDef.getId());
        verify(spyJobManagerService).cancelJob(jobId);
        verify(spyJobManagerService, never()).isJobScheduled(anyInt());
        verify(spyJobManagerService, never()).scheduleJob(anyInt(), anyString(), anyString());
    }

    @Test
    public void scheduleJob_withUnimplementedApiName_throwsNotImplementedException() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext,
                mockSyncJobDefRepository,
                mockJobTransactionService,
                mockDateUtil,
                mockLocalConfigService);

        int jobId = 12345;
        String invalidApiName = "nonExistentJobService";
        String syncFreq = "30";

        NotImplementedException exception = assertThrows(
                NotImplementedException.class,
                () -> jobManagerService.scheduleJob(jobId, invalidApiName, syncFreq)
        );

        assertEquals("Job service : " + invalidApiName + " not implemented", exception.getMessage());
        verify(mockJobScheduler, never()).schedule(any(JobInfo.class));
    }

    @Test
    public void triggerJobService_withJobNotScheduled_schedulesWithNullFreq() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);
        when(mockJobScheduler.getPendingJob(anyInt())).thenReturn(null);

        JobManagerServiceImpl triggerJobService = new JobManagerServiceImpl(mockContext,
                mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        JobManagerServiceImpl spyTriggerJobService = spy(triggerJobService);
        doReturn(JobScheduler.RESULT_SUCCESS).when(spyTriggerJobService).scheduleJob(anyInt(), anyString(), isNull());

        int jobId = 12345;
        String apiName = "packetSyncStatusJob";

        boolean result = spyTriggerJobService.triggerJobService(jobId, apiName);

        verify(mockJobScheduler).getPendingJob(jobId);
        verify(spyTriggerJobService).scheduleJob(jobId, apiName, null);
        assertTrue(result);
    }

    @Test
    public void triggerJobService_whenJobExists_reschedulesAndReturnsTrue() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);
        when(mockJobScheduler.getPendingJob(anyInt())).thenReturn(mockJobInfo);
        when(mockJobScheduler.schedule(any(JobInfo.class))).thenReturn(JobScheduler.RESULT_SUCCESS);

        JobManagerServiceImpl triggerJobService = new JobManagerServiceImpl(mockContext,
                mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        int jobId = 12345;
        String apiName = "packetSyncStatusJob";

        boolean result = triggerJobService.triggerJobService(jobId, apiName);

        verify(mockJobScheduler).getPendingJob(jobId);
        verify(mockJobScheduler).schedule(mockJobInfo);
        assertTrue(result);
    }

    @Test
    public void triggerJobService_withExistingPendingJob_reschedulesAndReturnsTrue() {
        when(mockJobScheduler.getPendingJob(anyInt())).thenReturn(mockJobInfo);
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);
        jobManagerService.jobScheduler = mockJobScheduler;

        boolean result = jobManagerService.triggerJobService(1, "packetSyncStatusJob");

        verify(mockJobScheduler).schedule(mockJobInfo);
        assertTrue(result);
    }

    @Test
    public void triggerJobService_withPendingJobInfo_returnsTrue() {
        when(mockJobScheduler.getPendingJob(anyInt())).thenReturn(mockJobInfo);
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);
        jobManagerService.jobScheduler = mockJobScheduler;

        boolean result = jobManagerService.triggerJobService(1, "packetSyncStatusJob");

        assertTrue(result);
    }

    @Test
    public void cancelJob_withValidJobId_callsSchedulerCancel() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext,
                mockSyncJobDefRepository,
                mockJobTransactionService,
                mockDateUtil,
                mockLocalConfigService
        );

        int validJobId = 12345;

        jobManagerService.cancelJob(validJobId);

        verify(mockJobScheduler, times(1)).cancel(validJobId);
    }

    @Test
    public void cancelJob_withNegativeJobId_callsSchedulerCancel() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext,
                mockSyncJobDefRepository,
                mockJobTransactionService,
                mockDateUtil,
                mockLocalConfigService
        );

        int negativeJobId = -123;

        jobManagerService.cancelJob(negativeJobId);

        verify(mockJobScheduler, times(1)).cancel(negativeJobId);
    }

    @Test
    public void cancelJob_withExistingJob_cancelsSuccessfully() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManager = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        int jobId = 123;
        jobManager.cancelJob(jobId);

        verify(mockJobScheduler).cancel(jobId);
    }

    @Test
    public void refreshJobStatus_withInactiveJob_cancelsJob() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManager = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        SyncJobDef inactiveJobDef = new SyncJobDef("inactive12345");
        inactiveJobDef.setIsActive(false);

        jobManager.refreshJobStatus(inactiveJobDef);

        int jobId = jobManager.generateJobServiceId(inactiveJobDef.getId());
        verify(mockJobScheduler).cancel(jobId);
    }

    @Test
    public void isJobImplementedOnRegClient_withPacketSyncStatusJob_returnsTrue() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        boolean result = jobManagerService.isJobImplementedOnRegClient("packetSyncStatusJob");

        assertTrue(result);
    }

    @Test (expected = NullPointerException.class)
    public void isJobImplementedOnRegClient_withNullApiName_throwsNullPointerException() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);
        boolean result = jobManagerService.isJobImplementedOnRegClient(null);

        assertFalse(result);
    }

    @Test
    public void isJobImplementedOnRegClient_withSynchConfigDataJob_returnsTrue() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);
        boolean result = jobManagerService.isJobImplementedOnRegClient("synchConfigDataJob");

        assertTrue(result);
    }

    @Test
    public void isJobImplementedOnRegClient_withKnownJobName_returnsTrue() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        boolean result = jobManagerService.isJobImplementedOnRegClient("packetSyncStatusJob");

        assertTrue(result);
    }

    @Test
    public void isJobImplementedOnRegClient_withImplementedAndNonImplementedJobs_returnsExpectedBoolean() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        boolean resultForImplementedJob = jobManagerService.isJobImplementedOnRegClient("packetSyncStatusJob");
        boolean resultForNonImplementedJob = jobManagerService.isJobImplementedOnRegClient("nonExistentJob");

        assertTrue(resultForImplementedJob);
        assertFalse(resultForNonImplementedJob);
    }

    @Test
    public void getLastSyncTime_withPositiveTimestamp_returnsFormattedDateTimeString() {
        int jobId = 12345;
        long lastSyncTimeSeconds = 1609459200L;
        String expectedDateTime = "Jan 1, 2021 12:00 AM";

        when(mockJobTransactionService.getLastSyncTime(jobId)).thenReturn(lastSyncTimeSeconds);
        when(mockDateUtil.getDateTime(lastSyncTimeSeconds)).thenReturn(expectedDateTime);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext,
                mockSyncJobDefRepository,
                mockJobTransactionService,
                mockDateUtil,
                mockLocalConfigService);

        String result = jobManagerService.getLastSyncTime(jobId);

        assertEquals(expectedDateTime, result);
        verify(mockJobTransactionService).getLastSyncTime(jobId);
        verify(mockDateUtil).getDateTime(lastSyncTimeSeconds);
    }

    @Test
    public void getLastSyncTime_withZeroTimestamp_returnsNaAndSkipsDateUtil() {
        int jobId = 12345;
        long lastSyncTimeSeconds = 0L;
        String naString = "N/A";

        when(mockJobTransactionService.getLastSyncTime(jobId)).thenReturn(lastSyncTimeSeconds);
        when(mockContext.getString(R.string.NA)).thenReturn(naString);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext,
                mockSyncJobDefRepository,
                mockJobTransactionService,
                mockDateUtil,
                mockLocalConfigService);

        String result = jobManagerService.getLastSyncTime(jobId);

        assertEquals(naString, result);
        verify(mockJobTransactionService).getLastSyncTime(jobId);
        verify(mockContext).getString(R.string.NA);
        verify(mockDateUtil, never()).getDateTime(anyLong());
    }

    @Test
    public void getLastSyncTime_withZeroTimestampVariant_returnsNa() {
        when(mockContext.getString(R.string.NA)).thenReturn("N/A");
        when(mockJobTransactionService.getLastSyncTime(anyInt())).thenReturn(0L);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        String result = jobManagerService.getLastSyncTime(1);

        assertEquals("N/A", result);
    }

    @Test
    public void getLastSyncTime_withValidTimestamp_delegatesToDateUtil() {
        long lastSyncTimeSeconds = 1622548800L;
        String expectedFormattedDate = "01 Jun 2021 12:00 PM";

        when(mockJobTransactionService.getLastSyncTime(anyInt())).thenReturn(lastSyncTimeSeconds);
        when(mockDateUtil.getDateTime(lastSyncTimeSeconds)).thenReturn(expectedFormattedDate);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        String result = jobManagerService.getLastSyncTime(1);

        assertEquals(expectedFormattedDate, result);
    }


    @Test
    public void getLastSyncTime_withValidJobId_delegatesToTransactionService() {
        long lastSyncTimeSeconds = 1622548800L;

        when(mockJobTransactionService.getLastSyncTime(anyInt())).thenReturn(lastSyncTimeSeconds);
        when(mockDateUtil.getDateTime(lastSyncTimeSeconds)).thenReturn("01 Jun 2021 12:00 PM");

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        String result = jobManagerService.getLastSyncTime(1);

        verify(mockJobTransactionService).getLastSyncTime(1);
        assertEquals("01 Jun 2021 12:00 PM", result);
    }

    @Test
    public void generateJobServiceId_withSyncJobDefId_returnsLastFiveCharsAsInt() {

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        String syncJobDefId = "JOB_12345";
        int result = jobManagerService.generateJobServiceId(syncJobDefId);

        assertEquals(12345, result);
    }

    @Test
    public void generateJobServiceId_withNullId_throwsNullPointerException() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        assertThrows(NullPointerException.class, () -> jobManagerService.generateJobServiceId(null));
    }

    @Test
    public void isJobScheduled_withPendingJob_returnsTrue() {
        when(mockJobScheduler.getPendingJob(42)).thenReturn(mockJobInfo);

        JobManagerServiceImpl service = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        assertTrue(service.isJobScheduled(42));
        verify(mockJobScheduler).getPendingJob(42);
    }

    @Test
    public void isJobScheduled_withNoPendingJob_returnsFalse() {
        when(mockJobScheduler.getPendingJob(77)).thenReturn(null);

        JobManagerServiceImpl service = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        assertFalse(service.isJobScheduled(77));
        verify(mockJobScheduler).getPendingJob(77);
    }

    @Test
    public void getAllSyncJobDefList_withValidRepo_delegatesToRepository() {
        List<SyncJobDef> expected = Collections.singletonList(new SyncJobDef("job00123"));
        when(mockSyncJobDefRepository.getAllSyncJobDefList()).thenReturn(expected);

        JobManagerServiceImpl service = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        assertSame(expected, service.getAllSyncJobDefList());
        verify(mockSyncJobDefRepository).getAllSyncJobDefList();
    }

    @Test
    public void getNextSyncTime_withValidCronExpression_usesNextCronFireTime() {
        String cronExpression = "0 0/30 * * * ?";
        SyncJobDef cronJob = new SyncJobDef("cron00001");
        cronJob.setId("cron00001");
        cronJob.setSyncFreq(cronExpression);
        cronJob.setApiName("packetSyncStatusJob");
        when(mockSyncJobDefRepository.getAllSyncJobDefList()).thenReturn(Collections.singletonList(cronJob));
        when(mockJobTransactionService.getLastSyncTime(anyInt())).thenReturn(0L);

        Instant nextExecution = CronExpressionParser.getNextExecutionTime(cronExpression);
        assertNotNull("Cron expression should yield a next execution time", nextExecution);
        long nextExecutionMillis = nextExecution.toEpochMilli();
        when(mockDateUtil.getDateTime(nextExecutionMillis)).thenReturn("formatted-time");

        JobManagerServiceImpl service = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        String result = service.getNextSyncTime(1);

        assertEquals("formatted-time", result);
        verify(mockDateUtil).getDateTime(nextExecutionMillis);
        verify(mockJobTransactionService, never()).getLastSyncTime(1);
    }

    @Test
    public void getNextSyncTime_withMissingJobDef_returnsNaString() {
        when(mockSyncJobDefRepository.getAllSyncJobDefList()).thenReturn(Collections.emptyList());

        JobManagerServiceImpl service = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil, mockLocalConfigService);

        String result = service.getNextSyncTime(999);

        assertEquals("NA", result);
        verify(mockSyncJobDefRepository).getAllSyncJobDefList();
        verify(mockJobTransactionService, never()).getLastSyncTime(anyInt());
    }
}