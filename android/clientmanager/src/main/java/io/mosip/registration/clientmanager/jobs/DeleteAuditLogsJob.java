package io.mosip.registration.clientmanager.jobs;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class DeleteAuditLogsJob extends SyncJobServiceBase {

    private static final String TAG = DeleteAuditLogsJob.class.getSimpleName();

    @Inject
    AuditManagerService auditManagerService;

    @Inject
    GlobalParamRepository globalParamRepository;

    public DeleteAuditLogsJob() {
        configureBuilder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    public boolean triggerJob(int jobId) {
        Log.d(TAG, TAG + " Started");
        try {
            long nowMs = System.currentTimeMillis();
            long threeDaysMs = TimeUnit.DAYS.toMillis(3);
            long cutoff = nowMs - threeDaysMs;
            // Use existing deletion flow that deletes before AUDIT_EXPORTED_TILL
            globalParamRepository.saveGlobalParam(RegistrationConstants.AUDIT_EXPORTED_TILL, String.valueOf(cutoff));
            boolean ok = auditManagerService.deleteAuditLogs(); 

            long timeStampInSeconds = TimeUnit.MILLISECONDS.toSeconds(nowMs);
            logJobTransaction(jobId, timeStampInSeconds);
            return ok;
        } catch (Exception e) {
            Log.e(TAG, TAG + " failed", e);
        }
        return false;
    }
}


