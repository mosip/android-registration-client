package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Entity class for sync job def
 *
 * @author Anshul vanawat
 */

@Entity(tableName = "sync_job_def")
@Data
@EqualsAndHashCode(callSuper = false)
public class SyncJobDef {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "api_name")
    private String apiName;
    @ColumnInfo(name = "parent_syncjob_id")
    private String parentSyncJobId;
    @ColumnInfo(name = "sync_freq")
    private String syncFreq;
    @ColumnInfo(name = "lock_duration")
    private String lockDuration;
    @ColumnInfo(name = "lang_code")
    private String langCode;
    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;
    @ColumnInfo(name = "is_active")
    private Boolean isActive;

}
