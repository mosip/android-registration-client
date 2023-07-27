package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration_client.model.DynamicResponsePigeon;

@Singleton
public class DynamicDetailsApi implements DynamicResponsePigeon.DynamicResponseApi {
    MasterDataService masterDataService;

    @Inject
    public DynamicDetailsApi(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }


    @Override
    public void getFieldValues(@NonNull String fieldName, @NonNull String langCode, @NonNull DynamicResponsePigeon.Result<List<String>> result) {
        List<String> response = new ArrayList<>();
        try {
            List<GenericValueDto> genericValueDtoList = this.masterDataService.getFieldValues(fieldName, langCode);
            genericValueDtoList.forEach((dto) -> {
                response.add(dto.getName());
            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch field values: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(response);
    }

    @Override
    public void getLocationValues(@NonNull String hierarchyLevelName, @NonNull String langCode, @NonNull DynamicResponsePigeon.Result<List<String>> result) {
        List<String> locationResponse = new ArrayList<>();
        try {
            List<GenericValueDto> genericValueDtoList = this.masterDataService.findLocationByHierarchyLevel(hierarchyLevelName, langCode);
            genericValueDtoList.forEach((dto) -> {
                locationResponse.add(dto.getName());
            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch field values: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(locationResponse);
    }
}
