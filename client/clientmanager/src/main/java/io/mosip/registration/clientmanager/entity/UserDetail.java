package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import lombok.Data;
/**
 * The Entity Class for User Detail details
 * 
 * @author Anshul Vanawat
 */
@Entity(tableName = "user_detail")
@Data
public class UserDetail extends RegistrationCommonFields implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@ColumnInfo(name = "id")
	private String id;

	@ColumnInfo(name = "reg_id")
	private String regId;

	@ColumnInfo(name = "name")
	private String name;

	@ColumnInfo(name = "email")
	private String email;

	@ColumnInfo(name = "mobile")
	private String mobile;

	@ColumnInfo(name = "status_code")
	private String statusCode;

	@ColumnInfo(name = "lang_code")
	private String langCode;

	@ColumnInfo(name = "last_login_dtimes")
	private Timestamp lastLoginDtimes;

	@ColumnInfo(name = "last_login_method")
	private String lastLoginMethod;

	@ColumnInfo(name = "unsuccessful_login_count")
	private Integer unsuccessfulLoginCount;

	@ColumnInfo(name = "userlock_till_dtimes")
	private Timestamp userLockTillDtimes;

	@ColumnInfo(name = "is_deleted")
	private Boolean isDeleted;

	@ColumnInfo(name = "del_dtimes")
	private Timestamp delDtimes;

	@ColumnInfo(name = "reg_cntr_id")
	private String regCenterId;

	@ColumnInfo(name = "is_onboarded")
	private boolean isOnboarded;
}
