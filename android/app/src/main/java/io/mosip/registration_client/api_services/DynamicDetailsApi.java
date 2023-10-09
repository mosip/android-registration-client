package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.entity.Language;
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
    public void getFieldValues(@NonNull String fieldName, @NonNull String langCode, @NonNull DynamicResponsePigeon.Result<List<DynamicResponsePigeon.DynamicFieldData>> result) {
        List<DynamicResponsePigeon.DynamicFieldData> response = new ArrayList<>();
        try {
            List<GenericValueDto> genericValueDtoList = this.masterDataService.getFieldValues(fieldName, langCode);
            genericValueDtoList.forEach((dto) -> {
                DynamicResponsePigeon.DynamicFieldData data = new DynamicResponsePigeon.DynamicFieldData.Builder()
                        .setCode(dto.getCode())
                        .setName(dto.getName())
                        .build();
                response.add(data);
            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch field values: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(response);
    }

    @Override
    public void getLocationValues(@NonNull String hierarchyLevelName, @NonNull String langCode, @NonNull DynamicResponsePigeon.Result<List<DynamicResponsePigeon.GenericData>> result) {
        List<DynamicResponsePigeon.GenericData> locationList = new ArrayList<>();
        try {
            int hierarchyLevel = this.masterDataService.getHierarchyLevel(hierarchyLevelName);
            List<GenericValueDto> genericValueList = this.masterDataService.findLocationByHierarchyLevel(hierarchyLevelName, langCode);
            genericValueList.forEach((v) -> {
                DynamicResponsePigeon.GenericData location = new DynamicResponsePigeon.GenericData.Builder()
                        .setCode(v.getCode())
                        .setName(v.getName())
                        .setLangCode(v.getLangCode())
                        .setHierarchyLevel((long) hierarchyLevel)
                        .build();
                locationList.add(location);
            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch location values: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(locationList);
    }

    @Override
    public void getDocumentValues(@NonNull String categoryCode, String applicantType, @NonNull String langCode, @NonNull DynamicResponsePigeon.Result<List<String>> result) {

        List<String> documentResponse = new ArrayList<>();
        try {
            documentResponse = this.masterDataService.getDocumentTypes(categoryCode, applicantType, langCode);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch document values: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(documentResponse);

    }

    @Override
    public void getLocationValuesBasedOnParent(@Nullable String parentCode, @NonNull String hierarchyLevelName, @NonNull String langCode, @NonNull DynamicResponsePigeon.Result<List<DynamicResponsePigeon.GenericData>> result) {
        List<DynamicResponsePigeon.GenericData> locationList = new ArrayList<>();
        try {
            int hierarchyLevel = this.masterDataService.getHierarchyLevel(hierarchyLevelName);
            List<GenericValueDto> genericValueList = this.masterDataService.findLocationByParentHierarchyCode(parentCode, langCode);
            genericValueList.forEach((v) -> {
                DynamicResponsePigeon.GenericData location = new DynamicResponsePigeon.GenericData.Builder()
                        .setCode(v.getCode())
                        .setName(v.getName())
                        .setLangCode(v.getLangCode())
                        .setHierarchyLevel((long) hierarchyLevel)
                        .build();
                locationList.add(location);
            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch location values: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(locationList);
    }

    @Override
    public void getAllLanguages(@NonNull DynamicResponsePigeon.Result<List<DynamicResponsePigeon.LanguageData>> result) {
        List<DynamicResponsePigeon.LanguageData> languageDataList = new ArrayList<>();
        try {
            List<Language> languageList = this.masterDataService.getAllLanguages();
            languageList.forEach((lang) -> {
                DynamicResponsePigeon.LanguageData languageData = new DynamicResponsePigeon.LanguageData.Builder()
                        .setCode(lang.getCode())
                        .setName(lang.getName())
                        .setNativeName(lang.getNativeName())
                        .build();
                languageDataList.add(languageData);
            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch language values failed: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(languageDataList);
    }
}
