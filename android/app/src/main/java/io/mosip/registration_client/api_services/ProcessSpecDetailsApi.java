/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

package io.mosip.registration_client.api_services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.SettingsSpecDto;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration_client.model.ProcessSpecPigeon;

@Singleton
public class ProcessSpecDetailsApi implements ProcessSpecPigeon.ProcessSpecApi {

    Context context;
    IdentitySchemaRepository identitySchemaRepository;
    GlobalParamRepository globalParamRepository;
    AuditManagerService auditManagerService;
    RegistrationService registrationService;

    @Inject
    public ProcessSpecDetailsApi(Context context,
                                 IdentitySchemaRepository identitySchemaRepository,
                                 GlobalParamRepository globalParamRepository,
                                 RegistrationService registrationService,
                                 AuditManagerService auditManagerService) {

        this.context = context;
        this.identitySchemaRepository = identitySchemaRepository;
        this.globalParamRepository = globalParamRepository;
        this.registrationService = registrationService;
        this.auditManagerService = auditManagerService;
    }

    @Override
    public void getUISchema(@NonNull ProcessSpecPigeon.Result<String> result) {
        try {
            String schemaJson = identitySchemaRepository.getSchemaJson(context,
                    identitySchemaRepository.getLatestSchemaVersion());
            result.success(schemaJson);
            return;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getUISchema", e);
        }
        result.success("");
    }

    @Override
    public void getStringValueGlobalParam(@NonNull String key, @NonNull ProcessSpecPigeon.Result<String> result) {
        try {
            String cachedString = globalParamRepository.getCachedStringGlobalParam(key);
            result.success(cachedString);
            return;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getStringValueGlobalParam", e);
        }
        result.success("");
    }

    @Override
    public void getNewProcessSpec(@NonNull ProcessSpecPigeon.Result<List<String>> result) {
        List<String> processSpecList = new ArrayList<>();
        try {
            List<ProcessSpecDto> processSpecDtoList = identitySchemaRepository.getAllProcessSpecDTO(context, identitySchemaRepository.getLatestSchemaVersion());
            if(processSpecDtoList == null || processSpecDtoList.isEmpty()) {
                ProcessSpecDto processSpecDto = identitySchemaRepository.getNewProcessSpec(context,
                        identitySchemaRepository.getLatestSchemaVersion());
                processSpecDtoList = Arrays.asList(processSpecDto);
            }
            processSpecDtoList.forEach((processSpecDto -> {
                ObjectWriter ow;
                try {
                    ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                    if (processSpecDto != null) {
                        String json = ow.writeValueAsString(processSpecDto);
                        processSpecList.add(json);
                    }
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error in fetching process spec", e);
                }
            }));
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getNewProcessSpec", e);
        }
        result.success(processSpecList);
    }

    @Override
    public void getMandatoryLanguageCodes(@NonNull ProcessSpecPigeon.Result<List<String>> result) {
        List<String> mandatoryLanguageList = new ArrayList<>();
        try {
            mandatoryLanguageList = globalParamRepository.getMandatoryLanguageCodes();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getMandatoryLanguageCodes", e);
        }
        result.success(mandatoryLanguageList);
    }

    @Override
    public void getOptionalLanguageCodes(@NonNull ProcessSpecPigeon.Result<List<String>> result) {
        List<String> optionalLanguageList = new ArrayList<>();
        try {
            optionalLanguageList = globalParamRepository.getOptionalLanguageCodes();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getOptionalLanguageCodes", e);
        }
        result.success(optionalLanguageList);
    }

    @Override
    public void getMinLanguageCount(@NonNull ProcessSpecPigeon.Result<Long> result) {
        int minLangCount = 0;
        try {
            minLangCount = globalParamRepository.getMinLanguageCount();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getMinLangCount", e);
        }
        result.success((long) minLangCount);
    }

    @Override
    public void getMaxLanguageCount(@NonNull ProcessSpecPigeon.Result<Long> result) {
        int maxLangCount = 0;
        try {
            maxLangCount = globalParamRepository.getMaxLanguageCount();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getMaxLangCount", e);
        }
        result.success((long) maxLangCount);
    }

    @Override
    public void getSettingSpec(@NonNull ProcessSpecPigeon.Result<List<String>> result) {
        List<String> settingSpecList = new ArrayList<>();
        try {
            List<SettingsSpecDto> settingsSpecDto = identitySchemaRepository.getSettingsSchema(context, identitySchemaRepository.getLatestSchemaVersion());
            for (SettingsSpecDto dto : settingsSpecDto) {
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(dto);
                settingSpecList.add(json);
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getSettingSpec", e);
        }
        result.success(settingSpecList);
    }
}
