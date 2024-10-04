package io.mosip.registration.clientmanager.spi;

import java.util.List;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.entity.Audit;

/**
 * The wrapper interface to log the audits
 *
 * @author Sahil Gaikwad
 * @author Anshul Vanawat
 * @since 1.0.0
 */
public interface AuditManagerService {

    /**
     * Audits the events across Registration-Client Module.
     *
     * @param auditEventEnum this {@code Enum} contains the event details namely eventId,
     *                       eventType and eventName
     * @param appModuleEnum  this {@code Enum} contains the application module details namely
     *                       moduleId and moduleName
     */
    void audit(AuditEvent auditEventEnum, Components appModuleEnum);

    void audit(AuditEvent auditEventEnum, Components appModuleEnum, String errorMsg);

    /**
     * Audits the events across Registration-Client Module.
     *
     * @param auditEventEnum this {@code Enum} contains the event details namely eventId,
     *                       eventType and eventName
     * @param appModuleId    this contains appModuleId
     * @param appModuleName  this contains appModuleName
     * @param refId          the ref id of the audit event
     * @param refIdType      the ref id type of the audit event
     */
    void audit(AuditEvent auditEventEnum, String appModuleId, String appModuleName, String refId,
               String refIdType);

    void audit(AuditEvent auditEventEnum, String appModuleId, String appModuleName);

    void audit(AuditEvent auditEventEnum, String appModuleId, String appModuleName, String errorMsg);


    /**
     * Delete Audit Logs
     *
     * @return true if deleted
     */
    boolean deleteAuditLogs();

    /**
     * Delete Audit Logs
     *
     * @return true if deleted
     */
    List<Audit> getAuditLogs(long fromDateTime);

}