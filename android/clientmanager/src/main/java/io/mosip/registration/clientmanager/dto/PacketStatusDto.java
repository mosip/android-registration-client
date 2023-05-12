package io.mosip.registration.clientmanager.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The DTO Class PacketStatusReaderDTO.
 *
 * @author Anshul vanawat
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
public class PacketStatusDto {

	@SerializedName("packetId")
	@Expose
	private String packetId;
	@SerializedName("statusCode")
	@Expose
	private String statusCode;

}
