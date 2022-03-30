package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

import lombok.Data;

/**
 * The Entity Class for Registration details
 *
 * @author Anshul Vanawat
 */
@Entity(tableName = "REGISTRATION")
@Data
public class Registration extends RegistrationCommonFields {

    @PrimaryKey
    @ColumnInfo(name = "PACKET_ID")
    private String packetId;

    @ColumnInfo(name = "ID")
    private String id;

    @ColumnInfo(name = "usr_id")
    private String usrId;

    @ColumnInfo(name = "REG_TYPE")
    private String regType;

    @ColumnInfo(name = "REF_REG_ID")
    private String refRegId;

    @ColumnInfo(name = "PREREG_ID")
    private String preRegId;

    @ColumnInfo(name = "STATUS_CODE")
    private String statusCode;

    @ColumnInfo(name = "LANG_CODE")
    private String langCode;

    @ColumnInfo(name = "STATUS_COMMENT")
    private String statusComment;

    @ColumnInfo(name = "STATUS_DTIMES")
    private Timestamp statusTimestamp;

    @ColumnInfo(name = "ACK_FILENAME")
    private String ackFilename;

    @ColumnInfo(name = "CLIENT_STATUS_CODE")
    private String clientStatusCode;

    @ColumnInfo(name = "SERVER_STATUS_CODE")
    private String serverStatusCode;

    @ColumnInfo(name = "CLIENT_STATUS_DTIME")
    private Timestamp clientStatusTimestamp;

    @ColumnInfo(name = "SERVER_STATUS_DTIME")
    private Timestamp serverStatusTimestamp;

    @ColumnInfo(name = "CLIENT_STATUS_COMMENT")
    private String clientStatusComments;

    @ColumnInfo(name = "SERVER_STATUS_COMMENT")
    private String serverStatusComments;

    @ColumnInfo(name = "REG_USR_ID")
    private String regUsrId;

    @ColumnInfo(name = "REGCNTR_ID")
    private String regCntrId;

    @ColumnInfo(name = "APPROVER_USR_ID")
    private String approverUsrId;

    @ColumnInfo(name = "APPROVER_ROLE_CODE")
    private String approverRoleCode;

    @ColumnInfo(name = "FILE_UPLOAD_STATUS")
    private String fileUploadStatus;

    @ColumnInfo(name = "UPLOAD_COUNT")
    private Short uploadCount;

    @ColumnInfo(name = "UPLOAD_DTIMES")
    private Timestamp uploadTimestamp;

    @ColumnInfo(name = "LATEST_REGTRN_ID")
    private String latestRegTrnId;

    @ColumnInfo(name = "LATEST_TRN_TYPE_CODE")
    private String latestTrnTypeCode;

    @ColumnInfo(name = "LATEST_TRN_STATUS_CODE")
    private String latestTrnStatusCode;

    @ColumnInfo(name = "LATEST_TRN_LANG_CODE")
    private String latestTrnLangCode;

    @ColumnInfo(name = "LATEST_REGTRN_DTIMES")
    private Timestamp latestRegTrnTimestamp;

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


//	@ManyToOne
//	@JoinColumn(name = "CR_BY", referencedColumnName = "id", insertable = false, updatable = false)
//	private UserDetail userdetail;

//	public void setStatusTimestamp(Timestamp statusTimestamp) {
//		this.statusTimestamp = Timestamp.valueOf(DateUtils.getUTCCurrentDateTime());
//	}

//	public void setClientStatusTimestamp(Timestamp clientStatusTimestamp) {
//		this.clientStatusTimestamp = Timestamp.valueOf(DateUtils.getUTCCurrentDateTime());
//	}

//	public void setServerStatusTimestamp(Timestamp serverStatusTimestamp) {
//		this.serverStatusTimestamp = Timestamp.valueOf(DateUtils.getUTCCurrentDateTime());
//	}

//	public void setUploadTimestamp(Timestamp uploadTimestamp) {
//		this.uploadTimestamp = Timestamp.valueOf(DateUtils.getUTCCurrentDateTime());
//	}


//	public void setLatestRegTrnTimestamp(Timestamp latestRegTrnTimestamp) {
//		this.latestRegTrnTimestamp = Timestamp.valueOf(DateUtils.getUTCCurrentDateTime());
//	}
}
