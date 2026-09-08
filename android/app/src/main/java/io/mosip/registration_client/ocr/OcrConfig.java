/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
public final class OcrConfig {

    private static final String TAG = "OcrConfig";
    private static final int DEFAULT_TIMEOUT_MS = 10000;
    private static final String DEFAULT_PROVIDER = "mlkit";

    private static final int DEFAULT_QUALITY_MAX_RETRIES = 8;
    private static final float DEFAULT_BRIGHTNESS_MIN = 80f;
    private static final float DEFAULT_BRIGHTNESS_MAX = 180f;
    private static final double DEFAULT_BLUR_VARIANCE = 300.0;

    public final boolean enabled;
    @NonNull public final String provider;
    public final int responseTimeoutMs;
    @Nullable public final String serviceUrl;

    public final int qualityMaxRetries;
    public final float qualityBrightnessMin;
    public final float qualityBrightnessMax;
    public final double qualityBlurVariance;

    private OcrConfig(
            boolean enabled, @NonNull String provider, int responseTimeoutMs, @Nullable String serviceUrl,
            int qualityMaxRetries, float qualityBrightnessMin, float qualityBrightnessMax, double qualityBlurVariance) {
        this.enabled = enabled;
        this.provider = provider;
        this.responseTimeoutMs = responseTimeoutMs;
        this.serviceUrl = serviceUrl;
        this.qualityMaxRetries = qualityMaxRetries;
        this.qualityBrightnessMin = qualityBrightnessMin;
        this.qualityBrightnessMax = qualityBrightnessMax;
        this.qualityBlurVariance = qualityBlurVariance;
    }

    @NonNull
    public static OcrConfig from(@NonNull GlobalParamRepository globalParamRepository) {
        boolean enabled = parseBoolean(
                safeGet(globalParamRepository, OcrParamKeys.OCR_ENABLED), false);

        String provider = safeGet(globalParamRepository, OcrParamKeys.OCR_PROVIDER);
        if (provider == null || provider.trim().isEmpty()) {
            provider = DEFAULT_PROVIDER;
        }

        int timeout = parseInt(
                safeGet(globalParamRepository, OcrParamKeys.OCR_RESPONSE_TIMEOUT), DEFAULT_TIMEOUT_MS);

        String serviceUrl = safeGet(globalParamRepository, OcrParamKeys.OCR_SERVICE_URL);

        int qualityMaxRetries = parseInt(
                safeGet(globalParamRepository, OcrParamKeys.OCR_QUALITY_MAX_RETRIES), DEFAULT_QUALITY_MAX_RETRIES);

        float brightnessMin = parseFloat(
                safeGet(globalParamRepository, OcrParamKeys.OCR_QUALITY_THRESHOLD_BRIGHTNESS_MIN), DEFAULT_BRIGHTNESS_MIN);
        float brightnessMax = parseFloat(
                safeGet(globalParamRepository, OcrParamKeys.OCR_QUALITY_THRESHOLD_BRIGHTNESS_MAX), DEFAULT_BRIGHTNESS_MAX);
        double blurVariance = parseDouble(
                safeGet(globalParamRepository, OcrParamKeys.OCR_QUALITY_THRESHOLD_BLUR_VARIANCE), DEFAULT_BLUR_VARIANCE);

        return new OcrConfig(
                enabled, provider.trim(), timeout, serviceUrl,
                qualityMaxRetries, brightnessMin, brightnessMax, blurVariance);
    }

    @Nullable
    private static String safeGet(@NonNull GlobalParamRepository repo, @NonNull String key) {
        try {
            return repo.getCachedStringGlobalParam(key);
        } catch (Exception e) {
            Log.e(TAG, "Failed reading global param " + key + ", falling back to default", e);
            return null;
        }
    }

    private static boolean parseBoolean(@Nullable String value, boolean fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        return Boolean.parseBoolean(value.trim());
    }

    private static int parseInt(@Nullable String value, int fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            Log.w(TAG, "Invalid integer for OCR param, using default " + fallback, e);
            return fallback;
        }
    }

    private static float parseFloat(@Nullable String value, float fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        try {
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException e) {
            Log.w(TAG, "Invalid float for OCR param, using default " + fallback, e);
            return fallback;
        }
    }

    private static double parseDouble(@Nullable String value, double fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            Log.w(TAG, "Invalid double for OCR param, using default " + fallback, e);
            return fallback;
        }
    }
}