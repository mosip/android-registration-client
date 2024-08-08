/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client.api_services;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
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
import io.mosip.registration.clientmanager.spi.AsyncPacketTaskCallBack;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration_client.utils.CustomToast;
import io.mosip.registration_client.MainActivity;
import io.mosip.registration_client.R;
import io.mosip.registration_client.model.PacketAuthPigeon;

@Singleton
public class PacketAuthenticationApi implements PacketAuthPigeon.PacketAuthApi {
    SyncRestService syncRestService;
    SyncRestUtil syncRestFactory;
    LoginService loginService;
    AuditManagerService auditManagerService;
    PacketService packetService;
    RegistrationRepository registrationRepository;

    private Activity activity;

    public void setCallbackActivity(MainActivity mainActivity){
        this.activity=mainActivity;
    }

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
    public void syncPacketAll(@NonNull List<String> packetIds, @NonNull PacketAuthPigeon.Result<Void> result) {
        Integer packetSize = packetIds.size();
        final Integer[] remainingPack = {packetSize, 0};
        CustomToast newToast = new CustomToast(activity);
        for (String value: packetIds){
            try {
                auditManagerService.audit(AuditEvent.SYNC_PACKET, Components.REG_PACKET_LIST);

                Integer remaining = packetSize - remainingPack[0];
                newToast.setText(String.format("Sync Packet Status : %s/%s Processed", remaining.toString(), packetSize.toString()));
                newToast.showToast();

                packetService.syncRegistration(value, new AsyncPacketTaskCallBack() {
                    @Override
                    public void inProgress(String RID) {
                        //Do nothing
                    }

                    @Override
                    public void onComplete(String RID, PacketTaskStatus status) {
                        if(status.equals(PacketTaskStatus.SYNC_COMPLETED)  || status.equals(PacketTaskStatus.SYNC_ALREADY_COMPLETED)){
                            remainingPack[1] += 1;
                        }
                        remainingPack[0] -= 1;

                        Integer remaining = packetSize - remainingPack[0];
                        newToast.setText(String.format("Sync Packet Status : %s/%s Processed", remaining.toString(), packetSize.toString()));

                        if(remainingPack[0] == 0){
                            Integer failed = packetSize - remainingPack[1];
                            newToast.setIcon(R.drawable.done);
                            String message = "Sync Packet Status :";
                            if(remainingPack[1] != 0){
                                message = message + String.format(" %s/%s Success", remainingPack[1], packetSize);
                            }
                            if(failed != 0){
                                message = message + String.format(" %s/%s Failed", failed, packetSize);
                            }
                            newToast.setText(message);
                            newToast.showToast();
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
        Integer packetSize = packetIds.size();
        final Integer[] remainingPack = {packetSize, 0};
        CustomToast newToast = new CustomToast(activity);
        for (String value: packetIds){
            try {
                auditManagerService.audit(AuditEvent.UPLOAD_PACKET, Components.REG_PACKET_LIST);
                Integer remaining = packetSize - remainingPack[0];
                newToast.setText(String.format("Upload Packet Status : %s/%s Processed", remaining.toString(), packetSize.toString()));
                newToast.showToast();

                packetService.uploadRegistration(value, new AsyncPacketTaskCallBack() {
                    @Override
                    public void inProgress(String RID) {
                        newToast.showToast();
                        //Do nothing
                    }

                    @Override
                    public void onComplete(String RID, PacketTaskStatus status) {
                        if(status.equals(PacketTaskStatus.UPLOAD_COMPLETED)  || status.equals(PacketTaskStatus.UPLOAD_ALREADY_COMPLETED)){
                            remainingPack[1] += 1;
                        }
                        remainingPack[0] -= 1;

                        Integer remaining = packetSize - remainingPack[0];
                        newToast.setText(String.format("Upload Packet Status : %s/%s Processed", remaining.toString(), packetSize.toString()));

                        if(remainingPack[0] == 0){
                            Integer failed = packetSize - remainingPack[1];
                            newToast.setIcon(R.drawable.done);
                            String message = "Upload Packet Status :";
                            if(remainingPack[1] != 0){
                                message = message + String.format(" %s/%s Success", remainingPack[1], packetSize);
                            }
                            if(failed != 0){
                                message = message + String.format(" %s/%s Failed", failed, packetSize);
                            }
                            newToast.setText(message);
                            newToast.showToast();
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
            List<Registration> allRegistration = packetService.getAllNotUploadedRegistrations(1,40);
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
    public void getAllCreatedRegistrationPacket(@NonNull PacketAuthPigeon.Result<List<String>> result) {
        List<String> packets = new ArrayList();
        try{
            List<Registration> allRegistration = packetService.getRegistrationsByStatus(PacketClientStatus.CREATED.name(), 40);
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
