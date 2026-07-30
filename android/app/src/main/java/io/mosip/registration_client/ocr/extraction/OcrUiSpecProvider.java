/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.extraction;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration_client.ocr.OcrParamKeys;
import io.mosip.registration_client.ocr.models.FieldSpec;

public class OcrUiSpecProvider {

    private static final String TAG = "OcrUiSpecProvider";

    private final GlobalParamRepository globalParamRepository;

    public OcrUiSpecProvider(@NonNull GlobalParamRepository globalParamRepository) {
        this.globalParamRepository = globalParamRepository;
    }

    @NonNull
    public List<FieldSpec> getSpec() {
        List<FieldSpec> result = new ArrayList<>();

        String rawJson;
        try {
            rawJson = globalParamRepository.getCachedStringGlobalParam(OcrParamKeys.OCR_UI_SPEC);
        } catch (Exception e) {
            Log.e(TAG, "Failed reading " + OcrParamKeys.OCR_UI_SPEC + " from global param cache", e);
            return result;
        }

        if (rawJson == null || rawJson.trim().isEmpty()) {
            Log.w(TAG, "No UI spec configured under " + OcrParamKeys.OCR_UI_SPEC);
            return result;
        }

        try {
            JSONArray fields = extractFieldsArray(new JSONTokener(rawJson).nextValue());
            for (int i = 0; i < fields.length(); i++) {
                JSONObject fieldJson = fields.optJSONObject(i);
                if (fieldJson == null) {
                    Log.w(TAG, "Skipping non-object spec entry at index " + i);
                    continue;
                }

                String id = fieldJson.optString("id", "").trim();
                if (id.isEmpty()) {
                    Log.w(TAG, "Skipping spec entry with missing id at index " + i);
                    continue;
                }

                result.add(new FieldSpec(
                        id,
                        nullableString(fieldJson, "type"),
                        nullableString(fieldJson, "controlType"),
                        nullableString(fieldJson, "subType")
                ));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse OCR UI spec JSON, treating as empty spec", e);
        }

        return result;
    }

    @NonNull
    private JSONArray extractFieldsArray(Object parsed) throws JSONException {
        if (parsed instanceof JSONArray) {
            return (JSONArray) parsed;
        }
        if (parsed instanceof JSONObject && ((JSONObject) parsed).has("fields")) {
            return ((JSONObject) parsed).getJSONArray("fields");
        }
        throw new JSONException(
                "OCR UI spec must be a JSON array of fields, or an object containing a 'fields' array");
    }

    private String nullableString(@NonNull JSONObject json, @NonNull String key) {
        return json.has(key) && !json.isNull(key) ? json.optString(key, null) : null;
    }
}