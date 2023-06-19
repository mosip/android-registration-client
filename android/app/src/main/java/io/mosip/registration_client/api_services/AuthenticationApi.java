package io.mosip.registration_client.api_services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.exception.InvalidMachineSpecIDException;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration_client.R;
import io.mosip.registration_client.model.AuthResponsePigeon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthenticationApi implements AuthResponsePigeon.AuthResponseApi {
    private Context context;
    AuthResponsePigeon.AuthResponse authResponse;
    SyncRestService syncRestService;
    SyncRestUtil syncRestFactory;
    LoginService loginService;
    AuditManagerService auditManagerService;
    SharedPreferences sharedPreferences;
    public static final String IS_OFFICER = "is_officer";
    public static final String IS_SUPERVISOR = "is_supervisor";
    public static final String IS_DEFAULT = "is_default";
    public static final String USER_NAME = "user_name";

    @Inject
    public AuthenticationApi(Context context, SyncRestService syncRestService, SyncRestUtil syncRestFactory,
                    LoginService loginService, AuditManagerService auditManagerService) {
        this.context = context;
        this.syncRestService = syncRestService;
        this.syncRestFactory = syncRestFactory;
        this.loginService = loginService;
        this.auditManagerService = auditManagerService;
        sharedPreferences = this.context.
                getSharedPreferences(
                        this.context.getString(R.string.app_name),
                        Context.MODE_PRIVATE);
    }

    private void doLogin(final String username, final String password) {
        Call<ResponseWrapper<String>> call = syncRestService.login(syncRestFactory.getAuthRequest(username, password));
        call.enqueue(new Callback<ResponseWrapper<String>>() {
            @Override
            public void onResponse(Call call, Response response) {
                ResponseWrapper<String> wrapper = (ResponseWrapper<String>) response.body();
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(wrapper);
                    if (error == null) {
                        try {
                            loginService.saveAuthToken(wrapper.getResponse());
                            authResponse = new AuthResponsePigeon.AuthResponse.Builder()
                                    .setResponse(wrapper.getResponse())
                                    .setUsername(sharedPreferences.getString(USER_NAME, null))
                                    .setIsDefault(sharedPreferences.getBoolean(IS_DEFAULT, false))
                                    .setIsOfficer(sharedPreferences.getBoolean(IS_OFFICER, false))
                                    .setIsSupervisor(sharedPreferences.getBoolean(IS_SUPERVISOR, false))
                                    .build();
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
                    authResponse = new AuthResponsePigeon.AuthResponse.Builder()
                            .setResponse("")
                            .setUsername("")
                            .setIsDefault(false)
                            .setIsOfficer(false)
                            .setIsSupervisor(false)
                            .setErrorCode(errorCode)
                            .build();
                    return;
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(getClass().getSimpleName(), "Login Failure! ");
                authResponse = new AuthResponsePigeon.AuthResponse.Builder()
                        .setResponse("")
                        .setUsername("")
                        .setIsDefault(false)
                        .setIsOfficer(false)
                        .setIsSupervisor(false)
                        .setErrorCode("REG_NETWORK_ERROR")
                        .build();
            }
        });
    }

    private void offlineLogin(final String username, final String password) {
        if(!loginService.isPasswordPresent(username)) {
            authResponse = new AuthResponsePigeon.AuthResponse.Builder()
                    .setResponse("")
                    .setUsername("")
                    .setIsDefault(false)
                    .setIsOfficer(false)
                    .setIsSupervisor(false)
                    .setErrorCode("REG_CRED_EXPIRED")
                    .build();
            return;
        }

        if(!loginService.validatePassword(username, password)) {
            authResponse = new AuthResponsePigeon.AuthResponse.Builder()
                    .setResponse("")
                    .setUsername("")
                    .setIsDefault(false)
                    .setIsOfficer(false)
                    .setIsSupervisor(false)
                    .setErrorCode("REG_INVALID_REQUEST")
                    .build();
            return;
        }



        if(loginService.getAuthToken() == null) {
            authResponse = new AuthResponsePigeon.AuthResponse.Builder()
                    .setResponse("")
                    .setUsername("")
                    .setIsDefault(false)
                    .setIsOfficer(false)
                    .setIsSupervisor(false)
                    .setErrorCode("REG_CRED_EXPIRED")
                    .build();
            return;
        }
        authResponse = new AuthResponsePigeon.AuthResponse.Builder()
                .setResponse(loginService.getAuthToken())
                .setUsername(sharedPreferences.getString(USER_NAME, null))
                .setIsDefault(sharedPreferences.getBoolean(IS_DEFAULT, false))
                .setIsOfficer(sharedPreferences.getBoolean(IS_OFFICER, false))
                .setIsSupervisor(sharedPreferences.getBoolean(IS_SUPERVISOR, false))
                .build();
    }

    @NonNull
    @Override
    public AuthResponsePigeon.AuthResponse login(@NonNull String username,
                                                 @NonNull String password,
                                                 @NonNull Boolean isConnected) {

        if(!isConnected) {
            offlineLogin(username, password);
            return authResponse;
        }
        doLogin(username, password);
        return authResponse;
    }
}
