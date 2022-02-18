package io.mosip.registration.packetmanager.service;

import static io.mosip.registration.keymanager.util.KeyManagerConstant.KEY_ENDEC;

import java.nio.charset.StandardCharsets;

import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.dto.PublicKeyRequestDto;
import io.mosip.registration.keymanager.dto.PublicKeyResponseDto;
import io.mosip.registration.keymanager.dto.SignRequestDto;
import io.mosip.registration.keymanager.dto.SignResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;

public class PacketCryptoServiceImpl implements IPacketCryptoService {

    ClientCryptoManagerService clientCryptoManagerService;
    public PacketCryptoServiceImpl(){
        //clientCryptoManagerService = new LocalClientCryptoServiceImpl();
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