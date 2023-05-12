package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;

/**
 * This Entity Class contains list of machine related data[mac address, serial number, machine name...]
 * with respect to language code.
 * The data for this table will come through sync from server master table.
 *
 * @author Anshul Vanawat
 */

@Entity(tableName = "machine_master")
@Data
public class MachineMaster implements Serializable {

    private static final long serialVersionUID = -5585825705521742941L;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "lang_code")
    private String langCode;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "validity_date_time")
    private Long validityDateTime;

    @ColumnInfo(name = "reg_center_id")
    private String regCenterId;

    @ColumnInfo(name = "is_active")
    private Boolean isActive;
}
