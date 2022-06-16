package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Audit Entity class with required fields to be captured and recorded
 *
 * @author Anshul Vanawat
 */
@Entity(tableName = "app_audit_log")
@Data
public class Audit {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "log_id")
    private int uuid;

    @ColumnInfo(name = "log_dtimes")
    private Long createdAt;

    @ColumnInfo(name = "event_id")
    private String eventId;

    @ColumnInfo(name = "event_name")
    private String eventName;

    @ColumnInfo(name = "event_type")
    private String eventType;

    @ColumnInfo(name = "action_dtimes")
    private Long actionTimeStamp;

    @ColumnInfo(name = "host_name")
    private String hostName;

    @ColumnInfo(name = "host_ip")
    private String hostIp;

    @ColumnInfo(name = "app_id")
    private String applicationId;

    @ColumnInfo(name = "app_name")
    private String applicationName;

    @ColumnInfo(name = "session_user_id")
    private String sessionUserId;

    @ColumnInfo(name = "session_user_name")
    private String sessionUserName;

    @ColumnInfo(name = "ref_id")
    private String refId;

    @ColumnInfo(name = "ref_id_type")
    private String refIdType;

    @ColumnInfo(name = "cr_by")
    private String createdBy;

    @ColumnInfo(name = "module_name")
    private String moduleName;

    @ColumnInfo(name = "module_id")
    private String moduleId;

    @ColumnInfo(name = "log_desc")
    private String description;

    public Audit(Long createdAt, String eventId, String eventName, String eventType, Long actionTimeStamp, String hostName, String hostIp, String applicationId, String applicationName, String sessionUserId, String sessionUserName, String refId, String refIdType, String createdBy, String moduleName, String moduleId, String description) {
        this.createdAt = createdAt;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventType = eventType;
        this.actionTimeStamp = actionTimeStamp;
        this.hostName = hostName;
        this.hostIp = hostIp;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.sessionUserId = sessionUserId;
        this.sessionUserName = sessionUserName;
        this.refId = refId;
        this.refIdType = refIdType;
        this.createdBy = createdBy;
        this.moduleName = moduleName;
        this.moduleId = moduleId;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Audit{" +
                "uuid=" + uuid +
                ", createdAt=" + createdAt +
                ", eventId='" + eventId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventType='" + eventType + '\'' +
                ", actionTimeStamp=" + actionTimeStamp +
                ", hostName='" + hostName + '\'' +
                ", hostIp='" + hostIp + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", sessionUserId='" + sessionUserId + '\'' +
                ", sessionUserName='" + sessionUserName + '\'' +
                ", refId='" + refId + '\'' +
                ", refIdType='" + refIdType + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", moduleId='" + moduleId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}