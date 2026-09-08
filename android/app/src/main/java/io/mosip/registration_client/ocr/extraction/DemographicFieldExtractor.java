/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.extraction;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.mlkit.vision.text.Text;

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

    private static final Pattern SEPARATOR =
            Pattern.compile("[;:—–\\-]\\s*");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");

    private static final Pattern PHONE_NOISE = Pattern.compile("[^\\d+]");
    private static final Pattern NON_DIGIT   = Pattern.compile("\\D");
    private static final Pattern LABEL_TOKEN_BOUNDARY =
            Pattern.compile("(?<![\\p{L}\\p{N}])%s(?![\\p{L}\\p{N}])");

    private final ExtractionConfig config;

    public DemographicFieldExtractor(@NonNull ExtractionConfig config) {
        this.config = config;
    }

    @NonNull
    public Map<String, String> extract(
            @NonNull Text visionText,
            @Nullable String documentType,
            @NonNull List<FieldSpec> spec) {

        // Reconstruct geometry-aware rows
        List<BlockGeometryParser.LayoutRow> rows = normalizeRows(BlockGeometryParser.parse(visionText));

        // Also keep the flat normalized lines for Pass 2 / Pass 3
        String normalizedText = TextNormalizer.normalize(visionText.getText());
        String[] flatLines    = normalizedText.split("\n", -1);

        return extractFromRows(rows, flatLines, documentType, spec);
    }

    @NonNull
    public Map<String, String> extract(
            @NonNull String rawText,
            @Nullable String documentType,
            @NonNull List<FieldSpec> spec) {

        String normalized = TextNormalizer.normalize(rawText);
        String[] lines    = normalized.split("\n", -1);

        // Build synthetic LayoutRows from flat lines (no geometry)
        List<BlockGeometryParser.LayoutRow> syntheticRows =
                BlockGeometryParser.parseFlat(lines);

        return extractFromRows(syntheticRows, lines, documentType, spec);
    }

    // ── Core extraction logic ─────────────────────────────────────────────────

    @NonNull
    private Map<String, String> extractFromRows(
            @NonNull List<BlockGeometryParser.LayoutRow> rows,
            @NonNull String[] flatLines,
            @Nullable String documentType,
            @NonNull List<FieldSpec> spec) {

        Map<String, String> result = new LinkedHashMap<>();
        double threshold = config.getFuzzyThreshold();
        List<String> allLabels = collectAllLabels(spec);

        for (FieldSpec field : spec) {
            List<String> labels = collectLabels(field);
            String normalized = null;
            if (!labels.isEmpty()) {
                normalized = normalizeCandidate(
                        pass1GeometryColonSplit(labels, rows, allLabels, threshold), field);
                if (normalized != null) {
                    result.put(field.getId(), normalized);
                    continue;
                }

                normalized = normalizeCandidate(
                        pass2FuzzyNextLine(labels, flatLines, allLabels, threshold), field);
                if (normalized != null) {
                    result.put(field.getId(), normalized);
                    continue;
                }
            }

            normalized = normalizeCandidate(pass3FieldRegex(field, rows, flatLines), field);
            if (normalized != null) {
                result.put(field.getId(), normalized);
            }
        }

        Log.d(TAG, "Extracted " + result.size() + "/" + spec.size()
                + " fields (docType=" + documentType + ")");
        return result;
    }
    @Nullable
    private String pass1GeometryColonSplit(
            @NonNull List<String> labels,
            @NonNull List<BlockGeometryParser.LayoutRow> rows,
            @NonNull List<String> allLabels,
            double threshold) {

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            BlockGeometryParser.LayoutRow row = rows.get(rowIndex);
            String rowLower = row.fullText.toLowerCase(Locale.US);

            for (String label : labels) {
                String labelLower = label.toLowerCase(Locale.US);

                int idx = findUsableExactLabelStart(rowLower, labelLower, allLabels);
                if (idx < 0) {
                    if (letterOrDigitCount(labelLower) < 4) continue;
                    if (!FuzzyMatcher.containsFuzzy(rowLower, labelLower, threshold)) continue;
                    idx = findApproximateStart(rowLower, labelLower, threshold);
                    if (idx < 0) continue;
                }

                String afterLabel = row.fullText.substring(
                        Math.min(idx + label.length(), row.fullText.length())).trim();

                Matcher sep = SEPARATOR.matcher(afterLabel);
                if (sep.lookingAt()) {
                    String value = afterLabel.substring(sep.end()).trim();
                    if (!value.isEmpty()) {
                        return collectContinuation(trimAtEmbeddedFieldLabel(value, allLabels), rows, rowIndex + 1, allLabels,
                                threshold);
                    }
                } else if (!afterLabel.isEmpty() && !isLikelyAnotherLabel(afterLabel)) {
                    return collectContinuation(trimAtEmbeddedFieldLabel(afterLabel, allLabels), rows, rowIndex + 1, allLabels,
                            threshold);
                }
            }
        }
        return null;
    }

    @Nullable
    private String pass2FuzzyNextLine(
            @NonNull List<String> labels,
            @NonNull String[] lines,
            @NonNull List<String> allLabels,
            double threshold) {

        for (int i = 0; i < lines.length; i++) {
            String line      = lines[i].trim();
            String lineLower = line.toLowerCase(Locale.US);
            if (lineLower.isEmpty()) continue;

            for (String label : labels) {
                String labelLower = label.toLowerCase(Locale.US);
                if (!FuzzyMatcher.containsFuzzy(lineLower, labelLower, threshold)) continue;

                // Check if value is on the same line after a separator
                int idx = findUsableExactLabelStart(lineLower, labelLower, allLabels);
                if (idx < 0) {
                    if (letterOrDigitCount(labelLower) < 4) continue;
                    idx = findApproximateStart(lineLower, labelLower, threshold);
                    if (idx < 0) continue;
                }
                String afterLabel = line.substring(
                        Math.min(idx + label.length(), line.length())).trim();
                Matcher sep = SEPARATOR.matcher(afterLabel);
                if (sep.lookingAt()) {
                    String sameLineValue = trimAtEmbeddedFieldLabel(
                            afterLabel.substring(sep.end()).trim(), allLabels);
                    if (!sameLineValue.isEmpty()) {
                        StringBuilder value = new StringBuilder(sameLineValue);
                        for (int j = i + 1; j < lines.length; j++) {
                            String nextLine = lines[j].trim();
                            if (nextLine.isEmpty()) break;
                            if (isFieldLabel(nextLine, allLabels, threshold)) break;
                            value.append(' ').append(trimAtEmbeddedFieldLabel(nextLine, allLabels));
                        }
                        return value.toString();
                    }
                }

                StringBuilder value = new StringBuilder();
                for (int j = i + 1; j < lines.length; j++) {
                    String nextLine = lines[j].trim();
                    if (nextLine.isEmpty()) break;
                    if (value.length() > 0 && isFieldLabel(nextLine, allLabels, threshold)) {
                        break;
                    }
                    if (value.length() > 0) value.append(' ');
                    value.append(trimAtEmbeddedFieldLabel(nextLine, allLabels));
                }
                if (value.length() > 0) return value.toString();
            }
        }
        return null;
    }

    @Nullable
    private String pass3FieldRegex(
            @NonNull FieldSpec field,
            @NonNull List<BlockGeometryParser.LayoutRow> rows,
            @NonNull String[] flatLines) {

        String subType = field.getSubType();
        if (subType == null || subType.isEmpty()) subType = field.getId();
        String key = subType.toLowerCase(Locale.US).replaceAll("[^a-z]", "");

        Map<String, String> regexMap = config.getFieldRegexMap();
        String patternStr = regexMap.get(key);
        if (patternStr == null) {
            patternStr = regexMap.get(field.getId().toLowerCase(Locale.US).replaceAll("[^a-z]", ""));
        }
        if (patternStr == null) {
            String descriptor = (field.getId() + " " + subType).toLowerCase(Locale.US);
            if (descriptor.contains("email")) {
                patternStr = regexMap.get("email");
            } else if (descriptor.contains("phone") || descriptor.contains("mobile")
                    || descriptor.contains("telephone") || descriptor.contains("tel")) {
                patternStr = regexMap.get("phone");
            } else if (descriptor.contains("date") || descriptor.contains("dob")) {
                patternStr = regexMap.get("dateofbirth");
            }
        }
        if (patternStr == null) return null;

        Pattern pattern;
        try {
            pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
        } catch (Exception e) {
            Log.w(TAG, "Invalid regex for field " + field.getId() + ": " + patternStr, e);
            return null;
        }

        for (BlockGeometryParser.LayoutRow row : rows) {
            Matcher m = pattern.matcher(row.fullText);
            if (m.find()) return m.group().trim();
        }

        for (String line : flatLines) {
            Matcher m = pattern.matcher(line.trim());
            if (m.find()) return m.group().trim();
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
                    for (String l : idLabels) {
                        if (!merged.contains(l)) merged.add(l);
                    }
                    return merged;
                }
                return subTypeLabels;
            }
        }
        return config.labelsForSubType(field.getId());
    }

    @NonNull
    private List<String> collectAllLabels(@NonNull List<FieldSpec> spec) {
        java.util.ArrayList<String> labels = new java.util.ArrayList<>();
        for (FieldSpec field : spec) {
            for (String label : collectLabels(field)) {
                if (!label.trim().isEmpty() && !labels.contains(label)) labels.add(label);
            }
            if (field.getSubType() != null && !field.getSubType().trim().isEmpty()
                    && !labels.contains(field.getSubType())) {
                labels.add(field.getSubType());
            }
            if (field.getId() != null && !field.getId().trim().isEmpty()
                    && !labels.contains(field.getId())) {
                labels.add(field.getId());
            }
        }
        java.util.ArrayList<String> ordered = new java.util.ArrayList<>(labels);
        ordered.sort((a, b) -> Integer.compare(b.length(), a.length()));
        return ordered;
    }

    @NonNull
    private static String collectContinuation(
            @NonNull String firstLine,
            @NonNull List<BlockGeometryParser.LayoutRow> rows,
            int start,
            @NonNull List<String> allLabels,
            double threshold) {
        StringBuilder value = new StringBuilder(firstLine.trim());
        for (int i = start; i < rows.size(); i++) {
            String line = rows.get(i).fullText.trim();
            if (line.isEmpty()) break;
            if (isFieldLabel(line, allLabels, threshold)) break;
            String continuation = trimAtEmbeddedFieldLabel(line, allLabels);
            if (continuation.isEmpty()) break;
            value.append(' ').append(continuation);
        }
        return value.toString().trim();
    }

    private static boolean isFieldLabel(
            @NonNull String line,
            @NonNull List<String> labels,
            double threshold) {
        String normalized = line.trim().toLowerCase(Locale.US);
        if (normalized.isEmpty() || normalized.length() > 40) return false;
        for (String label : labels) {
            String candidate = label.trim().toLowerCase(Locale.US);
            if (normalized.equals(candidate)) return true;
            int labelStart = findExactLabelStart(normalized, candidate);
            if (labelStart == 0 && isSeparatorAfterLabel(normalized, candidate.length())) return true;
            if (candidate.length() >= 4
                    && FuzzyMatcher.containsFuzzy(normalized, candidate, threshold)) {
                return true;
            }
        }
        return false;
    }

    private static int findApproximateStart(@NonNull String haystack,
                                            @NonNull String needle,
                                            double threshold) {
        String[] needleWords    = needle.split("\\s+");
        String[] haystackWords  = haystack.split("\\s+");
        if (needleWords.length == 0 || haystackWords.length == 0) return -1;

        String firstNeedle = needleWords[0];
        int charPos = 0;
        for (String word : haystackWords) {
            if (FuzzyMatcher.jaroWinkler(word, firstNeedle) >= threshold) {
                return charPos;
            }
            charPos += word.length() + 1;
        }
        return -1;
    }

    private static int findExactLabelStart(@NonNull String text, @NonNull String label) {
        if (label.isEmpty()) return -1;
        Matcher matcher = Pattern.compile(String.format(Locale.US,
                LABEL_TOKEN_BOUNDARY.pattern(), Pattern.quote(label))).matcher(text);
        return matcher.find() ? matcher.start() : -1;
    }

    private static int findUsableExactLabelStart(
            @NonNull String text, @NonNull String label, @NonNull List<String> allLabels) {
        if (label.isEmpty()) return -1;
        Matcher matcher = Pattern.compile(String.format(Locale.US,
                LABEL_TOKEN_BOUNDARY.pattern(), Pattern.quote(label))).matcher(text);
        while (matcher.find()) {
            if (!isPartOfMoreSpecificLabel(text, label, matcher.start(), allLabels)) {
                return matcher.start();
            }
        }
        return -1;
    }

    private static boolean isPartOfMoreSpecificLabel(
            @NonNull String text,
            @NonNull String matchedLabel,
            int matchedStart,
            @NonNull List<String> allLabels) {
        int matchedEnd = matchedStart + matchedLabel.length();
        for (String label : allLabels) {
            String candidate = label.trim().toLowerCase(Locale.US);
            if (candidate.length() <= matchedLabel.length()) continue;
            int candidateStart = findExactLabelStart(text, candidate);
            if (candidateStart >= 0 && candidateStart <= matchedStart
                    && candidateStart + candidate.length() >= matchedEnd) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSeparatorAfterLabel(@NonNull String text, int labelEnd) {
        return labelEnd < text.length() && SEPARATOR.matcher(text.substring(labelEnd).trim()).lookingAt();
    }

    private static int letterOrDigitCount(@NonNull String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetterOrDigit(text.charAt(i))) count++;
        }
        return count;
    }

    @NonNull
    private static String trimAtEmbeddedFieldLabel(@NonNull String value,
                                                    @NonNull List<String> labels) {
        int earliest = value.length();
        String lower = value.toLowerCase(Locale.US);
        for (String label : labels) {
            String candidate = label.trim().toLowerCase(Locale.US);
            if (candidate.isEmpty()) continue;
            int start = findExactLabelStart(lower, candidate);
            if (start <= 0) continue;
            int end = start + candidate.length();
            if (isSeparatorAfterLabel(lower, end)) earliest = Math.min(earliest, start);
        }
        return value.substring(0, earliest).trim();
    }

    @NonNull
    private static List<BlockGeometryParser.LayoutRow> normalizeRows(
            @NonNull List<BlockGeometryParser.LayoutRow> rows) {
        java.util.ArrayList<BlockGeometryParser.LayoutRow> normalized = new java.util.ArrayList<>();
        for (BlockGeometryParser.LayoutRow row : rows) {
            String text = TextNormalizer.normalize(row.fullText).replace('\n', ' ').trim();
            if (!text.isEmpty()) {
                normalized.add(new BlockGeometryParser.LayoutRow(text, row.fragments, row.bounds));
            }
        }
        return normalized;
    }

    private static boolean isLikelyAnotherLabel(@NonNull String s) {
        if (s.length() > 30) return false;
        // Contains digits → likely a value (DOB, phone, etc.)
        if (s.matches(".*\\d.*")) return false;
        // Contains separator → might be another label:value pair
        return s.contains(":") || s.contains("—");
    }


    @Nullable
    private String normalizeCandidate(@Nullable String value, @NonNull FieldSpec field) {
        if (value == null || value.trim().isEmpty()) return null;
        String normalized = normalizeValue(value, field);
        return normalized == null || normalized.isEmpty() ? null : normalized;
    }

    @Nullable
    private String normalizeValue(@NonNull String rawValue, @NonNull FieldSpec field) {
        String subType = field.getSubType();
        if (subType == null || subType.isEmpty()) subType = field.getId();
        String lower = subType.toLowerCase(Locale.US);

        if (lower.contains("date") || lower.equals("agedate") || lower.contains("dob")) {
            return DateNormalizer.toAgeDateFormat(rawValue, config.monthNames());
        }

        if (lower.contains("name") || lower.contains("father") || lower.contains("mother")) {
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

        if (lower.contains("blood")) {
            return TextNormalizer.normalizeBloodGroup(rawValue.trim());
        }

        return rawValue.trim();
    }

    @NonNull
    private static String toTitleCase(@NonNull String input) {
        if (input.isEmpty()) return input;
        String[] words = input.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (w.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(w.charAt(0)));
            if (w.length() > 1) sb.append(w.substring(1).toLowerCase(Locale.US));
        }
        return sb.toString();
    }

    @Nullable
    private String normalizeGender(@NonNull String raw) {
        Set<String> genderValues = config.genderValues();
        String lower = raw.toLowerCase(Locale.US).trim();
        for (String g : genderValues) {
            if (g.equalsIgnoreCase(lower)) return toTitleCase(g);
        }
        for (String word : raw.split("\\s+")) {
            String clean = word.replaceAll("[^\\p{L}]", "").toLowerCase(Locale.US);
            for (String g : genderValues) {
                if (g.equalsIgnoreCase(clean)) return toTitleCase(g);
            }
        }
        return null;
    }

    @Nullable
    private static String normalizeEmail(@NonNull String raw) {
        Matcher m = EMAIL_PATTERN.matcher(raw);
        return m.find() ? m.group().toLowerCase(Locale.US) : null;
    }

    @Nullable
    private static String normalizePhone(@NonNull String raw) {
        boolean hasPlus = raw.startsWith("+");
        String digits   = PHONE_NOISE.matcher(raw).replaceAll("");
        if (digits.isEmpty()) return null;
        return hasPlus ? "+" + digits : digits;
    }
}
