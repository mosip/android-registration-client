package io.mosip.registration.clientmanager.spi.crypto;

import io.mosip.registration.clientmanager.dto.crypto.*;

public interface ClientCryptoManagerService {

    SignResponseDto sign(SignRequestDto signRequestDto);

    SignVerifyResponseDto verifySign(SignVerifyRequestDto signVerifyRequestDto);

    CryptoResponseDto encrypt(CryptoRequestDto cryptoRequestDto);

    CryptoResponseDto decrypt(CryptoRequestDto cryptoRequestDto);

    PublicKeyResponseDto getPublicKey(PublicKeyRequestDto publicKeyRequestDto);
}
