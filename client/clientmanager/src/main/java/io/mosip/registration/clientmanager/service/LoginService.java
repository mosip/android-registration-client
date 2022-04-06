package io.mosip.registration.clientmanager.service;

import android.content.Context;

import android.util.Log;
import io.mosip.registration.clientmanager.config.SessionManager;
import org.json.JSONObject;

import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoginService {

    private static final String TAG = LoginService.class.getSimpleName();

    private SessionManager sessionManager;

    @Inject
    ClientCryptoManagerService clientCryptoManagerService;

    public LoginService(Context context, ClientCryptoManagerService clientCryptoManagerService) {
        this.clientCryptoManagerService = clientCryptoManagerService;
        this.sessionManager = SessionManager.getSessionManager(context);
    }

    public void saveAuthToken(String authResponse) throws Exception {
        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
        cryptoRequestDto.setValue(authResponse);
        CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);
        if(cryptoResponseDto != null) {
            byte[] decodedBytes = CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue());
            try {
                JSONObject jsonObject = new JSONObject(new String(decodedBytes));
                this.sessionManager.saveAuthToken(jsonObject.getString("token"));
            } catch (Exception ex) {
                Log.e(TAG, "Failed to parse the decrypted auth response", ex);
                throw ex;
            }
        }
    }

    public String fetchAuthToken() {
        return this.sessionManager.fetchAuthToken();
    }
}
