package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import io.mosip.registration.clientmanager.entity.id.CodeLanguageId;
import lombok.Data;

/**
 * This Entity Class contains list of locations that are being used in Registration with respect to language code.
 * The data for this table will come through sync from server master table .
 *
 * @author Anshul Vanawat
 */

@Data
@Entity(tableName = "location")
public class Location extends RegistrationCommonFields implements Serializable {

    private static final long serialVersionUID = -5585825705521742941L;

    @Embedded
    @PrimaryKey
    private CodeLanguageId codeLanguageId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "hierarchy_level")
    private int hierarchyLevel;

    @ColumnInfo(name = "hierarchy_level_name")
    private String hierarchyName;

    @ColumnInfo(name = "parent_loc_code")
    private String parentLocCode;
}
