package io.mosip.registration.packetmanager.dto.PacketWriter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIRInfo;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.VersionType;
import lombok.Data;


/**
 * 
 * BIR class with Builder to create data
 * 
 * @author Ramadurai Pandian
 *
 */

@Data
public class BiometricRecord implements Serializable {

	protected VersionType version;
	protected VersionType cbeffversion;
	protected BIRInfo birInfo;
	/**
	 * This can be of any modality, each subtype is an element in this list.
	 * it has type and subtype info in it
	 */
	protected List<BIR> segments;
	protected Map<String, String> others;
	
	public BiometricRecord() {
		this.segments = new ArrayList<>();
		this.others = new HashMap<>();
	}
	
	public BiometricRecord(VersionType version, VersionType cbeffversion, BIRInfo birInfo) {
		this.version = version;
		this.cbeffversion = cbeffversion;
		this.birInfo = birInfo;
		this.segments = new ArrayList<BIR>();
		this.others = new HashMap<>();
	}	

}
