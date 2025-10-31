package io.mosip.registration.clientmanager.jobs;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.mosip.registration.clientmanager.spi.AuditManagerService;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class DeleteAuditLogsJob extends SyncJobServiceBase {

    private static final String TAG = DeleteAuditLogsJob.class.getSimpleName();

    @Inject
    AuditManagerService auditManagerService;

    

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
            // Do not modify AUDIT_EXPORTED_TILL here; rely on existing configured value
            boolean ok = auditManagerService.deleteAuditLogs();
            long nowMs = System.currentTimeMillis();
            long timeStampInSeconds = TimeUnit.MILLISECONDS.toSeconds(nowMs);
            logJobTransaction(jobId, timeStampInSeconds);
            return ok;
        } catch (Exception e) {
            Log.e(TAG, TAG + " failed", e);
        }
        return false;
    }
}


