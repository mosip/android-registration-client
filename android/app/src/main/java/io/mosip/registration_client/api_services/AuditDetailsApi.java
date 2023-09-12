package io.mosip.registration_client.api_services;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

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
    public void audit(@NonNull AuditResponsePigeon.Result<Void> result) {

    }
}
