package io.mosip.registration.clientmanager.spi;

/**
 * Service interface for location validation and distance calculation
 * 
 * @author sachin sp
 */
public interface LocationValidationService {

    /**
     * Calculate distance between two GPS coordinates using Haversine formula
     * 
     * @param machineLongitude Machine's current longitude
     * @param machineLatitude Machine's current latitude
     * @param centerLongitude Registration center's longitude
     * @param centerLatitude Registration center's latitude
     * @return Distance in kilometers
     */
    double getDistance(double machineLongitude, double machineLatitude,
                      double centerLongitude, double centerLatitude);
}

