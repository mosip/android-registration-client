package io.mosip.registration.clientmanager.service;

import android.content.Context;

import android.util.Log;
import com.auth0.android.jwt.JWT;
import io.mosip.registration.clientmanager.config.SessionManager;
import io.mosip.registration.clientmanager.exception.InvalidMachineSpecIDException;
import org.json.JSONObject;

import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import org.mvel2.MVEL;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoginService {

    private static final String TAG = LoginService.class.getSimpleName();

    private SessionManager sessionManager;

    @Inject
    UserDetailRepository userDetailRepository;

    @Inject
    ClientCryptoManagerService clientCryptoManagerService;

    public LoginService(Context context, ClientCryptoManagerService clientCryptoManagerService, UserDetailRepository userDetailRepository) {
        this.clientCryptoManagerService = clientCryptoManagerService;
        this.userDetailRepository = userDetailRepository;
        this.sessionManager = SessionManager.getSessionManager(context);
    }

    public boolean isValidUserId(String userId) {
        return userDetailRepository.getUserDetailCount() != 0 ? userDetailRepository.isActiveUser(userId) : true;
    }

    public boolean isPasswordPresent(String userId) {
        return userDetailRepository.isPasswordPresent(userId);
    }
    public boolean validatePassword(String userId, String password) throws Exception {
        return userDetailRepository.isValidPassword(userId, password);
    }

    public void setPasswordHash(String userId, String password) throws Exception {
        userDetailRepository.setPasswordHash(userId, password);
    }

    public String getAuthToken() {
        return sessionManager.fetchAuthToken();
    }

    public void saveAuthToken(String authResponse) throws Exception {
        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
        cryptoRequestDto.setValue(authResponse);
        CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);
        if(cryptoResponseDto == null) {
            throw new InvalidMachineSpecIDException("Invalid Machine Spec ID found");
        }
        byte[] decodedBytes = CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue());
        try {
            JSONObject jsonObject = new JSONObject(new String(decodedBytes));
            Log.e(getClass().getSimpleName(), "Auth Token: " + jsonObject);
            this.sessionManager.saveAuthToken(jsonObject.getString("token"));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            throw ex;
        }
    }
}
