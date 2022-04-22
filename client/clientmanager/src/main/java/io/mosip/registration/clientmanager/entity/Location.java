package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This Entity Class contains list of locations that are being used in Registration with respect to language code.
 * The data for this table will come through sync from server master table .
 *
 * @author Anshul Vanawat
 */

@Data
@EqualsAndHashCode(callSuper=false)
@Entity(primaryKeys = {"code", "lang_code"}, tableName = "location")
public class Location implements Serializable {

    private static final long serialVersionUID = -5585825705521742941L;

    @NonNull
    @ColumnInfo(name = "code")
    private String code;

    @NonNull
    @ColumnInfo(name = "lang_code")
    private String langCode;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "hierarchy_level")
    private int hierarchyLevel;

    @ColumnInfo(name = "hierarchy_level_name")
    private String hierarchyName;

    @ColumnInfo(name = "parent_loc_code")
    private String parentLocCode;

    @ColumnInfo(name = "is_active")
    private Boolean isActive;

    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;
}
