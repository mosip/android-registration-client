package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.entity.Registration;
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
        offlineAuthentication(username, password, result);
    }

    @Override
    public void syncPacket(@NonNull String packetId, @NonNull PacketAuthPigeon.Result<Void> result) {
        try{
            packetService.syncRegistration(packetId);
        }catch(Exception e){
            Log.e(getClass().getSimpleName(), "Error packet Sync", e);
        }
    }

    @Override
    public void uploadPacket(@NonNull String packetId, @NonNull PacketAuthPigeon.Result<Void> result) {
        try{
            packetService.uploadRegistration(packetId);
        }catch(Exception e){
            Log.e(getClass().getSimpleName(), "Error packet Upload", e);
        }
    }

    @Override
    public void getAllRegistrationPacket(@NonNull PacketAuthPigeon.Result<List<String>> result) {
        try{
            List<Registration> allRegistration = packetService.getAllRegistrations(1,5);
            allRegistration.forEach(value-> {
                Log.e(getClass().getSimpleName(), value.toString());
            });
        }catch(Exception e){
            Log.e(getClass().getSimpleName(), "Unable to get packets", e);
        }
        result.success(Arrays.asList("Welcome", "to", "my", "list"));
    }
}
