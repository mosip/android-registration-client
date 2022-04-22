package io.mosip.registration.clientmanager.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.sql.Timestamp;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Entity Class for UserBiometric details
 * 
 * @author Anshul Vanawat
 */
@Entity(primaryKeys = {"usr_id", "bmtyp_code", "bmatt_code"}, tableName = "user_biometric")
@Data
@EqualsAndHashCode(callSuper=false)
public class UserBiometric {

	@NonNull
	@ColumnInfo(name = "usr_id")
	private String usrId;

	@NonNull
	@ColumnInfo(name = "bmtyp_code")
	private String bioTypeCode;

	@NonNull
	@ColumnInfo(name = "bmatt_code")
	private String bioAttributeCode;

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
