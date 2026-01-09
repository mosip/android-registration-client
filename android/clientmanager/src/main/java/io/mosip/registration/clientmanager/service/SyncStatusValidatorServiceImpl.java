package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.spi.LocationValidationService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.SyncStatusValidatorService;

/**
 * Validates sync status before registration.
 * 
 * @author Sachin S P
 */
@Singleton
public class SyncStatusValidatorServiceImpl implements SyncStatusValidatorService {

    private static final String TAG = SyncStatusValidatorServiceImpl.class.getSimpleName();

    private Context context;
    private SyncJobDefRepository syncJobDefRepository;
    private GlobalParamRepository globalParamRepository;
    private JobManagerService jobManagerService;
    private JobTransactionService jobTransactionService;
    private LocationValidationService locationValidationService;
    private MasterDataService masterDataService;
    private RegistrationCenterRepository registrationCenterRepository;

    @Inject
    public SyncStatusValidatorServiceImpl(
            Context context,
            SyncJobDefRepository syncJobDefRepository,
            GlobalParamRepository globalParamRepository,
            JobManagerService jobManagerService,
            JobTransactionService jobTransactionService,
            LocationValidationService locationValidationService,
            MasterDataService masterDataService,
            RegistrationCenterRepository registrationCenterRepository) {
        this.context = context;
        this.syncJobDefRepository = syncJobDefRepository;
        this.globalParamRepository = globalParamRepository;
        this.jobManagerService = jobManagerService;
        this.jobTransactionService = jobTransactionService;
        this.locationValidationService = locationValidationService;
        this.masterDataService = masterDataService;
        this.registrationCenterRepository = registrationCenterRepository;
    }

    /**
     * Validates sync job frequencies.
     * 
     * @throws ClientCheckedException if validation fails
     */
    @Override
    public void validateSyncStatus() throws Exception {
        Log.i(TAG, "Validating sync status started");

        try {
            // Validate sync job frequencies
            validatingSyncJobsConfig();

            Log.i(TAG, "Sync status validation completed successfully");

        } catch (ClientCheckedException e) {
            Log.e(TAG, "Sync status validation failed", e);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during sync status validation", e);
            throw new ClientCheckedException(context, R.string.err_004);
        }
    }

    /**
     * Validates sync jobs are within frequency limits.
     * Only checks jobs with sync history.
     * 
     * @throws ClientCheckedException if any job is overdue
     */
    private void validatingSyncJobsConfig() throws Exception {
        Log.i(TAG, "Validating sync jobs configuration started");

        List<SyncJobDef> activeJobs = syncJobDefRepository.getActiveSyncJobs();
        if (activeJobs == null || activeJobs.isEmpty()) {
            Log.w(TAG, "No active sync jobs found, skipping validation");
            return;
        }

        Map<String, String> jobFrequencyMap = getSyncJobFrequencies(activeJobs);
        if (jobFrequencyMap.isEmpty()) {
            Log.w(TAG, "No sync job frequencies configured, skipping validation");
            return;
        }
        int syncFailureCount = 0;
        StringBuilder errorDetails = new StringBuilder();

        for (SyncJobDef syncJobDef : activeJobs) {
            String jobId = syncJobDef.getId();
            String apiName = syncJobDef.getApiName();

            if (jobId == null || apiName == null) {
                Log.w(TAG, "Skipping job with null id or apiName");
                continue;
            }

            String configuredFrequencyStr = jobFrequencyMap.get(jobId);
            if (configuredFrequencyStr == null || configuredFrequencyStr.trim().isEmpty()) {
                Log.d(TAG, "No frequency configured for job: " + jobId + " (" + apiName + ")");
                continue;
            }

            try {
                int configuredFrequency = Integer.parseInt(configuredFrequencyStr.trim());
                int serviceJobId = jobManagerService.generateJobServiceId(jobId);
                long lastSyncTimeMillis = jobTransactionService.getLastSyncTime(serviceJobId);

                if (lastSyncTimeMillis == 0) {
                    Log.d(TAG, "No sync history for job: " + jobId + " (" + apiName + 
                        ") - Skipping validation");
                    continue;
                }

                Date lastSyncDate = new Date(lastSyncTimeMillis);
                int actualDays = getActualDays(lastSyncDate);

                Log.d(TAG, String.format(
                    "Job [%s] (%s): Configured frequency=%d days, Actual days since last sync=%d",
                    jobId, apiName, configuredFrequency, actualDays));
                if (actualDays > configuredFrequency) {
                    syncFailureCount++;
                    errorDetails.append("- ").append(apiName)
                        .append(": Last sync was ").append(actualDays)
                        .append(" days ago (limit: ").append(configuredFrequency).append(" days)\n");
                    
                    Log.w(TAG, String.format(
                        "Sync job [%s] (%s) is overdue. Configured: %d days, Actual: %d days since last sync",
                        jobId, apiName, configuredFrequency, actualDays));
                }

            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid frequency value for job: " + jobId + " (" + apiName + "): " + configuredFrequencyStr, e);
            } catch (Exception e) {
                Log.e(TAG, "Error validating job: " + jobId + " (" + apiName + ")", e);
            }
        }
        if (syncFailureCount > 0) {
            String errorMessage = "Registration blocked: " + syncFailureCount + 
                " sync job(s) are overdue:\n" + errorDetails.toString();
            Log.e(TAG, errorMessage);
            throw new ClientCheckedException(RegistrationConstants.OPT_TO_REG_TIME_SYNC_EXCEED, errorMessage);
        }

        Log.i(TAG, "All sync jobs validated successfully");
    }

