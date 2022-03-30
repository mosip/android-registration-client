package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

/**
 * This Entity class contains list of center types with respect to language code.
 * The data for this table will come through sync from server master table.
 *
 * @author Anshul Vanawat
 */

@Entity(tableName = "reg_center_type", primaryKeys = {"code", "langCode"})
@Data
public class RegistrationCenterType extends RegistrationCommonFields {

    private static final long serialVersionUID = 7869240207930949234L;

    @PrimaryKey
    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "lang_code")
    private String langCode;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "descr")
    private String descr;
}
