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

    private static final Pattern TAB_PATTERN = Pattern.compile("\t");
    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile(" {2,}");
    private static final Pattern STRAY_UNDERSCORE_START_PATTERN = Pattern.compile("(?m)^_+\\s*");
    private static final Pattern STRAY_UNDERSCORE_END_PATTERN = Pattern.compile("(?m)\\s*_+$");
    private static final Pattern NOISE_CHARS_PATTERN = Pattern.compile("[|\\\\]");
    private static final Pattern LINE_ENDINGS_PATTERN = Pattern.compile("\\r\\n|\\r");

    @NonNull
    public static String normalize(@NonNull String rawText) {
        String normalized = Normalizer.normalize(rawText, Normalizer.Form.NFKC);

        normalized = LINE_ENDINGS_PATTERN.matcher(normalized).replaceAll("\n");

        normalized = TAB_PATTERN.matcher(normalized).replaceAll(" ");

        normalized = MULTIPLE_SPACES_PATTERN.matcher(normalized).replaceAll(" ");

        normalized = NOISE_CHARS_PATTERN.matcher(normalized).replaceAll("");
        normalized = STRAY_UNDERSCORE_START_PATTERN.matcher(normalized).replaceAll("");
        normalized = STRAY_UNDERSCORE_END_PATTERN.matcher(normalized).replaceAll("");

        String[] lines = normalized.split("\n", -1);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            result.append(lines[i].trim());
            if (i < lines.length - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }
}
