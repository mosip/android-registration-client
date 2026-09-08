/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.extraction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateNormalizer {

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final List<DateTimeFormatter> CANDIDATE_FORMATS = Arrays.asList(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
    );
    private static final Pattern THREE_DIGIT_GROUPS =
            Pattern.compile("(\\d{1,4})[\\-/. ](\\d{1,2})[\\-/. ](\\d{2,4})");
    private static final char[][] OCR_SUBS = {
            {'O', '0'}, {'o', '0'},
            {'I', '1'}, {'l', '1'},
            {'S', '5'},
            {'B', '8'},
            {'Z', '2'}
    };

    private DateNormalizer() { }

    @Nullable
    public static String toAgeDateFormat(@NonNull String rawValue) {
        return toAgeDateFormat(rawValue, null);
    }

    @Nullable
    public static String toAgeDateFormat(@NonNull String rawValue,
                                         @Nullable Set<String> monthNames) {
        String trimmed = rawValue.trim();

        String result = tryFormats(trimmed);
        if (result != null) return result;

        String subbed = applyOcrSubstitutions(trimmed);
        if (!subbed.equals(trimmed)) {
            result = tryFormats(subbed);
            if (result != null) return result;
        }

        if (monthNames != null && !monthNames.isEmpty()) {
            result = tryMonthNameFormat(trimmed, monthNames);
            if (result != null) return result;
            // Also try on substituted string
            if (!subbed.equals(trimmed)) {
                result = tryMonthNameFormat(subbed, monthNames);
                if (result != null) return result;
            }
        }

        result = tryPartialRescue(trimmed);
        if (result != null) return result;
        return tryPartialRescue(subbed);
    }


    @Nullable
    private static String tryFormats(@NonNull String value) {
        for (DateTimeFormatter fmt : CANDIDATE_FORMATS) {
            try {
                return LocalDate.parse(value, fmt).format(OUTPUT_FORMAT);
            } catch (DateTimeParseException ignored) { }
        }
        return null;
    }

    @NonNull
    private static String applyOcrSubstitutions(@NonNull String input) {
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '-' || c == '/' || c == '.' || c == ' ') continue;
            for (char[] sub : OCR_SUBS) {
                if (c == sub[0]) {
                    chars[i] = sub[1];
                    break;
                }
            }
        }
        return new String(chars);
    }

    @Nullable
    private static String tryMonthNameFormat(@NonNull String value,
                                              @NonNull Set<String> monthNames) {
        String[] parts = value.trim().split("[\\s\\-/.,]+");
        if (parts.length != 3) return null;

        for (int monthPos = 0; monthPos < 3; monthPos++) {
            String candidate = parts[monthPos].toLowerCase(Locale.US)
                                              .replaceAll("[^a-z]", "");
            if (!monthNames.contains(candidate)) continue;

            int monthNum = monthNameToNumber(candidate);
            if (monthNum < 0) continue;

            try {
                if (monthPos == 1) {
                    int day  = Integer.parseInt(applyOcrSubstitutions(parts[0]));
                    int year = Integer.parseInt(applyOcrSubstitutions(parts[2]));
                    return LocalDate.of(year, monthNum, day).format(OUTPUT_FORMAT);
                } else if (monthPos == 0) {
                    int day  = Integer.parseInt(applyOcrSubstitutions(parts[1]));
                    int year = Integer.parseInt(applyOcrSubstitutions(parts[2]));
                    return LocalDate.of(year, monthNum, day).format(OUTPUT_FORMAT);
                }
                // monthPos == 2 (unusual) — skip
            } catch (Exception ignored) { }
        }
        return null;
    }

    @Nullable
    private static String tryPartialRescue(@NonNull String value) {
        Matcher m = THREE_DIGIT_GROUPS.matcher(applyOcrSubstitutions(value));
        if (!m.find()) return null;
        try {
            int a = Integer.parseInt(m.group(1));
            int b = Integer.parseInt(m.group(2));
            int c = Integer.parseInt(m.group(3));

            if (a > 31) {
                return LocalDate.of(a, b, c).format(OUTPUT_FORMAT);
            } else if (c > 31) {
                return LocalDate.of(c, b, a).format(OUTPUT_FORMAT);
            }
            return LocalDate.of(c, b, a).format(OUTPUT_FORMAT);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static int monthNameToNumber(@NonNull String lower) {
        switch (lower) {
            case "jan": case "january":   return 1;
            case "feb": case "february":  return 2;
            case "mar": case "march":     return 3;
            case "apr": case "april":     return 4;
            case "may":                   return 5;
            case "jun": case "june":      return 6;
            case "jul": case "july":      return 7;
            case "aug": case "august":    return 8;
            case "sep": case "sept": case "september": return 9;
            case "oct": case "october":   return 10;
            case "nov": case "november":  return 11;
            case "dec": case "december":  return 12;
            default: return -1;
        }
    }
}