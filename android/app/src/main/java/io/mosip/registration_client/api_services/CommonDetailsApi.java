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
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.LocalConfigService;
import io.mosip.registration_client.model.CommonDetailsPigeon;

@Singleton
public class CommonDetailsApi implements CommonDetailsPigeon.CommonDetailsApi {

    MasterDataService masterDataService;
    AuditManagerService auditManagerService;
    LocalConfigService localConfigService;

    @Inject
    public CommonDetailsApi(MasterDataService masterDataService, AuditManagerService auditManagerService, LocalConfigService localConfigService){
        this.masterDataService=masterDataService;
        this.auditManagerService = auditManagerService;
        this.localConfigService = localConfigService;
    }

    @Override
    public void getTemplateContent(@NonNull String templateName, @NonNull String langCode, @NonNull CommonDetailsPigeon.Result<String> result) {
        String response=masterDataService.getTemplateContent(templateName,langCode);
        result.success(response);
    }

    @Override
    public void getPreviewTemplateContent(@NonNull String templateTypeCode, @NonNull String langCode, @NonNull CommonDetailsPigeon.Result<String> result) {
        String response=masterDataService.getPreviewTemplateContent(templateTypeCode,langCode);
        result.success(response);
    }

    @Override
    public void getDocumentTypes(@NonNull String categoryCode, @NonNull String applicantType, @NonNull String langCode, @NonNull CommonDetailsPigeon.Result<List<String>> result) {
     List<String> response=masterDataService.getDocumentTypes(categoryCode,applicantType,langCode);
     result.success(response);
    }

    @Override
    public void getFieldValues(@NonNull String fieldName, @NonNull String langCode, @NonNull CommonDetailsPigeon.Result<List<String>> result) {
      List<GenericValueDto> output=masterDataService.getFieldValues(fieldName,langCode);
      List<String> response=new ArrayList<>();
      output.forEach((value)->{response.add(value.toString());});
      result.success(response);
    }

    @Override
    public void saveVersionToGlobalParam(@NonNull String id, @NonNull String value, @NonNull CommonDetailsPigeon.Result<String> result) {
        String response = "";
        try {
            masterDataService.saveGlobalParam(id, value);
            response = "OK";
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in save version.", e);
        }
        result.success(response);
    }

    @Override
    public void getVersionFromGlobalParam(@NonNull String id, @NonNull CommonDetailsPigeon.Result<String> result) {
        String response = "";
        try {
            response = masterDataService.getGlobalParamValue(id);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in save version.", e);
        }
        result.success(response);
    }

    @Override
    public void saveScreenHeaderToGlobalParam(@NonNull String id, @NonNull String value, @NonNull CommonDetailsPigeon.Result<String> result) {
        String response = "";
        try {
            masterDataService.saveGlobalParam(id, value);
            response = "OK";
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in save screen header.", e);
        }
        result.success(response);
    }

    @Override
    public void getRegistrationParams(@NonNull CommonDetailsPigeon.Result<Map<String, Object>> result) {
        try {
            Map<String, Object> response = masterDataService.getRegistrationParams();
            Log.i(getClass().getSimpleName(), "Registration params fetched: " + response);
            result.success(response);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error fetching registration params.", e);
            result.error(e);
        }
    }
    @Override
    public void getLocalConfigurations(@NonNull CommonDetailsPigeon.Result<Map<String, String>> result) {
        try {
            Map<String, String> response = localConfigService.getLocalConfigurations();
            Log.i(getClass().getSimpleName(), "Local configurations fetched: " + response);
            result.success(response);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error fetching local configurations.", e);
            result.error(e);
        }
    }

    @Override
    public void getPermittedConfigurationNames(@NonNull CommonDetailsPigeon.Result<List<String>> result) {
        try {
            List<String> response = localConfigService.getPermittedConfigurationNames();
            Log.i(getClass().getSimpleName(), "Permitted configuration names fetched: " + response);
            result.success(response);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error fetching permitted configuration names.", e);
            result.error(e);
        }
    }

    @Override
    public void modifyConfigurations(@NonNull Map<String, String> localPreferences, @NonNull CommonDetailsPigeon.Result<Void> result) {
        try {
            localConfigService.modifyConfigurations(localPreferences);
            Log.i(getClass().getSimpleName(), "Configurations modified successfully");
            result.success(null);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error modifying configurations.", e);
            result.error(e);
        }
    }
}
