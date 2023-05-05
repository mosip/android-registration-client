/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package io.mosip.registration_client;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.exception.InvalidMachineSpecIDException;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityService {
    String login_response = "";
    Map<String, String> responseMap = new HashMap<>();
    JSONObject object;

    public void usernameValidation(String username,
                                       LoginService loginService,
                                       MethodChannel.Result result) {
        if(!loginService.isValidUserId(username)) {
            responseMap.put("user_response", "User not present!");
            responseMap.put("isUserPresent", "false");
            object = new JSONObject(responseMap);
            result.success(object.toString());
            return;
        }
        responseMap.put("user_response", "User Validated!");
        responseMap.put("isUserPresent", "true");
        object = new JSONObject(responseMap);
        result.success(object.toString());
    }

    private boolean validateLogin(String username, String password, LoginService loginService) {
        if(username == null || username.trim().length() == 0){
            Log.e(getClass().getSimpleName(), "username incorrect");
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Log.e(getClass().getSimpleName(), "password incorrect");
            return false;
        }
        if(!loginService.isValidUserId(username)) {
            Log.e(getClass().getSimpleName(), "user not present");
            return false;
        }
        return true;
    }

    private void doLogin(final String username, final String password, MethodChannel.Result result,
                         SyncRestService syncRestService, SyncRestUtil syncRestFactory,
                         LoginService loginService){
        //TODO check if the machine is online, if offline check password hash locally
        Call<ResponseWrapper<String>> call = syncRestService.login(syncRestFactory.getAuthRequest(username, password));
        call.enqueue(new Callback<ResponseWrapper<String>>() {
            @Override
            public void onResponse(Call call, Response response) {
                ResponseWrapper<String> wrapper = (ResponseWrapper<String>) response.body();
                if(response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(wrapper);
                    if(error == null) {
                        try {
                            loginService.saveAuthToken(wrapper.getResponse());
                            login_response = wrapper.getResponse();
                            responseMap.put("isLoggedIn", "true");
                            responseMap.put("login_response", login_response);
                            object = new JSONObject(responseMap);
                            result.success(object.toString());
                            return;
                        } catch (InvalidMachineSpecIDException e) {
                            error = new ServiceError("", e.getMessage());
                            Log.e(getClass().getSimpleName(), "Failed to save auth token", e);
                        } catch (Exception e) {
                            error = new ServiceError("", e.getMessage());
                            Log.e(getClass().getSimpleName(), "Failed to save auth token", e);
                        }
                    }

                    Log.e(getClass().getSimpleName(), "Some error occurred! " + error);
                    login_response = error == null ? "Login Failed! Try Again" : "Error: " + error.getMessage();
                    responseMap.put("isLoggedIn", "false");
                    responseMap.put("login_response", login_response);
                    object = new JSONObject(responseMap);
                    result.success(object.toString());
                    return;
                }
                login_response = "Login Failed! Try Again";
                responseMap.put("isLoggedIn", "false");
                responseMap.put("login_response", login_response);
                object = new JSONObject(responseMap);
                result.success(object.toString());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(getClass().getSimpleName(), "Login Failure! ");
                result.error("404", "Custom error", null);
            }
        });
    }

    void executeLogin(String username, String password, MethodChannel.Result result,
                      SyncRestService syncRestService, SyncRestUtil syncRestFactory,
                      LoginService loginService, AuditManagerService auditManagerService) {

        auditManagerService.audit(AuditEvent.LOGIN_WITH_PASSWORD, Components.LOGIN);
        //validate form
        if(validateLogin(username, password, loginService)){
            doLogin(username, password, result, syncRestService, syncRestFactory, loginService);
        } else {
            result.error("VALIDATION_FAILED","User validation failed!", null);
        }
    }
}
