package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.RegistrationCenterDao;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import org.json.JSONObject;
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
public class RegistrationCenterRepositoryTest {

    @Mock
    private RegistrationCenterDao registrationCenterDao;

    private RegistrationCenterRepository registrationCenterRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        registrationCenterRepository = new RegistrationCenterRepository(registrationCenterDao);
    }

    @Test
    public void testGetRegistrationCenter() {
        // Create mock data using correct constructor
        RegistrationCenter center = new RegistrationCenter("10011", "en");

        List<RegistrationCenter> mockCenters = Arrays.asList(center);
        when(registrationCenterDao.getAllRegistrationCentersById("10011")).thenReturn(mockCenters);

        // Test method
        List<RegistrationCenter> result = registrationCenterRepository.getRegistrationCenter("10011");

        // Assertions
        assertEquals(1, result.size());
        assertEquals("10011", result.get(0).getId());
        assertEquals("en", result.get(0).getLangCode());
    }

    @Test
    public void testGetRegistrationCenterByCenterIdAndLangCode() {
        // Create mock data using correct constructor
        RegistrationCenter center = new RegistrationCenter("10011", "en");

        when(registrationCenterDao.getRegistrationCenterByCenterIdAndLangCode("10011", "en"))
                .thenReturn(center);

        // Test method
        RegistrationCenter result = registrationCenterRepository.getRegistrationCenterByCenterIdAndLangCode("10011", "en");

        // Assertions
        assertNotNull(result);
        assertEquals("10011", result.getId());
        assertEquals("en", result.getLangCode());
    }

    @Test
    public void testSaveRegistrationCenter() throws Exception {
        // Mock JSON object
        JSONObject mockJson = mock(JSONObject.class);
        when(mockJson.getString("id")).thenReturn("10011");
        when(mockJson.getString("langCode")).thenReturn("en");
        when(mockJson.getString("latitude")).thenReturn("33.986608");
        when(mockJson.getString("longitude")).thenReturn("-6.828873");
        when(mockJson.getString("locationCode")).thenReturn("10105");
        when(mockJson.getString("name")).thenReturn("name");
        when(mockJson.getBoolean("isActive")).thenReturn(true);

        // Execute save method
        registrationCenterRepository.saveRegistrationCenter(mockJson);

        // Capture and verify insertion
        ArgumentCaptor<RegistrationCenter> captor = ArgumentCaptor.forClass(RegistrationCenter.class);
        verify(registrationCenterDao, times(1)).insert(captor.capture());

        // Get the captured RegistrationCenter
        RegistrationCenter capturedCenter = captor.getValue();

        // Assertions
        assertEquals("10011", capturedCenter.getId());
        assertEquals("en", capturedCenter.getLangCode());
        assertEquals("33.986608", capturedCenter.getLatitude());
        assertEquals("-6.828873", capturedCenter.getLongitude());
        assertEquals("10105", capturedCenter.getLocationCode());
        assertEquals("name", capturedCenter.getName());
        assertTrue(capturedCenter.getIsActive());
    }
}
