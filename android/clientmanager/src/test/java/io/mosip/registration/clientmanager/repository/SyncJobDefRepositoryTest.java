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
        MockitoAnnotations.initMocks(this);
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
}
