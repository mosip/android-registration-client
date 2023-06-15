package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.flutter.plugin.common.MethodChannel;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration_client.models.UserPigeon;

@Singleton
public class UserDetailsApi implements UserPigeon.UserApi {
    UserPigeon.User user;

    LoginService loginService;

    @Inject
    public UserDetailsApi(LoginService loginService) {
        this.loginService = loginService;
    }

    public void usernameValidation(String username) {
        if(username == null || username.trim().length() == 0){
            user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsOnboarded(false)
                    .setErrorMessage("Please enter a valid username!")
                    .build();
            Log.e(getClass().getSimpleName(), "username incorrect");
            return;
        }

        if(!loginService.isValidUserId(username)) {
            user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsOnboarded(false)
                    .setErrorMessage("User not found!")
                    .build();
            return;
        }
        UserDetail userDetail = loginService.getUserDetailsByUserId(username);
        if(userDetail == null) {
            user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsOnboarded(false)
                    .build();
        } else {
            user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsActive(userDetail.getIsActive())
                    .setName(userDetail.getName())
                    .setEmail(userDetail.getEmail())
                    .setCenterId(userDetail.getRegCenterId())
                    .setIsOnboarded(userDetail.isOnboarded())
                    .build();
        }
    }


    @NonNull
    @Override
    public UserPigeon.User validateUser(@NonNull String username) {
        usernameValidation(username);
        return user;
    }
}