package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

/**
 * The Entity Class for Permitted Local Config
 * Stores server-defined permitted configuration values that can be overridden locally
 */
@Entity(tableName = "permitted_local_config")
@Data
public class PermittedLocalConfig {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "config_type")
    private String type;

    @ColumnInfo(name = "is_active")
    private Boolean isActive;

    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;

    @ColumnInfo(name = "del_dtimes")
    private Long delDtimes;
}
