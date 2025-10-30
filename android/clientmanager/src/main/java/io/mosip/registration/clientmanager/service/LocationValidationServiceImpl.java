package io.mosip.registration.clientmanager.service;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.spi.LocationValidationService;

/**
 * Implementation of LocationValidationService for distance calculation
 * 
 * @author sachin sp
 */
@Singleton
public class LocationValidationServiceImpl implements LocationValidationService {

    private static final String TAG = LocationValidationServiceImpl.class.getSimpleName();

    @Inject
    public LocationValidationServiceImpl() {
    }

    @Override
    public double getDistance(double machineLongitude, double machineLatitude,
                             double centerLongitude, double centerLatitude) {
        double earthRadiusInKM = 6371;
        double longitudeDiff = Math.toRadians(centerLongitude - machineLongitude);
        double latitudeDiff = Math.toRadians(centerLatitude - machineLatitude);

        double var = Math.sin(latitudeDiff / 2) * Math.sin(latitudeDiff / 2) +
                Math.sin(longitudeDiff / 2) * Math.sin(longitudeDiff / 2) * 
                Math.cos(Math.toRadians(machineLatitude)) * Math.cos(Math.toRadians(centerLatitude));
        
        double distance = earthRadiusInKM * (2 * Math.atan2(Math.sqrt(var), Math.sqrt(1 - var)));
        
        Log.i(TAG, String.format("Distance calculated: %.2f km", distance));
        
        return distance;
    }
}

