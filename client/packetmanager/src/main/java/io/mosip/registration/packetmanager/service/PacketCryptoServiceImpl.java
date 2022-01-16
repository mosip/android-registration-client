package io.mosip.registration.packetmanager.service;

import android.content.Context;

import io.mosip.registration.packetmanager.spi.IPacketCryptoService;

public class PacketCryptoServiceImpl implements IPacketCryptoService {

    private Context context;

    public PacketCryptoServiceImpl (Context context){
        this.context = context;
    }

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