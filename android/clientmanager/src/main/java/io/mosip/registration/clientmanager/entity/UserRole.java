package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity(tableName = "user_role", primaryKeys = {"usr_id", "role_code"})
@Data
@AllArgsConstructor
public class UserRole {

	@NonNull
	@ColumnInfo(name = "usr_id")
	private String usrId;

	@NonNull
	@ColumnInfo(name = "role_code")
	private String roleCode;

	@ColumnInfo(name = "lang_code")
	private String langCode;
}


