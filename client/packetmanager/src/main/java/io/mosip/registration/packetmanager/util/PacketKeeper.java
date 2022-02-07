package io.mosip.registration.packetmanager.util;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.util.Map;

import io.mosip.registration.keymanager.util.ConfigService;
import io.mosip.registration.packetmanager.dto.PacketWriter.Packet;
import io.mosip.registration.packetmanager.dto.PacketWriter.PacketInfo;
import io.mosip.registration.packetmanager.exception.BaseCheckedException;
import io.mosip.registration.packetmanager.exception.PacketKeeperException;
import io.mosip.registration.packetmanager.service.PacketCryptoServiceImpl;
import io.mosip.registration.packetmanager.service.PosixAdapterServiceImpl;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import io.mosip.registration.packetmanager.spi.ObjectAdapterService;

/**
 * The packet keeper is used to store & retrieve packet, creation of audit, encrypt and sign packet.
 * Packet keeper is used to get container information and list of sources from a packet.
 */
public class PacketKeeper {

    /**
     * The reg proc logger.
     */
    private static final String TAG = PacketKeeper.class.getSimpleName();

    private String PACKET_MANAGER_ACCOUNT;

    private ObjectAdapterService posixAdapter;

    private String adapterName;

    private IPacketCryptoService cryptoService;
    private static final String UNDERSCORE = "_";

    public PacketKeeper(Context context){
        cryptoService = new PacketCryptoServiceImpl();
        adapterName = ConfigService.getProperty("objectstore.adapter.name", context);
        PACKET_MANAGER_ACCOUNT = ConfigService.getProperty("packet.manager.account.name", context);
        posixAdapter = new PosixAdapterServiceImpl(context);
    }

    public PacketInfo putPacket(Packet packet) throws PacketKeeperException {
        try {
            //TODO encrypt packet
            byte[] encryptedSubPacket = cryptoService.encrypt(packet.getPacketInfo().getId(), packet.getPacket());

            // put packet in object store
            boolean response = getAdapter().putObject(PACKET_MANAGER_ACCOUNT,
                    packet.getPacketInfo().getId(), packet.getPacketInfo().getSource(),
                    packet.getPacketInfo().getProcess(), packet.getPacketInfo().getPacketName(), new ByteArrayInputStream(encryptedSubPacket));

            if (response) {
                PacketInfo packetInfo = packet.getPacketInfo();

                //TODO sign encrypted packet
                packetInfo.setSignature(CryptoUtil.encodeToURLSafeBase64(cryptoService.sign(packet.getPacket())));
                // generate encrypted packet hash
                packetInfo.setEncryptedHash(CryptoUtil.encodeToURLSafeBase64(HMACUtils2.generateHash(encryptedSubPacket)));
                Map<String, Object> metaMap = PacketManagerHelper.getMetaMap(packetInfo);
                metaMap = getAdapter().addObjectMetaData(PACKET_MANAGER_ACCOUNT,
                        packet.getPacketInfo().getId(), packet.getPacketInfo().getSource(), packet.getPacketInfo().getProcess(), packet.getPacketInfo().getPacketName(), metaMap);
                return PacketManagerHelper.getPacketInfo(metaMap);
            } else
                throw new PacketKeeperException(PacketManagerErrorCode
                        .PACKET_KEEPER_PUT_ERROR.getErrorCode(), "Unable to store packet in object store");


        } catch (Exception e) {
            Log.i(TAG, "putPacket: " + e.getStackTrace());
            if (e instanceof BaseCheckedException) {
                BaseCheckedException ex = (BaseCheckedException) e;
                throw new PacketKeeperException(ex.getErrorCode(), ex.getMessage());
            }
            throw new PacketKeeperException(PacketManagerErrorCode.PACKET_KEEPER_PUT_ERROR.getErrorCode(),
                    "Failed to persist packet in object store : " + e.getMessage(), e);
        }
    }

    private ObjectAdapterService getAdapter() {
        if (adapterName.equalsIgnoreCase("PosixAdapter"))
            return posixAdapter;
        else {
            Log.i(TAG, "getAdapter: " + adapterName + " Service not found");
            return null;
        }
    }

    public boolean deletePacket(String id, String source, String process) {
        return getAdapter().removeContainer(PACKET_MANAGER_ACCOUNT, id, source, process);
    }

    public boolean pack(String id, String source, String process) {
        return getAdapter().pack(PACKET_MANAGER_ACCOUNT, id, source, process);
    }

}