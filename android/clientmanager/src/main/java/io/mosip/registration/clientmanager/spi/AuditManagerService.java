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
     * Audits the events with custom description arguments.
     * <p>
     * If one or more arguments are provided, they are passed to {@link String#format(String, Object...)}
     * using the {@link AuditEvent#getDescription()} as the format string. This allows events whose
     * description contains one or more <code>%s</code> placeholders to be formatted with multiple
     * values. If the event description does not contain any placeholders, the first argument is used
     * as the full description override.
     *
     * @param auditEventEnum the audit event
     * @param appModuleId    the application module id
     * @param appModuleName  the application module name
     * @param arguments      optional description arguments to be applied to the event description
     */
    void auditWithArguments(AuditEvent auditEventEnum, String appModuleId, String appModuleName, String... arguments);

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