/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.DocumentDto;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration_client.model.DocumentDataPigeon;

@Singleton
public class DocumentDetailsApi implements DocumentDataPigeon.DocumentApi {
    private final RegistrationService registrationService;
    AuditManagerService auditManagerService;

    @Inject
    public DocumentDetailsApi(RegistrationService registrationService, AuditManagerService auditManagerService) {
        this.registrationService = registrationService;
        this.auditManagerService = auditManagerService;
    }


    @Override
    public void addDocument(@NonNull String fieldId, @NonNull String docType, @NonNull String reference, @NonNull byte[] bytes, @NonNull DocumentDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().addDocument(fieldId, docType,null,reference,bytes);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Add Document failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void removeDocument(@NonNull String fieldId, @NonNull Long pageIndex, @NonNull DocumentDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().removeDocument(fieldId, pageIndex.intValue());
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Remove Document failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void getScannedPages(@NonNull String fieldId, @NonNull DocumentDataPigeon.Result<List<DocumentDataPigeon.DocumentData>> result) {
        List<DocumentDataPigeon.DocumentData> scannedPages = new ArrayList<>();
        try {
            List<DocumentDto> scannedValues = this.registrationService.getRegistrationDto().getScannedPages(fieldId);
            scannedValues.forEach((v) -> {
                DocumentDataPigeon.DocumentData data = new DocumentDataPigeon.DocumentData.Builder()
                        .setDoc(v.getContent())
                        .setReferenceNumber(v.getRefNumber())
                        .build();
                scannedPages.add(data);
            });
            result.success(scannedPages);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Getting ScannedPages failed!" + Arrays.toString(e.getStackTrace()));
        }

    }
    @Override
    public void hasDocument(@NonNull String fieldId, @NonNull DocumentDataPigeon.Result<Boolean> result) {
        //TODO
    }

    @Override
    public void removeDocumentField(@NonNull String fieldId, @NonNull DocumentDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().removeDocumentField(fieldId);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Remove Document failed!" + Arrays.toString(e.getStackTrace()));
        }
    }
}