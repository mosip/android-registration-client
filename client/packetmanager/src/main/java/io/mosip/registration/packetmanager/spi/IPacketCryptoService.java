package io.mosip.registration.packetmanager.spi;

public interface IPacketCryptoService {

    public byte[] sign(byte[] packet);

    public byte[] encrypt(String id, byte[] packet);

}
