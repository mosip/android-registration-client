/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.engine;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

public interface OcrEngine {
    void extractText(
            @NonNull Bitmap bitmap,
            @NonNull String fileType,
            @NonNull OcrEngineCallback callback);
    void release();
    interface OcrEngineCallback {
        void onSuccess(@NonNull String rawText, float avgConfidence);

        void onFailure(
                @NonNull String errorCode,
                @NonNull String message,
                boolean isRetryable);
    }
}
