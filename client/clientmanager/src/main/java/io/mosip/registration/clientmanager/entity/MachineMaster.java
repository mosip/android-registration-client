package io.mosip.registration.clientmanager.entity;

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

    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "lang_code")
    private String langCode;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "serial_num")
    private String serialNum;

    @ColumnInfo(name = "ip_address")
    private String ipAddress;

    @ColumnInfo(name = "mac_address")
    private String macAddress;

    @ColumnInfo(name = "mspec_id")
    private String machineSpecId;

    @ColumnInfo(name = "validity_end_dtimes")
    private LocalDateTime validityDateTime;

    @ColumnInfo(name = "public_key" /*, columnDefinition = "CLOB"*/)
    private String publicKey;

    @ColumnInfo(name = "key_index")
    private String keyIndex;

    @ColumnInfo(name = "reg_cntr_id")
    private String regCenterId;
}
