package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class MachineMaster extends RegistrationCommonFields implements Serializable {

    private static final long serialVersionUID = -5585825705521742941L;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "lang_code")
    private String langCode;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "validity_end_dtimes")
    private LocalDateTime validityDateTime;

    @ColumnInfo(name = "reg_cntr_id")
    private String regCenterId;
}
