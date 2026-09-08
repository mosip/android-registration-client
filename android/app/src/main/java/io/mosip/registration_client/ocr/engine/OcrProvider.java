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

public interface OcrProvider {

    void extract(
            @NonNull Bitmap bitmap,
            @NonNull List<FieldSpec> spec,
            @NonNull OcrProviderCallback callback);

    void release();

    interface OcrProviderCallback {

        void onSuccess(@NonNull String documentType, float confidence, @NonNull Map<String, String> data);
        void onProviderError(@NonNull String errorCode, @Nullable String errorInfo);
        void onFailure(@NonNull String errorCode, @NonNull String message, boolean isRetryable);
    }
}