package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * @author Anshul vanawat
 */

@Entity(tableName = "loc_hierarchy_list", primaryKeys = {"hierarchyLevel", "hierarchyLevelName", "langCode"})
@Data
public class LocationHierarchy extends RegistrationCommonFields implements Serializable {

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "hierarchy_level")
    //length = 36
    private Integer hierarchyLevel;

    @NotNull
    @ColumnInfo(name = "hierarchy_level_name")
    //length=64
    private String hierarchyLevelName;

    @NotNull
    @ColumnInfo(name = "lang_code")
    //length=3
    private String langCode;
}
