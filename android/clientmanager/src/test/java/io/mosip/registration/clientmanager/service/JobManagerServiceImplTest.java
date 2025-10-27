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
import io.mosip.registration.clientmanager.util.DateUtil;

import java.util.ArrayList;
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

    private JobManagerServiceImpl jobManagerService;

    private static final String JOB_ID = "mosip.syncJobId";

    @Mock
    private JobInfo.Builder mockJobInfoBuilder;

    @Mock
    private JobInfo mockJobInfo;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

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
        when(mockDateUtil.getDateTime(lastSyncTime)).thenReturn("2024-11-27 00:00:00");

        String lastSync = jobManagerService.getLastSyncTime(jobId);

        assertEquals("2024-11-27 00:00:00", lastSync);
    }

//    @Test
//    public void getNextSyncTime_Test() {
//        int jobId = 1;
//        long lastSyncTime = 1609459200L;  // Example timestamp
//        long nextSyncTime = lastSyncTime + 15 * 60;
//
//        when(mockJobTransactionService.getLastSyncTime(jobId)).thenReturn(lastSyncTime);
//        when(mockDateUtil.getDateTime(nextSyncTime)).thenReturn("2024-11-27 00:15:00");
//
//        String nextSync = jobManagerService.getNextSyncTime(jobId);
//
//        assertEquals("2024-11-27 00:15:00", nextSync);
//    }

    @Test
    public void test_constructor_throws_exception_when_context_is_null() {
        Context nullContext = null;

        assertThrows(NullPointerException.class, () -> {
            new JobManagerServiceImpl(
                    nullContext,
                    mockSyncJobDefRepository,
                    mockJobTransactionService,
                    mockDateUtil
            );
        });
    }

    @Test
    public void test_refresh_all_jobs_fetches_all_sync_job_defs() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        List<SyncJobDef> mockJobDefList = new ArrayList<>();
        SyncJobDef jobDef1 = new SyncJobDef("job12345");
        SyncJobDef jobDef2 = new SyncJobDef("job67890");
        mockJobDefList.add(jobDef1);
        mockJobDefList.add(jobDef2);

        when(mockSyncJobDefRepository.getAllSyncJobDefList()).thenReturn(mockJobDefList);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        jobManagerService.refreshAllJobs();

        verify(mockSyncJobDefRepository).getAllSyncJobDefList();
    }

    @Test
    public void test_refresh_all_jobs_with_empty_list() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        List<SyncJobDef> emptyJobDefList = new ArrayList<>();
        when(mockSyncJobDefRepository.getAllSyncJobDefList()).thenReturn(emptyJobDefList);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        jobManagerService.refreshAllJobs();

        verify(mockSyncJobDefRepository).getAllSyncJobDefList();
        verify(mockJobScheduler, never()).getPendingJob(anyInt());
        verify(mockJobScheduler, never()).schedule(any(JobInfo.class));
    }


    @Test
    public void test_schedule_job_when_active_implemented_and_not_scheduled() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext,
                mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

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
    public void test_cancel_job_when_is_active_null() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext,
                mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

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
    public void test_schedule_job_with_unimplemented_api_name_throws_exception() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext,
                mockSyncJobDefRepository,
                mockJobTransactionService,
                mockDateUtil);

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
    public void test_when_job_not_scheduled_schedule_job_called_with_null_sync_freq() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);
        when(mockJobScheduler.getPendingJob(anyInt())).thenReturn(null);

        JobManagerServiceImpl triggerJobService = new JobManagerServiceImpl(mockContext,
                mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

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
    public void test_when_job_exists_reschedule_job() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);
        when(mockJobScheduler.getPendingJob(anyInt())).thenReturn(mockJobInfo);
        when(mockJobScheduler.schedule(any(JobInfo.class))).thenReturn(JobScheduler.RESULT_SUCCESS);

        JobManagerServiceImpl triggerJobService = new JobManagerServiceImpl(mockContext,
                mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        int jobId = 12345;
        String apiName = "packetSyncStatusJob";

        boolean result = triggerJobService.triggerJobService(jobId, apiName);

        verify(mockJobScheduler).getPendingJob(jobId);
        verify(mockJobScheduler).schedule(mockJobInfo);
        assertTrue(result);
    }

    @Test
    public void test_job_already_scheduled() {
        when(mockJobScheduler.getPendingJob(anyInt())).thenReturn(mockJobInfo);
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);
        jobManagerService.jobScheduler = mockJobScheduler;

        boolean result = jobManagerService.triggerJobService(1, "packetSyncStatusJob");

        verify(mockJobScheduler).schedule(mockJobInfo);
        assertTrue(result);
    }

    @Test
    public void test_job_already_scheduled_always_returns_true() {
        when(mockJobScheduler.getPendingJob(anyInt())).thenReturn(mockJobInfo);
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);
        jobManagerService.jobScheduler = mockJobScheduler;

        boolean result = jobManagerService.triggerJobService(1, "packetSyncStatusJob");

        assertTrue(result);
    }

    @Test
    public void test_cancel_job_with_valid_job_id() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext,
                mockSyncJobDefRepository,
                mockJobTransactionService,
                mockDateUtil
        );

        int validJobId = 12345;

        jobManagerService.cancelJob(validJobId);

        verify(mockJobScheduler, times(1)).cancel(validJobId);
    }

    @Test
    public void test_cancel_job_with_negative_job_id() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext,
                mockSyncJobDefRepository,
                mockJobTransactionService,
                mockDateUtil
        );

        int negativeJobId = -123;

        jobManagerService.cancelJob(negativeJobId);

        verify(mockJobScheduler, times(1)).cancel(negativeJobId);
    }

    @Test
    public void test_cancel_existing_scheduled_job() {
        when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManager = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        int jobId = 123;
        jobManager.cancelJob(jobId);

        verify(mockJobScheduler).cancel(jobId);
    }

    @Test
    public void test_cancel_inactive_job_in_refresh_status() {
       when(mockContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(mockJobScheduler);

        JobManagerServiceImpl jobManager = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        SyncJobDef inactiveJobDef = new SyncJobDef("inactive12345");
        inactiveJobDef.setIsActive(false);

        jobManager.refreshJobStatus(inactiveJobDef);

        int jobId = jobManager.generateJobServiceId(inactiveJobDef.getId());
        verify(mockJobScheduler).cancel(jobId);
    }

    @Test
    public void test_returns_true_for_packet_sync_status_job() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        boolean result = jobManagerService.isJobImplementedOnRegClient("packetSyncStatusJob");

        assertTrue(result);
    }

    @Test (expected = NullPointerException.class)
    public void test_returns_false_for_null_job_api_name() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);
        boolean result = jobManagerService.isJobImplementedOnRegClient(null);

        assertFalse(result);
    }

    @Test
    public void test_returns_true_for_synchConfigDataJob() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);
        boolean result = jobManagerService.isJobImplementedOnRegClient("synchConfigDataJob");

        assertTrue(result);
    }

    @Test
    public void test_identifies_implemented_job_services() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        boolean result = jobManagerService.isJobImplementedOnRegClient("packetSyncStatusJob");

        assertTrue(result);
    }

    @Test
    public void test_method_returns_boolean_value() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        boolean resultForImplementedJob = jobManagerService.isJobImplementedOnRegClient("packetSyncStatusJob");
        boolean resultForNonImplementedJob = jobManagerService.isJobImplementedOnRegClient("nonExistentJob");

        assertTrue(resultForImplementedJob);
        assertFalse(resultForNonImplementedJob);
    }

    @Test
    public void test_returns_formatted_datetime_when_last_sync_time_greater_than_zero() {
        int jobId = 12345;
        long lastSyncTimeSeconds = 1609459200L;
        String expectedDateTime = "Jan 1, 2021 12:00 AM";

        when(mockJobTransactionService.getLastSyncTime(jobId)).thenReturn(lastSyncTimeSeconds);
        when(mockDateUtil.getDateTime(lastSyncTimeSeconds)).thenReturn(expectedDateTime);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext,
                mockSyncJobDefRepository,
                mockJobTransactionService,
                mockDateUtil);

        String result = jobManagerService.getLastSyncTime(jobId);

        assertEquals(expectedDateTime, result);
        verify(mockJobTransactionService).getLastSyncTime(jobId);
        verify(mockDateUtil).getDateTime(lastSyncTimeSeconds);
    }

    @Test
    public void test_returns_na_when_last_sync_time_is_zero() {
        int jobId = 12345;
        long lastSyncTimeSeconds = 0L;
        String naString = "N/A";

        when(mockJobTransactionService.getLastSyncTime(jobId)).thenReturn(lastSyncTimeSeconds);
        when(mockContext.getString(R.string.NA)).thenReturn(naString);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(
                mockContext,
                mockSyncJobDefRepository,
                mockJobTransactionService,
                mockDateUtil);

        String result = jobManagerService.getLastSyncTime(jobId);

        assertEquals(naString, result);
        verify(mockJobTransactionService).getLastSyncTime(jobId);
        verify(mockContext).getString(R.string.NA);
        verify(mockDateUtil, never()).getDateTime(anyLong());
    }

    @Test
    public void test_returns_na_when_last_sync_time_is_zero_or_negative() {
        when(mockContext.getString(R.string.NA)).thenReturn("N/A");
        when(mockJobTransactionService.getLastSyncTime(anyInt())).thenReturn(0L);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        String result = jobManagerService.getLastSyncTime(1);

        assertEquals("N/A", result);
    }

    @Test
    public void test_correctly_formats_timestamp_using_date_util() {
        long lastSyncTimeSeconds = 1622548800L;
        String expectedFormattedDate = "01 Jun 2021 12:00 PM";

        when(mockJobTransactionService.getLastSyncTime(anyInt())).thenReturn(lastSyncTimeSeconds);
        when(mockDateUtil.getDateTime(lastSyncTimeSeconds)).thenReturn(expectedFormattedDate);

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        String result = jobManagerService.getLastSyncTime(1);

        assertEquals(expectedFormattedDate, result);
    }


    @Test
    public void test_retrieves_last_sync_time_seconds_from_service() {
        long lastSyncTimeSeconds = 1622548800L;

        when(mockJobTransactionService.getLastSyncTime(anyInt())).thenReturn(lastSyncTimeSeconds);
        when(mockDateUtil.getDateTime(lastSyncTimeSeconds)).thenReturn("01 Jun 2021 12:00 PM");

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        String result = jobManagerService.getLastSyncTime(1);

        verify(mockJobTransactionService).getLastSyncTime(1);
        assertEquals("01 Jun 2021 12:00 PM", result);
    }

    @Test
    public void test_extract_last_five_chars_and_convert_to_int() {

        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        String syncJobDefId = "JOB_12345";
        int result = jobManagerService.generateJobServiceId(syncJobDefId);

        assertEquals(12345, result);
    }

    @Test
    public void test_throws_exception_when_sync_job_def_id_is_null() {
        JobManagerServiceImpl jobManagerService = new JobManagerServiceImpl(mockContext, mockSyncJobDefRepository, mockJobTransactionService, mockDateUtil);

        assertThrows(NullPointerException.class, () -> {
            jobManagerService.generateJobServiceId(null);
        });
    }

}
