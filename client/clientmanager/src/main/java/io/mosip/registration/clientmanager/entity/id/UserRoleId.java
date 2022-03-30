package io.mosip.registration.clientmanager.entity.id;

import androidx.room.ColumnInfo;

import java.io.Serializable;
import lombok.Data;

/**
 * composite key for {@link io.mosip.registration.clientmanager.entity.UserRole}
 *
 * @author Anshul Vanawat
 */
@Data
public class UserRoleId implements Serializable {

	private static final long serialVersionUID = -8072043172665654382L;

	@ColumnInfo(name = "usr_id")
	private String usrId;
	@ColumnInfo(name = "role_code")
	private String roleCode;

}
