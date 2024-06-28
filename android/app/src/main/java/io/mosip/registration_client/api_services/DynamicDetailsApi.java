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
import androidx.annotation.Nullable;

import org.apache.velocity.VelocityContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.DocumentDto;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.entity.Language;
import io.mosip.registration.clientmanager.entity.Location;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PreRegistrationDataSyncService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.packetmanager.dto.PacketWriter.DocumentType;
import io.mosip.registration.packetmanager.dto.SimpleType;
import io.mosip.registration_client.model.DynamicResponsePigeon;

@Singleton
public class DynamicDetailsApi implements DynamicResponsePigeon.DynamicResponseApi {
    MasterDataService masterDataService;
    AuditManagerService auditManagerService;
    PreRegistrationDataSyncService preRegistrationData;
    RegistrationService registrationService;
    IdentitySchemaRepository identitySchemaService;
    Context appContext;

    @Inject
    public DynamicDetailsApi(Context appContext,MasterDataService masterDataService, AuditManagerService auditManagerService, PreRegistrationDataSyncService preRegistrationData,RegistrationService registrationService,IdentitySchemaRepository identitySchemaService) {
        this.appContext = appContext;
        this.masterDataService = masterDataService;
        this.auditManagerService = auditManagerService;
        this.preRegistrationData = preRegistrationData;
        this.registrationService = registrationService;
        this.identitySchemaService = identitySchemaService;
    }


    @Override
    public void getFieldValues(@NonNull String fieldName, @NonNull String langCode, @NonNull List<String> languages, @NonNull DynamicResponsePigeon.Result<List<DynamicResponsePigeon.DynamicFieldData>> result) {
        List<DynamicResponsePigeon.DynamicFieldData> response = new ArrayList<>();
        int numberOfLanguages = languages.size();
        List<GenericValueDto>[] resultList = new ArrayList[numberOfLanguages];
        try {
            for (int i = 0; i < languages.size(); i++) {
                resultList[i] = this.masterDataService.getFieldValues(fieldName, languages.get(i));
            }
            List<String> fullNameSet = concatenateValues(resultList);

            List<GenericValueDto> genericValueDtoList = this.masterDataService.getFieldValues(fieldName, langCode);

            for (int i = 0; i < genericValueDtoList.size(); i++) {
                String value = fullNameSet.isEmpty() ? genericValueDtoList.get(i).getName() : fullNameSet.get(i);
                GenericValueDto dto = genericValueDtoList.get(i);
                DynamicResponsePigeon.DynamicFieldData data = new DynamicResponsePigeon.DynamicFieldData.Builder()
                        .setCode(dto.getCode())
                        .setName(dto.getName())
                        .setConcatenatedName(value)
                        .build();
                response.add(data);
            }

        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch field values: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(response);
    }

    private List<String> concatenateValues(List<GenericValueDto>[] resultList) {
        List<String> fullNameSet = new ArrayList<>();

        if (resultList.length > 0 && resultList[0] != null) {
            for (int k = 0; k < resultList[0].size(); k++) {
                StringBuilder concatenated = new StringBuilder();
                for (int j = 0; j < resultList.length; j++) {
                    List<GenericValueDto> languageList = resultList[j];
                    if (k < languageList.size()) {
                        GenericValueDto dto = languageList.get(k);
                        String value = dto.toString();
                        concatenated.append(value);
                        if (j < resultList.length - 1 || k < languageList.size() - 1) {
                            concatenated.append(" / ");
                        }
                    }
                }
                String fullNameEntry = concatenated.toString().replaceAll("\\s/\\s$", "");
                fullNameSet.add(fullNameEntry);
            }
        }
        return fullNameSet;
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
    public void getLocationValuesBasedOnParent(@Nullable String parentCode, @NonNull String hierarchyLevelName, @NonNull String langCode, @NonNull List<String> languages, @NonNull DynamicResponsePigeon.Result<List<DynamicResponsePigeon.GenericData>> result) {
        List<DynamicResponsePigeon.GenericData> locationList = new ArrayList<>();
        int numberOfLanguages = languages.size();
        List<GenericValueDto>[] resultList = new ArrayList[numberOfLanguages];
        try {
            for (int i = 0; i < languages.size(); i++) {
                resultList[i] = this.masterDataService.findLocationByParentHierarchyCode(parentCode, languages.get(i));
            }
            List<String> fullNameSet = concatenateValues(resultList);
            List<GenericValueDto> genericValueList = this.masterDataService.findLocationByParentHierarchyCode(parentCode, langCode);
            for (int i = 0; i < genericValueList.size(); i++) {
                String value = fullNameSet.isEmpty() ? genericValueList.get(i).getName() : fullNameSet.get(i);
                GenericValueDto dto = genericValueList.get(i);
                DynamicResponsePigeon.GenericData data = new DynamicResponsePigeon.GenericData.Builder()
                        .setCode(dto.getCode())
                        .setName(dto.getName())
                        .setLangCode(dto.getLangCode())
                        .setConcatenatedName(value)
                        .build();
                locationList.add(data);
            }
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

    @Override
    public void fetchPreRegistrationDetails(@NonNull String preRegId, @NonNull DynamicResponsePigeon.Result<Map<String, Object>> result) {
        Map<String, Object> preRegistrationData = new HashMap<>();
        try {
            this.preRegistrationData.getPreRegistration(preRegId,false);
            RegistrationDto registrationDto = this.registrationService.getRegistrationDto();
            List<FieldSpecDto> fieldList = this.identitySchemaService.getAllFieldSpec(appContext, registrationDto.getSchemaVersion());

            for(FieldSpecDto field : fieldList) {
                if(field.getId().equalsIgnoreCase("IDSchemaVersion"))
                    continue;
                switch (field.getType()) {
                    case "documentType":
                        Map<String, Object> docData = getDocumentData(field, registrationDto);
                        if (docData != null) {
                            preRegistrationData.put(field.getId(), docData);
                        }
                        break;
                    case "biometricsType":
                        break;
                    default:
                        JSONObject jsonObject = new JSONObject(String.valueOf(registrationDto));
                        preRegistrationData = jsonObjectToMap(jsonObject);
                        break;
                };

            }

        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch Application ID details failed: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(preRegistrationData);
    }

    public static Map<String, Object> jsonObjectToMap(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        jsonObject.keys().forEachRemaining(key -> {
            Object value = null;
            try {
                value = jsonObject.get(key);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            if (value instanceof JSONArray) {
                List<Object> list = new ArrayList<>();
                JSONArray array = (JSONArray) value;
                for (int i = 0; i < array.length(); i++) {
                    Object item = null;
                    try {
                        item = array.get(i);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if (item instanceof JSONObject) {
                        list.add(jsonObjectToMap((JSONObject) item));
                    } else {
                        list.add(item);
                    }
                }
                map.put(key, list);
            } else if (value instanceof JSONObject) {
                map.put(key, jsonObjectToMap((JSONObject) value));
            } else {
                map.put(key, value);
            }
        });
        return map;
    }

    private Map<String, Object> getDocumentData(FieldSpecDto field, RegistrationDto registrationDto) {
        Map<String, Object> data = null;
        if (registrationDto.getDocuments().get(field.getId()) != null) {
            data = new HashMap<>();
          //  data.put("label", registrationDto.getDocuments().get(field.getId()));
            data.put("value", registrationDto.getDocuments().get(field.getId()).getType());
        }
        return data;
    }
}
