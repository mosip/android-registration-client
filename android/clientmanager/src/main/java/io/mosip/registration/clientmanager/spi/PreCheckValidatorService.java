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
     * Validation is skipped if GPS is disabled. If GPS is enabled, both coordinates must be non-null.
     * 
     * @param machineLongitude Machine longitude (required if GPS enabled)
     * @param machineLatitude Machine latitude (required if GPS enabled)
     * @throws Exception if GPS is enabled and coordinates are null, or if machine is outside allowed distance
     */
    void validateCenterToMachineDistance(Double machineLongitude, Double machineLatitude) throws Exception;
}

