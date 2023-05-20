package io.mosip.registration.clientmanager.dto;

import lombok.Data;

/**
 * This class is to capture the time duration for each event
 *
 * @author Anshul Vanawat
 * @since 1.0.0
 */

@Data
public class AuditDto {
    protected String uuid;
    protected Long createdAt;
    protected String eventId;
    protected String eventName;
    protected String eventType;
    protected Long actionTimeStamp;
    protected String hostName;
    protected String hostIp;
    protected String applicationId;
    protected String applicationName;
    protected String sessionUserId;
    protected String sessionUserName;
    protected String id;
    protected String idType;
    protected String createdBy;
    protected String moduleName;
    protected String moduleId;
    protected String description;
}
