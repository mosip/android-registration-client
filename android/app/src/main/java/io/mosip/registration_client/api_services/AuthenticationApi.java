/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client.api_services;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.BuildConfig;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.exception.InvalidMachineSpecIDException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration_client.R;
import io.mosip.registration_client.UploadBackgroundService;
import io.mosip.registration_client.model.AuthResponsePigeon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthenticationApi implements AuthResponsePigeon.AuthResponseApi {
    private Context context;
    SyncRestService syncRestService;
    SyncRestUtil syncRestFactory;
    LoginService loginService;
    AuditManagerService auditManagerService;
    SharedPreferences sharedPreferences;
    GlobalParamRepository globalParamRepository;
    public static final String IS_OFFICER = "is_officer";
    public static final String IS_SUPERVISOR = "is_supervisor";
    public static final String IS_DEFAULT = "is_default";
    public static final String USER_NAME = "user_name";
    public static final String IS_OPERATOR = "is_operator";
    public static final String PREFERRED_USERNAME = "preferred_username";
    public static final String USER_EMAIL = "user_email";


    @Inject
    public AuthenticationApi(Context context, SyncRestService syncRestService, SyncRestUtil syncRestFactory,
                    LoginService loginService, AuditManagerService auditManagerService, GlobalParamRepository globalParamRepository) {
        this.context = context;
        this.syncRestService = syncRestService;
        this.syncRestFactory = syncRestFactory;
        this.loginService = loginService;
        this.auditManagerService = auditManagerService;
        this.globalParamRepository = globalParamRepository;
        sharedPreferences = this.context.
                getSharedPreferences(
                        this.context.getString(R.string.app_name),
                        Context.MODE_PRIVATE);
    }

    private AuthResponsePigeon.AuthResponse getAuthErrorResponse(String errorCode) {
        AuthResponsePigeon.AuthResponse authResponse = new AuthResponsePigeon.AuthResponse.Builder()
                .setResponse("")
                .setUserId("")
                .setUsername("")
                .setUserEmail("")
                .setIsDefault(false)
                .setIsOfficer(false)
                .setIsSupervisor(false)
                .setIsOperator(false)
                .setErrorCode(errorCode)
                .build();

        return authResponse;
    }

    private void doLogin(final String username, final String password, AuthResponsePigeon.Result<AuthResponsePigeon.AuthResponse> result) {
        Call<ResponseWrapper<String>> call = syncRestService.login(syncRestFactory.getAuthRequest(username, password));
        call.enqueue(new Callback<ResponseWrapper<String>>() {
            @Override
            public void onResponse(Call call, Response response) {
                ResponseWrapper<String> wrapper = (ResponseWrapper<String>) response.body();
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(wrapper);
                    if (error == null) {
                        try {
                            loginService.saveAuthToken(wrapper.getResponse(), username);
                            loginService.setPasswordHash(username, password);
                            String preferredUsername = sharedPreferences.getString(PREFERRED_USERNAME, username);
                            String fullName = sharedPreferences.getString(USER_NAME, preferredUsername);
                            AuthResponsePigeon.AuthResponse authResponse = new AuthResponsePigeon.AuthResponse.Builder()
                                    .setResponse(wrapper.getResponse())
                                    .setUserId(username)
                                    .setUsername(fullName)
                                    .setUserEmail(sharedPreferences.getString(USER_EMAIL, ""))
                                    .setIsDefault(sharedPreferences.getBoolean(IS_DEFAULT, false))
                                    .setIsOfficer(sharedPreferences.getBoolean(IS_OFFICER, false))
                                    .setIsOperator(sharedPreferences.getBoolean(IS_OPERATOR, false))
                                    .setIsSupervisor(sharedPreferences.getBoolean(IS_SUPERVISOR, false))
                                    .build();
                            result.success(authResponse);
                            return;
                        } catch (InvalidMachineSpecIDException e) {
                            error = new ServiceError("", "Invalid Machine Spec ID found");
                            Log.e(getClass().getSimpleName(), "Failed to save auth token", e);
                        } catch (Exception e) {
                            error = new ServiceError("", e.getMessage());
                            Log.e(getClass().getSimpleName(), "Failed to save auth token", e);
                        }
                    }

                    String errorCode = "";
                    if (error == null) {
                        errorCode = "REG_TRY_AGAIN";
                    } else if (error.getMessage().equals("Invalid Request")) {
                        errorCode = "REG_INVALID_REQUEST";
                    } else if (error.getMessage().equals("Machine not found")) {
                        errorCode = "REG_MACHINE_NOT_FOUND";
                    } else {
                        errorCode = error.getMessage();
                    }
                    AuthResponsePigeon.AuthResponse authResponse = getAuthErrorResponse(errorCode);
                    result.success(authResponse);
                    return;
                }
                AuthResponsePigeon.AuthResponse authResponse = getAuthErrorResponse("REG_TRY_AGAIN");
                result.success(authResponse);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(getClass().getSimpleName(), "Login Failure! ", t);
                AuthResponsePigeon.AuthResponse authResponse = getAuthErrorResponse("REG_NETWORK_ERROR");
                result.success(authResponse);
            }
        });
    }

    private void offlineLogin(final String username, final String password, AuthResponsePigeon.Result<AuthResponsePigeon.AuthResponse> result) {
        if(!loginService.isPasswordPresent(username)) {
            AuthResponsePigeon.AuthResponse authResponse = getAuthErrorResponse("REG_CRED_EXPIRED");
            result.success(authResponse);
            return;
        }

        if(!loginService.validatePassword(username, password)) {
            AuthResponsePigeon.AuthResponse authResponse = getAuthErrorResponse("REG_INVALID_REQUEST");
            result.success(authResponse);
            return;
        }

        try {
            String token = loginService.saveUserAuthTokenOffline(username);
            String preferredUsername = sharedPreferences.getString(PREFERRED_USERNAME, username);
            String fullName = sharedPreferences.getString(USER_NAME, preferredUsername);
            AuthResponsePigeon.AuthResponse authResponse = new AuthResponsePigeon.AuthResponse.Builder()
                    .setResponse(token)
                    .setUserId(username)
                    .setUsername(fullName)
                    .setUserEmail(sharedPreferences.getString(USER_EMAIL, ""))
                    .setIsDefault(sharedPreferences.getBoolean(IS_DEFAULT, false))
                    .setIsOfficer(sharedPreferences.getBoolean(IS_OFFICER, false))
                    .setIsOperator(sharedPreferences.getBoolean(IS_OPERATOR, false))
                    .setIsSupervisor(sharedPreferences.getBoolean(IS_SUPERVISOR, false))
                    .build();
            result.success(authResponse);
        } catch (Exception ex) {
            AuthResponsePigeon.AuthResponse authResponse = getAuthErrorResponse("REG_CRED_EXPIRED");
            result.success(authResponse);
        }

    }

    @Override
    public void login(@NonNull String username, @NonNull String password, @NonNull Boolean isConnected, @NonNull AuthResponsePigeon.Result<AuthResponsePigeon.AuthResponse> result) {
        auditManagerService.audit(AuditEvent.LOGIN_WITH_PASSWORD, Components.LOGIN);
        if(!isConnected) {
            offlineLogin(username, password, result);
            return;
        }
        doLogin(username, password, result);
        return;
    }

    @Override
    public void logout(@NonNull AuthResponsePigeon.Result<String> result) {
        loginService.clearAuthToken(this.context);
        result.success("Logout Success");
    }

    @Override
    public void stopAlarmService(@NonNull AuthResponsePigeon.Result<String> result) {
        String resultString = "";
        try{
            Intent intent = new Intent(context, UploadBackgroundService.class);
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getForegroundService(
                        this.context,
                        0,  // Request code
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                );
            } else {
                pendingIntent = PendingIntent.getService(
                        this.context,
                        0,  // Request code
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
            }
            AlarmManager alarmManager = (AlarmManager) getSystemService(context, AlarmManager.class);
            assert alarmManager != null;
            alarmManager.cancel(pendingIntent);
            resultString = "Success";
        }catch (Exception e){
            resultString = "Fail to Stop Alarm Service";
            Log.e(getClass().getSimpleName(), "Failed to stop alarm service", e);
        }
        result.success(resultString);
    }

    @Override
    public void forgotPasswordUrl(@NonNull AuthResponsePigeon.Result<String> result) {
        System.out.println("<======forgot password url======>"+this.globalParamRepository.getCachedStringForgotPassword());
        String response = this.globalParamRepository.getCachedStringForgotPassword();
        result.success(response);
    }
}
