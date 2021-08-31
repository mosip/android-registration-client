package io.mosip.registration.clientmanager.service.crypto;

import io.mosip.registration.clientmanager.dto.crypto.*;
import io.mosip.registration.clientmanager.spi.crypto.ClientCryptoManagerService;

public class LocalClientCryptoServiceImpl implements ClientCryptoManagerService {


    @Override
    public SignResponseDto sign(SignRequestDto signRequestDto) {
        return null;
    }

    @Override
    public SignVerifyResponseDto verifySign(SignVerifyRequestDto signVerifyRequestDto) {
        return null;
    }

    @Override
    public CryptoResponseDto encrypt(CryptoRequestDto cryptoRequestDto) {
        return null;
    }

    @Override
    public CryptoResponseDto decrypt(CryptoRequestDto cryptoRequestDto) {
        return null;
    }

    @Override
    public PublicKeyResponseDto getPublicKey(PublicKeyRequestDto publicKeyRequestDto) {
        return null;
    }
}
