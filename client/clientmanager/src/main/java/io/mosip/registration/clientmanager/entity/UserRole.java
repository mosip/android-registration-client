package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import io.mosip.registration.clientmanager.entity.id.UserRoleId;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * The Entity Class for User Role details
 * 
 * @author Anshul Vanawat
 */
@Entity(tableName = "user_role")
@Data
public class UserRole extends RegistrationCommonFields {

	@Embedded
	@PrimaryKey
	private UserRoleId userRoleId;

	@ColumnInfo(name = "lang_code")
	private String langCode;
}
