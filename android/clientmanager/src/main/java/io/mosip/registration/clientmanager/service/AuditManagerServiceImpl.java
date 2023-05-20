package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.config.SessionManager;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.AuditReferenceIdTypes;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.entity.Audit;
import io.mosip.registration.clientmanager.repository.AuditRepository;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;

/**
 * Class to Audit the events of Android Registration Client.
 *
 * @author Anshul Vanawat
 * @since 1.0.0
 */

@Singleton
public class AuditManagerServiceImpl implements AuditManagerService {

    private String TAG = AuditManagerServiceImpl.class.getSimpleName();
    public static final String REGISTRATION_EVENTS = "REG-EVT";
    private static final String COLON_SEPARATED_DESCRIPTION = "%s: %s";

    private Context context;
    private AuditRepository auditRepository;
    private GlobalParamRepository globalParamRepository;

    @Inject
    public AuditManagerServiceImpl(Context context, AuditRepository auditRepository, GlobalParamRepository globalParamRepository) {
        this.auditRepository = auditRepository;
        this.globalParamRepository = globalParamRepository;
        this.context = context;
    }

    @Override
    public void audit(AuditEvent auditEventEnum, Components appModuleEnum) {
        audit(auditEventEnum, appModuleEnum.getId(), appModuleEnum.getName(), null);
    }

    @Override
    public void audit(AuditEvent auditEventEnum, String appModuleId, String appModuleName) {
        audit(auditEventEnum, appModuleId, appModuleName, null);
    }

    @Override
    public void audit(AuditEvent auditEventEnum, Components appModuleEnum, String errorMsg) {
        audit(auditEventEnum, appModuleEnum.getId(), appModuleEnum.getName(), errorMsg);
    }

    @Override
    public void audit(AuditEvent auditEventEnum, String appModuleId, String appModuleName, String errorMsg) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE);

        String sessionUserId = sharedPreferences.getString(SessionManager.USER_NAME, null);
        String rId = sharedPreferences.getString(SessionManager.RID, null);

        String refId, refIdType;
        if (auditEventEnum.getId().contains(REGISTRATION_EVENTS) && rId != null) {
            refId = rId;
            refIdType = AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId();
        } else if (sessionUserId != null) {
            refId = sessionUserId;
            refIdType = AuditReferenceIdTypes.USER_ID.name();
        } else {
            refId = this.context.getString(R.string.app_name);
            refIdType = AuditReferenceIdTypes.APPLICATION_ID.name();
        }

        addAudit(auditEventEnum, appModuleId, appModuleName, refId, refIdType, null);
    }

    @Override
    public void audit(AuditEvent auditEventEnum, String appModuleId, String appModuleName, String refId, String refIdType) {
        addAudit(auditEventEnum, appModuleId, appModuleName, refId, refIdType, null);
    }

    @Override
    public boolean deleteAuditLogs() {
        Log.i(TAG, "Deletion of Audit Logs Started");

        String tillDate = globalParamRepository.getGlobalParamValue(RegistrationConstants.AUDIT_EXPORTED_TILL);

        if (tillDate != null && !tillDate.isEmpty()) {
            try {
                /* Delete Audits before given Time */
                long tillDateLong = Long.parseLong(tillDate);
                auditRepository.deleteAllAuditsTillDate(tillDateLong);
                Log.i(TAG, "deleteAuditLogs: Deletion of Audit Logs Completed for datetime before : {}" + tillDateLong);
                return true;
            } catch (RuntimeException runtimeException) {
                Log.e(TAG, "deleteAuditLogs: Deletion of Audit Logs failed", runtimeException);
                return false;
            }
        } else {
            Log.e(TAG, "deleteAuditLogs: Deletion of Audit Logs failed, tillDate missing");
            return false;
        }
    }

    @Override
    public List<Audit> getAuditLogs(long fromDateTime) {
        Log.i(TAG, "Deletion of Audit Logs Started");
        return auditRepository.getAuditsFromDate(fromDateTime);
    }

    private void addAudit(AuditEvent auditEventEnum, String appModuleId, String appModuleName, String refId, String refIdType, String errorMsg) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.context.getString(R.string.app_name),
                Context.MODE_PRIVATE);

        String hostName = BuildConfig.BASE_URL;
        String hostIP = BuildConfig.BASE_URL;
        String sessionUserId = sharedPreferences.getString(SessionManager.USER_NAME, null);
        String sessionUserName = sharedPreferences.getString(SessionManager.USER_NAME, null);
        String applicationId = this.context.getString(R.string.app_name);
        String applicationName = this.context.getString(R.string.app_name);
        String description = errorMsg == null ? auditEventEnum.getDescription()
                : String.format(COLON_SEPARATED_DESCRIPTION, auditEventEnum.getDescription(), errorMsg);

        Audit audit = new Audit(
                System.currentTimeMillis(),
                auditEventEnum.getId(),
                auditEventEnum.getName(),
                auditEventEnum.getType(),
                System.currentTimeMillis(),
                hostName,
                hostIP,
                applicationId,
                applicationName,
                sessionUserId,
                sessionUserName,
                refId,
                refIdType,
                sessionUserId,
                appModuleName,
                appModuleId,
                description);

        auditRepository.insertAudit(audit);
    }
}
