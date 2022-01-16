package io.mosip.registration.packetmanager.service;

import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PacketCryptoServiceImpl implements IPacketCryptoService {

    @Override
    public byte[] sign(byte[] packet) {
        //TODO sign packet impl
        return packet;
    }

    @Override
    public byte[] encrypt(String id, byte[] packet) {
        //TODO Encrypt packet impl
        return packet;
    }

    @Override
    public byte[] decrypt(String id, byte[] packet) {
        //TODO decrypt packet impl
        return packet;
    }


    @Override
    public boolean verify(byte[] packet, byte[] signature) {
        //TODO verify packet impl
        return false;
    }
}