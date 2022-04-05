package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

/**
 * The Entity Class for Registration details
 *
 * @author Anshul Vanawat
 */
@Entity(tableName = "registration")
@Data
public class Registration {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "PACKET_ID")
    private String packetId;

    @ColumnInfo(name = "usr_id")
    private String usrId;

    @ColumnInfo(name = "REG_TYPE")
    private String regType;

    @ColumnInfo(name = "PREREG_ID")
    private String preRegId;

    @ColumnInfo(name = "ACK_FILENAME")
    private String ackFilename;

    @ColumnInfo(name = "CLIENT_STATUS")
    private String clientStatus;

    @ColumnInfo(name = "SERVER_STATUS")
    private String serverStatus;

    @ColumnInfo(name = "CLIENT_STATUS_DTIMES")
    private Long clientStatusDtimes;

    @ColumnInfo(name = "SERVER_STATUS_DTIMES")
    private Long serverStatusDtimes;

    @ColumnInfo(name = "CLIENT_STATUS_COMMENT")
    private String clientStatusComment;

    @ColumnInfo(name = "SERVER_STATUS_COMMENT")
    private String serverStatusComment;

    @ColumnInfo(name = "center_id")
    private String centerId;

    @ColumnInfo(name = "approved_by")
    private String approvedBy;

    @ColumnInfo(name = "APPROVER_ROLE_CODE")
    private String approverRoleCode;

    @ColumnInfo(name = "FILE_UPLOAD_STATUS")
    private String fileUploadStatus;

    @ColumnInfo(name = "UPLOAD_COUNT")
    private Short uploadCount;

    @ColumnInfo(name = "UPLOAD_DTIMES")
    private Long uploadDtimes;

    @ColumnInfo(name = "ADDITIONAL_INFO")
    private byte[] additionalInfo;

    @ColumnInfo(name = "APP_ID")
    private String appId;

    @ColumnInfo(name = "ADDITIONAL_INFO_REQ_ID")
    private String additionalInfoReqId;

    @ColumnInfo(name = "ACK_SIGNATURE")
    private String ackSignature;

    @ColumnInfo(name = "HAS_BWORDS")
    private Boolean hasBwords;

    @ColumnInfo(name = "IS_ACTIVE")
    private Boolean isActive;

    @ColumnInfo(name = "CR_BY")
    private String crBy;

    @ColumnInfo(name = "CR_DTIMES")
    private Long crDtime;

    @ColumnInfo(name = "UPD_BY")
    private String updBy;

    @ColumnInfo(name = "UPD_DTIMES")
    private Long updDtimes;

    @Override
    public String toString() {
        return packetId +
                "\nStatus : " + clientStatus +
                "\t\t" + ( serverStatus == null ? "Not Synced" : serverStatus);
    }

}
