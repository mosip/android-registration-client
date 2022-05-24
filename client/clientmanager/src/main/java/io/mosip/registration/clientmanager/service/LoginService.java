package io.mosip.registration.clientmanager.service;

import android.content.Context;

import android.util.Log;
import com.auth0.android.jwt.JWT;
import io.mosip.registration.clientmanager.config.SessionManager;
import org.json.JSONObject;

import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import org.mvel2.MVEL;

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

    public boolean isValidUserId(String userId) {
        //TODO sync user-details
        // and check if the user is mapped to this center and is active
        return  (Boolean) MVEL.eval("1==1");
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
                Log.e(TAG, ex.getMessage(), ex);
                throw ex;
            }
        }
    }
}
