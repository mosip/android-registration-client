package io.mosip.registration.packetmanager.spi;

public interface IPacketCryptoService {

    byte[] sign(byte[] packet);

    byte[] encrypt(byte[] packet);

}
