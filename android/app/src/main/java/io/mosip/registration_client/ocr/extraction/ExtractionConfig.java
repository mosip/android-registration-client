/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.extraction;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration_client.ocr.OcrParamKeys;

public final class ExtractionConfig {

    private static final String TAG = "ExtractionConfig";

    private final Map<String, List<String>> labelsMap;
    private final Set<String> genderValues;
    private final Set<String> monthNames;
    private final Set<String> headerPatterns;
    private final String noiseChars;

    public ExtractionConfig(@NonNull GlobalParamRepository repo) {
        this.labelsMap = loadLabels(repo);
        this.genderValues = loadGenderValues(repo);
        this.monthNames = loadMonthNames(repo);
        this.headerPatterns = loadHeaderPatterns(repo);
        this.noiseChars = loadNoiseChars(repo);
    }

    private Map<String, List<String>> loadLabels(GlobalParamRepository repo) {
        Map<String, List<String>> defaultLabels = new HashMap<>();
        defaultLabels.put("firstName", Arrays.asList("name", "first name", "given name", "given names", "full name"));
        defaultLabels.put("lastName", Arrays.asList("surname", "last name", "family name"));
        defaultLabels.put("dateOfBirth", Arrays.asList("date of birth", "dob", "d.o.b", "d.o.b.", "birth date", "born"));
        defaultLabels.put("gender", Arrays.asList("sex", "gender"));
        defaultLabels.put("address", Arrays.asList("address", "residential address"));
        defaultLabels.put("postalCode", Arrays.asList("pin", "pin code", "pincode", "zip", "postal code"));
        defaultLabels.put("phone", Arrays.asList("phone", "mobile", "tel", "contact"));
        defaultLabels.put("email", Arrays.asList("email", "e-mail"));
        defaultLabels.put("documentNumber", Arrays.asList("no", "number", "id", "card no", "document no"));
        defaultLabels.put("fatherName", Arrays.asList("father", "father's name", "father name", "s/o", "d/o", "w/o"));

        try {
            String param = repo.getCachedStringGlobalParam(OcrParamKeys.OCR_EXTRACTION_LABELS);
            if (param != null && !param.trim().isEmpty()) {
                JSONObject json = new JSONObject(param);
                Map<String, List<String>> parsedLabels = new HashMap<>();
                JSONArray names = json.names();
                if (names != null) {
                    for (int i = 0; i < names.length(); i++) {
                        String key = names.getString(i);
                        JSONArray arr = json.getJSONArray(key);
                        List<String> list = new ArrayList<>();
                        for (int j = 0; j < arr.length(); j++) {
                            list.add(arr.getString(j));
                        }
                        parsedLabels.put(key, Collections.unmodifiableList(list));
                    }
                }
                return Collections.unmodifiableMap(parsedLabels);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse " + OcrParamKeys.OCR_EXTRACTION_LABELS + ", using defaults", e);
        }

        Map<String, List<String>> unmodifiableDefaults = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : defaultLabels.entrySet()) {
            unmodifiableDefaults.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(unmodifiableDefaults);
    }

    private Set<String> loadGenderValues(GlobalParamRepository repo) {
        Set<String> defaults = new HashSet<>(Arrays.asList("male", "female", "m", "f", "transgender", "other"));
        return loadSetFromParam(repo, OcrParamKeys.OCR_EXTRACTION_GENDER_VALUES, defaults);
    }

    private Set<String> loadMonthNames(GlobalParamRepository repo) {
        Set<String> defaults = new HashSet<>(Arrays.asList(
                "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec",
                "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"
        ));
        return loadSetFromParam(repo, OcrParamKeys.OCR_EXTRACTION_MONTH_NAMES, defaults);
    }

    private Set<String> loadHeaderPatterns(GlobalParamRepository repo) {
        Set<String> defaults = new HashSet<>(Arrays.asList(
                "government of", "republic of", "income tax", "election commission", "dept of", "ministry of"
        ));
        return loadSetFromParam(repo, OcrParamKeys.OCR_EXTRACTION_HEADER_PATTERNS, defaults);
    }

    private Set<String> loadSetFromParam(GlobalParamRepository repo, String key, Set<String> defaults) {
        try {
            String param = repo.getCachedStringGlobalParam(key);
            if (param != null && !param.trim().isEmpty()) {
                JSONArray arr = new JSONArray(param);
                Set<String> parsedSet = new HashSet<>();
                for (int i = 0; i < arr.length(); i++) {
                    parsedSet.add(arr.getString(i));
                }
                return Collections.unmodifiableSet(parsedSet);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse " + key + ", using defaults", e);
        }
        return Collections.unmodifiableSet(defaults);
    }

    private String loadNoiseChars(GlobalParamRepository repo) {
        String defaults = "|\\";
        try {
            String param = repo.getCachedStringGlobalParam(OcrParamKeys.OCR_EXTRACTION_NOISE_CHARS);
            if (param != null && !param.isEmpty()) {
                return param;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get " + OcrParamKeys.OCR_EXTRACTION_NOISE_CHARS + ", using defaults", e);
        }
        return defaults;
    }

    @NonNull
    public List<String> labelsForSubType(@NonNull String subType) {
        List<String> labels = labelsMap.get(subType);
        return labels != null ? labels : Collections.emptyList();
    }

    @NonNull
    public Set<String> genderValues() {
        return genderValues;
    }

    @NonNull
    public Set<String> monthNames() {
        return monthNames;
    }

    @NonNull
    public Set<String> headerPatterns() {
        return headerPatterns;
    }

    @NonNull
    public String noiseChars() {
        return noiseChars;
    }
}
