package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;

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
     * @param refId          the ref id of the audit event
     * @param refIdType      the ref id type of the audit event
     */
    void audit(AuditEvent auditEventEnum, Components appModuleEnum, String refId,
               String refIdType);

    /**
     * Audits the events across Registration-Client Module.
     *
     * @param auditEventEnum this {@code Enum} contains the event details namely eventId,
     *                       eventType and eventName
     * @param appModuleEnum  this {@code Enum} contains the application module details namely
     *                       moduleId and moduleName
     */
    void audit(AuditEvent auditEventEnum, Components appModuleEnum);

    /**
     * Delete Audit Logs
     *
     * @return true if deleted
     */
    boolean deleteAuditLogs();

}