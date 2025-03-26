package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncRepositoryDao;
import io.mosip.registration.clientmanager.entity.PreRegistrationList;
import io.mosip.registration.clientmanager.service.PreRegistrationDataSyncDaoImpl;

@RunWith(RobolectricTestRunner.class)
public class PreRegistrationDataSyncDaoImplTest {

    @Mock
    private PreRegistrationDataSyncRepositoryDao preRegistrationRepositoryDao;

    @InjectMocks
    private PreRegistrationDataSyncDaoImpl preRegistrationDataSyncDao;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGet() {
        PreRegistrationList mockPreReg = new PreRegistrationList();
        when(preRegistrationRepositoryDao.findByPreRegId("43806531845284")).thenReturn(mockPreReg);

        PreRegistrationList result = preRegistrationDataSyncDao.get("43806531845284");

        assertNotNull(result);
        verify(preRegistrationRepositoryDao, times(1)).findByPreRegId("43806531845284");
    }

    @Test
    public void testGetById() {
        PreRegistrationList mockPreReg = new PreRegistrationList();
        when(preRegistrationRepositoryDao.getById("43806531845284")).thenReturn(mockPreReg);

        PreRegistrationList result = preRegistrationDataSyncDao.getById("43806531845284");

        assertNotNull(result);
        verify(preRegistrationRepositoryDao, times(1)).getById("43806531845284");
    }

    @Test
    public void testSave() {
        PreRegistrationList preReg = new PreRegistrationList();
        doNothing().when(preRegistrationRepositoryDao).insert(preReg);

        preRegistrationDataSyncDao.save(preReg);

        verify(preRegistrationRepositoryDao, times(1)).insert(preReg);
    }

    @Test
    public void testFetchRecordsToBeDeleted() {
        List<PreRegistrationList> mockList = Arrays.asList(new PreRegistrationList(), new PreRegistrationList());
        when(preRegistrationRepositoryDao.findByAppointmentDateBeforeAndIsDeleted("2024-01-01", false))
                .thenReturn(mockList);

        List<PreRegistrationList> result = preRegistrationDataSyncDao.fetchRecordsToBeDeleted("2024-01-01");

        assertEquals(2, result.size());
        verify(preRegistrationRepositoryDao, times(1)).findByAppointmentDateBeforeAndIsDeleted("2024-01-01", false);
    }

    @Test
    public void testUpdate() {
        when(preRegistrationRepositoryDao.update("43806531845284", "admin", "2024-03-13T10:00:00")).thenReturn(1L);

        long updatedRows = preRegistrationDataSyncDao.update("43806531845284", "admin", "2024-03-13T10:00:00");

        assertEquals(1, updatedRows);
        verify(preRegistrationRepositoryDao, times(1)).update("43806531845284", "admin", "2024-03-13T10:00:00");
    }

    @Test
    public void testGetLastPreRegPacketDownloadedTime() {
        PreRegistrationList mockPreReg = new PreRegistrationList();
        mockPreReg.setLastUpdatedPreRegTimeStamp("2024-03-13T12:00:00");

        when(preRegistrationRepositoryDao.findTopByOrderByLastUpdatedPreRegTimeStampDesc()).thenReturn(mockPreReg);

        String result = preRegistrationDataSyncDao.getLastPreRegPacketDownloadedTime();

        assertEquals("2024-03-13T12:00:00", result);
        verify(preRegistrationRepositoryDao, times(1)).findTopByOrderByLastUpdatedPreRegTimeStampDesc();
    }

    @Test
    public void testGetLastPreRegPacketDownloadedTime_NullCase() {
        when(preRegistrationRepositoryDao.findTopByOrderByLastUpdatedPreRegTimeStampDesc()).thenReturn(null);

        String result = preRegistrationDataSyncDao.getLastPreRegPacketDownloadedTime();

        assertNull(result);
        verify(preRegistrationRepositoryDao, times(1)).findTopByOrderByLastUpdatedPreRegTimeStampDesc();
    }
}
