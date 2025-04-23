package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.LocationDao;
import io.mosip.registration.clientmanager.dao.LocationHierarchyDao;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.entity.Location;
import io.mosip.registration.clientmanager.entity.LocationHierarchy;
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
public class LocationRepositoryTest {

    @Mock
    private LocationDao locationDao;

    @Mock
    private LocationHierarchyDao locationHierarchyDao;

    private LocationRepository locationRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        locationRepository = new LocationRepository(locationDao, locationHierarchyDao);
    }

    @Test
    public void testGetHierarchyLevel() {
        when(locationHierarchyDao.getHierarchyLevelFromName("Region")).thenReturn(1);
        assertEquals(1, (int) locationRepository.getHierarchyLevel("Region"));
    }

    @Test
    public void testGetLocations_WithParentLocCode() {
        List<GenericValueDto> locations = Arrays.asList(new GenericValueDto("Kenitra","KTA","en"));
        when(locationDao.findAllLocationByParentLocCode("RSK", "en")).thenReturn(locations);
        assertEquals(locations, locationRepository.getLocations("RSK", "en"));
    }

    @Test
    public void testGetLocations_WithoutParentLocCode() {
        List<GenericValueDto> locations = Arrays.asList(new GenericValueDto("Kenitra","KTA","en"));
        when(locationDao.findParentLocation("en")).thenReturn(locations);
        assertEquals(locations, locationRepository.getLocations(null, "en"));
    }

    @Test
    public void testGetLocationsBasedOnHierarchyLevel() {
        List<GenericValueDto> locations = Arrays.asList(new GenericValueDto("Kenitra","KTA","en"));
        when(locationDao.findAllLocationByHierarchyLevel(1, "en")).thenReturn(locations);
        assertEquals(locations, locationRepository.getLocationsBasedOnHierarchyLevel(1, "en"));
    }

    @Test
    public void testGetLocationsByCode() {
        List<GenericValueDto> locations = Arrays.asList(new GenericValueDto("Kenitra","KTA","en"));
        when(locationDao.findAllLocationByCode("KTA")) .thenReturn(locations);
        assertEquals(locations, locationRepository.getLocationsByCode("KTA"));
    }

    @Test
    public void testFindAllLocationsByLangCode() {
        List<Location> locations = Arrays.asList(new Location("1", "en"));
        when(locationDao.findAllLocationsByLangCode("en")).thenReturn(locations);
        assertEquals(locations, locationRepository.findAllLocationsByLangCode("en"));
    }

    @Test
    public void testSaveLocationData() throws Exception {
        JSONObject mockJson = mock(JSONObject.class);
        when(mockJson.getString("code")).thenReturn("KTA");
        when(mockJson.getString("langCode")).thenReturn("en");
        when(mockJson.getString("name")).thenReturn("Kenitra");
        when(mockJson.getInt("hierarchyLevel")).thenReturn(2);
        when(mockJson.getString("hierarchyName")).thenReturn("Province");
        when(mockJson.getBoolean("isActive")).thenReturn(true);
        when(mockJson.optBoolean("isDeleted")).thenReturn(false);
        when(mockJson.getString("parentLocCode")).thenReturn("RSK");

        locationRepository.saveLocationData(mockJson);

        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(locationDao, times(1)).insert(captor.capture());

        Location capturedLocation = captor.getValue();
        assertEquals("KTA", capturedLocation.getCode());
        assertEquals("en", capturedLocation.getLangCode());
        assertEquals("Kenitra", capturedLocation.getName());
        assertEquals(2, capturedLocation.getHierarchyLevel());
        assertEquals("Province", capturedLocation.getHierarchyName());
        assertTrue(capturedLocation.getIsActive());
        assertFalse(capturedLocation.getIsDeleted());
        assertEquals("RSK", capturedLocation.getParentLocCode());
    }

    @Test
    public void testSaveLocationHierarchyData() throws Exception {
        JSONObject mockJson = mock(JSONObject.class);
        when(mockJson.getInt("hierarchyLevel")).thenReturn(2);
        when(mockJson.getString("hierarchyLevelName")).thenReturn("Province");
        when(mockJson.getString("langCode")).thenReturn("en");

        locationRepository.saveLocationHierarchyData(mockJson);

        ArgumentCaptor<LocationHierarchy> captor = ArgumentCaptor.forClass(LocationHierarchy.class);
        verify(locationHierarchyDao, times(1)).insert(captor.capture());

        LocationHierarchy capturedHierarchy = captor.getValue();
        assertEquals(2, capturedHierarchy.getHierarchyLevel());
        assertEquals("Province", capturedHierarchy.getHierarchyLevelName());
        assertEquals("en", capturedHierarchy.getLangCode());
    }
}
