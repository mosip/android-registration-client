package io.mosip.registration_client.api_services;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration_client.model.CommonDetailsPigeon;

@Singleton
public class CommonDetailsApi implements CommonDetailsPigeon.CommonDetailsApi {

    MasterDataService masterDataService;

    @Inject
    public CommonDetailsApi(MasterDataService masterDataService){
        this.masterDataService=masterDataService;
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
}
