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
import io.mosip.registration.clientmanager.spi.PreCheckValidatorService;

/**
 * Validates pre-check requirements (sync status and GPS location).
 * 
 * @author Sachin S P
 */
@Singleton
public class PreCheckValidatorServiceImpl implements PreCheckValidatorService {

    private static final String TAG = PreCheckValidatorServiceImpl.class.getSimpleName();

    private Context context;
    private SyncJobDefRepository syncJobDefRepository;
    private GlobalParamRepository globalParamRepository;
    private JobManagerService jobManagerService;
    private JobTransactionService jobTransactionService;
    private LocationValidationService locationValidationService;
    private MasterDataService masterDataService;
    private RegistrationCenterRepository registrationCenterRepository;

    @Inject
    public PreCheckValidatorServiceImpl(
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
        try {
            validatingSyncJobsConfig();
        } catch (ClientCheckedException e) {
            Log.e(TAG, "Sync status validation failed", e);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during sync status validation", e);
            throw new ClientCheckedException(
                RegistrationConstants.OPT_TO_REG_TIME_SYNC_EXCEED,
                "Sync validation error: " + e.getMessage());
        }
    }

    /**
     * Validates sync jobs are within frequency limits.
     * Only checks jobs with sync history.
     * 
     * @throws ClientCheckedException if any job is overdue
     */
    private void validatingSyncJobsConfig() throws Exception {
        List<SyncJobDef> activeJobs = syncJobDefRepository.getActiveSyncJobs();
        if (activeJobs == null || activeJobs.isEmpty()) {
            return;
        }

        Map<String, String> jobFrequencyMap = getSyncJobFrequencies(activeJobs);
        if (jobFrequencyMap.isEmpty()) {
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
                continue;
            }

            try {
                int configuredFrequency = Integer.parseInt(configuredFrequencyStr.trim());
                int serviceJobId = jobManagerService.generateJobServiceId(jobId);
                long lastSyncTimeMillis = jobTransactionService.getLastSyncTime(serviceJobId);

                if (lastSyncTimeMillis == 0) {
                    // Job has never been synced - skip validation
                    continue;
                }

                Date lastSyncDate = new Date(lastSyncTimeMillis);
                int actualDays = getActualDays(lastSyncDate);

                if (actualDays > configuredFrequency) {
                    syncFailureCount++;
                    errorDetails.append("- ").append(apiName)
                        .append(": Last sync was ").append(actualDays)
                        .append(" days ago (limit: ").append(configuredFrequency).append(" days)\n");
                }

            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid frequency value for job: " + jobId + " (" + apiName + "): " + configuredFrequencyStr, e);
                syncFailureCount++;
                errorDetails.append("- ").append(apiName)
                    .append(": Invalid frequency configuration value: ").append(configuredFrequencyStr).append("\n");
            } catch (Exception e) {
                Log.e(TAG, "Error validating job: " + jobId + " (" + apiName + ")", e);
                syncFailureCount++;
                errorDetails.append("- ").append(apiName)
                    .append(": Validation error: ").append(e.getMessage()).append("\n");
            }
        }
        if (syncFailureCount > 0) {
            String errorMessage = "Registration blocked: " + syncFailureCount + 
                " sync job(s) are overdue:\n" + errorDetails.toString();
            Log.e(TAG, errorMessage);
            throw new ClientCheckedException(RegistrationConstants.OPT_TO_REG_TIME_SYNC_EXCEED, errorMessage);
        }
    }

    /**
     * Gets sync job frequencies from config.
     * 
     * @param activeJobs List of active sync jobs
     * @return Map of jobId to frequency value
     */
    private Map<String, String> getSyncJobFrequencies(List<SyncJobDef> activeJobs) {
        Map<String, String> jobsMap = new HashMap<>();

        for (SyncJobDef syncJobDef : activeJobs) {
            String jobId = syncJobDef.getId();
            String apiName = syncJobDef.getApiName();

            if (apiName == null || apiName.trim().isEmpty()) {
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
            }
        }

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
        String enableFlag = globalParamRepository.getCachedStringGpsDeviceEnableFlag();
        if (enableFlag == null || !"Y".equalsIgnoreCase(enableFlag)) {
            return;
        }

        // If GPS is enabled, machine coordinates are required
        if (machineLongitude == null || machineLatitude == null) {
            Log.e(TAG, "GPS validation enabled but machine coordinates not available");
            throw new ClientCheckedException(
                RegistrationConstants.OPT_TO_REG_OUTSIDE_LOCATION,
                context.getString(R.string.err_003));
        }

        CenterMachineDto centerMachineDto = masterDataService.getRegistrationCenterMachineDetails();
        if (centerMachineDto == null) {
            Log.e(TAG, "GPS validation enabled but center details not found");
            throw new ClientCheckedException(context, R.string.err_004);
        }

        List<RegistrationCenter> centers = registrationCenterRepository.getRegistrationCenter(
            centerMachineDto.getCenterId());

        if (centers == null || centers.isEmpty()) {
            Log.e(TAG, "GPS validation enabled but center not found");
            throw new ClientCheckedException(context, R.string.err_004);
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

            if (distanceMeters > maxAllowedDistance) {
                Log.e(TAG, String.format(
                    "Distance validation failed - Machine: %.6f,%.6f | Center: %.6f,%.6f | Distance: %.2f m (limit: %.2f m)",
                    machineLatitude, machineLongitude, centerLatitude, centerLongitude,
                    distanceMeters, maxAllowedDistance));
                throw new ClientCheckedException(
                    RegistrationConstants.OPT_TO_REG_OUTSIDE_LOCATION,
                    context.getString(R.string.err_003));
            }

        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid number format in center coordinates or max distance configuration", e);
            throw new ClientCheckedException(context, R.string.err_004);
        }
    }

}

