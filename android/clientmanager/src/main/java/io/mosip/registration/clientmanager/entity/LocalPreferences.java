package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

/**
 * The Entity Class for Local Preferences
 * Stores user-specific configuration overrides that can override global parameters
 */
@Entity(tableName = "local_preferences")
@Data
public class LocalPreferences {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "val")
    private String val;

    @ColumnInfo(name = "config_type")
    private String configType;

    @ColumnInfo(name = "machine_name")
    private String machineName;

    @ColumnInfo(name = "cr_by")
    private String crBy;

    @ColumnInfo(name = "cr_dtime")
    private Long crDtime;

    @ColumnInfo(name = "upd_by")
    private String updBy;

    @ColumnInfo(name = "upd_dtimes")
    private Long updDtimes;

    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;

    @ColumnInfo(name = "del_dtimes")
    private Long delDtimes;
}