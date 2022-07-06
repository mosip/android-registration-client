package io.mosip.registration.clientmanager.spi;

/**
 * @author Anshul vanawat
 * @since 1.0.0
 */
public interface PacketUploadService {

	void syncAndUploadPacket(String packetId, PacketUploadProgressCallBack callBack);

}