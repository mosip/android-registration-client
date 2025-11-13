package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link LocationValidationServiceImpl}.
 */
public class LocationValidationServiceImplTest {

    private LocationValidationServiceImpl locationValidationService;

    @Before
    public void setUp() {
        locationValidationService = new LocationValidationServiceImpl();
    }

    @Test
    public void testGetDistance_sameCoordinates_returnsZero() {
        double result = locationValidationService.getDistance(77.0, 12.0, 77.0, 12.0);
        assertEquals(0.0, result, 0.0001);
    }

    @Test
    public void testGetDistance_knownCoordinates_matchesExpected() {
        // Approx distance between Bangalore (12.9716, 77.5946) and Chennai (13.0827, 80.2707) ~ 290 km
        double result = locationValidationService.getDistance(77.5946, 12.9716, 80.2707, 13.0827);
        assertEquals(290.0, result, 5.0); // Allow +/- 5 km tolerance
    }

    @Test
    public void testGetDistance_symmetryProperty() {
        double forward = locationValidationService.getDistance(72.8777, 19.0760, 77.2090, 28.6139); // Mumbai -> Delhi
        double reverse = locationValidationService.getDistance(77.2090, 28.6139, 72.8777, 19.0760); // Delhi -> Mumbai
        assertEquals(forward, reverse, 0.0001);
    }

    @Test
    public void testGetDistance_returnsPositiveValueForDistinctPoints() {
        double result = locationValidationService.getDistance(-0.1276, 51.5074, 2.3522, 48.8566); // London -> Paris
        assertTrue(result > 0);
    }
}