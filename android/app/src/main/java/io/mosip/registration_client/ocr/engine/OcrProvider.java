/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.engine;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

import io.mosip.registration_client.ocr.models.FieldSpec;

/**
 * Common internal OCR provider interface (HLD §4.1). Every implementation —
 * on-device (ML Kit) or remote (HTTP service) — satisfies the same
 * {metadata, data} contract from §6.2 regardless of where extraction
 * actually runs. Provider selection is a deployment-time config choice
 * (mosip.registration.ocr.provider), not a runtime one.
 *
 * Timeout is deliberately NOT a parameter here — a single wrapper
 * ({@link TimeoutEnforcingOcrProvider}) applies mosip.registration.ocr.response.timeout
 * uniformly to whichever provider is active.
 *
 * Two distinct failure channels, matching the flow's two different
 * decision points:
 *  - {@link OcrProviderCallback#onProviderError} — a response WAS
 *    received (within timeout, well-formed), but it carries the
 *    provider's own in-band error per the standardized §6.2.1 table
 *    (0/101-111/5xx). This is the "error.errorCode populated?" branch.
 *  - {@link OcrProviderCallback#onFailure} — the client never got a
 *    usable response at all (timeout, network error, malformed JSON) or
 *    rejected the result after receiving it (no fields survived
 *    validation) — the §8 client-side failure taxonomy.
 */
public interface OcrProvider {

    void extract(
            @NonNull Bitmap bitmap,
            @NonNull List<FieldSpec> spec,
            @NonNull OcrProviderCallback callback);

    void release();

    interface OcrProviderCallback {

        /**
         * @param documentType detected label, or "UNKNOWN" if the
         *                     provider's confidence was too low to commit
         *                     to a label. Passed through as-is — no
         *                     hardcoded list of valid document types,
         *                     since the classification model/labels are
         *                     swappable per deployment.
         */
        void onSuccess(@NonNull String documentType, float confidence, @NonNull Map<String, String> data);

        /**
         * Provider returned a response, but it represents a failure per
         * the standardized errorCode table (§6.2.1) — e.g. 101 document
         * not detected, 106 image quality too low, 111 provider busy.
         */
        void onProviderError(@NonNull String errorCode, @Nullable String errorInfo);

        /** Client-side failure per §8 — no usable response was ever
         *  obtained, or the response was rejected after the fact. */
        void onFailure(@NonNull String errorCode, @NonNull String message, boolean isRetryable);
    }
}