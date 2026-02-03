/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    public void audit(@NonNull String id, @NonNull String componentId, @Nullable String description, @NonNull AuditResponsePigeon.Result<Void> result) {
        try {
            AuditEvent matchedEvent = Arrays.stream(AuditEvent.values())
                    .filter((event) -> Objects.equals(event.getId(), id) || Objects.equals(event.getName(), id))
                    .findFirst()
                    .orElse(null);
            if (matchedEvent != null) {
                auditEvent(matchedEvent, componentId, description);
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Exception in system audit event!", e);
        }
    }

    private void auditEvent(AuditEvent auditEvent, String componentId, String description) {
        Arrays.stream(Components.values()).forEach((component) -> {
            if (Objects.equals(component.getId(), componentId)) {
                if (description != null && !description.isEmpty()) {
                    auditManagerService.auditWithDescriptionOverride(auditEvent, component.getId(), component.getName(), description);
                } else {
                    auditManagerService.audit(auditEvent, component);
                }
            }
        });
    }
}
