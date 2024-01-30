/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration_client.model.PacketAuthPigeon;

@Singleton
public class PacketAuthenticationApi implements PacketAuthPigeon.PacketAuthApi {
    SyncRestService syncRestService;
    SyncRestUtil syncRestFactory;
    LoginService loginService;
    AuditManagerService auditManagerService;
    PacketService packetService;

    @Inject
    public PacketAuthenticationApi(SyncRestService syncRestService, SyncRestUtil syncRestFactory,
                                   LoginService loginService, PacketService packetService,
                                   AuditManagerService auditManagerService) {
        this.syncRestService = syncRestService;
        this.syncRestFactory = syncRestFactory;
        this.loginService = loginService;
        this.packetService = packetService;
        this.auditManagerService = auditManagerService;
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
            auditManagerService.audit(AuditEvent.CREATE_PACKET_AUTH_FAILED, Components.REGISTRATION);
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
    public void authenticate(@NonNull String username, @NonNull String password, @NonNull PacketAuthPigeon.Result<PacketAuthPigeon.PacketAuth> result) {
        auditManagerService.audit(AuditEvent.CREATE_PACKET_AUTH, Components.REGISTRATION);
        offlineAuthentication(username, password, result);
    }

    @Override
    public void syncPacket(@NonNull String packetId, @NonNull PacketAuthPigeon.Result<Void> result) {
        auditManagerService.audit(AuditEvent.SYNC_PACKET, Components.REG_PACKET_LIST);
        try{
            packetService.syncRegistration(packetId);
        }catch(Exception e){
            Log.e(getClass().getSimpleName(), "Error packet Sync", e);
        }
    }

    @Override
    public void uploadPacket(@NonNull String packetId, @NonNull PacketAuthPigeon.Result<Void> result) {
        auditManagerService.audit(AuditEvent.UPLOAD_PACKET, Components.REG_PACKET_LIST);
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
