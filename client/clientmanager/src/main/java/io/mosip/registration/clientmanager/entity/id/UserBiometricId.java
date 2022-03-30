package io.mosip.registration.clientmanager.entity.id;

import androidx.room.ColumnInfo;

import java.io.Serializable;

import lombok.Data;

/**
 * composite key for {@link io.mosip.registration.clientmanager.entity.UserBiometric}
 * 
 * @author Anshul Vanawat
 */
@Data
public class UserBiometricId implements Serializable {

	private static final long serialVersionUID = 4356301394048825993L;

	@ColumnInfo(name = "usr_id")
	private String usrId;
	@ColumnInfo(name = "bmtyp_code")
	private String bioTypeCode;
	@ColumnInfo(name = "bmatt_code")
	private String bioAttributeCode;
}
