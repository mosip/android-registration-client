package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

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
//schema = "audit"
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Audit extends BaseAudit {

    @NotNull
    @Size(min = 1, max = 64)
    @ColumnInfo(name = "event_id")//, nullable = false, updatable = false, length = 64)
    @NotNull
    private String eventId;

    @NotNull
    @Size(min = 1, max = 128)
    @ColumnInfo(name = "event_name")//, nullable = false, updatable = false, length = 128)
    private String eventName;

    @NotNull
    @Size(min = 1, max = 64)
    @ColumnInfo(name = "event_type")//, nullable = false, updatable = false, length = 64)
    private String eventType;

    @NotNull
    @ColumnInfo(name = "action_dtimes")//, nullable = false, updatable = false)
    private LocalDateTime actionTimeStamp;

    @NotNull
    @Size(min = 1, max = 128)
    @ColumnInfo(name = "host_name")//, nullable = false, updatable = false, length = 128)
    private String hostName;

    @NotNull
    @Size(min = 1, max = 256)
    @ColumnInfo(name = "host_ip")//, nullable = false, updatable = false, length = 256)
    private String hostIp;

    @NotNull
    @Size(min = 1, max = 64)
    @ColumnInfo(name = "app_id")//, nullable = false, updatable = false, length = 64)
    private String applicationId;

    @NotNull
    @Size(min = 1, max = 128)
    @ColumnInfo(name = "app_name")//, nullable = false, updatable = false, length = 128)
    private String applicationName;

    @NotNull
    @Size(min = 1, max = 256)
    @ColumnInfo(name = "session_user_id")//, nullable = false, updatable = false, length = 256)
    private String sessionUserId;

    @Size(max = 128)
    @ColumnInfo(name = "session_user_name")//, updatable = false, length = 128)
    private String sessionUserName;

    @Size( max = 64)
    @ColumnInfo(name = "ref_id")//, nullable = true, updatable = false, length = 64)
    private String id;

    @Size(max = 64)
    @ColumnInfo(name = "ref_id_type")//, nullable = true, updatable = false, length = 64)
    private String idType;

    @NotNull
    @Size(min = 1, max = 256)
    @ColumnInfo(name = "cr_by")//, nullable = false, updatable = false, length = 256)
    private String createdBy;

    @Size(max = 128)
    @ColumnInfo(name = "module_name")//, updatable = false, length = 128)
    private String moduleName;

    @Size(max = 64)
    @ColumnInfo(name = "module_id")//, updatable = false, length = 64)
    private String moduleId;

    @Size(max = 2048)
    @ColumnInfo(name = "log_desc")//, updatable = false, length = 2048)
    private String description;
}