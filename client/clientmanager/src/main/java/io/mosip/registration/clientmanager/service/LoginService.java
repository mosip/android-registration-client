package io.mosip.registration.clientmanager.service;

import android.content.Context;
import io.mosip.registration.clientmanager.factory.SyncRestFactory;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;

@Singleton
public class LoginService {

    private static final String TAG = LoginService.class.getSimpleName();

    @Inject
    ClientCryptoManagerService clientCryptoManagerService;

    @Inject
    SyncRestFactory syncRestFactory;

    @Inject
    SyncRestService syncRestService;

    public LoginService(Context context, ClientCryptoManagerService clientCryptoManagerService) {
        this.clientCryptoManagerService = clientCryptoManagerService;
    }

    public void login(String authResponse) {
        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
        cryptoRequestDto.setValue(authResponse);
        CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);
        byte[] decodedBytes = CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue());
        String jsonString = new String(decodedBytes);
        //TODO save the auth-token in DB
        //wrapper.getResponse().getToken();
        //wrapper.getResponse().getRefreshToken();
    }
}
