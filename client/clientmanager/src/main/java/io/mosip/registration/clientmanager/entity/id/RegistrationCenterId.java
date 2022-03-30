package io.mosip.registration.clientmanager.entity.id;

import androidx.room.ColumnInfo;

import java.io.Serializable;

import lombok.Data;

/**
 * composite key for {@link io.mosip.registration.clientmanager.entity.RegistrationCenter}
 * 
 * @author Anshul Vanawat
 */
@Data
public class RegistrationCenterId implements Serializable {

	private static final long serialVersionUID = -7306845601917592413L;

	@ColumnInfo(name = "id")
	private String id;

	@ColumnInfo(name = "lang_code")
	private String langCode;
}
