package io.mosip.registration.packetmanager.service;

import static io.mosip.registration.keymanager.util.KeyManagerConstant.KEY_ENDEC;

import android.content.Context;

import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.dto.PublicKeyRequestDto;
import io.mosip.registration.keymanager.dto.PublicKeyResponseDto;
import io.mosip.registration.keymanager.dto.SignRequestDto;
import io.mosip.registration.keymanager.dto.SignResponseDto;
import io.mosip.registration.keymanager.service.LocalClientCryptoServiceImpl;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;

/**
 * @Author Anshul Vanawat
 */
@Singleton
public class PacketCryptoServiceImpl implements IPacketCryptoService {

    ClientCryptoManagerService clientCryptoManagerService;

    @Inject
    public PacketCryptoServiceImpl(Context context){

        //TODO Dependency Inject
        clientCryptoManagerService = new LocalClientCryptoServiceImpl(context);
    }

    @Override
    public byte[] sign(byte[] packet) {
        String packetData = new String(packet, StandardCharsets.UTF_8);

        SignRequestDto signRequestDto = new SignRequestDto(packetData);

        SignResponseDto signResponseDto = clientCryptoManagerService.sign(signRequestDto);
        String signedPacketData = signResponseDto.getData();

        return signedPacketData.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] encrypt(byte[] packet) {
        String packetData = new String(packet, StandardCharsets.UTF_8);

        PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto(KEY_ENDEC);
        PublicKeyResponseDto publicKeyResponseDto = clientCryptoManagerService.getPublicKey(publicKeyRequestDto);

        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto(
                packetData, publicKeyResponseDto.getPublicKey());
        CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.encrypt(cryptoRequestDto);
        String encryptedPacketData = cryptoResponseDto.getValue();

        return encryptedPacketData.getBytes(StandardCharsets.UTF_8);
    }
}