package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration_client.model.AuditResponsePigeon;

@Singleton
public class AuditDetailsApi implements AuditResponsePigeon.AuditResponseApi {
    AuditManagerService auditManagerService;

    @Inject
    public AuditDetailsApi(AuditManagerService auditManagerService) {
        this.auditManagerService = auditManagerService;
    }

    @Override
    public void audit(@NonNull String id, @NonNull String componentId, @NonNull AuditResponsePigeon.Result<Void> result) {
        try {
            Arrays.stream(AuditEvent.values()).forEach((event) -> {
                if(Objects.equals(event.getId(), id)) {
                    auditEvent(event, componentId);
                }
            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Exception in system audit event!", e);
        }
    }

    private void auditEvent(AuditEvent auditEvent, String componentId) {
        Arrays.stream(Components.values()).forEach((component) -> {
            if(Objects.equals(component.getId(), componentId)) {
                auditManagerService.audit(auditEvent, component);
            }
        });
    }
}
