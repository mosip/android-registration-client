package io.mosip.registration.clientmanager.service;

import android.content.Context;

import android.util.Log;
import com.auth0.android.jwt.JWT;
import io.mosip.registration.clientmanager.config.SessionManager;
import io.mosip.registration.clientmanager.entity.UserDetail;
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
     
    public UserDetail getUserDetailsByUserId(String userId){
        return userDetailRepository.getUserDetailByUserId(userId);
    } 
    
    public boolean isPasswordPresent(String userId) {
        return userDetailRepository.isPasswordPresent(userId);
    }
    public boolean validatePassword(String userId, String password) {
        return userDetailRepository.isValidPassword(userId, password);
    }

    public void setPasswordHash(String userId, String password) {
        userDetailRepository.setPasswordHash(userId, password);
    }


    public List<String> saveAuthToken(String authResponse, String userId) throws Exception {
        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
        cryptoRequestDto.setValue(authResponse);
        CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);
        if(cryptoResponseDto == null) {
            throw new InvalidMachineSpecIDException("Invalid Machine Spec ID found");
        }
        byte[] decodedBytes = CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue());
        try {
            JSONObject jsonObject = new JSONObject(new String(decodedBytes));

            String token = jsonObject.getString("token");
            String refreshToken = jsonObject.getString("refreshToken");
            long tExpiry = Long.parseLong(jsonObject.getString("expiryTime"));
            long rExpiry = Long.parseLong(jsonObject.getString("refreshExpiryTime"));
            userDetailRepository.saveUserAuthToken(userId, token, refreshToken, tExpiry, rExpiry);
            List<String> roles=this.sessionManager.saveAuthToken(token);

            return roles;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            throw ex;
        }
    }


    public String saveUserAuthTokenOffline(String userId) throws Exception {
        String token = userDetailRepository.getUserAuthToken(userId);

        if(token != null && !token.isEmpty()) {
            try {
                this.sessionManager.saveAuthToken(token);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
                throw ex;
            }
        }

        return token;
    }
}
