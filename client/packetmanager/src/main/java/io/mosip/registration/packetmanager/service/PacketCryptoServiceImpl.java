package io.mosip.registration.packetmanager.service;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.dto.PublicKeyRequestDto;
import io.mosip.registration.keymanager.dto.PublicKeyResponseDto;
import io.mosip.registration.keymanager.dto.SignRequestDto;
import io.mosip.registration.keymanager.dto.SignResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.keymanager.util.KeyManagerConstant;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;

/**
 * @Author Anshul Vanawat
 */
@Singleton
public class PacketCryptoServiceImpl implements IPacketCryptoService {

    private ClientCryptoManagerService clientCryptoManagerService;

    @Inject
    public PacketCryptoServiceImpl(Context context, ClientCryptoManagerService clientCryptoManagerService){
        this.clientCryptoManagerService = clientCryptoManagerService;
    }

    @Override
    public byte[] sign(byte[] packet) {
        SignRequestDto signRequestDto = new SignRequestDto(CryptoUtil.base64encoder.encodeToString(packet));
        SignResponseDto signResponseDto = clientCryptoManagerService.sign(signRequestDto);
        return CryptoUtil.base64decoder.decode(signResponseDto.getData());
    }

    @Override
    public byte[] encrypt(byte[] packet) {
        //TODO packet to be created with refId machine key, Need to change this logic
        PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto(KeyManagerConstant.ENCDEC_ALIAS);
        PublicKeyResponseDto publicKeyResponseDto = clientCryptoManagerService.getPublicKey(publicKeyRequestDto);
        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto(
                CryptoUtil.base64encoder.encodeToString(packet), publicKeyResponseDto.getPublicKey());
        CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.encrypt(cryptoRequestDto);
        return CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue());
    }
}