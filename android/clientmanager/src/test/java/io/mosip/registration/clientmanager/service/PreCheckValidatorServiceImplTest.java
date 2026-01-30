package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.spi.LocationValidationService;
import io.mosip.registration.clientmanager.spi.MasterDataService;

/**
 * Unit tests for PreCheckValidatorServiceImpl.
 * 
 * @author Sachin S P
 */
@RunWith(MockitoJUnitRunner.class)
public class PreCheckValidatorServiceImplTest {

    @Mock
    private Context mockContext;

    @Mock
    private SyncJobDefRepository mockSyncJobDefRepository;

    @Mock
    private GlobalParamRepository mockGlobalParamRepository;

    @Mock
    private JobManagerService mockJobManagerService;

    @Mock
    private JobTransactionService mockJobTransactionService;

    @Mock
    private LocationValidationService mockLocationValidationService;

    @Mock
    private MasterDataService mockMasterDataService;

    @Mock
    private RegistrationCenterRepository mockRegistrationCenterRepository;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    @InjectMocks
    private PreCheckValidatorServiceImpl preCheckValidatorService;

    private static final String JOB_ID_1 = "MDS_J00001";
    private static final String JOB_ID_2 = "PDS_J00003";
    private static final String API_NAME_1 = "masterSyncJob";
    private static final String API_NAME_2 = "preRegistrationDataSyncJob";
    private static final String CENTER_ID = "CENTER_001";
    private static final int SERVICE_JOB_ID_1 = 1;
    private static final int SERVICE_JOB_ID_2 = 3;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockContext.getString(R.string.app_name)).thenReturn("RegistrationClient");
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putLong(anyString(), anyLong())).thenReturn(mockEditor);
    }

    // ========== validateSyncStatus() Tests ==========

    @Test
    public void testValidateSyncStatus_Success_AllJobsWithinLimit() throws Exception {
        // Setup: Create active jobs
        List<SyncJobDef> activeJobs = createActiveJobs();
        when(mockSyncJobDefRepository.getActiveSyncJobs()).thenReturn(activeJobs);

        // Setup: Configure frequencies
        when(mockGlobalParamRepository.getCachedStringGlobalParam(
            RegistrationConstants.MOSIP_REGISTRATION + API_NAME_1 + RegistrationConstants.DOT + RegistrationConstants.FREQUENCY))
            .thenReturn("190");
        when(mockGlobalParamRepository.getCachedStringGlobalParam(
            RegistrationConstants.MOSIP_REGISTRATION + API_NAME_2 + RegistrationConstants.DOT + RegistrationConstants.FREQUENCY))
            .thenReturn("190");

        // Setup: Last sync times (within limit - 1 day ago)
        when(mockJobManagerService.generateJobServiceId(JOB_ID_1)).thenReturn(SERVICE_JOB_ID_1);
        when(mockJobManagerService.generateJobServiceId(JOB_ID_2)).thenReturn(SERVICE_JOB_ID_2);
        
        long oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000L);
        when(mockJobTransactionService.getLastSyncTime(SERVICE_JOB_ID_1)).thenReturn(oneDayAgo);
        when(mockJobTransactionService.getLastSyncTime(SERVICE_JOB_ID_2)).thenReturn(oneDayAgo);

        // Execute
        preCheckValidatorService.validateSyncStatus();

        // Verify: No exception thrown
        verify(mockSyncJobDefRepository).getActiveSyncJobs();
    }

    @Test(expected = ClientCheckedException.class)
    public void testValidateSyncStatus_Failure_JobOverdue() throws Exception {
        // Setup: Create active jobs
        List<SyncJobDef> activeJobs = createActiveJobs();
        when(mockSyncJobDefRepository.getActiveSyncJobs()).thenReturn(activeJobs);

        // Setup: Configure frequency (1 day limit)
        when(mockGlobalParamRepository.getCachedStringGlobalParam(
            RegistrationConstants.MOSIP_REGISTRATION + API_NAME_1 + RegistrationConstants.DOT + RegistrationConstants.FREQUENCY))
            .thenReturn("1");

        // Setup: Last sync time (2 days ago - overdue)
        when(mockJobManagerService.generateJobServiceId(JOB_ID_1)).thenReturn(SERVICE_JOB_ID_1);
        long twoDaysAgo = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L);
        when(mockJobTransactionService.getLastSyncTime(SERVICE_JOB_ID_1)).thenReturn(twoDaysAgo);

        // Execute - should throw exception
        preCheckValidatorService.validateSyncStatus();
    }

    @Test
    public void testValidateSyncStatus_NoActiveJobs() throws Exception {
        // Setup: No active jobs
        when(mockSyncJobDefRepository.getActiveSyncJobs()).thenReturn(new ArrayList<>());

        // Execute
        preCheckValidatorService.validateSyncStatus();

        // Verify: No exception, validation skipped
        verify(mockSyncJobDefRepository).getActiveSyncJobs();
    }

    @Test
    public void testValidateSyncStatus_JobWithoutSyncHistory_Skipped() throws Exception {
        // Setup: Create active jobs
        List<SyncJobDef> activeJobs = createActiveJobs();
        when(mockSyncJobDefRepository.getActiveSyncJobs()).thenReturn(activeJobs);

        // Setup: Configure frequency
        when(mockGlobalParamRepository.getCachedStringGlobalParam(
            RegistrationConstants.MOSIP_REGISTRATION + API_NAME_1 + RegistrationConstants.DOT + RegistrationConstants.FREQUENCY))
            .thenReturn("190");

        // Setup: No sync history (returns 0) - job should be skipped
        when(mockJobManagerService.generateJobServiceId(JOB_ID_1)).thenReturn(SERVICE_JOB_ID_1);
        when(mockJobTransactionService.getLastSyncTime(SERVICE_JOB_ID_1)).thenReturn(0L);

        // Execute - should not throw exception, job without history is skipped
        preCheckValidatorService.validateSyncStatus();

        // Verify: No exception - job without history is skipped
        verify(mockSyncJobDefRepository).getActiveSyncJobs();
    }

    @Test
    public void testValidateSyncStatus_NoFrequencyConfigured_Skipped() throws Exception {
        // Setup: Create active jobs
        List<SyncJobDef> activeJobs = createActiveJobs();
        when(mockSyncJobDefRepository.getActiveSyncJobs()).thenReturn(activeJobs);

        // Setup: No frequency configured (returns null)
        when(mockGlobalParamRepository.getCachedStringGlobalParam(anyString())).thenReturn(null);

        // Execute
        preCheckValidatorService.validateSyncStatus();

        // Verify: No exception - job without frequency is skipped
        verify(mockSyncJobDefRepository).getActiveSyncJobs();
    }

    @Test(expected = ClientCheckedException.class)
    public void testValidateSyncStatus_MultipleJobsOverdue() throws Exception {
        // Setup: Create multiple active jobs
        List<SyncJobDef> activeJobs = new ArrayList<>();
        SyncJobDef job1 = new SyncJobDef();
        job1.setId(JOB_ID_1);
        job1.setApiName(API_NAME_1);
        job1.setIsActive(true);
        
        SyncJobDef job2 = new SyncJobDef();
        job2.setId(JOB_ID_2);
        job2.setApiName(API_NAME_2);
        job2.setIsActive(true);
        
        activeJobs.add(job1);
        activeJobs.add(job2);
        
        when(mockSyncJobDefRepository.getActiveSyncJobs()).thenReturn(activeJobs);

        // Setup: Configure frequencies (1 day limit)
        when(mockGlobalParamRepository.getCachedStringGlobalParam(
            RegistrationConstants.MOSIP_REGISTRATION + API_NAME_1 + RegistrationConstants.DOT + RegistrationConstants.FREQUENCY))
            .thenReturn("1");
        when(mockGlobalParamRepository.getCachedStringGlobalParam(
            RegistrationConstants.MOSIP_REGISTRATION + API_NAME_2 + RegistrationConstants.DOT + RegistrationConstants.FREQUENCY))
            .thenReturn("1");

        // Setup: Both jobs overdue (2 days ago)
        when(mockJobManagerService.generateJobServiceId(JOB_ID_1)).thenReturn(SERVICE_JOB_ID_1);
        when(mockJobManagerService.generateJobServiceId(JOB_ID_2)).thenReturn(SERVICE_JOB_ID_2);
        
        long twoDaysAgo = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L);
        when(mockJobTransactionService.getLastSyncTime(SERVICE_JOB_ID_1)).thenReturn(twoDaysAgo);
        when(mockJobTransactionService.getLastSyncTime(SERVICE_JOB_ID_2)).thenReturn(twoDaysAgo);

        // Execute - should throw exception
        try {
            preCheckValidatorService.validateSyncStatus();
        } catch (ClientCheckedException e) {
            // Verify error code
            assertEquals(RegistrationConstants.OPT_TO_REG_TIME_SYNC_EXCEED, e.getErrorCode());
            assertTrue(e.getMessage().contains("2 sync job(s) are overdue"));
            throw e;
        }
    }

    // ========== validateCenterToMachineDistance() Tests ==========

    @Test
    public void testValidateCenterToMachineDistance_GPSDisabled_Skipped() throws Exception {
        // Setup: GPS validation disabled
        when(mockGlobalParamRepository.getCachedStringGpsDeviceEnableFlag()).thenReturn("Y");

        // Execute
        preCheckValidatorService.validateCenterToMachineDistance(77.5946, 12.9716);

        // Verify: Validation skipped
        verify(mockGlobalParamRepository).getCachedStringGpsDeviceEnableFlag();
        verify(mockLocationValidationService, never()).getDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    public void testValidateCenterToMachineDistance_LocationNull_Skipped() throws Exception {
        // Setup: GPS enabled
        when(mockGlobalParamRepository.getCachedStringGpsDeviceEnableFlag()).thenReturn("N");

        // Execute with null location
        preCheckValidatorService.validateCenterToMachineDistance(null, null);

        // Verify: Validation skipped
        verify(mockLocationValidationService, never()).getDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    public void testValidateCenterToMachineDistance_WithinDistance_Success() throws Exception {
        // Setup: GPS enabled
        when(mockGlobalParamRepository.getCachedStringGpsDeviceEnableFlag()).thenReturn("N");

        // Setup: Center details
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId(CENTER_ID);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);

        RegistrationCenter center = new RegistrationCenter();
        center.setId(CENTER_ID);
        center.setLangCode("eng");
        center.setLatitude("12.9716");
        center.setLongitude("77.5946");
        List<RegistrationCenter> centers = new ArrayList<>();
        centers.add(center);
        when(mockRegistrationCenterRepository.getRegistrationCenter(CENTER_ID)).thenReturn(centers);

        // Setup: Distance calculation (within limit - 100 meters)
        when(mockLocationValidationService.getDistance(78.5946, 13.9716, 77.5946, 12.9716))
            .thenReturn(0.1); // 0.1 km = 100 meters

        // Setup: Max allowed distance (500 meters)
        when(mockGlobalParamRepository.getCachedStringMachineToCenterDistance()).thenReturn("500");

        // Execute
        preCheckValidatorService.validateCenterToMachineDistance(78.5946, 13.9716);

        // Verify: No exception thrown
        verify(mockLocationValidationService).getDistance(78.5946, 13.9716, 77.5946, 12.9716);
    }

    @Test(expected = ClientCheckedException.class)
    public void testValidateCenterToMachineDistance_OutsideDistance_ThrowsException() throws Exception {
        // Setup: GPS enabled
        when(mockGlobalParamRepository.getCachedStringGpsDeviceEnableFlag()).thenReturn("N");

        // Setup: Center details
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId(CENTER_ID);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);

        RegistrationCenter center = new RegistrationCenter();
        center.setId(CENTER_ID);
        center.setLangCode("eng");
        center.setLatitude("12.9716");
        center.setLongitude("77.5946");
        List<RegistrationCenter> centers = new ArrayList<>();
        centers.add(center);
        when(mockRegistrationCenterRepository.getRegistrationCenter(CENTER_ID)).thenReturn(centers);

        // Setup: Distance calculation (outside limit - 1000 meters)
        when(mockLocationValidationService.getDistance(78.5946, 13.9716, 77.5946, 12.9716))
            .thenReturn(1.0); // 1.0 km = 1000 meters

        // Setup: Max allowed distance (500 meters)
        when(mockGlobalParamRepository.getCachedStringMachineToCenterDistance()).thenReturn("500");

        // Execute - should throw exception
        try {
            preCheckValidatorService.validateCenterToMachineDistance(78.5946, 13.9716);
        } catch (ClientCheckedException e) {
            // Verify error code
            assertEquals(RegistrationConstants.OPT_TO_REG_OUTSIDE_LOCATION, e.getErrorCode());
            throw e;
        }
    }

    @Test(expected = ClientCheckedException.class)
    public void testValidateCenterToMachineDistance_CenterDetailsNotFound_ThrowsException() throws Exception {
        // Setup: GPS enabled
        when(mockGlobalParamRepository.getCachedStringGpsDeviceEnableFlag()).thenReturn("Y");

        // Setup: Center details not found
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(null);

        // Execute - should throw exception
        preCheckValidatorService.validateCenterToMachineDistance(78.5946, 13.9716);
    }

    @Test(expected = ClientCheckedException.class)
    public void testValidateCenterToMachineDistance_CenterNotFound_ThrowsException() throws Exception {
        // Setup: GPS enabled
        when(mockGlobalParamRepository.getCachedStringGpsDeviceEnableFlag()).thenReturn("Y");

        // Setup: Center details found but center not found in repository
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId(CENTER_ID);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(mockRegistrationCenterRepository.getRegistrationCenter(CENTER_ID)).thenReturn(null);

        // Execute - should throw exception
        preCheckValidatorService.validateCenterToMachineDistance(78.5946, 13.9716);
    }

    @Test(expected = ClientCheckedException.class)
    public void testValidateCenterToMachineDistance_EmptyCentersList_ThrowsException() throws Exception {
        // Setup: GPS enabled
        when(mockGlobalParamRepository.getCachedStringGpsDeviceEnableFlag()).thenReturn("Y");

        // Setup: Center details found but empty centers list
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId(CENTER_ID);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(mockRegistrationCenterRepository.getRegistrationCenter(CENTER_ID)).thenReturn(new ArrayList<>());

        // Execute - should throw exception
        preCheckValidatorService.validateCenterToMachineDistance(78.5946, 13.9716);
    }

    @Test(expected = ClientCheckedException.class)
    public void testValidateCenterToMachineDistance_CenterCoordinatesMissing_ThrowsException() throws Exception {
        // Setup: GPS enabled
        when(mockGlobalParamRepository.getCachedStringGpsDeviceEnableFlag()).thenReturn("N");

        // Setup: Center details
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId(CENTER_ID);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);

        RegistrationCenter center = new RegistrationCenter();
        center.setLatitude(null); // Missing coordinates
        center.setLongitude(null);
        List<RegistrationCenter> centers = new ArrayList<>();
        centers.add(center);
        when(mockRegistrationCenterRepository.getRegistrationCenter(CENTER_ID)).thenReturn(centers);

        // Execute - should throw exception
        preCheckValidatorService.validateCenterToMachineDistance(78.5946, 13.9716);
    }

    @Test(expected = ClientCheckedException.class)
    public void testValidateCenterToMachineDistance_MaxDistanceConfigMissing_ThrowsException() throws Exception {
        // Setup: GPS enabled
        when(mockGlobalParamRepository.getCachedStringGpsDeviceEnableFlag()).thenReturn("N");

        // Setup: Center details
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId(CENTER_ID);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);

        RegistrationCenter center = new RegistrationCenter();
        center.setId(CENTER_ID);
        center.setLangCode("eng");
        center.setLatitude("12.9716");
        center.setLongitude("77.5946");
        List<RegistrationCenter> centers = new ArrayList<>();
        centers.add(center);
        when(mockRegistrationCenterRepository.getRegistrationCenter(CENTER_ID)).thenReturn(centers);

        // Setup: Distance calculation
        when(mockLocationValidationService.getDistance(78.5946, 13.9716, 77.5946, 12.9716))
            .thenReturn(0.1);

        // Setup: Max distance config missing
        when(mockGlobalParamRepository.getCachedStringMachineToCenterDistance()).thenReturn(null);

        // Execute - should throw exception
        preCheckValidatorService.validateCenterToMachineDistance(78.5946, 13.9716);
    }

    @Test
    public void testValidateCenterToMachineDistance_InvalidCoordinateFormat_ThrowsException() throws Exception {
        // Setup: GPS enabled
        when(mockGlobalParamRepository.getCachedStringGpsDeviceEnableFlag()).thenReturn("N");

        // Setup: Center details
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId(CENTER_ID);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);

        RegistrationCenter center = new RegistrationCenter();
        center.setId(CENTER_ID);
        center.setLangCode("eng");
        center.setLatitude("invalid"); // Invalid format
        center.setLongitude("77.5946");
        List<RegistrationCenter> centers = new ArrayList<>();
        centers.add(center);
        when(mockRegistrationCenterRepository.getRegistrationCenter(CENTER_ID)).thenReturn(centers);

        // Execute - should throw exception
        try {
            preCheckValidatorService.validateCenterToMachineDistance(78.5946, 13.9716);
            fail("Expected ClientCheckedException for invalid coordinate format");
        } catch (ClientCheckedException e) {
            // Expected
        }
    }

    @Test
    public void testValidateSyncStatus_JobWithNullId_Skipped() throws Exception {
        // Setup: Create job with null ID
        List<SyncJobDef> activeJobs = new ArrayList<>();
        SyncJobDef job = new SyncJobDef();
        job.setId(null);
        job.setApiName(API_NAME_1);
        job.setIsActive(true);
        activeJobs.add(job);
        
        when(mockSyncJobDefRepository.getActiveSyncJobs()).thenReturn(activeJobs);

        // Execute
        preCheckValidatorService.validateSyncStatus();

        // Verify: No exception, job skipped
        verify(mockSyncJobDefRepository).getActiveSyncJobs();
    }

    @Test
    public void testValidateSyncStatus_JobWithNullApiName_Skipped() throws Exception {
        // Setup: Create job with null apiName
        List<SyncJobDef> activeJobs = new ArrayList<>();
        SyncJobDef job = new SyncJobDef();
        job.setId(JOB_ID_1);
        job.setApiName(null);
        job.setIsActive(true);
        activeJobs.add(job);
        
        when(mockSyncJobDefRepository.getActiveSyncJobs()).thenReturn(activeJobs);

        // Execute
        preCheckValidatorService.validateSyncStatus();

        // Verify: No exception, job skipped
        verify(mockSyncJobDefRepository).getActiveSyncJobs();
    }

    @Test(expected = ClientCheckedException.class)
    public void testValidateSyncStatus_InvalidFrequencyValue_ThrowsException() throws Exception {
        // Setup: Create active jobs
        List<SyncJobDef> activeJobs = createActiveJobs();
        when(mockSyncJobDefRepository.getActiveSyncJobs()).thenReturn(activeJobs);

        // Setup: Invalid frequency value (non-numeric)
        when(mockGlobalParamRepository.getCachedStringGlobalParam(
            RegistrationConstants.MOSIP_REGISTRATION + API_NAME_1 + RegistrationConstants.DOT + RegistrationConstants.FREQUENCY))
            .thenReturn("invalid");

        // Execute - should throw exception due to invalid frequency value
        preCheckValidatorService.validateSyncStatus();
    }

    @Test
    public void testValidateCenterToMachineDistance_ExactDistanceLimit_Passes() throws Exception {
        // Setup: GPS enabled
        when(mockGlobalParamRepository.getCachedStringGpsDeviceEnableFlag()).thenReturn("N");

        // Setup: Center details
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId(CENTER_ID);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);

        RegistrationCenter center = new RegistrationCenter();
        center.setId(CENTER_ID);
        center.setLangCode("eng");
        center.setLatitude("12.9716");
        center.setLongitude("77.5946");
        List<RegistrationCenter> centers = new ArrayList<>();
        centers.add(center);
        when(mockRegistrationCenterRepository.getRegistrationCenter(CENTER_ID)).thenReturn(centers);

        // Setup: Distance exactly at limit (500 meters)
        when(mockLocationValidationService.getDistance(78.5946, 13.9716, 77.5946, 12.9716))
            .thenReturn(0.5); // 0.5 km = 500 meters

        // Setup: Max allowed distance (500 meters)
        when(mockGlobalParamRepository.getCachedStringMachineToCenterDistance()).thenReturn("500");

        // Execute - should pass (distance == limit, not > limit)
        preCheckValidatorService.validateCenterToMachineDistance(78.5946, 13.9716);

        // Verify: No exception thrown
        verify(mockLocationValidationService).getDistance(78.5946, 13.9716, 77.5946, 12.9716);
    }

    // ========== Helper Methods ==========

    private List<SyncJobDef> createActiveJobs() {
        List<SyncJobDef> jobs = new ArrayList<>();
        
        SyncJobDef job1 = new SyncJobDef();
        job1.setId(JOB_ID_1);
        job1.setApiName(API_NAME_1);
        job1.setIsActive(true);
        jobs.add(job1);
        
        SyncJobDef job2 = new SyncJobDef();
        job2.setId(JOB_ID_2);
        job2.setApiName(API_NAME_2);
        job2.setIsActive(true);
        jobs.add(job2);
        
        return jobs;
    }
}

