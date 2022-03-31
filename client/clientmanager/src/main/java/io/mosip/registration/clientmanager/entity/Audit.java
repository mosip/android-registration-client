package io.mosip.registration.clientmanager.entity;

import static androidx.room.ColumnInfo.TEXT;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * The Audit Entity class with required fields to be captured and recorded
 *
 * @author Anshul Vanawat
 *
 */
@Entity(tableName = "app_audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Audit {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "log_id")
    private String uuid;

    @ColumnInfo(name = "log_dtimes")
    private LocalDateTime createdAt;

    @ColumnInfo(name = "event_id")
    private String eventId;

    @ColumnInfo(name = "event_name")
    private String eventName;

    @ColumnInfo(name = "event_type")
    private String eventType;

    @ColumnInfo(name = "action_dtimes")
    private LocalDateTime actionTimeStamp;

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
}