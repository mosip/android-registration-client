package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.SyncJobDefDao;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SyncJobDefRepositoryTest {

    @Mock
    private SyncJobDefDao syncJobDefDao;

    private SyncJobDefRepository syncJobDefRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        syncJobDefRepository = new SyncJobDefRepository(syncJobDefDao);
    }

    @Test
    public void testSaveSyncJobDef() {
        // Using the correct constructor
        SyncJobDef syncJobDef = new SyncJobDef("RC_001");
        syncJobDef.setName("Test Job");

        syncJobDefRepository.saveSyncJobDef(syncJobDef);

        // Capture and verify
        ArgumentCaptor<SyncJobDef> captor = ArgumentCaptor.forClass(SyncJobDef.class);
        verify(syncJobDefDao, times(1)).insert(captor.capture());

        SyncJobDef capturedSyncJob = captor.getValue();
        assertEquals("RC_001", capturedSyncJob.getId());
        assertEquals("Test Job", capturedSyncJob.getName());
    }

    @Test
    public void testGetAllSyncJobDefList() {
        // Using the correct constructor
        SyncJobDef job1 = new SyncJobDef("RC_001");
        job1.setName("Job One");

        SyncJobDef job2 = new SyncJobDef("RC_002");
        job2.setName("Job Two");

        List<SyncJobDef> mockJobs = Arrays.asList(job1, job2);
        when(syncJobDefDao.findAll()).thenReturn(mockJobs);

        List<SyncJobDef> result = syncJobDefRepository.getAllSyncJobDefList();

        assertEquals(2, result.size());
        assertEquals("RC_001", result.get(0).getId());
        assertEquals("Job One", result.get(0).getName());
        assertEquals("RC_002", result.get(1).getId());
        assertEquals("Job Two", result.get(1).getName());
    }

    @Test
    public void testGetActiveSyncJobs() {
        SyncJobDef activeJob = new SyncJobDef("RC_001");
        activeJob.setName("Active Job");
        activeJob.setIsActive(true);
        List<SyncJobDef> activeJobs = Arrays.asList(activeJob);
        when(syncJobDefDao.findAllByActiveStatus(true)).thenReturn(activeJobs);

        List<SyncJobDef> result = syncJobDefRepository.getActiveSyncJobs();

        assertEquals(1, result.size());
        assertEquals("RC_001", result.get(0).getId());
        assertTrue(result.get(0).getIsActive());
        verify(syncJobDefDao).findAllByActiveStatus(true);
    }

    @Test
    public void testGetSyncJobDefById() {
        SyncJobDef job = new SyncJobDef("JOB_123");
        job.setName("Test Job");
        when(syncJobDefDao.findOneById("JOB_123")).thenReturn(job);

        SyncJobDef result = syncJobDefRepository.getSyncJobDefById("JOB_123");

        assertNotNull(result);
        assertEquals("JOB_123", result.getId());
        assertEquals("Test Job", result.getName());
        verify(syncJobDefDao).findOneById("JOB_123");
    }

    @Test
    public void testGetSyncJobDefById_NotFound() {
        when(syncJobDefDao.findOneById("NON_EXISTENT")).thenReturn(null);

        SyncJobDef result = syncJobDefRepository.getSyncJobDefById("NON_EXISTENT");

        assertNull(result);
        verify(syncJobDefDao).findOneById("NON_EXISTENT");
    }

    @Test
    public void testGetSyncJobDefByApiName() {
        SyncJobDef job = new SyncJobDef("RC_001");
        job.setName("Config Sync");
        job.setApiName("syncConfig");
        when(syncJobDefDao.findOneByApiName("syncConfig")).thenReturn(job);

        SyncJobDef result = syncJobDefRepository.getSyncJobDefByApiName("syncConfig");

        assertNotNull(result);
        assertEquals("RC_001", result.getId());
        assertEquals("syncConfig", result.getApiName());
        verify(syncJobDefDao).findOneByApiName("syncConfig");
    }

    @Test
    public void testGetSyncJobDefByApiName_NotFound() {
        when(syncJobDefDao.findOneByApiName("unknown")).thenReturn(null);

        SyncJobDef result = syncJobDefRepository.getSyncJobDefByApiName("unknown");

        assertNull(result);
        verify(syncJobDefDao).findOneByApiName("unknown");
    }
}
