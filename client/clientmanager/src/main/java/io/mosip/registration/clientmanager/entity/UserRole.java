package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import lombok.Data;

/**
 * The Entity Class for User Role details
 * 
 * @author Anshul Vanawat
 */
@Entity(primaryKeys = {"usr_id", "role_code"}, tableName = "user_role")
@Data
public class UserRole extends RegistrationCommonFields {

	@NonNull
	@ColumnInfo(name = "usr_id")
	private String usrId;

	@NonNull
	@ColumnInfo(name = "role_code")
	private String roleCode;

	@ColumnInfo(name = "lang_code")
	private String langCode;
}
