package io.mosip.registration.packetmanager.spi;

/**
 * @Author Anshul Vanawat
 */
public interface IPacketCryptoService {

    byte[] sign(byte[] packet);

    byte[] encrypt(byte[] packet);

}
