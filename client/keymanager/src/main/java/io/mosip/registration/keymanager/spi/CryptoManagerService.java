package io.mosip.registration.keymanager.spi;

import io.mosip.registration.keymanager.dto.CryptoManagerRequestDto;
import io.mosip.registration.keymanager.dto.CryptoManagerResponseDto;

public interface CryptoManagerService {

    public CryptoManagerResponseDto encrypt(CryptoManagerRequestDto cryptoRequestDto) throws Exception;

}
