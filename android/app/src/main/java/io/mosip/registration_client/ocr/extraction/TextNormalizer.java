/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.extraction;

import androidx.annotation.NonNull;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class TextNormalizer {

    private TextNormalizer() {}

    private static final Pattern TAB_PATTERN =
            Pattern.compile("\t");
    private static final Pattern MULTIPLE_SPACES_PATTERN =
            Pattern.compile(" {2,}");
    private static final Pattern STRAY_UNDERSCORE_START_PATTERN =
            Pattern.compile("(?m)^_+\\s*");
    private static final Pattern STRAY_UNDERSCORE_END_PATTERN =
            Pattern.compile("(?m)\\s*_+$");
    private static final Pattern NOISE_CHARS_PATTERN =
            Pattern.compile("[|\\\\]");
    private static final Pattern LINE_ENDINGS_PATTERN =
            Pattern.compile("\\r\\n|\\r");
    private static final Pattern SMART_QUOTES_PATTERN =
            Pattern.compile("[\u201C\u201D\u201E\u201F\u2018\u2019]");

    @NonNull
    public static String normalize(@NonNull String rawText) {
        String s = Normalizer.normalize(rawText, Normalizer.Form.NFKC);

        s = LINE_ENDINGS_PATTERN.matcher(s).replaceAll("\n");
        s = TAB_PATTERN.matcher(s).replaceAll(" ");
        s = MULTIPLE_SPACES_PATTERN.matcher(s).replaceAll(" ");
        s = NOISE_CHARS_PATTERN.matcher(s).replaceAll("");
        s = SMART_QUOTES_PATTERN.matcher(s).replaceAll("\"");
        s = STRAY_UNDERSCORE_START_PATTERN.matcher(s).replaceAll("");
        s = STRAY_UNDERSCORE_END_PATTERN.matcher(s).replaceAll("");

        String[] lines = s.split("\n", -1);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if ((line.startsWith(":") || line.startsWith(";")) && !hasAlphanumBeforeSeparator(line)) {
                line = line.substring(1).trim();
            }
            result.append(line);
            if (i < lines.length - 1) result.append('\n');
        }

        return result.toString();
    }

    @NonNull
    public static String normalizeBloodGroup(@NonNull String raw) {
        String normalized = raw
                .replaceAll("([AaBbOo]+)\\u2022", "$1+")
                .replaceAll("([AaBbOo]+)\\.", "$1+")
                .trim();

        // Standardise spacing around (ve) / (ve)
        normalized = normalized.replaceAll("\\+\\s*\\(\\s*[Vv][Ee]\\s*\\)", "+ (ve)");
        normalized = normalized.replaceAll("-\\s*\\(\\s*[Vv][Ee]\\s*\\)", "- (ve)");

        return normalized;
    }

    private static boolean hasAlphanumBeforeSeparator(@NonNull String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ':' || c == ';') return false;
            if (Character.isLetterOrDigit(c)) return true;
        }
        return false;
    }
}
