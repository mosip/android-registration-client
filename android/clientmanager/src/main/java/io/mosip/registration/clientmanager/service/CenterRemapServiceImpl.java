/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.repository.DynamicFieldRepository;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.LocationRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.repository.TemplateRepository;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.clientmanager.repository.UserRoleRepository;
import io.mosip.registration.clientmanager.constant.PacketTaskStatus;
import io.mosip.registration.clientmanager.spi.AsyncPacketTaskCallBack;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.clientmanager.spi.CenterRemapService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.PreRegistrationDataSyncService;

/**
 * Executes the four-step center remap cleanup process.
 *
 * <p>Step execution order must be preserved:
 * <ol>
 *   <li>Disable background sync jobs so no new data arrives during cleanup</li>
 *   <li>Upload any pending packets so no work is lost</li>
 *   <li>Delete all local packets (registration + pre-registration)</li>
 *   <li>Purge center-specific master data and reset the remap flag</li>
 * </ol>
 */
@Singleton
public class CenterRemapServiceImpl implements CenterRemapService {

    private static final String TAG = CenterRemapServiceImpl.class.getSimpleName();
    private static final String SYNC_LAST_UPDATED = "sync.lastupdated";
    private static final String MASTER_DATA_LAST_UPDATED = "masterdata.lastupdated";

    private final Context context;
    private final GlobalParamRepository globalParamRepository;
    private final RegistrationRepository registrationRepository;
    private final PacketService packetService;
    private final PreRegistrationDataSyncService preRegistrationDataSyncService;
    private final SyncJobDefRepository syncJobDefRepository;
    private final DynamicFieldRepository dynamicFieldRepository;
    private final IdentitySchemaRepository identitySchemaRepository;
    private final LocationRepository locationRepository;
    private final RegistrationCenterRepository registrationCenterRepository;
    private final TemplateRepository templateRepository;
    private final UserBiometricRepository userBiometricRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuditManagerService auditManagerService;
    private final KeyStoreRepository keyStoreRepository;

    @Inject
    public CenterRemapServiceImpl(Context context,
                                  GlobalParamRepository globalParamRepository,
                                  RegistrationRepository registrationRepository,
                                  PacketService packetService,
                                  PreRegistrationDataSyncService preRegistrationDataSyncService,
                                  SyncJobDefRepository syncJobDefRepository,
                                  DynamicFieldRepository dynamicFieldRepository,
                                  IdentitySchemaRepository identitySchemaRepository,
                                  LocationRepository locationRepository,
                                  RegistrationCenterRepository registrationCenterRepository,
                                  TemplateRepository templateRepository,
                                  UserBiometricRepository userBiometricRepository,
                                  UserDetailRepository userDetailRepository,
                                  UserRoleRepository userRoleRepository,
                                  AuditManagerService auditManagerService,
                                  KeyStoreRepository keyStoreRepository) {
        this.context = context;
        this.globalParamRepository = globalParamRepository;
        this.registrationRepository = registrationRepository;
        this.packetService = packetService;
        this.preRegistrationDataSyncService = preRegistrationDataSyncService;
        this.syncJobDefRepository = syncJobDefRepository;
        this.dynamicFieldRepository = dynamicFieldRepository;
        this.identitySchemaRepository = identitySchemaRepository;
        this.locationRepository = locationRepository;
        this.registrationCenterRepository = registrationCenterRepository;
        this.templateRepository = templateRepository;
        this.userBiometricRepository = userBiometricRepository;
        this.userDetailRepository = userDetailRepository;
        this.userRoleRepository = userRoleRepository;
        this.auditManagerService = auditManagerService;
        this.keyStoreRepository = keyStoreRepository;
    }

    @Override
    public void handleRemapStep(int step) throws Exception {
        Log.i(TAG, "Remap step " + step + " started");
        auditManagerService.audit(AuditEvent.MACHINE_REMAPPED, Components.CENTER_MACHINE_REMAP);
        switch (step) {
            case 1:
                disableAllSyncJobs();
                break;
            case 2:
                syncAndUploadAllPendingPackets();
                break;
            case 3:
                deleteAllLocalPackets();
                break;
            case 4:
                purgeAndReset();
                break;
            default:
                throw new IllegalArgumentException("Invalid remap step: " + step + ". Valid range is 1–4.");
        }
        Log.i(TAG, "Remap step " + step + " completed");
    }

    private void disableAllSyncJobs() {
        syncJobDefRepository.disableAllJobs();
        Log.i(TAG, "Step 1: all sync jobs disabled");
    }

    private void syncAndUploadAllPendingPackets() throws Exception {
        if (!isNetworkAvailable()) {
            throw new Exception("Network unavailable — cannot upload pending packets");
        }

        packetService.syncAllPacketStatus();
        auditManagerService.audit(AuditEvent.MACHINE_REMAPPED, Components.PACKET_STATUS_SYNCHED);

        List<Registration> pending = registrationRepository.getAllPendingForProcessing();
        for (Registration reg : pending) {
            CompletableFuture<Void> uploadDone = new CompletableFuture<>();
            packetService.uploadRegistration(reg.getPacketId(), new AsyncPacketTaskCallBack() {
                @Override
                public void inProgress(String RID) {}

                @Override
                public void onComplete(String RID, PacketTaskStatus status) {
                    if (status == PacketTaskStatus.UPLOAD_FAILED) {
                        uploadDone.completeExceptionally(new Exception("Upload failed for packet: " + RID));
                    } else {
                        uploadDone.complete(null);
                    }
                }
            });
            // Block until this packet's upload finishes (or times out after 2 min).
            uploadDone.get(2, TimeUnit.MINUTES);
        }
        auditManagerService.audit(AuditEvent.MACHINE_REMAPPED, Components.PACKETS_UPLOADED);
        Log.i(TAG, "Step 2: uploaded " + pending.size() + " packet(s)");
    }

    private void deleteAllLocalPackets() {
        packetService.deleteAllRegistrationPackets();
        preRegistrationDataSyncService.deleteAllPreRegRecords();
        auditManagerService.audit(AuditEvent.MACHINE_REMAPPED, Components.PACKET_SYNCHED);
        Log.i(TAG, "Step 3: all local packets deleted");
    }

    private void purgeAndReset() {
        // Wipe all user data so the app behaves like a fresh install after the remap restart.
        userRoleRepository.deleteAll();
        userBiometricRepository.deleteAll();
        userDetailRepository.deleteAll();

        // Delete center-specific master data
        registrationCenterRepository.deleteAll();
        templateRepository.deleteAll();
        dynamicFieldRepository.deleteAll();
        identitySchemaRepository.deleteAll(context);
        locationRepository.deleteAll();

        // Clear all cached certificates so the initial sync after remap fetches fresh
        // policy keys and CA/kernel certs for the new center without conflicts.
        keyStoreRepository.deleteAll();

        // Clear sync timestamps so getLastSyncTime() returns "LastSyncTimeIsNull" on next login,
        // which triggers the initial sync that re-downloads all center data for the new center.
        globalParamRepository.saveGlobalParam(SYNC_LAST_UPDATED, null);
        globalParamRepository.saveGlobalParam(MASTER_DATA_LAST_UPDATED, null);

        // Reset remap flag — UPDATE in place; global_param is never deleted
        globalParamRepository.saveGlobalParam(RegistrationConstants.MACHINE_CENTER_CHANGED, "false");

        auditManagerService.audit(AuditEvent.MACHINE_REMAPPED, Components.CLEAN_UP);
        Log.i(TAG, "Step 4: center data purged, sync timestamps cleared, remap flag reset");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}