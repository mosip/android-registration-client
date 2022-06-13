package io.mosip.registration.packetmanager.service;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.keymanager.dto.*;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.keymanager.util.KeyManagerConstant;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import io.mosip.registration.packetmanager.util.DateUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static io.mosip.registration.keymanager.service.CryptoManagerServiceImpl.GCM_AAD_LENGTH;
import static io.mosip.registration.keymanager.service.CryptoManagerServiceImpl.GCM_NONCE_LENGTH;

/**
 * @Author Anshul Vanawat
 */
@Singleton
public class PacketCryptoServiceImpl implements IPacketCryptoService {

    private Context context;
    private ClientCryptoManagerService clientCryptoManagerService;
    private CryptoManagerService cryptoManagerService;

    @Inject
    public PacketCryptoServiceImpl(Context context, ClientCryptoManagerService clientCryptoManagerService,
                                   CryptoManagerService cryptoManagerService){
        this.context  = context;
        this.clientCryptoManagerService = clientCryptoManagerService;
        this.cryptoManagerService = cryptoManagerService;
    }

    @Override
    public byte[] sign(byte[] packet) {
        SignRequestDto signRequestDto = new SignRequestDto(CryptoUtil.base64encoder.encodeToString(packet));
        SignResponseDto signResponseDto = clientCryptoManagerService.sign(signRequestDto);
        return CryptoUtil.base64decoder.decode(signResponseDto.getData());
    }

    @Override
    public byte[] encrypt(String refId, byte[] packet) throws Exception {
        CryptoManagerRequestDto cryptomanagerRequestDto = new CryptoManagerRequestDto();
        cryptomanagerRequestDto.setApplicationId("REGISTRATION");
        cryptomanagerRequestDto.setData(CryptoUtil.base64encoder.encodeToString(packet));
        cryptomanagerRequestDto.setReferenceId(refId);
        SecureRandom sRandom = new SecureRandom();
        byte[] nonce = new byte[GCM_NONCE_LENGTH];
        byte[] aad = new byte[GCM_AAD_LENGTH];
        sRandom.nextBytes(nonce);
        sRandom.nextBytes(aad);
        cryptomanagerRequestDto.setAad(CryptoUtil.base64encoder.encodeToString(aad));
        cryptomanagerRequestDto.setSalt(CryptoUtil.base64encoder.encodeToString(nonce));
        cryptomanagerRequestDto.setTimeStamp(LocalDateTime.now(ZoneOffset.UTC));

        byte[] encryptedData = CryptoUtil.base64decoder.decode(cryptoManagerService.encrypt(cryptomanagerRequestDto).getData());
        return mergeEncryptedData(encryptedData, nonce, aad);
    }

    public static byte[] mergeEncryptedData(byte[] encryptedData, byte[] nonce, byte[] aad) {
        byte[] finalEncData = new byte[encryptedData.length + GCM_AAD_LENGTH + GCM_NONCE_LENGTH];
        System.arraycopy(nonce, 0, finalEncData, 0, nonce.length);
        System.arraycopy(aad, 0, finalEncData, nonce.length, aad.length);
        System.arraycopy(encryptedData, 0, finalEncData, nonce.length + aad.length,	encryptedData.length);
        return finalEncData;
    }
}