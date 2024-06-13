/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client.api_services;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.constant.PacketTaskStatus;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.service.LoginService;
import io.mosip.registration.clientmanager.service.PacketServiceImpl;
import io.mosip.registration.clientmanager.spi.AsyncPacketTaskCallBack;
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
    RegistrationRepository registrationRepository;

    @Inject
    public PacketAuthenticationApi(SyncRestService syncRestService, SyncRestUtil syncRestFactory,
                                   LoginService loginService, PacketService packetService,RegistrationRepository registrationRepository ,
                                   AuditManagerService auditManagerService) {
        this.syncRestService = syncRestService;
        this.syncRestFactory = syncRestFactory;
        this.loginService = loginService;
        this.packetService = packetService;
        this.registrationRepository = registrationRepository;
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
    public void syncPacketAll(@NonNull List<String> packetIds, @NonNull PacketAuthPigeon.Result<Void> result) {
        Log.e(getClass().getSimpleName(), packetIds.toString());
        final Integer[] remainingPack = {packetIds.size()};
        for (String value: packetIds){
            try {
                Log.d(getClass().getSimpleName(), "Syncing " + value);
                auditManagerService.audit(AuditEvent.SYNC_PACKET, Components.REG_PACKET_LIST);
                packetService.syncRegistration(value, new AsyncPacketTaskCallBack() {
                    @Override
                    public void inProgress(String RID) {
                        //Do nothing
                    }

                    @Override
                    public void onComplete(String RID, PacketTaskStatus status) {
                        remainingPack[0] -= 1;
                        Log.d(getClass().getSimpleName(), "Remaining pack"+ remainingPack[0]);
                        if(remainingPack[0] == 0){
                            Log.d(getClass().getSimpleName(), "Last Packet"+RID);
                            result.success(null);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @Override
    public void uploadPacketAll(@NonNull List<String> packetIds, @NonNull PacketAuthPigeon.Result<Void> result) {
        Log.e(getClass().getSimpleName(), packetIds.toString());
        final Integer[] remainingPack = {packetIds.size()};
        for (String value: packetIds){
            try {
                Log.d(getClass().getSimpleName(), "Uploading " + value);
                auditManagerService.audit(AuditEvent.UPLOAD_PACKET, Components.REG_PACKET_LIST);
                packetService.uploadRegistration(value, new AsyncPacketTaskCallBack() {
                    @Override
                    public void inProgress(String RID) {
                        //Do nothing
                    }

                    @Override
                    public void onComplete(String RID, PacketTaskStatus status) {
                        remainingPack[0] -= 1;
                        Log.d(getClass().getSimpleName(), "Remaining pack"+ remainingPack[0]);
                        if(remainingPack[0] == 0){
                            Log.d(getClass().getSimpleName(), "Last Packet"+RID);
                            result.success(null);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @Override
    public void getAllRegistrationPacket(@NonNull PacketAuthPigeon.Result<List<String>> result) {
        List<String> packets = new ArrayList();
        try{
            List<Registration> allRegistration = packetService.getAllRegistrations(1,20);
            ObjectMapper mapper = new ObjectMapper();
            for (Registration element : allRegistration) {
                String json = mapper.writeValueAsString(element);
                packets.add(json);
            }
        }catch(Exception e){
            Log.e(getClass().getSimpleName(), "Unable to get packets", e);
        }
        result.success(packets);
    }

    @Override
    public void updatePacketStatus(@NonNull String packetId, @Nullable String serverStatus, @NonNull String clientStatus, @NonNull PacketAuthPigeon.Result<Void> result) {
        registrationRepository.updateStatus(packetId, serverStatus, clientStatus);
    }
}
