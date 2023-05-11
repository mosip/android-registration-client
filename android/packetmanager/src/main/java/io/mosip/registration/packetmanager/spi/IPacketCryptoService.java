package io.mosip.registration.packetmanager.spi;

/**
 * @Author Anshul Vanawat
 */
public interface IPacketCryptoService {

    byte[] sign(byte[] packet);

    byte[] encrypt(String refId, byte[] packet) throws Exception;

}
