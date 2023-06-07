package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Entity Class for User Detail details
 * 
 * @author Anshul Vanawat
 */
@Entity(tableName = "user_detail")
@Data
@EqualsAndHashCode(callSuper=false)
public class UserDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	@PrimaryKey
	@ColumnInfo(name = "id")
	private String id;

	@ColumnInfo(name = "name")
	private String name;

	@ColumnInfo(name = "email")
	private String email;

	@ColumnInfo(name = "mobile")
	private String mobile;

	@ColumnInfo(name = "lang_code")
	private String langCode;

	@ColumnInfo(name = "last_login_dtimes")
	private Long lastLoginDtimes;

	@ColumnInfo(name = "last_login_method")
	private String lastLoginMethod;

	@ColumnInfo(name = "unsuccessful_login_count")
	private Integer unsuccessfulLoginCount;

	@ColumnInfo(name = "userlock_till_dtimes")
	private Long userLockTillDtimes;

	@ColumnInfo(name = "is_active")
	private Boolean isActive;

	@ColumnInfo(name = "is_deleted")
	private Boolean isDeleted;

	@ColumnInfo(name = "reg_cntr_id")
	private String regCenterId;

	@ColumnInfo(name = "is_onboarded")
	private boolean isOnboarded;

	@ColumnInfo(name = "is_supervisor")
	private boolean isSupervisor;

	@ColumnInfo(name = "is_default")
	private boolean isDefault;

	@ColumnInfo(name = "is_officer")
	private boolean isOfficer;
}
