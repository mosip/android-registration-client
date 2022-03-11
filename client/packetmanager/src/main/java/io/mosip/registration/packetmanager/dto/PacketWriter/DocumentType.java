package io.mosip.registration.packetmanager.dto.PacketWriter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentType {
	
	private String value;
	private String type;
	private String format;

}
