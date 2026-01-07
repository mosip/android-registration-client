package io.mosip.registration.clientmanager.spi;

/**
 * Validates sync status before registration.
 * 
 * @author Sachin S P
 */
public interface SyncStatusValidatorService {

    /**
     * Validates sync job frequencies.
     * 
     * @throws Exception if validation fails
     */
    void validateSyncStatus() throws Exception;

    /**
     * Validates machine distance from registration center using GPS.
     * 
     * @param machineLongitude Machine longitude
     * @param machineLatitude Machine latitude
     * @throws Exception if machine is outside allowed distance
     */
    void validateCenterToMachineDistance(Double machineLongitude, Double machineLatitude) throws Exception;
}

