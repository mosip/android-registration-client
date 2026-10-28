/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.extraction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class FuzzyMatcher {

    public static final double DEFAULT_THRESHOLD = 0.82;

    private static final double PREFIX_SCALE = 0.1;
    private static final int    MAX_PREFIX    = 4;

    private FuzzyMatcher() {}

    public static double jaroWinkler(@Nullable String s1, @Nullable String s2) {
        if (s1 == null || s2 == null) return 0.0;
        if (s1.equals(s2)) return 1.0;
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;

        double jaro = jaro(s1, s2);

        int prefix = 0;
        int minLen = Math.min(Math.min(s1.length(), s2.length()), MAX_PREFIX);
        for (int i = 0; i < minLen; i++) {
            if (s1.charAt(i) == s2.charAt(i)) prefix++;
            else break;
        }

        return jaro + prefix * PREFIX_SCALE * (1.0 - jaro);
    }
    public static boolean containsFuzzy(
            @Nullable String haystack,
            @Nullable String needle,
            double threshold) {

        if (haystack == null || needle == null || needle.isEmpty()) return false;

        if (haystack.contains(needle)) return true;

        if (jaroWinkler(haystack, needle) >= threshold) return true;

        String[] haystackWords = haystack.split("\\s+");
        String[] needleWords   = needle.split("\\s+");
        int nw = needleWords.length;
        if (haystackWords.length < nw) return false;

        for (int i = 0; i <= haystackWords.length - nw; i++) {
            StringBuilder candidate = new StringBuilder();
            for (int j = 0; j < nw; j++) {
                if (j > 0) candidate.append(' ');
                candidate.append(haystackWords[i + j]);
            }
            if (jaroWinkler(candidate.toString(), needle) >= threshold) return true;
        }

        return false;
    }

    public static boolean containsFuzzy(@Nullable String haystack, @Nullable String needle) {
        return containsFuzzy(haystack, needle, DEFAULT_THRESHOLD);
    }

    private static double jaro(@NonNull String s1, @NonNull String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        int matchWindow = Math.max(len1, len2) / 2 - 1;
        if (matchWindow < 0) matchWindow = 0;

        boolean[] s1Matched = new boolean[len1];
        boolean[] s2Matched = new boolean[len2];

        int matches = 0;

        for (int i = 0; i < len1; i++) {
            int start = Math.max(0, i - matchWindow);
            int end   = Math.min(i + matchWindow + 1, len2);
            for (int j = start; j < end; j++) {
                if (s2Matched[j] || s1.charAt(i) != s2.charAt(j)) continue;
                s1Matched[i] = true;
                s2Matched[j] = true;
                matches++;
                break;
            }
        }

        if (matches == 0) return 0.0;

        int transpositions = 0;
        int k = 0;
        for (int i = 0; i < len1; i++) {
            if (!s1Matched[i]) continue;
            while (!s2Matched[k]) k++;
            if (s1.charAt(i) != s2.charAt(k)) transpositions++;
            k++;
        }

        return (matches / (double) len1
                + matches / (double) len2
                + (matches - transpositions / 2.0) / matches) / 3.0;
    }
}
