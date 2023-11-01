package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.entity.Language;
import io.mosip.registration.clientmanager.entity.Location;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration_client.model.DynamicResponsePigeon;

@Singleton
public class DynamicDetailsApi implements DynamicResponsePigeon.DynamicResponseApi {
    MasterDataService masterDataService;
    AuditManagerService auditManagerService;

    @Inject
    public DynamicDetailsApi(MasterDataService masterDataService, AuditManagerService auditManagerService) {
        this.masterDataService = masterDataService;
        this.auditManagerService = auditManagerService;
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
    public void getLocationValues(@NonNull String hierarchyLevelName, @NonNull String langCode, @NonNull DynamicResponsePigeon.Result<List<DynamicResponsePigeon.GenericData>> result) {
        List<DynamicResponsePigeon.GenericData> locationList = new ArrayList<>();
        try {
            int level = Integer.parseInt(hierarchyLevelName);
            List<GenericValueDto> genericValueList = this.masterDataService.findLocationByHierarchyLevel(level, langCode);
            genericValueList.forEach((v) -> {
                DynamicResponsePigeon.GenericData location = new DynamicResponsePigeon.GenericData.Builder()
                        .setCode(v.getCode())
                        .setName(v.getName())
                        .setLangCode(v.getLangCode())
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
        List<GenericValueDto> genericValueList = new ArrayList<>();
        try {
            genericValueList = this.masterDataService.findLocationByParentHierarchyCode(parentCode, langCode);
            genericValueList.forEach((v) -> {
                DynamicResponsePigeon.GenericData location = new DynamicResponsePigeon.GenericData.Builder()
                        .setCode(v.getCode())
                        .setName(v.getName())
                        .setLangCode(v.getLangCode())
                        .build();
                locationList.add(location);
            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch location values based on parent: " + Arrays.toString(e.getStackTrace()));
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

    @Override
    public void getLocationHierarchyMap(@NonNull DynamicResponsePigeon.Result<Map<String, String>> result) {
        Map<String, String> hierarchyMap = new HashMap<>();
        try {
            List<Location> locationList = this.masterDataService.findAllLocationsByLangCode("eng");
            locationList.forEach((locationHierarchy) -> {
                String levelName = locationHierarchy.getHierarchyName();
                int level = locationHierarchy.getHierarchyLevel();
                hierarchyMap.put(""+level, levelName);
            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch location hierarchy map failed: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(hierarchyMap);
    }
}
