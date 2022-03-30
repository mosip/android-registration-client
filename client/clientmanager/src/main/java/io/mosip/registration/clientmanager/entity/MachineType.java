package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;

/**
 * This Entity Class contains list of machine types[Desktop,Laptop...] with respect to language code.
 * The data for this table will come through sync from server master table.
 *
 * @author Anshul Vanawat
 */
@Entity(tableName = "machine_type")
@Data
public class MachineType extends RegistrationCommonFields implements Serializable {

    private static final long serialVersionUID = -8541947587557590379L;

    @PrimaryKey
    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "descr")
    private String description;
}
