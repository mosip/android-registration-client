/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
public final class OcrAuditLogger {

    private static final String TAG = "OCR_AUDIT";

    private OcrAuditLogger() { }

    public static void logProviderError(@NonNull String errorCode, @Nullable String errorInfo) {
        Log.w(TAG, "reason=provider_error errorCode=" + errorCode + " errorInfo=" + errorInfo);
    }

    public static void logClientFailure(@NonNull String reason, @NonNull String message) {
        Log.w(TAG, "reason=" + reason.toLowerCase() + " message=" + message);
    }

    public static void logSuccess(@NonNull String documentType, int fieldsExtracted) {
        Log.i(TAG, "reason=success documentType=" + documentType + " fieldsExtracted=" + fieldsExtracted);
    }

    public static void logQualityAttempt(
            int attemptNumber, @NonNull QualityAnalyzer.QualityResult result, boolean forcedOverride) {
        Log.i(TAG, "reason=quality_attempt attempt=" + attemptNumber
                + " pass=" + result.isAcceptable
                + " brightness=" + result.brightness
                + " blurVariance=" + result.blurVariance
                + " forcedOverride=" + forcedOverride
                + " guidance=" + result.guidanceMessage);
    }
}