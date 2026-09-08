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

    static final float DEFAULT_FUZZY_THRESHOLD = 0.82f;


    private final Map<String, List<String>> labelsMap;
    private final Set<String>               genderValues;
    private final Set<String>               monthNames;
    private final Set<String>               headerPatterns;
    private final String                    noiseChars;
    private final float                     fuzzyThreshold;
    private final Map<String, String>       fieldRegexMap;


    public ExtractionConfig(@NonNull GlobalParamRepository repo) {
        this.labelsMap      = loadLabels(repo);
        this.genderValues   = loadGenderValues(repo);
        this.monthNames     = loadMonthNames(repo);
        this.headerPatterns = loadHeaderPatterns(repo);
        this.noiseChars     = loadNoiseChars(repo);
        this.fuzzyThreshold = loadFuzzyThreshold(repo);
        this.fieldRegexMap  = loadFieldRegexMap(repo);
    }

    @NonNull
    public List<String> labelsForSubType(@NonNull String subType) {
        List<String> labels = labelsMap.get(subType);
        if (labels != null) return labels;

        String canonical = canonicalKey(subType);
        for (Map.Entry<String, List<String>> entry : labelsMap.entrySet()) {
            if (canonicalKey(entry.getKey()).equals(canonical)) {
                return entry.getValue();
            }
        }
        return Collections.emptyList();
    }

    @NonNull public Set<String> genderValues()   { return genderValues;   }
    @NonNull public Set<String> monthNames()     { return monthNames;     }
    @NonNull public Set<String> headerPatterns() { return headerPatterns; }
    @NonNull public String      noiseChars()     { return noiseChars;     }

    public float getFuzzyThreshold() { return fuzzyThreshold; }

    @NonNull public Map<String, String> getFieldRegexMap() { return fieldRegexMap; }

    @NonNull
    private static String canonicalKey(@NonNull String value) {
        return value.replaceAll("[^\\p{Alnum}]", "").toLowerCase(java.util.Locale.US);
    }


    private Map<String, List<String>> loadLabels(GlobalParamRepository repo) {
        Map<String, List<String>> defaults = new HashMap<>();

        defaults.put("firstName",  Arrays.asList(
                "name", "first name", "given name", "given names", "full name",
                "applicant name", "holder name", "cardholder"));
        defaults.put("lastName",   Arrays.asList(
                "surname", "last name", "family name", "last"));
        defaults.put("dateOfBirth", Arrays.asList(
                "date of birth", "dob", "d.o.b", "d.o.b.", "birth date",
                "born", "birthdate", "date de naissance", "birth"));
        defaults.put("gender",     Arrays.asList(
                "sex", "gender", "sexo"));
        defaults.put("address",    Arrays.asList(
                "address", "residential address", "permanent address",
                "addr", "residence"));
        defaults.put("postalCode", Arrays.asList(
                "pin", "pin code", "pincode", "zip", "zip code",
                "postal code", "post code"));
        defaults.put("phone",      Arrays.asList(
                "phone", "mobile", "tel", "telephone", "contact",
                "cell", "mob", "mobile no", "phone no", "contact no"));
        defaults.put("email",      Arrays.asList(
                "email", "e-mail", "e mail", "mail"));
        defaults.put("documentNumber", Arrays.asList(
                "no", "number", "id", "card no", "document no",
                "id no", "card number", "serial no", "reg no",
                "registration no", "enrollment no"));
        defaults.put("fatherName", Arrays.asList(
                "father", "father's name", "father name",
                "s/o", "d/o", "w/o", "c/o", "guardian"));
        defaults.put("motherName", Arrays.asList(
                "mother", "mother's name", "mother name"));
        defaults.put("bloodGroup", Arrays.asList(
                "blood group", "blood type", "blood", "b.g", "b/g",
                "blood grp", "bg"));

        try {
            String param = repo.getCachedStringGlobalParam(OcrParamKeys.OCR_EXTRACTION_LABELS);
            if (param != null && !param.trim().isEmpty()) {
                JSONObject json = new JSONObject(param);
                Map<String, List<String>> parsed = new HashMap<>();
                JSONArray names = json.names();
                if (names != null) {
                    for (int i = 0; i < names.length(); i++) {
                        String key = names.getString(i);
                        JSONArray arr = json.getJSONArray(key);
                        List<String> list = new ArrayList<>();
                        for (int j = 0; j < arr.length(); j++) {
                            list.add(arr.getString(j));
                        }
                        parsed.put(key, Collections.unmodifiableList(list));
                    }
                }
                for (Map.Entry<String, List<String>> e : defaults.entrySet()) {
                    if (!parsed.containsKey(e.getKey())) {
                        parsed.put(e.getKey(), e.getValue());
                    }
                }
                return Collections.unmodifiableMap(parsed);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse " + OcrParamKeys.OCR_EXTRACTION_LABELS
                    + ", using built-in defaults", e);
        }

        // Return immutable defaults
        Map<String, List<String>> result = new HashMap<>();
        for (Map.Entry<String, List<String>> e : defaults.entrySet()) {
            result.put(e.getKey(), Collections.unmodifiableList(e.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    private Set<String> loadGenderValues(GlobalParamRepository repo) {
        Set<String> defaults = new HashSet<>(Arrays.asList(
                "male", "female", "m", "f", "transgender", "other", "others"));
        return loadSetFromParam(repo, OcrParamKeys.OCR_EXTRACTION_GENDER_VALUES, defaults);
    }

    private Set<String> loadMonthNames(GlobalParamRepository repo) {
        Set<String> defaults = new HashSet<>(Arrays.asList(
                "jan", "feb", "mar", "apr", "may", "jun",
                "jul", "aug", "sep", "sept", "oct", "nov", "dec",
                "january", "february", "march", "april", "june", "july",
                "august", "september", "october", "november", "december"
        ));
        return loadSetFromParam(repo, OcrParamKeys.OCR_EXTRACTION_MONTH_NAMES, defaults);
    }

    private Set<String> loadHeaderPatterns(GlobalParamRepository repo) {
        Set<String> defaults = new HashSet<>(Arrays.asList(
                "government of", "republic of", "income tax",
                "election commission", "dept of", "ministry of",
                "id card", "identity card", "voter id"
        ));
        return loadSetFromParam(repo, OcrParamKeys.OCR_EXTRACTION_HEADER_PATTERNS, defaults);
    }

    private Set<String> loadSetFromParam(GlobalParamRepository repo,
                                          String key,
                                          Set<String> defaults) {
        try {
            String param = repo.getCachedStringGlobalParam(key);
            if (param != null && !param.trim().isEmpty()) {
                JSONArray arr = new JSONArray(param);
                Set<String> result = new HashSet<>();
                for (int i = 0; i < arr.length(); i++) result.add(arr.getString(i));
                return Collections.unmodifiableSet(result);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse " + key + ", using defaults", e);
        }
        return Collections.unmodifiableSet(defaults);
    }

    private String loadNoiseChars(GlobalParamRepository repo) {
        try {
            String param = repo.getCachedStringGlobalParam(OcrParamKeys.OCR_EXTRACTION_NOISE_CHARS);
            if (param != null && !param.isEmpty()) return param;
        } catch (Exception e) {
            Log.e(TAG, "Failed to get " + OcrParamKeys.OCR_EXTRACTION_NOISE_CHARS, e);
        }
        return "|\\";  // default noise chars
    }

    private float loadFuzzyThreshold(GlobalParamRepository repo) {
        try {
            String param = repo.getCachedStringGlobalParam(
                    OcrParamKeys.OCR_EXTRACTION_FUZZY_THRESHOLD);
            if (param != null && !param.isEmpty()) {
                return Float.parseFloat(param.trim());
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse " + OcrParamKeys.OCR_EXTRACTION_FUZZY_THRESHOLD
                    + ", using default " + DEFAULT_FUZZY_THRESHOLD, e);
        }
        return DEFAULT_FUZZY_THRESHOLD;
    }

    private Map<String, String> loadFieldRegexMap(GlobalParamRepository repo) {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("dateofbirth",  "\\d{1,4}[\\-/.]\\d{1,2}[\\-/.]\\d{2,4}");
        defaults.put("dob",          "\\d{1,4}[\\-/.]\\d{1,2}[\\-/.]\\d{2,4}");
        defaults.put("phone",        "\\+?[\\d][\\d\\-\\s]{7,14}");
        defaults.put("mobile",       "\\+?[\\d][\\d\\-\\s]{7,14}");
        defaults.put("email",        "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");
        defaults.put("bloodgroup",   "[AaBoO]{1,2}[+\\-]?(\\s*\\(\\s*[Vv][Ee]\\s*\\))?");
        defaults.put("postalcode",   "\\b\\d{4,10}\\b");

        try {
            String param = repo.getCachedStringGlobalParam(
                    OcrParamKeys.OCR_EXTRACTION_FIELD_REGEXES);
            if (param != null && !param.trim().isEmpty()) {
                JSONObject json = new JSONObject(param);
                JSONArray names = json.names();
                if (names != null) {
                    for (int i = 0; i < names.length(); i++) {
                        String key = names.getString(i);
                        defaults.put(key.toLowerCase(), json.getString(key));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse " + OcrParamKeys.OCR_EXTRACTION_FIELD_REGEXES
                    + ", using defaults", e);
        }

        return Collections.unmodifiableMap(defaults);
    }
}
