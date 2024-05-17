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

//    public String getId() {
//        return id;
//    }
//    public void setId(String id) {
//        this.id = id;
//    }
//    public String getPreRegId() {
//        return preRegId;
//    }
//    public void setPreRegId(String preRegId) {
//        this.preRegId = preRegId;
//    }
//    public String getPreRegType() {
//        return preRegType;
//    }
//    public void setPreRegType(String preRegType) {
//        this.preRegType = preRegType;
//    }
//    public String getParentPreRegId() {
//        return parentPreRegId;
//    }
//    public void setParentPreRegId(String parentPreRegId) {
//        this.parentPreRegId = parentPreRegId;
//    }
//    public String getAppointmentDate() {
//        return appointmentDate;
//    }
//    public void setAppointmentDate(String appointmentDate) {
//        this.appointmentDate = appointmentDate;
//    }
//    public String getPacketSymmetricKey() {
//        return packetSymmetricKey;
//    }
//    public void setPacketSymmetricKey(String packetSymmetricKey) {
//        this.packetSymmetricKey = packetSymmetricKey;
//    }
//    public String getStatusCode() {
//        return statusCode;
//    }
//    public void setStatusCode(String statusCode) {
//        this.statusCode = statusCode;
//    }
//    public String getStatusComment() {
//        return statusComment;
//    }
//    public void setStatusComment(String statusComment) {
//        this.statusComment = statusComment;
//    }
//    public String getPacketPath() {
//        return packetPath;
//    }
//    public void setPacketPath(String packetPath) {
//        this.packetPath = packetPath;
//    }
//    public String getsJobId() {
//        return sJobId;
//    }
//    public void setsJobId(String sJobId) {
//        this.sJobId = sJobId;
//    }
//    public String getSynctrnId() {
//        return synctrnId;
//    }
//    public void setSynctrnId(String synctrnId) {
//        this.synctrnId = synctrnId;
//    }
//    public String getLangCode() {
//        return langCode;
//    }
//    public void setLangCode(String langCode) {
//        this.langCode = langCode;
//    }
//    public Boolean getIsDeleted() {
//        return isDeleted;
//    }
//    public void setIsDeleted(Boolean isDeleted) {
//        this.isDeleted = isDeleted;
//    }
//    public String getDelDtimes() {
//        return delDtimes;
//    }
//    public void setDelDtimes(String delDtimes) {
//        this.delDtimes = delDtimes;
//    }
//
    public String getLastUpdatedPreRegTimeStamp() {
        return lastUpdatedPreRegTimeStamp;
    }

    public void setLastUpdatedPreRegTimeStamp(String lastUpdatedPreRegTimeStamp) {
        this.lastUpdatedPreRegTimeStamp = lastUpdatedPreRegTimeStamp;
    }


}
