package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration_client.model.PacketAuthPigeon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class PacketAuthenticationApi implements PacketAuthPigeon.PacketAuthApi {
    SyncRestService syncRestService;
    SyncRestUtil syncRestFactory;
    LoginService loginService;

    PacketService packetService;

    @Inject
    public PacketAuthenticationApi(SyncRestService syncRestService, SyncRestUtil syncRestFactory,
                                   LoginService loginService, PacketService packetService) {
        this.syncRestService = syncRestService;
        this.syncRestFactory = syncRestFactory;
        this.loginService = loginService;
        this.packetService = packetService;
    }

    private PacketAuthPigeon.PacketAuth getAuthErrorResponse(String errorCode) {
        PacketAuthPigeon.PacketAuth packetAuth = new PacketAuthPigeon.PacketAuth.Builder()
                .setResponse("")
                .setErrorCode(errorCode)
                .build();
        return packetAuth;
    }

    private void onlineAuthentication(String username, String password, PacketAuthPigeon.Result<PacketAuthPigeon.PacketAuth> result) {
        Call<ResponseWrapper<String>> call = syncRestService.login(syncRestFactory.getAuthRequest(username, password));
        call.enqueue(new Callback<ResponseWrapper<String>>() {
            @Override
            public void onResponse(Call call, Response response) {
                ResponseWrapper<String> wrapper = (ResponseWrapper<String>) response.body();
                if (response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(wrapper);
                    if (error == null) {
                        PacketAuthPigeon.PacketAuth packetAuth = new PacketAuthPigeon.PacketAuth.Builder()
                                .setResponse(wrapper.getResponse())
                                .build();
                        result.success(packetAuth);
                        return;
                    }
                    Log.e(getClass().getSimpleName(), response.raw().toString());
                    PacketAuthPigeon.PacketAuth packetAuth = getAuthErrorResponse(error.getMessage());
                    result.success(packetAuth);
                    return;
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(getClass().getSimpleName(), "Authentication Failed!");
                PacketAuthPigeon.PacketAuth packetAuth = getAuthErrorResponse("REG_NETWORK_ERROR");
                result.success(packetAuth);
            }
        });
    }

    private void offlineAuthentication(String username, String password, PacketAuthPigeon.Result<PacketAuthPigeon.PacketAuth> result) {
        if (!loginService.validatePassword(username, password)) {
            PacketAuthPigeon.PacketAuth packetAuth = getAuthErrorResponse("REG_INVALID_REQUEST");
            result.success(packetAuth);
            return;
        }

        PacketAuthPigeon.PacketAuth packetAuth = new PacketAuthPigeon.PacketAuth.Builder()
                .setResponse("REG_AUTHENTICATION_SUCCESS")
                .build();
        result.success(packetAuth);
    }

    @Override
    public void authenticate(@NonNull String username, @NonNull String password, @NonNull Boolean isConnected, @NonNull PacketAuthPigeon.Result<PacketAuthPigeon.PacketAuth> result) {
        if(!isConnected) {
            offlineAuthentication(username, password, result);
            return;
        }
        onlineAuthentication(username, password, result);
    }

    @Override
    public void syncPacket(@NonNull String packetId, @NonNull PacketAuthPigeon.Result<Void> result) {
        try{
            packetService.syncRegistration(packetId);
            Log.e(getClass().getSimpleName(), "success");
        }catch(Exception e){
            Log.e(getClass().getSimpleName(), "Error packet Sync", e);
        }
    }

    @Override
    public void uploadPacket(@NonNull String packetId, @NonNull PacketAuthPigeon.Result<Void> result) {
        try{
            packetService.uploadRegistration(packetId);
            Log.e(getClass().getSimpleName(), "success");
        }catch(Exception e){
            Log.e(getClass().getSimpleName(), "Error packet Upload", e);
        }
    }
}
