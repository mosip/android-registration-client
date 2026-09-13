/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.engine;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.mosip.registration_client.ocr.OcrConfig;
import io.mosip.registration_client.ocr.extraction.ExtractionConfig;

/**
 * Builds the active {@link OcrProvider} from config — a deployment-time
 * choice (mosip.registration.ocr.provider), not something the operator
 * picks at runtime. Always returns the provider wrapped in
 * {@link TimeoutEnforcingOcrProvider} so response.timeout applies
 * uniformly regardless of which provider is selected.
 */
public final class OcrProviderFactory {

    private static final String TAG = "OcrProviderFactory";
    private static final String PROVIDER_REMOTE = "remote";
    private static final String PROVIDER_MLKIT = "mlkit";

    private OcrProviderFactory() { }

    @NonNull
    public static OcrProvider create(
            @NonNull OcrConfig config,
            @Nullable DocumentClassifier documentClassifier,
            @NonNull ExtractionConfig extractionConfig) {
        OcrProvider provider;

        if (PROVIDER_REMOTE.equalsIgnoreCase(config.provider)) {
            if (config.serviceUrl == null || config.serviceUrl.trim().isEmpty()) {
                throw new IllegalStateException(
                        "mosip.registration.ocr.service.url must be set when ocr.provider = remote");
            }
            provider = new RemoteOcrProvider(config.serviceUrl, config.responseTimeoutMs);
        } else {
            if (!PROVIDER_MLKIT.equalsIgnoreCase(config.provider)) {
                Log.w(TAG, "Unrecognized ocr.provider '" + config.provider + "', defaulting to mlkit");
            }
            provider = new MlKitOcrProvider(documentClassifier, extractionConfig);
        }

        return new TimeoutEnforcingOcrProvider(provider, config.responseTimeoutMs);
    }
}