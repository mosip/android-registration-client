/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.service.TemplateService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.packetmanager.util.JsonUtils;
import io.mosip.registration_client.model.RegistrationDataPigeon;

@Singleton
public class RegistrationApi implements RegistrationDataPigeon.RegistrationDataApi {
    private final RegistrationService registrationService;
    RegistrationDto registrationDto;
    TemplateService templateService;
    AuditManagerService auditManagerService;

    @Inject
    public RegistrationApi(RegistrationService registrationService, TemplateService templateService,
                           AuditManagerService auditManagerService) {
        this.registrationService = registrationService;
        this.templateService = templateService;
        this.auditManagerService = auditManagerService;
    }

    @Override
    public void startRegistration(@NonNull List<String> languages, @NonNull RegistrationDataPigeon.Result<String> result) {
        auditManagerService.audit(AuditEvent.REGISTRATION_START, Components.REGISTRATION);
        String response = "";
        try {
            this.registrationDto = registrationService.startRegistration(languages);
        } catch (Exception e) {
            response = e.getMessage();
            Log.e(getClass().getSimpleName(), "Registration start failed", e);
        }
        result.success(response);
    }

    @Override
    public void evaluateMVEL(@NonNull String fieldData, @NonNull String expression, @NonNull RegistrationDataPigeon.Result<Boolean> result) {
        try {
            FieldSpecDto fieldSpecDto = JsonUtils.jsonStringToJavaObject(fieldData, new TypeReference<FieldSpecDto>() {
            });
            this.registrationDto = this.registrationService.getRegistrationDto();
            boolean isFieldVisible = UserInterfaceHelperService.isRequiredField(fieldSpecDto, this.registrationDto.getMVELDataContext());
            result.success(isFieldVisible);
            return;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Object Mapping error: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(false);
    }

    @Override
    public void getPreviewTemplate(@NonNull Boolean isPreview, @NonNull Map<String, String> templateTitleValues, @NonNull RegistrationDataPigeon.Result<String> result) {
        String template = "";
        try {
            this.registrationDto = this.registrationService.getRegistrationDto();
            template = this.templateService.getTemplate(this.registrationDto, isPreview, templateTitleValues);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch template failed: ", e);
        }
        result.success(template);
    }

    @Override
    public void submitRegistrationDto(@NonNull String makerName, @NonNull RegistrationDataPigeon.Result<RegistrationDataPigeon.RegistrationSubmitResponse> result) {
        String response = "";
        String errorCode = "";
        try {
            response = this.registrationService.getRegistrationDto().getRId();
            registrationService.submitRegistrationDto(makerName);
        } catch (Exception e) {
            errorCode = e.getMessage();
            auditManagerService.audit(AuditEvent.CREATE_PACKET_FAILED, Components.REGISTRATION, errorCode);
            Log.e(getClass().getSimpleName(), "Failed on registration submission", e);
        }
        RegistrationDataPigeon.RegistrationSubmitResponse registrationSubmitResponse =
                new RegistrationDataPigeon.RegistrationSubmitResponse
                        .Builder()
                        .setRId(response)
                        .setErrorCode(errorCode)
                        .build();
        result.success(registrationSubmitResponse);
    }
}

