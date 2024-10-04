package io.mosip.registration.packetmanager.exception;

public class PacketCreatorException extends BaseCheckedException {


	private static final long serialVersionUID = 80279436742398851L;
	
	public PacketCreatorException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
