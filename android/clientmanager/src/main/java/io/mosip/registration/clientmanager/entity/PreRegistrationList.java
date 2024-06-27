package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


import lombok.Data;
import lombok.NoArgsConstructor;


@Entity(tableName = "pre_registration_list")
@Data
@NoArgsConstructor
public class PreRegistrationList {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;
    @ColumnInfo(name = "prereg_id")
    private String preRegId;
    @ColumnInfo(name = "prereg_type")
    private String preRegType;
    @ColumnInfo(name = "parent_prereg_id")
    private String parentPreRegId;
    @ColumnInfo(name = "appointment_date")
    private String appointmentDate;
    @ColumnInfo(name = "packet_symmetric_key")
    private String packetSymmetricKey;
    @ColumnInfo(name = "status_code")
    private String statusCode;
    @ColumnInfo(name = "status_comment")
    private String statusComment;
    @ColumnInfo(name = "packet_path")
    private String packetPath;
    @ColumnInfo(name = "sjob_id")
    private String sJobId;
    @ColumnInfo(name = "synctrn_id")
    private String synctrnId;

    @ColumnInfo(name = "last_upd_dtimes")
    private String lastUpdatedPreRegTimeStamp;

    @ColumnInfo(name = "lang_code")
    private String langCode;
    @ColumnInfo(name = "is_deleted")
    private Boolean isDeleted;
    @ColumnInfo(name = "del_dtimes")
    private String delDtimes;
    @ColumnInfo(name = "upd_by")
    private String updBy;
    @ColumnInfo(name = "upd_dtimes")
    private String updDtimes;
    @ColumnInfo(name = "is_active")
    private Boolean isActive;
    @ColumnInfo(name = "cr_by")
    private String crBy;
    @ColumnInfo(name = "cr_dtime")
    private String crDtime;

    public String getLastUpdatedPreRegTimeStamp() {
        return lastUpdatedPreRegTimeStamp;
    }

    public void setLastUpdatedPreRegTimeStamp(String lastUpdatedPreRegTimeStamp) {
        this.lastUpdatedPreRegTimeStamp = lastUpdatedPreRegTimeStamp;
    }


}
