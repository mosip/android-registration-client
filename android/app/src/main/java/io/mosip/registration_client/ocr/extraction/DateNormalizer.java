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

public final class DateNormalizer {

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final List<DateTimeFormatter> CANDIDATE_FORMATS = Arrays.asList(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    );

    private DateNormalizer() { }

    @Nullable
    public static String toAgeDateFormat(@NonNull String rawValue) {
        String trimmed = rawValue.trim();
        for (DateTimeFormatter format : CANDIDATE_FORMATS) {
            try {
                LocalDate date = LocalDate.parse(trimmed, format);
                return date.format(OUTPUT_FORMAT);
            } catch (DateTimeParseException ignored) {
                // try next candidate
            }
        }
        return null;
    }
}