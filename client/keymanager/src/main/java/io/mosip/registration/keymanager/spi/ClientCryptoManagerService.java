package io.mosip.registration.keymanager.spi;

import io.mosip.registration.keymanager.dto.*;


/**
 * @Author George T Abraham
 * @Author Eric John
 */
public interface ClientCryptoManagerService {

    SignResponseDto sign(SignRequestDto signRequestDto);

    SignVerifyResponseDto verifySign(SignVerifyRequestDto signVerifyRequestDto);

    CryptoResponseDto encrypt(CryptoRequestDto cryptoRequestDto);

    CryptoResponseDto decrypt(CryptoRequestDto cryptoRequestDto);

    PublicKeyResponseDto getPublicKey(PublicKeyRequestDto publicKeyRequestDto);
}
