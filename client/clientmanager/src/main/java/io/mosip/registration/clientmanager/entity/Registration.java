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
    @ColumnInfo(name = "packet_id")
    private String packetId;

    @ColumnInfo(name = "usr_id")
    private String usrId;

    @ColumnInfo(name = "reg_type")
    private String regType;

    @ColumnInfo(name = "prereg_id")
    private String preRegId;

    @ColumnInfo(name = "file_path")
    private String filePath;

    @ColumnInfo(name = "client_status")
    private String clientStatus;

    @ColumnInfo(name = "server_status")
    private String serverStatus;

    @ColumnInfo(name = "client_status_dtimes")
    private Long clientStatusDtimes;

    @ColumnInfo(name = "server_status_dtimes")
    private Long serverStatusDtimes;

    @ColumnInfo(name = "client_status_comment")
    private String clientStatusComment;

    @ColumnInfo(name = "server_status_comment")
    private String serverStatusComment;

    @ColumnInfo(name = "center_id")
    private String centerId;

    @ColumnInfo(name = "approved_by")
    private String approvedBy;

    @ColumnInfo(name = "approver_role_code")
    private String approverRoleCode;

    @ColumnInfo(name = "file_upload_status")
    private String fileUploadStatus;

    @ColumnInfo(name = "upload_count")
    private Short uploadCount;

    @ColumnInfo(name = "upload_dtimes")
    private Long uploadDtimes;

    @ColumnInfo(name = "additional_info")
    private byte[] additionalInfo;

    @ColumnInfo(name = "app_id")
    private String appId;

    @ColumnInfo(name = "additional_info_req_id")
    private String additionalInfoReqId;

    @ColumnInfo(name = "ack_signature")
    private String ackSignature;

    @ColumnInfo(name = "has_bwords")
    private Boolean hasBwords;

    @ColumnInfo(name = "is_active")
    private Boolean isActive;

    @ColumnInfo(name = "cr_by")
    private String crBy;

    @ColumnInfo(name = "cr_dtimes")
    private Long crDtime;

    @ColumnInfo(name = "upd_by")
    private String updBy;

    @ColumnInfo(name = "upd_dtimes")
    private Long updDtimes;

    @Override
    public String toString() {
        return packetId + "\n" + (serverStatus == null ? clientStatus : serverStatus);
    }

}
