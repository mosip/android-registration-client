package io.mosip.registration.clientmanager.entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity(primaryKeys = {"level", "level_name", "lang_code"}, tableName = "location_hierarchy")
public class LocationHierarchy {

    @NonNull
    @ColumnInfo(name = "level")
    private int hierarchyLevel;

    @NonNull
    @ColumnInfo(name = "level_name")
    private String hierarchyLevelName;

    @NonNull
    @ColumnInfo(name = "lang_code")
    private String langCode;
}
