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
@Data
@Entity(tableName = "loc_hierarchy_list", primaryKeys = {"hierarchyLevel", "hierarchyLevelName", "langCode"})
public class LocationHierarchy extends RegistrationCommonFields implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "hierarchy_level")
    private Integer hierarchyLevel;

    @ColumnInfo(name = "hierarchy_level_name")
    private String hierarchyLevelName;

    @ColumnInfo(name = "lang_code")
    private String langCode;
}
