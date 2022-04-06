package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * The Entity Class for RegistrationCommonFields.
 *
 * @author Anshul vanawat
 */
@Data
public class RegistrationCommonFields {

    @ColumnInfo(name = "IS_ACTIVE")
    private Boolean isActive;
    @ColumnInfo(name = "CR_BY")
    protected String crBy;
    @ColumnInfo(name = "CR_DTIMES")
    protected Long crDtime;
    @ColumnInfo(name = "UPD_BY")
    protected String updBy;
    @ColumnInfo(name = "UPD_DTIMES")
    protected Long updDtimes;
}
