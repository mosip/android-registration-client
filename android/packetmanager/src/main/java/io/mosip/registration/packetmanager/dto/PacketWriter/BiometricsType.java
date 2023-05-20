package io.mosip.registration.packetmanager.dto.PacketWriter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BiometricsType {
	
	private String format;
	private double version;
	private String value;
	
}
