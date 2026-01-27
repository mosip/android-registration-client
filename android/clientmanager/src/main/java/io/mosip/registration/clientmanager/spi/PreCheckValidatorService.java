package io.mosip.registration.clientmanager.spi;

/**
 * Validates pre-check requirements (sync status and GPS location).
 * 
 * @author Sachin S P
 */
public interface PreCheckValidatorService {

    /**
     * Validates sync job frequencies.
     * 
     * @throws Exception if validation fails
     */
    void validateSyncStatus() throws Exception;

    /**
     * Validates machine distance from registration center using GPS.
     * 
     * @param machineLongitude Machine longitude (validation skipped if null)
     * @param machineLatitude Machine latitude (validation skipped if null)
     * @throws Exception if machine is outside allowed distance
     */
    void validateCenterToMachineDistance(Double machineLongitude, Double machineLatitude) throws Exception;
}