    /**
     * Gets sync job frequencies from config.
     * 
     * @param activeJobs List of active sync jobs
     * @return Map of jobId to frequency value
     */
    private Map<String, String> getSyncJobFrequencies(List<SyncJobDef> activeJobs) {
        Log.i(TAG, "Fetching sync job frequencies started");

        Map<String, String> jobsMap = new HashMap<>();

        for (SyncJobDef syncJobDef : activeJobs) {
            String jobId = syncJobDef.getId();
            String apiName = syncJobDef.getApiName();

            if (apiName == null || apiName.trim().isEmpty()) {
                Log.w(TAG, "Skipping job with null or empty apiName: " + jobId);
                continue;
            }

            String propertyName = RegistrationConstants.MOSIP_REGISTRATION
                + apiName
                + RegistrationConstants.DOT
                + RegistrationConstants.FREQUENCY;

            String configuredValue = globalParamRepository.getCachedStringGlobalParam(propertyName);

            if (configuredValue != null && !configuredValue.trim().isEmpty() 
                && !configuredValue.equalsIgnoreCase("null")) {
                jobsMap.put(jobId, configuredValue.trim());
                Log.d(TAG, String.format(
                    "Loaded frequency for job [%s] (%s): %s days",
                    jobId, apiName, configuredValue.trim()));
            } else {
                Log.d(TAG, "Frequency property not found for job [" + jobId + "] (" + apiName + "): " + propertyName);
            }
        }

        Log.i(TAG, "Fetched " + jobsMap.size() + " sync job frequency configurations");
        return jobsMap;
    }

    /**
     * Calculates days since last sync.
     * 
     * @param lastSyncDate Last sync date
     * @return Days since last sync
     */
    private int getActualDays(Date lastSyncDate) {
        if (lastSyncDate == null) {
            return 0;
        }

        long millisecondsDifference = new Date().getTime() - lastSyncDate.getTime();
        long daysDifference = millisecondsDifference / (24 * 60 * 60 * 1000);
        return (int) daysDifference;
    }

    /**
     * Validates machine distance from registration center.
     * 
     * @param machineLongitude Machine longitude
     * @param machineLatitude Machine latitude
     * @throws ClientCheckedException if machine is outside allowed distance
     */
    @Override
    public void validateCenterToMachineDistance(Double machineLongitude, Double machineLatitude) throws Exception {
        Log.i(TAG, "Validating center to machine distance started");

        String enableFlag = globalParamRepository.getCachedStringGpsDeviceEnableFlag();
        if (enableFlag == null || !"Y".equalsIgnoreCase(enableFlag)) {
            Log.d(TAG, "GPS distance validation not enabled, skipping");
            return;
        }

        if (machineLongitude == null || machineLatitude == null) {
            Log.d(TAG, "Machine GPS location not available, skipping validation");
            return;
        }

        CenterMachineDto centerMachineDto = masterDataService.getRegistrationCenterMachineDetails();
        if (centerMachineDto == null) {
            Log.w(TAG, "Center details not found, skipping distance validation");
            return;
        }

        List<RegistrationCenter> centers = registrationCenterRepository.getRegistrationCenter(
            centerMachineDto.getCenterId());

        if (centers == null || centers.isEmpty()) {
            Log.w(TAG, "Center not found, skipping distance validation");
            return;
        }

        RegistrationCenter center = centers.get(0);
        String centerLatStr = center.getLatitude();
        String centerLonStr = center.getLongitude();

        if (centerLatStr == null || centerLonStr == null ||
            centerLatStr.isEmpty() || centerLonStr.isEmpty()) {
            Log.e(TAG, "Center coordinates not available");
            throw new ClientCheckedException(context, R.string.err_004);
        }

        try {
            double centerLatitude = Double.parseDouble(centerLatStr);
            double centerLongitude = Double.parseDouble(centerLonStr);

            double distanceKm = locationValidationService.getDistance(
                machineLongitude, machineLatitude,
                centerLongitude, centerLatitude);

            double distanceMeters = distanceKm * 1000;

            String maxDistanceStr = globalParamRepository.getCachedStringMachineToCenterDistance();
            if (maxDistanceStr == null || maxDistanceStr.isEmpty()) {
                Log.e(TAG, "Max allowed distance configuration not found");
                throw new ClientCheckedException(context, R.string.err_004);
            }

            double maxAllowedDistance = Double.parseDouble(maxDistanceStr);

            Log.d(TAG, String.format(
                "Distance validation: Calculated=%.2f meters, Max allowed=%.2f meters",
                distanceMeters, maxAllowedDistance));

            if (distanceMeters > maxAllowedDistance) {
                Log.e(TAG, String.format(
                    "Machine is outside allowed distance: %.2f meters (limit: %.2f meters)",
                    distanceMeters, maxAllowedDistance));
                throw new ClientCheckedException(
                    RegistrationConstants.OPT_TO_REG_OUTSIDE_LOCATION,
                    context.getString(R.string.err_outside_registration_center));
            }

            Log.i(TAG, "Location validated successfully - machine is within allowed distance");

        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid center coordinates format", e);
            throw new ClientCheckedException(context, R.string.err_004);
        }
    }

}

