package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(tableName = "user_pwd")
@Data
@EqualsAndHashCode(callSuper=false)
public class UserPassword implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "usr_id")
    private String usrId;

    @ColumnInfo(name = "salt")
    private String salt;

    @ColumnInfo(name = "pwd")
    private String pwd;

    @ColumnInfo(name = "status_code")
    private String statusCode;

    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;

    @ColumnInfo(name = "del_dtimes")
    private String delDtimes;

    @ColumnInfo(name = "UPD_DTIMES")
    protected String updDtimes;

    @ColumnInfo(name = "IS_ACTIVE")
    private Boolean isActive;

    @ColumnInfo(name = "CR_BY")
    protected String crBy;

    @ColumnInfo(name = "CR_DTIMES")
    protected String crDtime;

    @ColumnInfo(name = "UPD_BY")
    protected String updBy;
}
