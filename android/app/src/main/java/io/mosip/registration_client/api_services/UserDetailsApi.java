package io.mosip.registration_client.api_services;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.flutter.Log;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration_client.model.UserPigeon;

@Singleton
public class UserDetailsApi implements UserPigeon.UserApi {
    UserPigeon.User user;

    LoginService loginService;
    RegistrationCenterRepository registrationCenterRepository;

    @Inject
    public UserDetailsApi(LoginService loginService, RegistrationCenterRepository registrationCenterRepository) {
        this.loginService = loginService;
        this.registrationCenterRepository = registrationCenterRepository;
    }

    @Override
    public void validateUser(@NonNull String username, @NonNull UserPigeon.Result<UserPigeon.User> result) {
        if (username == null || username.trim().length() == 0) {
            user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsOnboarded(false)
                    .setErrorCode("REG_USER_EMPTY")
                    .build();
            result.success(user);
            return;
        }

        if (!loginService.isValidUserId(username)) {
            user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsOnboarded(false)
                    .setErrorCode("REG_USER_NOT_FOUND")
                    .build();
            result.success(user);
            return;
        }
        UserDetail userDetail = loginService.getUserDetailsByUserId(username);
        if (userDetail == null) {
            user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsActive(true)
                    .setIsOnboarded(false)
                    .setCenterId("")
                    .setName(username)
                    .setEmail("")
                    .setCenterName("")
                    .setFailedAttempts("0")
                    .build();
            result.success(user);
        } else {
            List<RegistrationCenter> registrationCenterList =
                    registrationCenterRepository.getRegistrationCenter(userDetail.getRegCenterId());
            String regCenter = "";
            if(registrationCenterList != null && !registrationCenterList.isEmpty()) {
                regCenter = registrationCenterList.get(0).getName();
            }
            user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsActive(userDetail.getIsActive())
                    .setName(userDetail.getName())
                    .setEmail(userDetail.getEmail())
                    .setCenterId(userDetail.getRegCenterId())
                    .setIsOnboarded(userDetail.isOnboarded())
                    .setCenterName(regCenter)
                    .build();
            result.success(user);
        }
    }
}