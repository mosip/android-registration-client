package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * The Entity Class for Registration details
 *
 * @author Anshul Vanawat
 */
@Entity(tableName = "registration")
@Data
public class Registration extends RegistrationCommonFields {

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
    private LocalDateTime clientStatusDtimes;

    @ColumnInfo(name = "SERVER_STATUS_DTIMES")
    private LocalDateTime serverStatusDtimes;

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
    private LocalDateTime uploadDtimes;

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

}
