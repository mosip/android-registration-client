/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package io.mosip.registration_client;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.clientmanager.exception.InvalidMachineSpecIDException;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityService {
    String login_response = "";
    String error_code = "";
    Map<String, Object> responseMap = new HashMap<>();
    List<String> roles = new ArrayList<>();
    JSONObject object;

    public void usernameValidation(String username,
                                       LoginService loginService,
                                       MethodChannel.Result result, UserDetailRepository userDetailRepository) {
        if(!loginService.isValidUserId(username)) {
            responseMap.put("user_response", "User not found!");
            responseMap.put("isUserPresent", false);
            responseMap.put("user_details", "");
            responseMap.put("error_code", "404");
            object = new JSONObject(responseMap);
            result.success(object.toString());
            return;
        }
        UserDetail userDetail=loginService.getUserDetailsByUserId(username);
        responseMap.put("user_response", "User Validated!");
        responseMap.put("isUserPresent", true);
        responseMap.put("user_details",userDetail == null ? "" : userDetail.toString());
        responseMap.put("error_code", "");

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
                         LoginService loginService) throws Exception {
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
                            roles=loginService.saveAuthToken(wrapper.getResponse());
                            login_response = wrapper.getResponse();
                            loginService.setPasswordHash(username, password);
                            responseMap.put("isLoggedIn", true);
                            responseMap.put("login_response", login_response);
                            responseMap.put("roles", roles);
                            responseMap.put("error_code", "");
                            object = new JSONObject(responseMap);
                            result.success(object.toString());
                            return;
                        } catch (InvalidMachineSpecIDException e) {
                            error = new ServiceError("", "Invalid Machine Spec ID found");
                            Log.e(getClass().getSimpleName(), "Failed to save auth token", e);
                        } catch (Exception e) {
                            error = new ServiceError("", e.getMessage());
                            Log.e(getClass().getSimpleName(), "Failed to save auth token", e);
                        }
                    }

                    if(error == null) {
                        login_response = "Login Failed! Try Again";
                        error_code = "500";
                    } else if(error.getMessage().equals("Invalid Request")) {
                        login_response = "Password Incorrect";
                        error_code = "401";
                    } else if(error.getMessage().equals("Machine not found")) {
                        login_response = "Machine not found!";
                        error_code = "MACHINE_NOT_FOUND";
                    } else {
                        login_response = error.getMessage();
                        error_code = "400";
                    }
                    responseMap.put("isLoggedIn", false);
                    responseMap.put("login_response", login_response);
                    responseMap.put("roles", roles);
                    responseMap.put("error_code", error_code);
                    object = new JSONObject(responseMap);
                    result.success(object.toString());
                    return;
                }
                login_response = "Login Failed! Try Again";
                responseMap.put("isLoggedIn", false);
                responseMap.put("login_response", login_response);
                responseMap.put("roles", roles);
                responseMap.put("error_code", "500");
                object = new JSONObject(responseMap);
                result.success(object.toString());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(getClass().getSimpleName(), "Login Failure! ");
                responseMap.put("isLoggedIn", false);
                responseMap.put("roles", roles);
                responseMap.put("login_response", "Login failed. Check network connection!");
                responseMap.put("error_code", "501");
                object = new JSONObject(responseMap);
                result.success(object.toString());
            }
        });
    }

    private void offlineLogin(final String username, final String password, MethodChannel.Result result,
                              LoginService loginService) throws Exception {
        if(!loginService.isPasswordPresent(username)) {
            login_response = "Credentials not found or are expired. Please try online login!";
            responseMap.put("isLoggedIn", false);
            responseMap.put("roles", roles);
            responseMap.put("login_response", login_response);
            responseMap.put("error_code", "OFFLINE");
            object = new JSONObject(responseMap);
            result.success(object.toString());
            return;
        }

        if(!loginService.validatePassword(username, password)) {
            login_response = "Password incorrect!";
            responseMap.put("isLoggedIn", false);
            responseMap.put("roles", roles);
            responseMap.put("login_response", login_response);
            responseMap.put("error_code", "401");
            object = new JSONObject(responseMap);
            result.success(object.toString());
            return;
        }

        login_response = loginService.getAuthToken();

        if(login_response == null) {
            login_response = "Credentials not found or are expired!. Please try online login!";
            responseMap.put("isLoggedIn", false);
            responseMap.put("roles", roles);
            responseMap.put("login_response", login_response);
            responseMap.put("error_code", "OFFLINE");
            object = new JSONObject(responseMap);
            result.success(object.toString());
            return;
        }
        responseMap.put("isLoggedIn", true);
        responseMap.put("login_response", login_response);
        responseMap.put("roles", roles);
        responseMap.put("error_code", "");
        object = new JSONObject(responseMap);
        result.success(object.toString());
    }

    void executeLogin(String username, String password, MethodChannel.Result result,
                      SyncRestService syncRestService, SyncRestUtil syncRestFactory,
                      LoginService loginService, AuditManagerService auditManagerService,
                      boolean isConnected) throws Exception {

        auditManagerService.audit(AuditEvent.LOGIN_WITH_PASSWORD, Components.LOGIN);
        //validate form
        if(validateLogin(username, password, loginService)){
            if(!isConnected) {
                offlineLogin(username, password, result, loginService);
                return;
            }
            doLogin(username, password, result, syncRestService, syncRestFactory, loginService);
        } else {
            result.error("VALIDATION_FAILED","User validation failed!", null);
        }
    }
}
