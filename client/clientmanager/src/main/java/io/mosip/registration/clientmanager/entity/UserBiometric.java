package io.mosip.registration.clientmanager.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

import io.mosip.registration.clientmanager.entity.id.UserBiometricId;
import lombok.Data;
/**
 * The Entity Class for UserBiometric details
 * 
 * @author Anshul Vanawat
 */
@Entity(tableName = "user_biometric")
@Data
public class UserBiometric extends RegistrationCommonFields {

	@Embedded
	@PrimaryKey
	private UserBiometricId userBiometricId;

	@ColumnInfo(name = "bio_template")
	private byte[] bioTemplate;

	@ColumnInfo(name = "quality_score")
	private Integer qualityScore;

	@ColumnInfo(name = "no_of_retry")
	private Integer numberOfRetry;

	@ColumnInfo(name = "is_deleted")
	private Boolean isDeleted;

	@ColumnInfo(name = "del_dtimes")
	private Timestamp delDtimes;
}
