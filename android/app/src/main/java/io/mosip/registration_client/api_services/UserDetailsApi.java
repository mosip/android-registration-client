package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.entity.RegistrationCenter;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration_client.model.MachinePigeon;
import io.mosip.registration_client.model.UserPigeon;

@Singleton
public class UserDetailsApi implements UserPigeon.UserApi {
    LoginService loginService;
    RegistrationCenterRepository registrationCenterRepository;
    MasterDataService masterDataService;
    AuditManagerService auditManagerService;

    @Inject
    public UserDetailsApi(LoginService loginService, RegistrationCenterRepository registrationCenterRepository,
                          MasterDataService masterDataService, AuditManagerService auditManagerService) {
        this.loginService = loginService;
        this.registrationCenterRepository = registrationCenterRepository;
        this.masterDataService = masterDataService;
        this.auditManagerService = auditManagerService;
    }

    @Override
    public void validateUser(@NonNull String username, @NonNull String langCode, @NonNull UserPigeon.Result<UserPigeon.User> result) {
        if (username == null || username.trim().length() == 0) {
            UserPigeon.User user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsOnboarded(false)
                    .setErrorCode("REG_USER_EMPTY")
                    .build();
            result.success(user);
            return;
        }

        if (!loginService.isValidUserId(username)) {
            UserPigeon.User user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsOnboarded(false)
                    .setErrorCode("REG_USER_NOT_FOUND")
                    .build();
            result.success(user);
            return;
        }
        UserDetail userDetail = loginService.getUserDetailsByUserId(username);
        CenterMachineDto centerMachineDto = this.masterDataService.getRegistrationCenterMachineDetails();
        boolean centerStatus = true;
        boolean machineStatus = true;
        String centerId = "";
        String centerName = "";
        if(centerMachineDto != null) {
            centerStatus = centerMachineDto.getCenterStatus();
            machineStatus = centerMachineDto.getMachineStatus();
        }
        if (userDetail == null) {
            UserPigeon.User user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsActive(true)
                    .setIsOnboarded(false)
                    .setCenterId(centerId)
                    .setName(username)
                    .setEmail("")
                    .setCenterName(centerName)
                    .setFailedAttempts("0")
                    .setCenterStatus(centerStatus)
                    .setMachineStatus(machineStatus)
                    .build();
            result.success(user);
        } else {
            centerId = userDetail.getRegCenterId();
            centerName = getCenterName(centerId, langCode);
            UserPigeon.User user = new UserPigeon.User.Builder()
                    .setUserId(username)
                    .setIsActive(userDetail.getIsActive())
                    .setName(userDetail.getName())
                    .setEmail(userDetail.getEmail())
                    .setCenterId(userDetail.getRegCenterId())
                    .setIsOnboarded(userDetail.isOnboarded())
                    .setCenterName(centerName)
                    .setCenterStatus(centerStatus)
                    .setMachineStatus(machineStatus)
                    .build();
            result.success(user);
        }
    }

    public String getCenterName(String regCenterId, String langCode) {
        List<RegistrationCenter> registrationCenterList = new ArrayList<>();
        RegistrationCenter registrationCenter;
        String regCenter = "";
        try {
            registrationCenter = registrationCenterRepository.getRegistrationCenterByCenterIdAndLangCode(regCenterId, langCode);
            if(registrationCenter != null) {
                regCenter = registrationCenter.getName();
                return regCenter;
            }

            registrationCenterList =
                    registrationCenterRepository.getRegistrationCenter(regCenterId);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getCenterName", e);
        }

        if(registrationCenterList != null && !registrationCenterList.isEmpty()) {
            regCenter = registrationCenterList.get(0).getName();
        }
        return regCenter;
    }
}