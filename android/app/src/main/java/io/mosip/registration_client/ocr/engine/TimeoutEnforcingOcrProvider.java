/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.engine;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.mosip.registration_client.ocr.models.FieldSpec;
import io.mosip.registration_client.ocr.models.OcrError;

/**
 * Applies mosip.registration.ocr.response.timeout uniformly to whichever
 * provider is active — "response within timeout?" in the flow. Wraps
 * either provider so neither implements its own timeout.
 *
 * Guards against the delegate calling back after the timeout has already
 * fired (AtomicBoolean latch) — a late result is simply dropped.
 */
public class TimeoutEnforcingOcrProvider implements OcrProvider {

    private final OcrProvider delegate;
    private final int timeoutMs;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public TimeoutEnforcingOcrProvider(@NonNull OcrProvider delegate, int timeoutMs) {
        this.delegate = delegate;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public void extract(
            @NonNull Bitmap bitmap,
            @NonNull List<FieldSpec> spec,
            @NonNull OcrProviderCallback callback) {

        AtomicBoolean completed = new AtomicBoolean(false);

        Runnable timeoutRunnable = () -> {
            if (completed.compareAndSet(false, true)) {
                callback.onFailure(
                        OcrError.ErrorCode.TIMEOUT.name(),
                        "OCR provider did not respond within " + timeoutMs + "ms",
                        true);
            }
        };
        handler.postDelayed(timeoutRunnable, timeoutMs);

        delegate.extract(bitmap, spec, new OcrProviderCallback() {
            @Override
            public void onSuccess(@NonNull String documentType, float confidence, @NonNull Map<String, String> data) {
                if (completed.compareAndSet(false, true)) {
                    handler.removeCallbacks(timeoutRunnable);
                    callback.onSuccess(documentType, confidence, data);
                }
            }

            @Override
            public void onProviderError(@NonNull String errorCode, @Nullable String errorInfo) {
                if (completed.compareAndSet(false, true)) {
                    handler.removeCallbacks(timeoutRunnable);
                    callback.onProviderError(errorCode, errorInfo);
                }
            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String message, boolean isRetryable) {
                if (completed.compareAndSet(false, true)) {
                    handler.removeCallbacks(timeoutRunnable);
                    callback.onFailure(errorCode, message, isRetryable);
                }
            }
        });
    }

    @Override
    public void release() {
        handler.removeCallbacksAndMessages(null);
        delegate.release();
    }
}