/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.extraction;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.mosip.registration_client.ocr.models.FieldSpec;

public final class DemographicFieldExtractor {

    private static final String TAG = "DemFieldExtractor";

    private static final Pattern SEPARATOR = Pattern.compile("[:—\\-]\\s*");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");

    private static final Pattern PHONE_NOISE = Pattern.compile("[^\\d+]");

    private static final Pattern NON_DIGIT = Pattern.compile("\\D");

    private final ExtractionConfig config;

    public DemographicFieldExtractor(@NonNull ExtractionConfig config) {
        this.config = config;
    }

    @NonNull
    public Map<String, String> extract(
            @NonNull String rawText,
            @Nullable String documentType,
            @NonNull List<FieldSpec> spec) {

        String normalized = TextNormalizer.normalize(rawText);
        String[] lines = normalized.split("\n", -1);

        Map<String, String> result = new LinkedHashMap<>();

        for (FieldSpec field : spec) {
            String value = extractField(field, lines);
            if (value != null) {
                result.put(field.getId(), value);
            }
        }

        Log.d(TAG, "Extracted " + result.size() + "/" + spec.size()
                + " fields (docType=" + documentType + ")");
        return result;
    }
    @Nullable
    private String extractField(@NonNull FieldSpec field, @NonNull String[] lines) {
        // Collect labels from both subType and id (fallback)
        List<String> labels = collectLabels(field);
        if (labels.isEmpty()) {
            return null;
        }

        for (String label : labels) {
            String value = findValueByLabel(label, lines);
            if (value != null && !value.isEmpty()) {
                String normalized = normalizeValue(value, field);
                if (normalized != null && !normalized.isEmpty()) {
                    return normalized;
                }
            }
        }
        return null;
    }
    @NonNull
    private List<String> collectLabels(@NonNull FieldSpec field) {
        String subType = field.getSubType();
        if (subType != null && !subType.isEmpty()) {
            List<String> subTypeLabels = config.labelsForSubType(subType);
            if (!subTypeLabels.isEmpty()) {
                List<String> idLabels = config.labelsForSubType(field.getId());
                if (!idLabels.isEmpty()) {
                    java.util.ArrayList<String> merged = new java.util.ArrayList<>(subTypeLabels);
                    for (String idLabel : idLabels) {
                        if (!merged.contains(idLabel)) {
                            merged.add(idLabel);
                        }
                    }
                    return merged;
                }
                return subTypeLabels;
            }
        }

        return config.labelsForSubType(field.getId());
    }
    @Nullable
    private String findValueByLabel(@NonNull String label, @NonNull String[] lines) {
        String labelLower = label.toLowerCase(Locale.US);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String lineLower = line.toLowerCase(Locale.US);
            int labelIdx = lineLower.indexOf(labelLower);
            if (labelIdx < 0) continue;

            String afterLabel = line.substring(labelIdx + label.length()).trim();

            Matcher sepMatcher = SEPARATOR.matcher(afterLabel);
            if (sepMatcher.lookingAt()) {
                String value = afterLabel.substring(sepMatcher.end()).trim();
                if (!value.isEmpty()) {
                    return value;
                }
            }
            else if (!afterLabel.isEmpty()) {
                return afterLabel;
            }

            for (int j = i + 1; j < lines.length; j++) {
                String nextLine = lines[j].trim();
                if (!nextLine.isEmpty()) {
                    return nextLine;
                }
            }
        }
        return null;
    }
    @Nullable
    private String normalizeValue(@NonNull String rawValue, @NonNull FieldSpec field) {
        String subType = field.getSubType();
        if (subType == null || subType.isEmpty()) {
            subType = field.getId();
        }

        String lower = subType.toLowerCase(Locale.US);

        if (lower.contains("date") || lower.equals("agedate") || lower.contains("dob")) {
            return DateNormalizer.toAgeDateFormat(rawValue);
        }

        if (lower.contains("name") || lower.contains("father")) {
            return toTitleCase(rawValue.trim());
        }

        if (lower.contains("gender") || lower.equals("sex")) {
            return normalizeGender(rawValue.trim());
        }

        if (lower.contains("email")) {
            return normalizeEmail(rawValue.trim());
        }

        if (lower.contains("phone") || lower.contains("mobile") || lower.contains("tel")) {
            return normalizePhone(rawValue.trim());
        }

        if (lower.contains("postal") || lower.contains("pin") || lower.contains("zip")) {
            String digits = NON_DIGIT.matcher(rawValue).replaceAll("");
            return digits.isEmpty() ? null : digits;
        }

        return rawValue.trim();
    }

    @NonNull
    private String toTitleCase(@NonNull String input) {
        if (input.isEmpty()) return input;
        String[] words = input.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (w.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(w.charAt(0)));
            if (w.length() > 1) {
                sb.append(w.substring(1).toLowerCase(Locale.US));
            }
        }
        return sb.toString();
    }

    @Nullable
    private String normalizeGender(@NonNull String raw) {
        Set<String> genderValues = config.genderValues();
        String lower = raw.toLowerCase(Locale.US).trim();

        // Direct match against known values
        for (String gender : genderValues) {
            if (gender.equalsIgnoreCase(lower)) {
                return toTitleCase(gender);
            }
        }

        String[] words = raw.split("\\s+");
        for (String word : words) {
            String clean = word.replaceAll("[^\\p{L}]", "").toLowerCase(Locale.US);
            for (String gender : genderValues) {
                if (gender.equalsIgnoreCase(clean)) {
                    return toTitleCase(gender);
                }
            }
        }
        return null;
    }

    @Nullable
    private String normalizeEmail(@NonNull String raw) {
        Matcher matcher = EMAIL_PATTERN.matcher(raw);
        return matcher.find() ? matcher.group().toLowerCase(Locale.US) : null;
    }

    @Nullable
    private String normalizePhone(@NonNull String raw) {
        boolean hasPlus = raw.startsWith("+");
        String digits = PHONE_NOISE.matcher(raw).replaceAll("");
        if (digits.isEmpty()) return null;
        return hasPlus ? "+" + digits : digits;
    }
}
