package io.mosip.registration.clientmanager.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The DTO Class PacketStatusReaderDTO.
 *
 * @author Anshul vanawat
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
public class PacketIdDto {

	@SerializedName("packetId")
	@Expose
	private String packetId;

}
