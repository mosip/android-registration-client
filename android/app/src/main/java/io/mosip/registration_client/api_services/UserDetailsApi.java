package io.mosip.registration_client.api_services;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration_client.model.UserPigeon;

@Singleton
public class UserDetailsApi implements UserPigeon.UserApi {
    UserPigeon.User user;

    LoginService loginService;

    @Inject
    public UserDetailsApi(LoginService loginService) {
        this.loginService = loginService;
    }

    public void logout(){
        loginService.clearAuthToken();
    }
    public void usernameValidation(String username) {
        if(username == null || username.trim().length() == 0){
            user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsOnboarded(false)
                    .setErrorCode("REG_USER_EMPTY")
                    .build();
            return;
        }

        if(!loginService.isValidUserId(username)) {
            user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsOnboarded(false)
                    .setErrorCode("REG_USER_NOT_FOUND")
                    .build();
            return;
        }
        UserDetail userDetail = loginService.getUserDetailsByUserId(username);
        if(userDetail == null) {
            user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsOnboarded(false)
                    .setCenterId("")
                    .setName(username)
                    .setCenterName("")
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
    @NonNull
    @Override
    public UserPigeon.User logoutUser() {
        logout();
        return user;
    }
}