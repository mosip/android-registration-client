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

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration_client.model.CommonDetailsPigeon;

@Singleton
public class CommonDetailsApi implements CommonDetailsPigeon.CommonDetailsApi {

    MasterDataService masterDataService;
    AuditManagerService auditManagerService;

    @Inject
    public CommonDetailsApi(MasterDataService masterDataService, AuditManagerService auditManagerService){
        this.masterDataService=masterDataService;
        this.auditManagerService = auditManagerService;
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
}
